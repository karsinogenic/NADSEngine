package com.nads.nadsengine.Repositories;

import com.nads.nadsengine.Models.TblVwNads;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface VwNadsRepository extends JpaRepository<TblVwNads, String> {

    @Query(value = "Select TOP 10 * from TBL_VW_NADS", nativeQuery = true)
    List<TblVwNads> findAllCustom();

    @Query(value = "Select * from TBL_VW_NADS where ID_NUMBER LIKE %:param%", nativeQuery = true)
    List<TblVwNads> findByIdCustom(String param);

    @Query(value = "Select TOP 100 * from TBL_VW_NADS where ID_NUMBER LIKE %:id% AND (CUST_NAME_PRIMARY LIKE %:nama% OR CUST_NAME_SUPPLEMENT LIKE %:nama%) ORDER BY ID_NUMBER ASC", nativeQuery = true)
    List<TblVwNads> findByIdNama(String id, String nama);

}
