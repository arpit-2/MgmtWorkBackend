package com.railse.hiring.workforcemgmt.mapper;

import com.railse.hiring.workforcemgmt.dto.*;
import com.railse.hiring.workforcemgmt.model.TaskManagement;
import org.mapstruct.*;

import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface ITaskManagementMapper {

    TaskManagementDto modelToDto(TaskManagement model);

    List<TaskManagementDto> modelListToDtoList(List<TaskManagement> models);

    default List<ActivityLogDto> mapActivityLogsToDto(List<String> logs) {
        if (logs == null) return null;
        return logs.stream().map(msg -> {
            ActivityLogDto dto = new ActivityLogDto();
            dto.setMessage(msg);
            dto.setTimestamp(System.currentTimeMillis()); // or set a proper timestamp if available
            return dto;
        }).collect(Collectors.toList());
    }

    default List<String> mapActivityLogsToString(List<ActivityLogDto> dtos) {
        if (dtos == null) return null;
        return dtos.stream().map(ActivityLogDto::getMessage).collect(Collectors.toList());
    }
}
