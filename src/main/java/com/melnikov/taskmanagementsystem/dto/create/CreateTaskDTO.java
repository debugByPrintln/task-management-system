package com.melnikov.taskmanagementsystem.dto.create;

import com.melnikov.taskmanagementsystem.model.utils.Priority;
import com.melnikov.taskmanagementsystem.model.utils.Status;
import lombok.Data;


@Data
public class CreateTaskDTO {
    private String title;
    private String description;
    private Status status;
    private Priority priority;
    private Long authorId;
    private Long assigneeId;
}
