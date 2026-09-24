package com.luppol.lifebalance.adapter.web.api.planning;

import com.luppol.lifebalance.domain.planning.Quadrant;
import com.luppol.lifebalance.domain.planning.Tally;
import com.luppol.lifebalance.domain.planning.WeekScorecard;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

public record ScorecardResponse(LocalDate weekStart, TallyResponse overall, TallyResponse bigRocks,
                                Map<Quadrant, TallyResponse> byQuadrant, List<RoleTallyResponse> byRole) {
    static ScorecardResponse from(WeekScorecard scorecard) {
        Map<Quadrant, TallyResponse> byQuadrant = new EnumMap<>(Quadrant.class);
        for (Quadrant quadrant : Quadrant.values()) {
            byQuadrant.put(quadrant, TallyResponse.from(scorecard.byQuadrant().getOrDefault(quadrant, Tally.NONE)));
        }
        List<RoleTallyResponse> byRole = scorecard.byRole().entrySet().stream()
                .map(RoleTallyResponse::from)
                .sorted(Comparator.comparing(RoleTallyResponse::roleId))
                .toList();
        return new ScorecardResponse(scorecard.week().monday(), TallyResponse.from(scorecard.overall()),
                TallyResponse.from(scorecard.bigRocks()), byQuadrant, byRole);
    }
}
