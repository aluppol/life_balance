package com.luppol.lifebalance.adapter.persistence.person;

import com.luppol.lifebalance.domain.person.PersonKind;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface PersonJpaRepository extends JpaRepository<PersonEntity, String> {
    @Query("select person.id from PersonEntity person where person.kind = :kind order by person.id")
    List<String> findIdsByKind(PersonKind kind);

    @Modifying
    @Query("delete from PersonEntity person where person.id = :id")
    void deleteInBulk(String id);
}
