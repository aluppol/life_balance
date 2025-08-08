package com.luppol.life_balance.repositories;

import com.luppol.life_balance.models.User;

public interface UserRepository extends BaseRepository<User> {
    @Override
    default Class<User> getDomainClass() { return User.class; }

    boolean existsByUsername(String username);

}
