package com.example.Iot_Project.repository;


import com.example.Iot_Project.enity.Room;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RoomRepository extends MongoRepository<Room, String> {
    List<Room> findByFloor(Integer floor);
}
