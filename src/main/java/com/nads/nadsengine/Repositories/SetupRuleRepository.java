package com.nads.nadsengine.Repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.nads.nadsengine.Models.SetupRule;

public interface SetupRuleRepository extends JpaRepository<SetupRule, Long> {

    @Query("select s from SetupRule s where s.kodeRule = :kode and s.is_active=true")
    Optional<SetupRule> findByKode(@Param("kode") String kode);

    @Query("select s.excessiveThreshold from SetupRule s where s.kodeRule = :kode")
    Integer findThresholdByKode(@Param("kode") String kode);

    @Query("select s from SetupRule s where s.is_active= ?1")
    List<SetupRule> findAllActive(Boolean is_active);
}
