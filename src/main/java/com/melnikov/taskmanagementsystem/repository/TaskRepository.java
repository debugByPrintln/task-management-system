package com.melnikov.taskmanagementsystem.repository;

import com.melnikov.taskmanagementsystem.model.Task;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {
    Page<Task> findByAuthorId(Long authorId, Pageable pageable);
    Page<Task> findByAssigneeId(Long assigneeId, Pageable pageable);
}
