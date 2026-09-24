package com.luppol.lifebalance.adapter.web.api.planning;

import com.luppol.lifebalance.domain.planning.Tally;

public record TallyResponse(int planned, int completed) {
    static TallyResponse from(Tally tally) {
        return new TallyResponse(tally.planned(), tally.completed());
    }
}
