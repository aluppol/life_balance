package com.luppol.life_balance.auth;

import com.luppol.life_balance.auth.moderls.User;
import com.luppol.life_balance.exceptions.NotFoundException;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

interface UserRepository extends Repository<User, Long> {
    User save(User u);

    boolean existsByUsername(String username);
    boolean existsByEmail(String email);

    Optional<User> findById(Long id);
    Optional<User> findByEmail(String email);
    Optional<User> findByUsername(String username);

    @Modifying(flushAutomatically = true, clearAutomatically = true)
    @Query("update User u set u.password = :hash where u.id = :id")
    int updatePassword(@Param("id") long id, @Param("hash") String hash);

    @Modifying(flushAutomatically = true, clearAutomatically = true)
    @Query("update User u set u.email = :email where u.id = :id")
    int updateEmail(@Param("id") long id, @Param("email") String email);

    void deleteById(Long id);

    default User findRequired(Long id) {
        return findById(id).orElseThrow(
                () -> new NotFoundException("%s with id %d not found!".formatted(
                        "User",
                        id
                ))
        );
    }
}
