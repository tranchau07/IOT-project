package com.example.Iot_Project.service.auto;

import com.example.Iot_Project.enity.*;
import com.example.Iot_Project.enums.*;
import com.example.Iot_Project.repository.ClassroomRepository;
import com.example.Iot_Project.repository.SensorReadingRepository;
import com.example.Iot_Project.service.MqttMessageHandlerService;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.*;
import java.util.*;

@Service
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class AutoControlEngineService {
    ClassroomRepository classroomRepository;
    SensorReadingRepository sensorReadingRepository;
    MqttMessageHandlerService mqttMessageHandlerService;


    ZoneId zone = ZoneId.of("Asia/Ho_Chi_Minh");

    public void evaluate(String classroomId) throws JsonProcessingException {
        Classroom classroom = classroomRepository.findById(classroomId)
                .orElse(null);

        if (classroom == null) return;

        Instant now = Instant.now();

        if (!deviceReady(classroom)) return;

        // 1. Safety layer
        if (handleSafety(classroom)) return;

        // 2. Schedule layer
        if (handleSchedule(classroom, now)) return;

        // 3. Empty room layer
        handleEmptyRoom(classroom, now);
    }

    @Scheduled(fixedRate = 60000)
    public void evaluateAll() throws JsonProcessingException {
        List<Classroom> classrooms = classroomRepository.findAll();
        for (Classroom classroom : classrooms) {
            evaluate(classroom.getId());
        }
    }

    private boolean deviceReady(Classroom classroom) {
        Device device = classroom.getDevice();
        return device != null
                && PowerStatus.ON.equals(device.getPower())
                && ConnectivityStatus.ONLINE.equals(device.getConnectivity());
    }

    private boolean handleSafety(Classroom classroom) {
        return false;
    }

    private boolean handleSchedule(Classroom classroom, Instant now) throws JsonProcessingException {

        LocalDate today = LocalDate.now(zone);
        int todayValue = today.getDayOfWeek().getValue(); // 1-7

        Optional<Schedule> scheduleToday =
                classroom.getSchedules()
                        .stream()
                        .filter(s -> s.getDayOfWeek() == todayValue)
                        .min(Comparator.comparing(Schedule::getStartTime));

        if (scheduleToday.isEmpty())
            return false;

        Schedule schedule = scheduleToday.get();

        LocalTime nowTime = LocalTime.now(zone);
        LocalTime start = LocalTime.parse(schedule.getStartTime());
        LocalTime end = LocalTime.parse(schedule.getEndTime());

        LocalTime beforeStart = start.minusMinutes(10);
        LocalTime afterEnd = end.plusMinutes(10);

        if (!nowTime.isBefore(beforeStart) && nowTime.isBefore(start)) {

            double avgTemp = getAverageTempLast30Min(classroom.getId());

            CurrentState state = buildPreClassState( classroom ,avgTemp);

            sendIfChanged(classroom, state, Reason.SCHEDULE_START);
            return true;
        }

        if (!nowTime.isBefore(start) && nowTime.isBefore(end)) {
            return true;
        }

        if (!nowTime.isBefore(end) && nowTime.isBefore(afterEnd)) {

            boolean hasNextSchedule =
                    hasNextScheduleWithin10Min(classroom, nowTime);

            if (!hasNextSchedule) {
                sendIfChanged(classroom,
                        buildAllOffState(classroom),
                        Reason.SCHEDULE_START);
            }
            return true;
        }

        return false;
    }

    private void handleEmptyRoom(Classroom classroom, Instant now) throws JsonProcessingException {

        SensorReading latest =
                sensorReadingRepository.findFirstByClassroomIdOrderByTimestampDesc(
                        classroom.getId());

        if (latest == null) return;

        if (latest.getEnvironment().getOccupancy() > 0)
            return;

        Instant lastOccupied =
                sensorReadingRepository.findLastOccupiedTime(classroom.getId());

        if (lastOccupied == null) return;

        Duration emptyDuration =
                Duration.between(lastOccupied, now);

        if (emptyDuration.toMinutes() >= 10) {

            sendIfChanged(classroom,
                    buildAllOffState(classroom),
                    Reason.ROOM_EMPTY);
        }
    }


    private double getAverageTempLast30Min(String classroomId) {
        Instant fromTime = Instant.now().minus(Duration.ofMinutes(30));
        Double avg = sensorReadingRepository.getAverageTemp(classroomId, fromTime);
        return avg != null ? avg : 0;
    }

    private CurrentState buildPreClassState(Classroom classroom,
                                            double avgTemp) {

        CurrentState current = classroom.getCurrentState();

        CurrentState state = new CurrentState();

        int lightCount = current.getLightStates().size();
        int fanCount = current.getFanSpeed().size();

        List<Integer> lights = new ArrayList<>();
        for (int i = 0; i < lightCount; i++) {
            lights.add(1);
        }
        state.setLightStates(lights);

        List<Integer> fans = new ArrayList<>();
        for (int i = 0; i < fanCount; i++) {
            fans.add(1);
        }
        state.setFanSpeed(fans);

        state.setPower(PowerStatus.ON);

        if (avgTemp < 28) {
            state.setAcMode(AcMode.OFF);
            state.setAcTemp((double) 0);
        }
        else if (avgTemp < 32) {
            state.setAcMode(AcMode.ECO);
            state.setAcTemp( 26.0);
        }
        else {
            state.setAcMode(AcMode.COOL);
            state.setAcTemp(26.0);
        }

        state.setLastUpdated(Instant.now());

        return state;
    }

    private CurrentState buildAllOffState(Classroom classroom) {

        CurrentState current = classroom.getCurrentState();
        CurrentState state = new CurrentState();

        int lightCount = current.getLightStates().size();
        int fanCount = current.getFanSpeed().size();

        state.setLightStates(Collections.nCopies(lightCount, 0));
        state.setFanSpeed(Collections.nCopies(fanCount, 0));

        state.setAcMode(AcMode.OFF);
        state.setAcTemp(0.0);
        state.setPower(PowerStatus.OFF);
        state.setLastUpdated(Instant.now());

        return state;
    }

    private boolean hasNextScheduleWithin10Min(Classroom classroom,
                                               LocalTime nowTime) {

        return classroom.getSchedules()
                .stream()
                .anyMatch(s -> {
                    LocalTime start = LocalTime.parse(s.getStartTime());
                    return start.isAfter(nowTime)
                            && start.isBefore(nowTime.plusMinutes(10));
                });
    }

    private void sendIfChanged(Classroom classroom,
                               CurrentState newState,
                               Reason reason) throws JsonProcessingException {

        if (!newState.equals(classroom.getCurrentState())) {
            ControlLog controlLog = new ControlLog();
            controlLog.setCommand(newState);
            controlLog.setReason(Reason.SCHEDULE_START);
            controlLog.setTimestamp(Instant.now());
            controlLog.setStatus(CommandStatus.SENT);
            controlLog.setMode(ModeControl.SCHEDULE);
            controlLog.setClassroomId(classroom.getId());


            mqttMessageHandlerService.sendControlCommand(
                    classroom.getDevice().getDeviceId(),
                    classroom.getId(),
                    classroom.getBuilding(),
                    controlLog
            );

            classroom.setCurrentState(newState);
            classroomRepository.save(classroom);
        }
    }
}
