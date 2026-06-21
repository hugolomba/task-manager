package com.hugo.taskmanager.service;

import com.hugo.taskmanager.dto.TaskRequest;
import com.hugo.taskmanager.dto.TaskResponse;
import com.hugo.taskmanager.entity.Task;
import com.hugo.taskmanager.entity.User;
import com.hugo.taskmanager.exception.TaskNotFoundException;
import com.hugo.taskmanager.exception.UserNotFoundException;
import com.hugo.taskmanager.mapper.TaskMapper;
import com.hugo.taskmanager.repository.CategoryRepository;
import com.hugo.taskmanager.repository.TaskRepository;
import com.hugo.taskmanager.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TaskServiceTest {

    @Mock
    private TaskRepository taskRepository;

    @Mock
    private TaskMapper taskMapper;

    @Mock
    private UserRepository userRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @InjectMocks
    private TaskService taskService;

    @Test
    void createsTaskForAuthenticatedUser() {
        User user = User.builder()
                .id(1L)
                .username("hugo")
                .name("Hugo")
                .surname("Lomba")
                .build();
        TaskRequest request = new TaskRequest("Study Spring", "Write unit tests", false, null);
        Task mappedTask = Task.builder()
                .title(request.title())
                .description(request.description())
                .completed(request.completed())
                .user(user)
                .build();
        Task savedTask = Task.builder()
                .id(10L)
                .title(request.title())
                .description(request.description())
                .completed(request.completed())
                .user(user)
                .build();
        TaskResponse response = new TaskResponse(10L, "Study Spring", "Write unit tests", false, null, null, null);

        when(userRepository.findByUsername("hugo")).thenReturn(Optional.of(user));
        when(taskMapper.toEntity(request, user, null)).thenReturn(mappedTask);
        when(taskRepository.save(mappedTask)).thenReturn(savedTask);
        when(taskMapper.toResponse(savedTask)).thenReturn(response);

        TaskResponse result = taskService.createTask("hugo", request);

        assertSame(response, result);
        ArgumentCaptor<Task> taskCaptor = ArgumentCaptor.forClass(Task.class);
        verify(taskRepository).save(taskCaptor.capture());
        assertEquals("hugo", taskCaptor.getValue().getUser().getUsername());
    }

    @Test
    void rejectsTaskCreationWhenAuthenticatedUserIsMissing() {
        TaskRequest request = new TaskRequest("Study Spring", "Write unit tests", false, null);
        when(userRepository.findByUsername("missing")).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> taskService.createTask("missing", request));
    }

    @Test
    void onlyFindsTasksOwnedByAuthenticatedUser() {
        Task task = Task.builder()
                .id(10L)
                .title("Study Spring")
                .description("Write unit tests")
                .completed(false)
                .build();
        TaskResponse response = new TaskResponse(10L, "Study Spring", "Write unit tests", false, null, null, null);

        when(taskRepository.findByIdAndUserUsername(10L, "hugo")).thenReturn(Optional.of(task));
        when(taskMapper.toResponse(task)).thenReturn(response);

        TaskResponse result = taskService.getTaskById("hugo", 10L);

        assertSame(response, result);
        verify(taskRepository).findByIdAndUserUsername(10L, "hugo");
    }

    @Test
    void throwsNotFoundWhenTaskDoesNotBelongToUser() {
        when(taskRepository.findByIdAndUserUsername(10L, "hugo")).thenReturn(Optional.empty());

        assertThrows(TaskNotFoundException.class, () -> taskService.getTaskById("hugo", 10L));
    }

    @Test
    void deletesOnlyTasksOwnedByAuthenticatedUser() {
        Task task = Task.builder().id(10L).title("Study Spring").completed(false).build();
        when(taskRepository.findByIdAndUserUsername(10L, "hugo")).thenReturn(Optional.of(task));

        taskService.deleteTask("hugo", 10L);

        verify(taskRepository).delete(task);
    }
}
