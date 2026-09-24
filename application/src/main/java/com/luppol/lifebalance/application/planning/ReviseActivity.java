package com.luppol.lifebalance.application.planning;

import com.luppol.lifebalance.domain.person.PersonId;
import com.luppol.lifebalance.domain.planning.ActivityDetails;
import com.luppol.lifebalance.domain.planning.ActivityId;

public record ReviseActivity(ActivityId id, PersonId owner, ActivityDetails details) {
}
