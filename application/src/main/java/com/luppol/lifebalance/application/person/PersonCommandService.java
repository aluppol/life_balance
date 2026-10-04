package com.luppol.lifebalance.application.person;

import com.luppol.lifebalance.application.demo.DemoWorkspace;
import com.luppol.lifebalance.domain.person.GuestWorkspaceLimit;
import com.luppol.lifebalance.domain.person.Person;
import com.luppol.lifebalance.domain.person.PersonId;
import com.luppol.lifebalance.domain.person.PersonRepository;
import com.luppol.lifebalance.domain.role.LifeRole;
import com.luppol.lifebalance.domain.role.LifeRoleRepository;

public class PersonCommandService implements PersonCommands {
    private final PersonRepository people;
    private final LifeRoleRepository lifeRoles;
    private final DemoWorkspace demoWorkspace;
    private final GuestWorkspaceLimit guestWorkspaceLimit;

    public PersonCommandService(PersonRepository people, LifeRoleRepository lifeRoles, DemoWorkspace demoWorkspace,
                                GuestWorkspaceLimit guestWorkspaceLimit) {
        this.people = people;
        this.lifeRoles = lifeRoles;
        this.demoWorkspace = demoWorkspace;
        this.guestWorkspaceLimit = guestWorkspaceLimit;
    }

    @Override
    public void enrollMember(PersonId id) {
        enroll(Person.member(id));
    }

    @Override
    public void enrollGuest(PersonId id) {
        makeRoomForOneMoreGuest();
        enroll(Person.guest(id));
        demoWorkspace.furnish(id);
    }

    @Override
    public void resetGuestWorkspaces() {
        people.removeAllGuests();
    }

    private void makeRoomForOneMoreGuest() {
        people.removeAllButNewestGuests(guestWorkspaceLimit.maximum() - 1);
    }

    private void enroll(Person person) {
        people.enroll(person);
        lifeRoles.add(LifeRole.sharpenTheSaw(person.id()));
    }
}
