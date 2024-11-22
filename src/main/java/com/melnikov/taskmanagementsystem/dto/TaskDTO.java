package com.melnikov.taskmanagementsystem.dto;

import com.melnikov.taskmanagementsystem.model.utils.Priority;
import com.melnikov.taskmanagementsystem.model.utils.Status;
import lombok.Data;

import java.util.List;

@Data
public class TaskDTO {
    private Long id;
    private String title;
    private String description;
    private Status status;
    private Priority priority;
    private Long authorId;
    private Long assigneeId;
    private List<CommentDTO> comments;
}
