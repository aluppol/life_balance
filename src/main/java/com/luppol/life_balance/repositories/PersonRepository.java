package com.luppol.life_balance.repositories;

import com.luppol.life_balance.models.Person;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PersonRepository extends JpaRepository<Person, Long> {
    boolean existsByFirstNameAndLastName(String firstName, String lastName);
    boolean existsByPhoneNumber(String phoneNumber);
}
