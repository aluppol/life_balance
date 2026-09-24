package com.luppol.lifebalance.adapter.persistence.role;

import com.luppol.lifebalance.adapter.persistence.PersistenceTest;
import com.luppol.lifebalance.domain.ConflictException;
import com.luppol.lifebalance.domain.goal.Goal;
import com.luppol.lifebalance.domain.goal.GoalDetails;
import com.luppol.lifebalance.domain.goal.GoalId;
import com.luppol.lifebalance.domain.planning.ActivityDetails;
import com.luppol.lifebalance.domain.planning.ActivityId;
import com.luppol.lifebalance.domain.planning.PlannedActivity;
import com.luppol.lifebalance.domain.planning.Quadrant;
import com.luppol.lifebalance.domain.planning.WeekStart;
import com.luppol.lifebalance.domain.role.LifeRole;
import com.luppol.lifebalance.domain.role.LifeRoleId;
import com.luppol.lifebalance.domain.role.LifeRoleKind;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class LifeRoleRepositoryAdapterTest extends PersistenceTest {
    private final LifeRole saw = LifeRole.sharpenTheSaw(OWNER);
    private final LifeRole parent = LifeRole.personal(LifeRoleId.random(), OWNER, "Parent", "Raise kind kids", 1);

    @BeforeEach
    void enrollPeople() {
        enroll(OWNER, STRANGER);
        lifeRoles.add(parent);
        lifeRoles.add(saw);
        flushAndClear();
    }

    @Test
    void findAllByOwner_ordersByPosition() {
        assertThat(lifeRoles.findAllByOwner(OWNER)).containsExactly(saw, parent);
        assertThat(lifeRoles.findAllByOwner(STRANGER)).isEmpty();
    }

    @Test
    void findById_keepsTheKind() {
        assertThat(lifeRoles.findById(OWNER, saw.id())).get().extracting(LifeRole::kind)
                .isEqualTo(LifeRoleKind.SHARPEN_THE_SAW);
        assertThat(lifeRoles.findById(STRANGER, saw.id())).isEmpty();
    }

    @Test
    void add_allowsOnlyOneSharpenTheSawPerPerson() {
        LifeRole second = new LifeRole(LifeRoleId.random(), OWNER, "Renewal", "", LifeRoleKind.SHARPEN_THE_SAW, 3);

        assertThatThrownBy(() -> lifeRoles.add(second)).isInstanceOf(ConflictException.class);
    }

    @Test
    void update_andUpdateAll_changeStoredRoles() {
        lifeRoles.update(parent.revisedTo("Father", "Be there"));
        lifeRoles.updateAll(List.of(saw.movedTo(7)));
        flushAndClear();

        assertThat(lifeRoles.findById(OWNER, parent.id())).contains(parent.revisedTo("Father", "Be there"));
        assertThat(lifeRoles.findById(OWNER, saw.id())).contains(saw.movedTo(7));
    }

    @Test
    void update_rejectsADuplicateName() {
        assertThatThrownBy(() -> lifeRoles.update(parent.revisedTo("SHARPEN THE SAW", "")))
                .isInstanceOf(ConflictException.class)
                .hasMessage("A life role named 'SHARPEN THE SAW' already exists");
    }

    @Test
    void isInUse_seesGoals() {
        assertThat(lifeRoles.isInUse(OWNER, parent.id())).isFalse();

        goals.add(Goal.set(GoalId.random(), OWNER, new GoalDetails(parent.id(), "Teach cycling", "", Optional.empty(), Set.of())));
        flushAndClear();

        assertThat(lifeRoles.isInUse(OWNER, parent.id())).isTrue();
        assertThat(lifeRoles.isInUse(OWNER, saw.id())).isFalse();
    }

    @Test
    void isInUse_seesActivities() {
        ActivityDetails details = new ActivityDetails(saw.id(), Optional.empty(), "Run", Quadrant.IMPORTANT_NOT_URGENT,
                Optional.empty());
        activities.add(PlannedActivity.planned(ActivityId.random(), OWNER, new WeekStart(LocalDate.of(2026, 9, 21)), details));
        flushAndClear();

        assertThat(lifeRoles.isInUse(OWNER, saw.id())).isTrue();
    }

    @Test
    void remove_onlyDeletesTheOwnersRole() {
        lifeRoles.remove(STRANGER, parent.id());
        assertThat(lifeRoles.findById(OWNER, parent.id())).isPresent();

        lifeRoles.remove(OWNER, parent.id());
        assertThat(lifeRoles.findById(OWNER, parent.id())).isEmpty();
    }
}
