package com.example.post.model;

import lombok.*;

import com.example.common.model.Auditable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = false)
public class Comment extends Auditable {
    private String id;
    private String userId;
    private String text;
}
