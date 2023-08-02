package com.eazybytes.eazyschool.repository;

import com.eazybytes.eazyschool.model.Contact;


import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/*
@Repository stereotype annotation is used to add a bean of this class
type to the Spring context and indicate that given Bean is used to perform
DB related operations and
* */
@Repository
public interface ContactRepository extends JpaRepository <Contact, Integer> {

    List<Contact> findByStatus (String status);

    /*@Query ("SELECT c FROM Contact c WHERE c.status = ?1" )*/
    /*@Query ("SELECT c FROM Contact c WHERE c.status = :status ") *//*JPQL query*/
    @Query (value = "SELECT * FROM contact_msg c WHERE c.status = :status", nativeQuery = true)
    Page<Contact> findByStatusWithQuery (/*@Param("status")*/ String status, Pageable pageable);

    @Transactional
    @Modifying
    @Query ("UPDATE Contact c SET c.status = ?1 WHERE c.contactId = ?2")
    int updateStatusById(String status, int id);
    /** @return 'int' because it refers to each record*/


    /** Alternative methods to those above where the queries are being written in the Entity itself
     * using: NamedQuery */

    Page<Contact> findOpenMsgs (String status, Pageable pageable);

    @Transactional
    @Modifying
    int updateMsgStatus (String status, int id);


    /** Alternative methods to those above where the queries are being written in the Entity itself
     * using: NamedNativeQuery  - Sorting will not work using this approach!! */

    @Query(nativeQuery = true)
    Page<Contact> findOpenMsgsNative(@Param("status") String status, Pageable pageable);

    @Transactional
    @Modifying
    @Query(nativeQuery = true)
    int updateMsgStatusNative(String status, int id);

}
