package com.example.Iot_Project.mapper;

import com.example.Iot_Project.enity.Device;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

import java.util.List;

@Mapper(componentModel = "spring")
public interface RoomMapper {
    Room toRoom(RoomRequest request);

    RoomResponse toRoomResponse(Room room);

    List<RoomResponse> toRoomResponses(List<Room> rooms);

    void updateRoom(@MappingTarget Room room, RoomRequest request);
}
