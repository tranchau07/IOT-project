package com.example.Iot_Project.repository;

import com.example.Iot_Project.enity.Command;
import com.example.Iot_Project.enity.Device;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.time.Instant;
import java.util.List;

public interface CommandRepository extends MongoRepository<Command, String> {
}
