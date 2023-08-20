package com.nads.nadsengine.Repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.nads.nadsengine.Models.MasterDbInput;

public interface DBInputRepository extends JpaRepository<MasterDbInput, Long> {

    @Query("select m from MasterDbInput m where m.namaDb=:namadb")
    List<MasterDbInput> findAllParam(@Param("namadb") String namaDB);

    @Query("select m.paramInput from MasterDbInput m where m.namaDb=:namadb")
    List<String> findAllParamInput(@Param("namadb") String namaDB);

    @Query("select m.paramDb from MasterDbInput m where m.namaDb=:namadb and m.paramInput = :paraminput")
    String findParamDb(@Param("namadb") String namaDB, @Param("paraminput") String paraminput);
}
