package com.luppol.lifebalance.application.role;

import com.luppol.lifebalance.domain.ConflictException;
import com.luppol.lifebalance.domain.Invariants;
import com.luppol.lifebalance.domain.Ordering;
import com.luppol.lifebalance.domain.person.PersonId;
import com.luppol.lifebalance.domain.role.LifeRole;
import com.luppol.lifebalance.domain.role.LifeRoleId;
import com.luppol.lifebalance.domain.role.LifeRoleRepository;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class LifeRoleCommandService implements LifeRoleCommands {
    private final LifeRoleRepository lifeRoles;

    public LifeRoleCommandService(LifeRoleRepository lifeRoles) {
        this.lifeRoles = lifeRoles;
    }

    @Override
    public void add(AddLifeRole command) {
        List<LifeRole> existing = lifeRoles.findAllByOwner(command.owner());
        Invariants.requireRoomForOneMore(existing.size(), LifeRole.MAXIMUM_PER_PERSON, "life roles");
        requireUniqueName(existing, command.name());
        int position = Ordering.nextPosition(existing, LifeRole::position);
        lifeRoles.add(LifeRole.personal(command.id(), command.owner(), command.name(), command.description(), position));
    }

    @Override
    public void revise(ReviseLifeRole command) {
        LifeRole role = lifeRoles.findRequired(command.owner(), command.id());
        List<LifeRole> others = lifeRoles.findAllByOwner(command.owner()).stream()
                .filter(other -> !other.id().equals(command.id()))
                .toList();
        requireUniqueName(others, command.name());
        lifeRoles.update(role.revisedTo(command.name(), command.description()));
    }

    @Override
    public void reorder(ReorderLifeRoles command) {
        Map<LifeRoleId, LifeRole> byId = lifeRoles.findAllByOwner(command.owner()).stream()
                .collect(Collectors.toMap(LifeRole::id, Function.identity()));
        Ordering.requirePermutation(command.orderedIds(), byId.keySet());
        List<LifeRoleId> ids = command.orderedIds();
        lifeRoles.updateAll(IntStream.range(0, ids.size())
                .mapToObj(position -> byId.get(ids.get(position)).movedTo(position))
                .toList());
    }

    @Override
    public void remove(PersonId owner, LifeRoleId id) {
        LifeRole role = lifeRoles.findRequired(owner, id);
        if (role.isBuiltIn()) {
            throw new ConflictException("The %s role cannot be removed".formatted(role.name()));
        }
        if (lifeRoles.isInUse(owner, id)) {
            throw new ConflictException("The %s role still has goals or activities".formatted(role.name()));
        }
        lifeRoles.remove(owner, id);
    }

    private static void requireUniqueName(List<LifeRole> roles, String name) {
        if (roles.stream().anyMatch(role -> role.isNamed(name))) {
            throw new ConflictException("A life role named '%s' already exists".formatted(name));
        }
    }
}
