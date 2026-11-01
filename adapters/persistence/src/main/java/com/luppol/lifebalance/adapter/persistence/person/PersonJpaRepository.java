package com.luppol.lifebalance.adapter.persistence.person;

import com.luppol.lifebalance.domain.person.PersonKind;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface PersonJpaRepository extends JpaRepository<PersonEntity, String> {
    long countByKind(PersonKind kind);

    @Query(value = """
            select * from core.person
            where kind = :kind and last_active_at < clock_timestamp() - make_interval(days => :days)
            order by last_active_at, id""", nativeQuery = true)
    List<PersonEntity> findAllOfKindInactiveFor(String kind, int days);

    @Modifying
    @Query(value = """
            update core.person set last_active_at = clock_timestamp()
            where id = :id and last_active_at < date_trunc('day', clock_timestamp())""", nativeQuery = true)
    void markActiveOncePerDay(String id);

    @Modifying
    @Query("delete from PersonEntity person where person.kind = :kind")
    void deleteAllOfKind(PersonKind kind);

    @Modifying
    @Query("""
            delete from PersonEntity person where person.id in (
                select ranked.id from PersonEntity ranked where ranked.kind = :kind
                order by ranked.enrolledAt desc, ranked.id desc offset :newestToKeep)""")
    void deleteAllButNewestOfKind(PersonKind kind, int newestToKeep);

    @Modifying
    @Query(value = """
            delete from core.person
            where kind = :kind and last_active_at < clock_timestamp() - make_interval(days => :days)""",
            nativeQuery = true)
    void deleteAllOfKindInactiveFor(String kind, int days);
}
