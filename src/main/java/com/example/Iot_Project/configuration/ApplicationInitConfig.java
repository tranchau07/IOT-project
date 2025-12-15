package com.example.Iot_Project.configuration;

import com.example.Iot_Project.enity.User;
import com.example.Iot_Project.enums.Role;
import com.example.Iot_Project.repository.UserRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.HashSet;

@Slf4j
@Configuration
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ApplicationInitConfig {
    PasswordEncoder passwordEncoder;
    UserRepository userRepository;
    @Bean
    ApplicationRunner applicationRunner(){
        return args -> {
          if(!userRepository.existsByUsername("admin")){
            var roles = new HashSet<String>();
            roles.add(Role.ADMIN.name());

              User user = User.builder()
                      .username("admin")
                      .password(passwordEncoder.encode("admin"))
                      .roles(roles)
                      .build();

              userRepository.save(user);

              log.warn("Account 'admin' has been created successfully with default password 'admin', please change password");
          }

        };
    }
}
