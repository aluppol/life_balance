package com.luppol.lifebalance.adapter.web.api.review;

import com.luppol.lifebalance.application.review.WeeklyReviewCommands;
import com.luppol.lifebalance.application.review.WeeklyReviewQueries;
import com.luppol.lifebalance.domain.person.PersonId;
import com.luppol.lifebalance.domain.planning.WeekStart;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/weeks/{weekStart}/review")
@Tag(name = "Weekly review", description = "What went well, what was learned, which dimensions were renewed")
public class WeeklyReviewController {
    private final WeeklyReviewCommands commands;
    private final WeeklyReviewQueries queries;

    public WeeklyReviewController(WeeklyReviewCommands commands, WeeklyReviewQueries queries) {
        this.commands = commands;
        this.queries = queries;
    }

    @GetMapping
    @Operation(summary = "Read the review of a week (404 until one is written)")
    public WeeklyReviewResponse find(PersonId owner, @PathVariable WeekStart weekStart) {
        return WeeklyReviewResponse.from(queries.find(owner, weekStart));
    }

    @PutMapping
    @Operation(summary = "Write or rewrite the review of a week")
    public WeeklyReviewResponse record(PersonId owner, @PathVariable WeekStart weekStart,
                                       @Valid @RequestBody WeeklyReviewRequest request) {
        commands.record(request.toReview(owner, weekStart));
        return find(owner, weekStart);
    }
}
