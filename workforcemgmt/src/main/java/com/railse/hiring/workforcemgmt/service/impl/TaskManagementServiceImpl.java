package com.railse.hiring.workforcemgmt.service.impl;

import com.railse.hiring.workforcemgmt.common.exception.ResourceNotFoundException;
import com.railse.hiring.workforcemgmt.dto.*;
import com.railse.hiring.workforcemgmt.mapper.ITaskManagementMapper;
import com.railse.hiring.workforcemgmt.model.Comment;
import com.railse.hiring.workforcemgmt.model.TaskManagement;
import com.railse.hiring.workforcemgmt.model.enums.Priority;
import com.railse.hiring.workforcemgmt.model.enums.Task;
import com.railse.hiring.workforcemgmt.model.enums.TaskStatus;
import com.railse.hiring.workforcemgmt.repository.TaskRepository;
import com.railse.hiring.workforcemgmt.service.TaskManagementService;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class TaskManagementServiceImpl implements TaskManagementService {

    private final TaskRepository taskRepository;
    private final ITaskManagementMapper taskMapper;

    public TaskManagementServiceImpl(TaskRepository taskRepository, ITaskManagementMapper taskMapper) {
        this.taskRepository = taskRepository;
        this.taskMapper = taskMapper;
    }

    @Override
    public TaskManagementDto findTaskById(Long id) {
        TaskManagement task = taskRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found with id: " + id));
        return taskMapper.modelToDto(task);
    }

    @Override
    public List<TaskManagementDto> createTasks(TaskCreateRequest createRequest) {
        List<TaskManagement> createdTasks = new ArrayList<>();
        for (TaskCreateRequest.RequestItem item : createRequest.getRequests()) {
            TaskManagement newTask = new TaskManagement();
            newTask.setReferenceId(item.getReferenceId());
            newTask.setReferenceType(item.getReferenceType());
            newTask.setTask(item.getTask());
            newTask.setAssigneeId(item.getAssigneeId());
            newTask.setPriority(item.getPriority());
            newTask.setTaskDeadlineTime(item.getTaskDeadlineTime());
            newTask.setStatus(TaskStatus.ASSIGNED);
            newTask.setDescription("New task created.");
            newTask.setComments(new ArrayList<>());
            newTask.setActivityLogs(new ArrayList<>(List.of(
                    "Task created and assigned to ID: " + item.getAssigneeId()
            )));
            createdTasks.add(taskRepository.save(newTask));
        }
        return taskMapper.modelListToDtoList(createdTasks);
    }

    @Override
    public List<TaskManagementDto> updateTasks(UpdateTaskRequest updateRequest) {
        List<TaskManagement> updatedTasks = new ArrayList<>();
        for (UpdateTaskRequest.RequestItem item : updateRequest.getRequests()) {
            TaskManagement task = taskRepository.findById(item.getTaskId())
                    .orElseThrow(() -> new ResourceNotFoundException("Task not found with id: " + item.getTaskId()));

            if (item.getTaskStatus() != null) {
                task.setStatus(item.getTaskStatus());
                task.getActivityLogs().add("Status updated to " + item.getTaskStatus());
            }
            if (item.getDescription() != null) {
                task.setDescription(item.getDescription());
                task.getActivityLogs().add("Description updated.");
            }
            updatedTasks.add(taskRepository.save(task));
        }
        return taskMapper.modelListToDtoList(updatedTasks);
    }

    @Override
    public String assignByReference(AssignByReferenceRequest request) {
        List<Task> applicableTasks = Task.getTasksByReferenceType(request.getReferenceType());
        List<TaskManagement> existingTasks = taskRepository.findByReferenceIdAndReferenceType(request.getReferenceId(), request.getReferenceType());

        for (Task taskType : applicableTasks) {
            List<TaskManagement> tasksOfType = existingTasks.stream()
                    .filter(t -> t.getTask() == taskType && t.getStatus() != TaskStatus.COMPLETED)
                    .collect(Collectors.toList());

            if (!tasksOfType.isEmpty()) {
                for (TaskManagement taskToUpdate : tasksOfType) {
                    if (!taskToUpdate.getAssigneeId().equals(request.getAssigneeId())) {
                        taskToUpdate.setStatus(TaskStatus.CANCELLED);
                        taskToUpdate.getActivityLogs().add("Task cancelled due to reassignment.");
                    } else {
                        taskToUpdate.getActivityLogs().add("Task reassigned.");
                    }
                    taskRepository.save(taskToUpdate);
                }
            } else {
                TaskManagement newTask = new TaskManagement();
                newTask.setReferenceId(request.getReferenceId());
                newTask.setReferenceType(request.getReferenceType());
                newTask.setTask(taskType);
                newTask.setAssigneeId(request.getAssigneeId());
                newTask.setStatus(TaskStatus.ASSIGNED);
                newTask.setComments(new ArrayList<>());
                newTask.setActivityLogs(new ArrayList<>(List.of(
                        "Task created via assignByReference for " + taskType
                )));
                taskRepository.save(newTask);
            }
        }
        return "Tasks assigned successfully for reference " + request.getReferenceId();
    }

    @Override
    public List<TaskManagementDto> fetchTasksByDate(TaskFetchByDateRequest request) {
        List<TaskManagement> tasks = taskRepository.findByAssigneeIdIn(request.getAssigneeIds());
        long start = request.getStartDate();
        long end = request.getEndDate();

        return taskMapper.modelListToDtoList(
                tasks.stream()
                        .filter(task -> task.getStatus() != TaskStatus.CANCELLED)
                        .filter(task ->
                                (task.getTaskDeadlineTime() >= start && task.getTaskDeadlineTime() <= end)
                                        || (task.getTaskDeadlineTime() < start && task.getStatus() != TaskStatus.COMPLETED)
                        )
                        .collect(Collectors.toList())
        );
    }

    @Override
    public TaskManagementDto updateTaskPriority(PriorityUpdateRequest request) {
        TaskManagement task = taskRepository.findById(request.getTaskId())
                .orElseThrow(() -> new ResourceNotFoundException("Task not found with id: " + request.getTaskId()));

        task.setPriority(request.getPriority());
        task.getActivityLogs().add("Priority changed to " + request.getPriority());
        taskRepository.save(task);

        return taskMapper.modelToDto(task);
    }

    @Override
    public List<TaskManagementDto> fetchTasksByPriority(String priority) {
        Priority prio = Priority.valueOf(priority.toUpperCase());
        return taskMapper.modelListToDtoList(
                taskRepository.findAll().stream()
                        .filter(t -> t.getPriority() == prio)
                        .collect(Collectors.toList())
        );
    }

    @Override
    public void addComment(CommentRequest request) {
        TaskManagement task = taskRepository.findById(request.getTaskId())
                .orElseThrow(() -> new ResourceNotFoundException("Task not found with id: " + request.getTaskId()));

        if (task.getComments() == null) {
            task.setComments(new ArrayList<>());
        }
        task.getComments().add(new Comment(request.getUserId(), request.getComment()));
        task.getActivityLogs().add("User " + request.getUserId() + " added a comment.");
        taskRepository.save(task);
    }

    @Override
    public List<CommentDto> getComments(Long taskId) {
        TaskManagement task = taskRepository.findById(taskId)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found with id: " + taskId));

        List<Comment> comments = task.getComments();
        if (comments == null) return new ArrayList<>();

        return comments.stream()
                .map(comment -> {
                    CommentDto dto = new CommentDto();
                    dto.setUserId(comment.getUserId());
                    dto.setComment(comment.getComment());
                    return dto;
                })
                .collect(Collectors.toList());
    }

    @Override
    public List<ActivityLogDto> getActivityLog(Long taskId) {
        TaskManagement task = taskRepository.findById(taskId)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found with id: " + taskId));

        List<String> logs = task.getActivityLogs();
        if (logs == null) return new ArrayList<>();

        return logs.stream()
                .map(message -> {
                    ActivityLogDto dto = new ActivityLogDto();
                    dto.setMessage(message);
                    dto.setTimestamp(System.currentTimeMillis());
                    return dto;
                })
                .collect(Collectors.toList());
    }
}
