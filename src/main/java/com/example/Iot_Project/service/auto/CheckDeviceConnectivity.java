package com.example.Iot_Project.service.auto;

import com.example.Iot_Project.enity.Classroom;
import com.example.Iot_Project.enums.ConnectivityStatus;
import com.example.Iot_Project.repository.ClassroomRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.MessageChannel;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class CheckDeviceConnectivity {
    ClassroomRepository classroomRepository;


    @Scheduled(fixedRate = 10000)
    public void publishDeviceStatusPeriodically() {
        Instant threshold = Instant.now().minusSeconds(30);
        List<Classroom> classroom = classroomRepository.findAll();
        classroom.forEach(c -> {
            if(c.getDevice().getLastSeen().isBefore(threshold)){
                c.getDevice().setConnectivity(ConnectivityStatus.OFFLINE);
                classroomRepository.save(c);
            }
        });

    }
}
