package com.nads.nadsengine.Models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "setup_rule")
public class SetupRule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long Id;

    @Column(unique = true)
    private String kodeRule;

    private String databaseName;

    private Integer nDays;

    private Integer excessiveThreshold;

    private String deskripsi;

    @Column(columnDefinition = "BOOLEAN DEFAULT true")
    private Boolean is_active;
}
