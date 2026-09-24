package com.luppol.lifebalance.application.value;

import com.luppol.lifebalance.domain.ConflictException;
import com.luppol.lifebalance.domain.Ordering;
import com.luppol.lifebalance.domain.person.PersonId;
import com.luppol.lifebalance.domain.value.CoreValue;
import com.luppol.lifebalance.domain.value.CoreValueId;
import com.luppol.lifebalance.domain.value.CoreValueRepository;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class CoreValueCommandService implements CoreValueCommands {
    private final CoreValueRepository coreValues;

    public CoreValueCommandService(CoreValueRepository coreValues) {
        this.coreValues = coreValues;
    }

    @Override
    public void add(AddCoreValue command) {
        List<CoreValue> existing = coreValues.findAllByOwner(command.owner());
        requireUniqueName(existing, command.name());
        int position = Ordering.nextPosition(existing, CoreValue::position);
        coreValues.add(new CoreValue(command.id(), command.owner(), command.name(), command.description(), position));
    }

    @Override
    public void revise(ReviseCoreValue command) {
        CoreValue value = coreValues.findRequired(command.owner(), command.id());
        List<CoreValue> others = coreValues.findAllByOwner(command.owner()).stream()
                .filter(other -> !other.id().equals(command.id()))
                .toList();
        requireUniqueName(others, command.name());
        coreValues.update(value.revisedTo(command.name(), command.description()));
    }

    @Override
    public void reorder(ReorderCoreValues command) {
        Map<CoreValueId, CoreValue> byId = coreValues.findAllByOwner(command.owner()).stream()
                .collect(Collectors.toMap(CoreValue::id, Function.identity()));
        Ordering.requirePermutation(command.orderedIds(), byId.keySet());
        List<CoreValueId> ids = command.orderedIds();
        coreValues.updateAll(IntStream.range(0, ids.size())
                .mapToObj(position -> byId.get(ids.get(position)).movedTo(position))
                .toList());
    }

    @Override
    public void remove(PersonId owner, CoreValueId id) {
        coreValues.findRequired(owner, id);
        coreValues.remove(owner, id);
    }

    private static void requireUniqueName(List<CoreValue> values, String name) {
        if (values.stream().anyMatch(value -> value.isNamed(name))) {
            throw new ConflictException("A core value named '%s' already exists".formatted(name));
        }
    }
}
