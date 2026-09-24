package com.luppol.lifebalance.application.value;

import com.luppol.lifebalance.domain.person.PersonId;
import com.luppol.lifebalance.domain.value.CoreValue;
import com.luppol.lifebalance.domain.value.CoreValueId;
import com.luppol.lifebalance.domain.value.CoreValueRepository;

import java.util.List;

public class CoreValueQueryService implements CoreValueQueries {
    private final CoreValueRepository coreValues;

    public CoreValueQueryService(CoreValueRepository coreValues) {
        this.coreValues = coreValues;
    }

    @Override
    public List<CoreValue> listAll(PersonId owner) {
        return coreValues.findAllByOwner(owner);
    }

    @Override
    public CoreValue find(PersonId owner, CoreValueId id) {
        return coreValues.findRequired(owner, id);
    }
}
