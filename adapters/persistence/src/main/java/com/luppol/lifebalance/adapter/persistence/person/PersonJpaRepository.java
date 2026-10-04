package com.luppol.lifebalance.adapter.persistence.person;

import com.luppol.lifebalance.domain.person.PersonKind;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface PersonJpaRepository extends JpaRepository<PersonEntity, String> {
    @Modifying
    @Query("delete from PersonEntity person where person.kind = :kind")
    void deleteAllOfKind(PersonKind kind);

    @Modifying
    @Query("""
            delete from PersonEntity person where person.id in (
                select ranked.id from PersonEntity ranked where ranked.kind = :kind
                order by ranked.enrolledAt desc, ranked.id desc offset :newestToKeep)""")
    void deleteAllButNewestOfKind(PersonKind kind, int newestToKeep);
}
