package com.example.demo.persistence.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "subjects", schema = "public")
public class Subject implements PersistableEntity {
    @Id
    @Column(name = "subject_id", nullable = false)
    private Integer id;

    @Size(max = 100)
    @NotNull
    @Column(name = "subject_name", nullable = false, length = 100)
    private String subjectName;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "teacher_id", nullable = false)
    private User teacher;

    @NotNull
    @Column(name = "active", nullable = false)
    private Boolean active = false;

}