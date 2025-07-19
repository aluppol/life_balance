package com.luppol.life_balance.repositories;

import com.luppol.life_balance.models.Person;

public interface PersonRepository extends BaseRepository<Person> {
    @Override
    default Class<Person> getDomainClass() { return Person.class; }

    boolean existsByFirstNameAndLastName(String firstName, String lastName);
    boolean existsByPhoneNumber(String phoneNumber);
}
