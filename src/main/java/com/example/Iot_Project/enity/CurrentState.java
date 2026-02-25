package com.example.Iot_Project.enity;

import com.example.Iot_Project.enums.AcMode;
import com.example.Iot_Project.enums.PowerStatus;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.Instant;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CurrentState {
    AcMode acMode;
    Double acTemp;
    List<Integer> lightStates;
    List<Integer> fanSpeed;
    PowerStatus power;
    Instant lastUpdated;
}
