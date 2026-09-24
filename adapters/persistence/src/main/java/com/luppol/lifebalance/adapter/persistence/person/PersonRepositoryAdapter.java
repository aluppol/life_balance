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

import java.util.List;

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
    public List<PersonId> findGuests() {
        return people.findIdsByKind(PersonKind.GUEST).stream().map(PersonId::new).toList();
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
    public void remove(PersonId id) {
        entityManager.flush();
        people.deleteInBulk(id.value());
        entityManager.clear();
    }
}
