package com.luppol.lifebalance.adapter.persistence.person;

import com.luppol.lifebalance.domain.person.Person;
import com.luppol.lifebalance.domain.person.PersonAlreadyEnrolledException;
import com.luppol.lifebalance.domain.person.PersonId;
import com.luppol.lifebalance.domain.person.PersonKind;
import com.luppol.lifebalance.domain.person.PersonRepository;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityManager;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.stereotype.Component;

@Component
public class PersonRepositoryAdapter implements PersonRepository {
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
    public void enroll(Person person) {
        try {
            entityManager.persist(PersonEntity.from(person));
            entityManager.flush();
        } catch (EntityExistsException | ConstraintViolationException duplicate) {
            throw new PersonAlreadyEnrolledException(person.id());
        }
    }

    @Override
    public void removeAllGuests() {
        removeInBulk(() -> people.deleteAllOfKind(PersonKind.GUEST));
    }

    @Override
    public void removeAllButNewestGuests(int newestToKeep) {
        removeInBulk(() -> people.deleteAllButNewestOfKind(PersonKind.GUEST, newestToKeep));
    }

    private void removeInBulk(Runnable deletion) {
        entityManager.flush();
        deletion.run();
        entityManager.clear();
    }
}
