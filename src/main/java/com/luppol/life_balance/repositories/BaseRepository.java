package com.luppol.life_balance.repositories;

import com.luppol.life_balance.exceptions.NotFoundException;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;

@NoRepositoryBean
public interface BaseRepository <T> extends JpaRepository<T, Long> {
    default T findRequired(Long id) {
        return findById(id).orElseThrow(
                () -> new NotFoundException("%s with id %d not found!".formatted(
                        getDomainClass().getSimpleName(),
                        id
                ))
        );
    }

    Class<T> getDomainClass();
}
