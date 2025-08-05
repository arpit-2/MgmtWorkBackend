package com.railse.hiring.workforcemgmt.controller;

import com.railse.hiring.workforcemgmt.common.model.response.Response;
import com.railse.hiring.workforcemgmt.dto.*;
import com.railse.hiring.workforcemgmt.model.enums.Priority;
import com.railse.hiring.workforcemgmt.service.TaskManagementService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/task-mgmt")
public class TaskManagementController {

    private final TaskManagementService taskManagementService;

    public TaskManagementController(TaskManagementService taskManagementService) {
        this.taskManagementService = taskManagementService;
    }

    @GetMapping("/{id}")
    public Response<TaskManagementDto> getTaskById(@PathVariable Long id) {
        return new Response<>(taskManagementService.findTaskById(id));
    }

    @PostMapping("/create")
    public Response<List<TaskManagementDto>> createTasks(@RequestBody TaskCreateRequest request) {
        return new Response<>(taskManagementService.createTasks(request));
    }

    @PostMapping("/update")
    public Response<List<TaskManagementDto>> updateTasks(@RequestBody UpdateTaskRequest request) {
        return new Response<>(taskManagementService.updateTasks(request));
    }

    @PostMapping("/assign-by-ref")
    public Response<String> assignByReference(@RequestBody AssignByReferenceRequest request) {
        return new Response<>(taskManagementService.assignByReference(request));
    }

    @PostMapping("/fetch-by-date/v2")
    public Response<List<TaskManagementDto>> fetchByDate(@RequestBody TaskFetchByDateRequest request) {
        return new Response<>(taskManagementService.fetchTasksByDate(request));
    }

    @PatchMapping("/priority")
    public Response<TaskManagementDto> updatePriority(@RequestBody PriorityUpdateRequest request) {
        return new Response<>(taskManagementService.updateTaskPriority(request));
    }

    @GetMapping("/priority/{priority}")
    public Response<List<TaskManagementDto>> fetchByPriority(@PathVariable String priority) {
        return new Response<>(taskManagementService.fetchTasksByPriority(priority));
    }


    @PostMapping("/{taskId}/comment")
    public Response<String> addComment(@PathVariable Long taskId, @RequestBody CommentRequest request) {
        request.setTaskId(taskId);
        taskManagementService.addComment(request);
        return new Response<>("Comment added successfully");
    }



    @GetMapping("/{taskId}/comments")
    public Response<List<CommentDto>> getComments(@PathVariable Long taskId) {
        return new Response<>(taskManagementService.getComments(taskId));
    }

    @GetMapping("/{taskId}/activities")
    public Response<List<ActivityLogDto>> getActivityLog(@PathVariable Long taskId) {
        return new Response<>(taskManagementService.getActivityLog(taskId));
    }
}
