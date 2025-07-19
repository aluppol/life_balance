package com.luppol.life_balance.repositories;

import com.luppol.life_balance.models.Mission;

public interface MissionRepository extends BaseRepository<Mission> {
    @Override
    default Class<Mission> getDomainClass() { return Mission.class; }
}
