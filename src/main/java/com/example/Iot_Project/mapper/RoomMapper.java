package com.example.Iot_Project.mapper;

import com.example.Iot_Project.dto.request.DeviceRequest;
import com.example.Iot_Project.dto.request.RoomRequest;
import com.example.Iot_Project.dto.response.DeviceResponse;
import com.example.Iot_Project.dto.response.RoomResponse;
import com.example.Iot_Project.enity.Device;
import com.example.Iot_Project.enity.Room;
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
