package com.luppol.lifebalance.adapter.persistence.role;

import com.luppol.lifebalance.adapter.persistence.Flushing;
import com.luppol.lifebalance.domain.person.PersonId;
import com.luppol.lifebalance.domain.role.LifeRole;
import com.luppol.lifebalance.domain.role.LifeRoleId;
import com.luppol.lifebalance.domain.role.LifeRoleRepository;
import jakarta.persistence.EntityManager;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class LifeRoleRepositoryAdapter implements LifeRoleRepository {
    private final LifeRoleJpaRepository lifeRoles;
    private final EntityManager entityManager;

    public LifeRoleRepositoryAdapter(LifeRoleJpaRepository lifeRoles, EntityManager entityManager) {
        this.lifeRoles = lifeRoles;
        this.entityManager = entityManager;
    }

    @Override
    public List<LifeRole> findAllByOwner(PersonId owner) {
        return lifeRoles.findAllByPersonIdOrderByPositionAscNameAsc(owner.value()).stream()
                .map(LifeRoleEntity::toDomain)
                .toList();
    }

    @Override
    public Optional<LifeRole> findById(PersonId owner, LifeRoleId id) {
        return lifeRoles.findByPersonIdAndId(owner.value(), id.value()).map(LifeRoleEntity::toDomain);
    }

    @Override
    public boolean isInUse(PersonId owner, LifeRoleId id) {
        return lifeRoles.hasGoals(owner.value(), id.value()) || lifeRoles.hasActivities(owner.value(), id.value());
    }

    @Override
    public void add(LifeRole role) {
        entityManager.persist(LifeRoleEntity.from(role));
        Flushing.flushOrReportConflict(entityManager, duplicateName(role));
    }

    @Override
    public void update(LifeRole role) {
        entityManager.merge(LifeRoleEntity.from(role));
        Flushing.flushOrReportConflict(entityManager, duplicateName(role));
    }

    @Override
    public void updateAll(List<LifeRole> roles) {
        roles.forEach(role -> entityManager.merge(LifeRoleEntity.from(role)));
    }

    @Override
    public void remove(PersonId owner, LifeRoleId id) {
        lifeRoles.deleteOwned(owner.value(), id.value());
    }

    private static String duplicateName(LifeRole role) {
        return "A life role named '%s' already exists".formatted(role.name());
    }
}
