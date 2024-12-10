package com.example.demo.persistence.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.Instant;

@Getter
@Setter
@Entity
@Table(name = "grades", schema = "public")
public class Grade implements PersistableEntity{
    @Id
    @Column(name = "grade_id", nullable = false)
    private Integer id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "student_subject_id", nullable = false)
    private StudentsSubject studentSubject;

    @NotNull
    @Column(name = "grade_value", nullable = false, precision = 5, scale = 2)
    private BigDecimal gradeValue;

    @NotNull
    @Column(name = "added_date", nullable = false)
    private Timestamp addedDate;

    @NotNull
    @Column(name = "active", nullable = false)
    private Boolean active = false;

}