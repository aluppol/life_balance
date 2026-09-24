package com.luppol.lifebalance.domain.role;

import com.luppol.lifebalance.domain.RuleViolationException;
import com.luppol.lifebalance.domain.person.PersonId;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class LifeRoleTest {
    private static final PersonId OWNER = new PersonId("owner");
    private static final LifeRoleId ID = new LifeRoleId(UUID.fromString("00000000-0000-0000-0000-000000000002"));
    private static final LifeRole PARENT = LifeRole.personal(ID, OWNER, "Parent", "Raise kind kids", 1);

    @Test
    void personal_isNotBuiltIn() {
        assertThat(PARENT.kind()).isEqualTo(LifeRoleKind.PERSONAL);
        assertThat(PARENT.isBuiltIn()).isFalse();
    }

    @Test
    void sharpenTheSaw_isTheBuiltInFirstRole() {
        LifeRole saw = LifeRole.sharpenTheSaw(OWNER);
        assertThat(saw.isBuiltIn()).isTrue();
        assertThat(saw.name()).isEqualTo("Sharpen the Saw");
        assertThat(saw.description()).isEqualTo(LifeRole.SHARPEN_THE_SAW_DESCRIPTION);
        assertThat(saw.position()).isZero();
        assertThat(saw.owner()).isEqualTo(OWNER);
    }

    @Test
    void revisedTo_keepsKindAndPosition() {
        assertThat(PARENT.revisedTo("Father", "Be there"))
                .isEqualTo(new LifeRole(ID, OWNER, "Father", "Be there", LifeRoleKind.PERSONAL, 1));
    }

    @Test
    void movedTo_changesPositionOnly() {
        assertThat(PARENT.movedTo(4).position()).isEqualTo(4);
        assertThat(PARENT.movedTo(4).name()).isEqualTo("Parent");
    }

    @Test
    void isNamed_ignoresCase() {
        assertThat(PARENT.isNamed("parent")).isTrue();
        assertThat(PARENT.isNamed("Friend")).isFalse();
    }

    @Test
    void role_validatesItsFields() {
        assertThatThrownBy(() -> LifeRole.personal(ID, OWNER, " ", "", 0)).isInstanceOf(RuleViolationException.class);
        assertThatThrownBy(() -> LifeRole.personal(ID, OWNER, "n".repeat(101), "", 0))
                .hasMessage("Life role name must be at most 100 characters");
        assertThatThrownBy(() -> LifeRole.personal(ID, OWNER, "Parent", "d".repeat(1001), 0))
                .hasMessage("Life role description must be at most 1000 characters");
        assertThatThrownBy(() -> LifeRole.personal(ID, OWNER, "Parent", "", -1))
                .hasMessage("Life role position must not be negative");
        assertThatThrownBy(() -> new LifeRole(ID, OWNER, "Parent", "", null, 0)).hasMessage("Life role kind is required");
        assertThatThrownBy(() -> LifeRole.personal(null, OWNER, "Parent", "", 0)).hasMessage("Life role id is required");
        assertThatThrownBy(() -> LifeRole.personal(ID, null, "Parent", "", 0)).hasMessage("Life role owner is required");
    }

    @Test
    void id_isRandomAndPrintable() {
        LifeRoleId random = LifeRoleId.random();
        assertThat(random).isNotEqualTo(LifeRoleId.random()).hasToString(random.value().toString());
        assertThatThrownBy(() -> new LifeRoleId(null)).isInstanceOf(RuleViolationException.class);
    }
}
