package com.luppol.lifebalance.application.fakes;

import com.luppol.lifebalance.domain.person.PersonId;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

public class OwnedRecords<K, V> {
    private final Map<K, V> records = new LinkedHashMap<>();
    private final Function<V, K> id;
    private final Function<V, PersonId> owner;

    public OwnedRecords(Function<V, K> id, Function<V, PersonId> owner) {
        this.id = id;
        this.owner = owner;
    }

    public List<V> findAllByOwner(PersonId person, Comparator<V> order) {
        return records.values().stream().filter(record -> owner.apply(record).equals(person)).sorted(order).toList();
    }

    public Optional<V> findById(PersonId person, K key) {
        return Optional.ofNullable(records.get(key)).filter(record -> owner.apply(record).equals(person));
    }

    public void put(V record) {
        records.put(id.apply(record), record);
    }

    public void remove(PersonId person, K key) {
        findById(person, key).ifPresent(record -> records.remove(key));
    }

    public void removeAllOf(PersonId person) {
        new ArrayList<>(records.values()).stream()
                .filter(record -> owner.apply(record).equals(person))
                .forEach(record -> records.remove(id.apply(record)));
    }

    public List<V> all() {
        return List.copyOf(records.values());
    }
}
