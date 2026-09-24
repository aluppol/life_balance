package com.luppol.lifebalance.application.value;

import com.luppol.lifebalance.domain.person.PersonId;
import com.luppol.lifebalance.domain.value.CoreValue;
import com.luppol.lifebalance.domain.value.CoreValueId;

import java.util.List;

public interface CoreValueQueries {
    List<CoreValue> listAll(PersonId owner);

    CoreValue find(PersonId owner, CoreValueId id);
}
