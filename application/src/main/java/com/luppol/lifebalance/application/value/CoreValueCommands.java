package com.luppol.lifebalance.application.value;

import com.luppol.lifebalance.domain.person.PersonId;
import com.luppol.lifebalance.domain.value.CoreValueId;

public interface CoreValueCommands {
    void add(AddCoreValue command);

    void revise(ReviseCoreValue command);

    void reorder(ReorderCoreValues command);

    void remove(PersonId owner, CoreValueId id);
}
