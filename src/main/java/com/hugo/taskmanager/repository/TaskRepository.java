package com.hugo.taskmanager.repository;

import com.hugo.taskmanager.entity.Task;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface TaskRepository extends JpaRepository<Task, Long> {

    Page<Task> findByUserUsername(String username, Pageable pageable);

    List<Task> findByUserUsername(String username);

    Optional<Task> findByIdAndUserUsername(Long id, String username);

    Page<Task> findByUserUsernameAndCompleted(String username, boolean completed, Pageable pageable);

    List<Task> findByUserUsernameAndCompleted(String username, boolean completed);

    Page<Task> findByUserUsernameAndTitleContainingIgnoreCase(String username, String title, Pageable pageable);

    List<Task> findByUserUsernameAndTitleContainingIgnoreCase(String username, String title);

    @Query("""
            SELECT t FROM Task t
            WHERE t.user.username = :username
              AND LOWER(t.title) LIKE LOWER(CONCAT('%', :title, '%'))
              AND t.completed = :completed
            """)
    Page<Task> findByUserUsernameAndTitleContainingIgnoreCaseAndCompleted(
            @Param("username") String username,
            @Param("title") String title,
            @Param("completed") boolean completed,
            Pageable pageable);

}
