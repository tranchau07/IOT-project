package com.example.Iot_Project.enity;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Energy {
    Double totalKw;
    Double energyKwh;
}
