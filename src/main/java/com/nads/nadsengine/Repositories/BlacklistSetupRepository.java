package com.nads.nadsengine.Repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.nads.nadsengine.Models.BlacklistSetup;

public interface BlacklistSetupRepository extends JpaRepository<BlacklistSetup, Long> {

    @Query("SELECT DISTINCT b.table_name FROM BlacklistSetup b where b.is_active=true")
    List<String> findDistinct();

    @Query("SELECT DISTINCT b.input_field_name FROM BlacklistSetup b")
    List<String> findDistinctInput();

    @Query("SELECT b FROM BlacklistSetup b WHERE b.table_name = ?1")
    List<BlacklistSetup> findByTable_name(String string);

}
