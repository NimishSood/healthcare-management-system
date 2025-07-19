package com.example.healthcare.entity;
import com.example.healthcare.entity.enums.AttachmentParentType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "attachments")
@Getter
@Setter
public class Attachment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, name = "parent_type")
    private AttachmentParentType parentType;

    @Column(nullable = false, name = "parent_id")
    private Long parentId;

    @Column(nullable = false)
    private String fileName;

    @Column(nullable = false)
    private String storageKey;

    @Column(nullable = false)
    private long fileSize;

    @Column(nullable = false)
    private String contentType;

    @Column(nullable = false)
    private LocalDateTime uploadedAt;

    @PrePersist
    public void prePersist() {
        uploadedAt = LocalDateTime.now();
    }
}