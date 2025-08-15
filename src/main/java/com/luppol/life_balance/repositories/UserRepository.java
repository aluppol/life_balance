package com.luppol.life_balance.repositories;

import com.luppol.life_balance.models.User;

import java.util.Optional;

public interface UserRepository extends BaseRepository<User> {
    @Override
    default Class<User> getDomainClass() { return User.class; }

    boolean existsByUsername(String username);
    boolean existsByEmail(String username);

    Optional<User> findByEmail(String email);
    Optional<User> findByUsername(String username);
}
