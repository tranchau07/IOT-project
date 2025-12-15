package com.example.Iot_Project.service;

import com.example.Iot_Project.dto.request.RoomRequest;
import com.example.Iot_Project.dto.response.RoomResponse;
import com.example.Iot_Project.enity.Room;
import com.example.Iot_Project.enums.ErrorCode;
import com.example.Iot_Project.exception.AppException;
import com.example.Iot_Project.mapper.RoomMapper;
import com.example.Iot_Project.repository.RoomRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class RoomService {
    RoomRepository roomRepository;
    RoomMapper roomMapper;

    public RoomResponse create(RoomRequest request){

        Room room = roomMapper.toRoom(request);

        return roomMapper.toRoomResponse(roomRepository.save(room));
    }
    public List<RoomResponse> getAll(){
        List<Room> rooms = roomRepository.findAll();
        return roomMapper.toRoomResponses(rooms);
    }
    public RoomResponse getById(String id){
        Room room = roomRepository.findById(id).orElseThrow(()
                -> new AppException(ErrorCode.UNCATEGORIZED_EXCEPTION));

        return roomMapper.toRoomResponse(room);
    }

    public RoomResponse update(RoomRequest request,String id){
        Room room = roomRepository.findById(id).orElseThrow(() ->
                new AppException(ErrorCode.UNCATEGORIZED_EXCEPTION));
        roomMapper.updateRoom(room, request);

        return roomMapper.toRoomResponse(roomRepository.save(room));
    }

    public void delete(String id){
        roomRepository.deleteById(id);
    }


}
