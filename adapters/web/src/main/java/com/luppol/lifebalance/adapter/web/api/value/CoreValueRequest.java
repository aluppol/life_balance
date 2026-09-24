package com.luppol.lifebalance.adapter.web.api.value;

import com.luppol.lifebalance.application.value.AddCoreValue;
import com.luppol.lifebalance.application.value.ReviseCoreValue;
import com.luppol.lifebalance.domain.person.PersonId;
import com.luppol.lifebalance.domain.value.CoreValue;
import com.luppol.lifebalance.domain.value.CoreValueId;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.Objects;

public record CoreValueRequest(
        @NotBlank @Size(max = CoreValue.MAXIMUM_NAME_LENGTH) String name,
        @Size(max = CoreValue.MAXIMUM_DESCRIPTION_LENGTH) String description) {

    AddCoreValue toAddition(CoreValueId id, PersonId owner) {
        return new AddCoreValue(id, owner, name, descriptionOrEmpty());
    }

    ReviseCoreValue toRevision(CoreValueId id, PersonId owner) {
        return new ReviseCoreValue(id, owner, name, descriptionOrEmpty());
    }

    private String descriptionOrEmpty() {
        return Objects.requireNonNullElse(description, "");
    }
}
