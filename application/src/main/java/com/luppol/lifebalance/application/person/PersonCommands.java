package com.luppol.lifebalance.application.person;

import com.luppol.lifebalance.domain.person.InactivityPeriod;
import com.luppol.lifebalance.domain.person.PersonId;

public interface PersonCommands {
    void enrollMember(PersonId id);

    void enrollGuest(PersonId id);

    void recordActivity(PersonId id);

    void resetGuestWorkspaces();

    void removeInactiveMembers(InactivityPeriod period);
}
