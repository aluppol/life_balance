package com.luppol.lifebalance.adapter.persistence.person;

import com.luppol.lifebalance.domain.person.InactivityPeriod;
import com.luppol.lifebalance.domain.person.MemberActivity;
import com.luppol.lifebalance.domain.person.Person;
import com.luppol.lifebalance.domain.person.PersonAlreadyEnrolledException;
import com.luppol.lifebalance.domain.person.PersonId;
import com.luppol.lifebalance.domain.person.PersonKind;
import com.luppol.lifebalance.domain.person.PersonRepository;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityManager;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class PersonRepositoryAdapter implements PersonRepository {
    private static final String MEMBER_KIND = PersonKind.MEMBER.name();

    private final PersonJpaRepository people;
    private final EntityManager entityManager;

    public PersonRepositoryAdapter(PersonJpaRepository people, EntityManager entityManager) {
        this.people = people;
        this.entityManager = entityManager;
    }

    @Override
    public boolean isEnrolled(PersonId id) {
        return people.existsById(id.value());
    }

    @Override
    public long countMembers() {
        return people.countByKind(PersonKind.MEMBER);
    }

    @Override
    public List<MemberActivity> findInactiveMembers(InactivityPeriod period) {
        return people.findAllOfKindInactiveFor(MEMBER_KIND, period.days()).stream()
                .map(PersonEntity::toMemberActivity)
                .toList();
    }

    @Override
    public void enroll(Person person) {
        try {
            entityManager.persist(PersonEntity.from(person));
            entityManager.flush();
        } catch (EntityExistsException | ConstraintViolationException duplicate) {
            throw new PersonAlreadyEnrolledException(person.id());
        }
    }

    @Override
    public void recordActivity(PersonId id) {
        runInBulk(() -> people.markActiveOncePerDay(id.value()));
    }

    @Override
    public void removeAllGuests() {
        runInBulk(() -> people.deleteAllOfKind(PersonKind.GUEST));
    }

    @Override
    public void removeAllButNewestGuests(int newestToKeep) {
        runInBulk(() -> people.deleteAllButNewestOfKind(PersonKind.GUEST, newestToKeep));
    }

    @Override
    public void removeInactiveMembers(InactivityPeriod period) {
        runInBulk(() -> people.deleteAllOfKindInactiveFor(MEMBER_KIND, period.days()));
    }

    private void runInBulk(Runnable statement) {
        entityManager.flush();
        statement.run();
        entityManager.clear();
    }
}
