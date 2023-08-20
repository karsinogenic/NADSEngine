package com.nads.nadsengine.Repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.nads.nadsengine.Models.NewRule;

public interface RulesRepository extends JpaRepository<NewRule, Long> {

    @Query("Select nr from NewRule nr where (:kode IS NULL OR nr.KodeRule = :kode)")
    List<NewRule> findByKode(@Param("kode") String kode);

}
