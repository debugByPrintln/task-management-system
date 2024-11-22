package com.melnikov.taskmanagementsystem.dto.create;

import lombok.Data;

@Data
public class CreateCommentDTO {
    private String text;
    private Long taskId;
    private Long authorId;
}
