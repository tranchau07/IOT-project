package com.example.Iot_Project.service.auto;

import com.example.Iot_Project.document.Classroom;
import com.example.Iot_Project.document.ControlLog;
import com.example.Iot_Project.document.SensorReading;
import com.example.Iot_Project.enums.*;
import com.example.Iot_Project.model.CurrentState;
import com.example.Iot_Project.model.Device;
import com.example.Iot_Project.model.Schedule;
import com.example.Iot_Project.repository.mongo.ClassroomRepository;
import com.example.Iot_Project.repository.mongo.SensorReadingRepository;
import com.example.Iot_Project.service.MqttMessageHandlerService;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.*;
import java.util.*;

import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class AutoControlEngineService {
    ClassroomRepository classroomRepository;
    SensorReadingRepository sensorReadingRepository;
    MqttMessageHandlerService mqttMessageHandlerService;
    MongoTemplate mongoTemplate;
    SimpMessagingTemplate messagingTemplate;

    ZoneId zone = ZoneId.of("Asia/Ho_Chi_Minh");

    public void evaluate(Classroom classroom) throws JsonProcessingException {
        Instant now = Instant.now();

        if (!isDeviceOnline(classroom)) return;

        SensorReading latest = sensorReadingRepository.findFirstByClassroomIdOrderByTimestampDesc(classroom.getId());

        // Rule Engine Logic: Priority-based execution
        // 1. Safety layer (Highest priority) - Always check first
        if (handleSafety(classroom, latest)) return;

        // Block all other automation if the room is locked due to safety
        if (classroom.isFaultLatched()) return;

        // 2. Schedule layer (Medium priority) - Can turn room ON or OFF
        if (handleSchedule(classroom, now)) return;

        // 3. Empty room layer (Lowest priority) - Only if room is currently ON
        if (PowerStatus.ON.equals(classroom.getDevice().getPower())) {
            handleEmptyRoom(classroom, now, latest);
        }
    }

    @Scheduled(fixedRate = 60000)
    public void evaluateAll() {
        log.debug("[AUTO-ENGINE] Bắt đầu chu kỳ quét tự động hóa cho tất cả các phòng...");
        List<Classroom> classrooms = classroomRepository.findAll();
        classrooms.parallelStream().forEach(classroom -> {
            try {
                evaluate(classroom);
            } catch (JsonProcessingException e) {
                log.error("Error evaluating classroom {}", classroom.getId(), e);
            }
        });
    }

    private boolean isDeviceOnline(Classroom classroom) {
        Device device = classroom.getDevice();
        return device != null && ConnectivityStatus.ONLINE.equals(device.getConnectivity());
    }

    private boolean handleSafety(Classroom classroom, SensorReading latest) throws JsonProcessingException {
        if (latest == null || latest.getVoltage() == null) return false;

        double voltage = latest.getVoltage();
        if (voltage > 240) {
            log.warn("[AUTO-ENGINE-SAFETY] NGUY HIỂM: Điện áp phòng {} vượt ngưỡng ({}V). Kích hoạt ngắt điện KHẨN CẤP (EMERGENCY) toàn phòng!", classroom.getId(), voltage);
            
            if (!classroom.isFaultLatched()) {
                classroom.setFaultLatched(true);
                Query query = new Query(Criteria.where("_id").is(classroom.getId()));
                Update update = new Update().set("faultLatched", true);
                mongoTemplate.updateFirst(query, update, Classroom.class);
            }

            sendIfChanged(classroom, buildAllOffState(classroom), Reason.SAFETY_VOLTAGE, ModeControl.EMERGENCY);
            return true; // STOP all other logic
        }
        return false;
    }

    private boolean handleSchedule(Classroom classroom, Instant now) throws JsonProcessingException {
        LocalDate today = LocalDate.now(zone);
        int todayValue = today.getDayOfWeek().getValue();
        LocalTime nowTime = LocalTime.now(zone);

        if (classroom.getSchedules() == null) return false;

        List<Schedule> schedulesToday = classroom.getSchedules().stream()
                .filter(s -> s.getDayOfWeek() == todayValue)
                .sorted(Comparator.comparing(Schedule::getStartTime))
                .toList();

        if (schedulesToday.isEmpty()) {
            return false;
        }

        for (int i = 0; i < schedulesToday.size(); i++) {
            Schedule schedule = schedulesToday.get(i);
            LocalTime start = LocalTime.parse(schedule.getStartTime());
            LocalTime end = LocalTime.parse(schedule.getEndTime());

            LocalTime beforeStart = start.minusMinutes(10);
            LocalTime afterEnd = end.plusMinutes(10);

            // Pre-cooling 10 mins before class
            if (!nowTime.isBefore(beforeStart) && nowTime.isBefore(start)) {
                double avgTemp = getAverageTempLast30Min(classroom.getId());
                log.info("[AUTO-ENGINE-SCHEDULE] Chuẩn bị vào tiết học ({} -> {}) tại phòng {}. Bật làm mát trước (Pre-cooling) (Nhiệt độ TB 30p: {}C)", start, end, classroom.getId(), avgTemp);
                CurrentState state = buildPreClassState(classroom, avgTemp);
                sendIfChanged(classroom, state, Reason.SCHEDULE_START, ModeControl.SCHEDULE);
                return true;
            }

            // During class, don't intervene (let manual control handle it)
            if (!nowTime.isBefore(start) && nowTime.isBefore(end)) {
                return true;
            }

            // After class ends, check if next class is soon
            if (!nowTime.isBefore(end) && nowTime.isBefore(afterEnd)) {
                boolean hasNextSchedule = false;
                if (i + 1 < schedulesToday.size()) {
                    Schedule next = schedulesToday.get(i + 1);
                    LocalTime nextStart = LocalTime.parse(next.getStartTime());
                    if (!nextStart.isAfter(nowTime.plusMinutes(10))) {
                        hasNextSchedule = true;
                    }
                }

                if (!hasNextSchedule) {
                    log.info("[AUTO-ENGINE-SCHEDULE] Hết tiết học tại phòng {} và không có lớp tiếp theo. Kích hoạt tắt toàn bộ thiết bị.", classroom.getId());
                    sendIfChanged(classroom, buildAllOffState(classroom), Reason.SCHEDULE_END, ModeControl.SCHEDULE);
                }
                return true;
            }
        }
        return false;
    }

    private void handleEmptyRoom(Classroom classroom, Instant now, SensorReading latest) throws JsonProcessingException {
        if (latest == null || latest.getEnvironment() == null || latest.getEnvironment().getOccupancy() == null) return;

        if (latest.getEnvironment().getOccupancy() > 0) return;

        var lastOccupiedReadingOptional = sensorReadingRepository.findLastOccupiedReading(classroom.getId());

        if (lastOccupiedReadingOptional.isEmpty()) return;

        Instant lastOccupied = lastOccupiedReadingOptional.get().getTimestamp();
        Duration emptyDuration = Duration.between(lastOccupied, now);

        if (emptyDuration.toMinutes() >= 10) {
            log.info("[AUTO-ENGINE-IDLE] Phát hiện phòng {} trống không người quá 10 phút (từ {}). Tự động ngắt điện thiết bị.", classroom.getId(), lastOccupied);
            sendIfChanged(classroom, buildAllOffState(classroom), Reason.ROOM_EMPTY, ModeControl.AUTOMATIC);
        }
    }

    private double getAverageTempLast30Min(String classroomId) {
        Instant fromTime = Instant.now().minus(Duration.ofMinutes(30));
        Double avg = sensorReadingRepository.getAverageTemp(classroomId, fromTime);
        return avg != null ? avg : 0;
    }

    private CurrentState buildPreClassState(Classroom classroom, double avgTemp) {
        CurrentState current = classroom.getCurrentState();
        CurrentState state = new CurrentState();

        int lightCount = (current != null && current.getLightStates() != null) ? current.getLightStates().size() : 2;
        int fanCount = (current != null && current.getFanSpeed() != null) ? current.getFanSpeed().size() : 2;

        state.setLightStates(new ArrayList<>(Collections.nCopies(lightCount, 1)));
        state.setFanSpeed(new ArrayList<>(Collections.nCopies(fanCount, 1)));
        state.setPower(PowerStatus.ON);

        if (avgTemp < 28) {
            state.setAcMode(AcMode.OFF);
            state.setAcTemp(0.0);
        } else if (avgTemp < 32) {
            state.setAcMode(AcMode.ECO);
            state.setAcTemp(26.0);
        } else {
            state.setAcMode(AcMode.COOL);
            state.setAcTemp(26.0);
        }
        state.setLastUpdated(Instant.now());
        return state;
    }

    private CurrentState buildAllOffState(Classroom classroom) {
        CurrentState current = classroom.getCurrentState();
        CurrentState state = new CurrentState();

        int lightCount = (current != null && current.getLightStates() != null) ? current.getLightStates().size() : 2;
        int fanCount = (current != null && current.getFanSpeed() != null) ? current.getFanSpeed().size() : 2;

        state.setLightStates(Collections.nCopies(lightCount, 0));
        state.setFanSpeed(Collections.nCopies(fanCount, 0));
        state.setAcMode(AcMode.OFF);
        state.setAcTemp(0.0);
        state.setPower(PowerStatus.OFF);
        state.setLastUpdated(Instant.now());
        return state;
    }

    private void sendIfChanged(Classroom classroom, CurrentState newState, Reason reason, ModeControl modeControl) throws JsonProcessingException {
        if (!newState.equals(classroom.getCurrentState())) {
            log.info("[AUTO-ENGINE] Quyết định thay đổi trạng thái phòng {}. Trigger reason: {}. Payload Command: {}", classroom.getId(), reason, newState);
            ControlLog controlLog = new ControlLog();
            controlLog.setCommand(newState);
            controlLog.setReason(reason);
            controlLog.setTimestamp(Instant.now());
            controlLog.setStatus(CommandStatus.SENT);
            controlLog.setMode(modeControl);
            controlLog.setClassroomId(classroom.getId());

            mqttMessageHandlerService.sendControlCommand(
                    classroom.getDevice().getDeviceId(),
                    classroom.getId(),
                    classroom.getBuilding(),
                    controlLog
            );

            // FIX RACE CONDITION: Use MongoTemplate partial update instead of save() to avoid overriding device.lastSeen heartbeat!
            Query query = new Query(Criteria.where("_id").is(classroom.getId()));
            Update update = new Update().set("currentState", newState);
            mongoTemplate.updateFirst(query, update, Classroom.class);

            // Realtime push
            messagingTemplate.convertAndSend("/topic/classroom/" + classroom.getId() + "/state", newState);
        }
    }
}
