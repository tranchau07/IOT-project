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
    import java.util.stream.Collectors;

    import static java.util.stream.Collectors.*;

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

        private boolean handleSafety(Classroom classroom) throws JsonProcessingException {

            SensorReading latest =
                    sensorReadingRepository.findFirstByClassroomIdOrderByTimestampDesc(
                            classroom.getId());

            if (latest == null) return false;

            double voltage = latest.getVoltage();

            if (voltage > 240) {

                sendIfChanged(classroom,
                        buildAllOffState(classroom),
                        Reason.SAFETY_VOLTAGE,
                        ModeControl.EMERGENCY);

                return true; // STOP all other logic
            }

            return false;
        }
        private boolean handleSchedule(Classroom classroom, Instant now) throws JsonProcessingException {

            LocalDate today = LocalDate.now(zone);
            int todayValue = today.getDayOfWeek().getValue();
            LocalTime nowTime = LocalTime.now(zone);

            List<Schedule> schedulesToday = classroom.getSchedules()
                    .stream()
                    .filter(s -> s.getDayOfWeek() == todayValue)
                    .sorted(Comparator.comparing(Schedule::getStartTime))
                    .collect(toList());

            if (schedulesToday.isEmpty()) {
                return false;
            }

            for (int i = 0; i < schedulesToday.size(); i++) {

                Schedule schedule = schedulesToday.get(i);

                LocalTime start = LocalTime.parse(schedule.getStartTime());
                LocalTime end = LocalTime.parse(schedule.getEndTime());

                LocalTime beforeStart = start.minusMinutes(10);
                LocalTime afterEnd = end.plusMinutes(10);

                if (!nowTime.isBefore(beforeStart) && nowTime.isBefore(start)) {

                    double avgTemp = getAverageTempLast30Min(classroom.getId());
                    CurrentState state = buildPreClassState(classroom, avgTemp);

                    sendIfChanged(classroom, state,
                            Reason.SCHEDULE_START,
                            ModeControl.SCHEDULE);

                    return true;
                }

                if (!nowTime.isBefore(start) && nowTime.isBefore(end)) {
                    return true;
                }

                if (!nowTime.isBefore(end) && nowTime.isBefore(afterEnd)) {

                    boolean hasNextSchedule = false;

                    // Kiểm tra lớp tiếp theo trong vòng 10 phút
                    if (i + 1 < schedulesToday.size()) {
                        Schedule next = schedulesToday.get(i + 1);
                        LocalTime nextStart = LocalTime.parse(next.getStartTime());

                        if (!nextStart.isAfter(nowTime.plusMinutes(10))) {
                            hasNextSchedule = true;
                        }
                    }

                    if (!hasNextSchedule) {
                        sendIfChanged(classroom,
                                buildAllOffState(classroom),
                                Reason.SCHEDULE_END,
                                ModeControl.SCHEDULE);
                    }

                    return true;
                }
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

            var lastOccupiedReadingOptional =
                    sensorReadingRepository.findLastOccupiedReading(classroom.getId());

            if (lastOccupiedReadingOptional.isEmpty()) return;

            Instant lastOccupied = lastOccupiedReadingOptional.get().getTimestamp();
            Duration emptyDuration =
                    Duration.between(lastOccupied, now);

            if (emptyDuration.toMinutes() >= 10) {

                sendIfChanged(classroom,
                        buildAllOffState(classroom),
                        Reason.ROOM_EMPTY,
                        ModeControl.AUTOMATIC);
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

            if (current == null) {
                return state;
            }

            int lightCount = current.getLightStates() != null ? current.getLightStates().size() : 0;
            int fanCount = current.getFanSpeed() != null ? current.getFanSpeed().size() : 0;

            // Bật toàn bộ đèn
            List<Integer> lights = new ArrayList<>(Collections.nCopies(lightCount, 1));
            state.setLightStates(lights);

            // Quạt chạy mức 1
            List<Integer> fans = new ArrayList<>(Collections.nCopies(fanCount, 1));
            state.setFanSpeed(fans);

            state.setPower(PowerStatus.ON);

            // Điều hòa theo nhiệt độ
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
                                   Reason reason, ModeControl modeControl) throws JsonProcessingException {

            if (!newState.equals(classroom.getCurrentState())) {
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

                classroom.setCurrentState(newState);
                classroomRepository.save(classroom);
            }
        }
    }
