package com.luppol.lifebalance.adapter.web.api.role;

import com.luppol.lifebalance.application.role.AddLifeRole;
import com.luppol.lifebalance.application.role.ReviseLifeRole;
import com.luppol.lifebalance.domain.person.PersonId;
import com.luppol.lifebalance.domain.role.LifeRole;
import com.luppol.lifebalance.domain.role.LifeRoleId;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.Objects;

public record LifeRoleRequest(
        @NotBlank @Size(max = LifeRole.MAXIMUM_NAME_LENGTH) String name,
        @Size(max = LifeRole.MAXIMUM_DESCRIPTION_LENGTH) String description) {

    AddLifeRole toAddition(LifeRoleId id, PersonId owner) {
        return new AddLifeRole(id, owner, name, descriptionOrEmpty());
    }

    ReviseLifeRole toRevision(LifeRoleId id, PersonId owner) {
        return new ReviseLifeRole(id, owner, name, descriptionOrEmpty());
    }

    private String descriptionOrEmpty() {
        return Objects.requireNonNullElse(description, "");
    }
}
