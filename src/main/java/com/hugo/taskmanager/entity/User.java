package com.hugo.taskmanager.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "users")

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String username;

    private String name;

    private String surname;

    private LocalDateTime createdAt;

    @OneToMany(mappedBy = "user")
    private List<Task> tasks;

    @PrePersist
    protected void OnCreate() {
        this.createdAt = LocalDateTime.now();
    }
}
