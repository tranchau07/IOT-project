package com.example.Iot_Project.configuration;

import com.example.Iot_Project.entity.Role;
import com.example.Iot_Project.entity.User;
import com.example.Iot_Project.document.Classroom;
import com.example.Iot_Project.model.Device;
import com.example.Iot_Project.model.CurrentState;
import com.example.Iot_Project.enums.AcMode;
import com.example.Iot_Project.enums.ConnectivityStatus;
import com.example.Iot_Project.enums.PowerStatus;
import com.example.Iot_Project.enums.DeviceType;
import com.example.Iot_Project.enums.ErrorCode;
import com.example.Iot_Project.exception.AppException;
import com.example.Iot_Project.repository.jpa.RoleRepository;
import com.example.Iot_Project.repository.jpa.UserRepository;
import com.example.Iot_Project.repository.mongo.ClassroomRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.Instant;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

@Slf4j
@Configuration
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ApplicationInitConfig {
    PasswordEncoder passwordEncoder;
    UserRepository userRepository;
    RoleRepository roleRepository;
    ClassroomRepository classroomRepository;

    @Bean
    ApplicationRunner applicationRunner(){
        return args -> {
          // 1. Seed Default Admin Account
          if(!userRepository.existsByUsername("admin")){
              Role role = roleRepository.findById("ADMIN").orElseThrow(() -> new AppException(ErrorCode.ROLE_NOT_EXISTED));
              Set<Role> roles = new HashSet<>();
              roles.add(role);

              User user = User.builder()
                      .username("admin")
                      .password(passwordEncoder.encode("admin"))
                      .roles(roles)
                      .build();

              userRepository.save(user);

              log.warn("Account 'admin' has been created successfully with default password 'admin', please change password");
          }

          // 2. Seed Default Classroom for Wokwi ESP32 Simulator Integration
          String simulatorClassroomId = "699b0889512fe68fc2da3d82";
          if (!classroomRepository.existsById(simulatorClassroomId)) {
              Classroom classroom = Classroom.builder()
                      .id(simulatorClassroomId)
                      .name("Room 302")
                      .building("A")
                      .capacity(50)
                      .createdAt(Instant.now())
                      .device(Device.builder()
                              .deviceId("ESP32_01")
                              .type(DeviceType.SMART_ROOM_GATEWAY)
                              .connectivity(ConnectivityStatus.OFFLINE)
                              .power(PowerStatus.OFF)
                              .lastSeen(Instant.now())
                              .build())
                      .currentState(CurrentState.builder()
                              .power(PowerStatus.OFF)
                              .acMode(AcMode.OFF)
                              .acTemp(24.0)
                              .lightStates(Arrays.asList(0, 0))
                              .fanSpeed(Arrays.asList(0, 0))
                              .lastUpdated(Instant.now())
                              .build())
                      .build();

              classroomRepository.save(classroom);
              log.warn("WOKWI-INTEGRATION: Seeded default Classroom '{}' matching ESP32 simulator configurations successfully.", simulatorClassroomId);
          }
        };
    }
}
