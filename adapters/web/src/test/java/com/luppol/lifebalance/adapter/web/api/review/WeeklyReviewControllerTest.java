package com.luppol.lifebalance.adapter.web.api.review;

import com.luppol.lifebalance.adapter.web.WebTest;
import com.luppol.lifebalance.domain.planning.WeekStart;
import com.luppol.lifebalance.domain.review.RenewalDimension;
import com.luppol.lifebalance.domain.review.WeeklyReview;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import java.time.LocalDate;
import java.util.Set;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class WeeklyReviewControllerTest extends WebTest {
    private static final WeekStart WEEK = new WeekStart(LocalDate.of(2026, 9, 14));
    private final WeeklyReview review = new WeeklyReview(OWNER, WEEK, "Shipped", "Sleep more",
            Set.of(RenewalDimension.SPIRITUAL, RenewalDimension.PHYSICAL));

    @Test
    void find_listsDimensionsInTheirNaturalOrder() throws Exception {
        when(weeklyReviewQueries.find(OWNER, WEEK)).thenReturn(review);

        mvc.perform(get("/api/weeks/2026-09-14/review").with(member()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.weekStart").value("2026-09-14"))
                .andExpect(jsonPath("$.accomplishments").value("Shipped"))
                .andExpect(jsonPath("$.lessons").value("Sleep more"))
                .andExpect(jsonPath("$.renewedDimensions[0]").value("PHYSICAL"))
                .andExpect(jsonPath("$.renewedDimensions[1]").value("SPIRITUAL"));
    }

    @Test
    void record_storesTheReview() throws Exception {
        when(weeklyReviewQueries.find(OWNER, WEEK)).thenReturn(review);

        mvc.perform(put("/api/weeks/2026-09-14/review").with(member()).contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"accomplishments":"Shipped","lessons":"Sleep more",
                                 "renewedDimensions":["SPIRITUAL","PHYSICAL"]}
                                """))
                .andExpect(status().isOk());

        verify(weeklyReviewCommands).record(review);
    }

    @Test
    void record_treatsMissingAnswersAsEmpty() throws Exception {
        when(weeklyReviewQueries.find(OWNER, WEEK)).thenReturn(review);

        mvc.perform(put("/api/weeks/2026-09-14/review").with(member()).contentType(MediaType.APPLICATION_JSON)
                        .content("{\"renewedDimensions\":[]}"))
                .andExpect(status().isOk());

        verify(weeklyReviewCommands).record(new WeeklyReview(OWNER, WEEK, "", "", Set.of()));
    }

    @Test
    void record_rejectsAnUnknownDimension() throws Exception {
        mvc.perform(put("/api/weeks/2026-09-14/review").with(member()).contentType(MediaType.APPLICATION_JSON)
                        .content("{\"renewedDimensions\":[\"FINANCIAL\"]}"))
                .andExpect(status().isBadRequest());
    }
}
