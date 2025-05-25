package com.luppol.life_balance.repositories;

import com.luppol.life_balance.models.Mission;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MissionRepository extends JpaRepository<Mission, Long> {
}
