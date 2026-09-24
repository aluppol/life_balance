package com.luppol.lifebalance.adapter.persistence.value;

import com.luppol.lifebalance.adapter.persistence.Flushing;
import com.luppol.lifebalance.domain.person.PersonId;
import com.luppol.lifebalance.domain.value.CoreValue;
import com.luppol.lifebalance.domain.value.CoreValueId;
import com.luppol.lifebalance.domain.value.CoreValueRepository;
import jakarta.persistence.EntityManager;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class CoreValueRepositoryAdapter implements CoreValueRepository {
    private final CoreValueJpaRepository coreValues;
    private final EntityManager entityManager;

    public CoreValueRepositoryAdapter(CoreValueJpaRepository coreValues, EntityManager entityManager) {
        this.coreValues = coreValues;
        this.entityManager = entityManager;
    }

    @Override
    public List<CoreValue> findAllByOwner(PersonId owner) {
        return coreValues.findAllByPersonIdOrderByPositionAscNameAsc(owner.value()).stream()
                .map(CoreValueEntity::toDomain)
                .toList();
    }

    @Override
    public Optional<CoreValue> findById(PersonId owner, CoreValueId id) {
        return coreValues.findByPersonIdAndId(owner.value(), id.value()).map(CoreValueEntity::toDomain);
    }

    @Override
    public void add(CoreValue value) {
        entityManager.persist(CoreValueEntity.from(value));
        Flushing.flushOrReportConflict(entityManager, duplicateName(value));
    }

    @Override
    public void update(CoreValue value) {
        entityManager.merge(CoreValueEntity.from(value));
        Flushing.flushOrReportConflict(entityManager, duplicateName(value));
    }

    @Override
    public void updateAll(List<CoreValue> values) {
        values.forEach(value -> entityManager.merge(CoreValueEntity.from(value)));
    }

    @Override
    public void remove(PersonId owner, CoreValueId id) {
        coreValues.deleteOwned(owner.value(), id.value());
    }

    private static String duplicateName(CoreValue value) {
        return "A core value named '%s' already exists".formatted(value.name());
    }
}
