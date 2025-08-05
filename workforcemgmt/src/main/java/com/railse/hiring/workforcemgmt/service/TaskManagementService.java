package com.railse.hiring.workforcemgmt.service;

import com.railse.hiring.workforcemgmt.dto.*;

import java.util.List;

public interface TaskManagementService {
    List<TaskManagementDto> createTasks(TaskCreateRequest request);
    List<TaskManagementDto> updateTasks(UpdateTaskRequest request);
    String assignByReference(AssignByReferenceRequest request);
    List<TaskManagementDto> fetchTasksByDate(TaskFetchByDateRequest request);
    TaskManagementDto findTaskById(Long id);
    TaskManagementDto updateTaskPriority(PriorityUpdateRequest request);
    List<TaskManagementDto> fetchTasksByPriority(String priority);
    void addComment(CommentRequest request);
    List<CommentDto> getComments(Long taskId);
    List<ActivityLogDto> getActivityLog(Long taskId);

}
