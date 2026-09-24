package com.luppol.lifebalance.application.demo;

import com.luppol.lifebalance.domain.goal.GoalStatus;
import com.luppol.lifebalance.domain.planning.Quadrant;
import com.luppol.lifebalance.domain.review.RenewalDimension;

import java.time.DayOfWeek;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static com.luppol.lifebalance.domain.planning.Quadrant.IMPORTANT_NOT_URGENT;
import static com.luppol.lifebalance.domain.planning.Quadrant.IMPORTANT_URGENT;
import static com.luppol.lifebalance.domain.planning.Quadrant.NOT_IMPORTANT_URGENT;
import static java.time.DayOfWeek.FRIDAY;
import static java.time.DayOfWeek.MONDAY;
import static java.time.DayOfWeek.SATURDAY;
import static java.time.DayOfWeek.SUNDAY;
import static java.time.DayOfWeek.THURSDAY;
import static java.time.DayOfWeek.TUESDAY;
import static java.time.DayOfWeek.WEDNESDAY;

final class DemoContent {
    static final String MISSION = "I live by principles I choose, not by moods I happen to have. "
            + "I am fully present for my family, I do honest and excellent work, "
            + "and I keep growing in body, mind, heart and spirit.";

    static final List<DemoValue> VALUES = List.of(
            new DemoValue("Integrity", "Keep promises, especially the small ones."),
            new DemoValue("Family", "Be fully present at home; the phone can wait."),
            new DemoValue("Growth", "Learn something hard every quarter."),
            new DemoValue("Health", "Move, sleep and eat like it matters."));

    static final List<DemoRole> ROLES = List.of(
            new DemoRole("Parent", "Raise curious, kind and brave kids."),
            new DemoRole("Partner", "Build a marriage we both look forward to."),
            new DemoRole("Engineer", "Ship software people trust."),
            new DemoRole("Friend", "Show up for the people who show up for me."));

    static final List<DemoGoal> GOALS = List.of(
            goal("Teach Mia to ride a bike", "Parent", "No training wheels by her birthday.", 3, List.of("Family", "Growth")),
            goal("Plan our anniversary trip", "Partner", "Somewhere neither of us has been.", 5, List.of("Family")),
            goal("Ship offline mode", "Engineer", "Sync that never loses a change.", 6, List.of("Integrity", "Growth")),
            goal("Run a 10K under 55 minutes", "Sharpen the Saw", "Three runs a week, one of them long.", 8,
                    List.of("Health", "Growth")),
            new DemoGoal("Call an old friend every week", "Friend", "", Optional.empty(), List.of("Family"),
                    GoalStatus.ACTIVE),
            new DemoGoal("Re-read The 7 Habits", "Sharpen the Saw", "One habit a week, with notes.", Optional.empty(),
                    List.of("Growth"), GoalStatus.ACHIEVED));

    static final List<DemoActivity> THIS_WEEK = List.of(
            activity("Weekly planning: big rocks first", "Sharpen the Saw", null, IMPORTANT_NOT_URGENT, MONDAY, true),
            activity("Fix the checkout outage", "Engineer", null, IMPORTANT_URGENT, MONDAY, true),
            activity("Design review for offline sync", "Engineer", "Ship offline mode", IMPORTANT_NOT_URGENT, TUESDAY, true),
            activity("Tempo run, 5K", "Sharpen the Saw", "Run a 10K under 55 minutes", IMPORTANT_NOT_URGENT, WEDNESDAY, true),
            activity("Call Sam", "Friend", "Call an old friend every week", IMPORTANT_NOT_URGENT, THURSDAY, false),
            activity("Date night", "Partner", null, IMPORTANT_NOT_URGENT, FRIDAY, false),
            activity("Bike practice in the park", "Parent", "Teach Mia to ride a bike", IMPORTANT_NOT_URGENT, SATURDAY, false),
            activity("Long run, 8K", "Sharpen the Saw", "Run a 10K under 55 minutes", IMPORTANT_NOT_URGENT, SUNDAY, false),
            activity("Shortlist anniversary destinations", "Partner", "Plan our anniversary trip", IMPORTANT_NOT_URGENT,
                    null, false),
            activity("Answer the vendor survey", "Engineer", null, NOT_IMPORTANT_URGENT, null, false));

    static final List<DemoActivity> LAST_WEEK = List.of(
            activity("Weekly planning: big rocks first", "Sharpen the Saw", null, IMPORTANT_NOT_URGENT, MONDAY, true),
            activity("Write the offline sync design doc", "Engineer", "Ship offline mode", IMPORTANT_NOT_URGENT, TUESDAY, true),
            activity("Easy run, 4K", "Sharpen the Saw", "Run a 10K under 55 minutes", IMPORTANT_NOT_URGENT, WEDNESDAY, true),
            activity("Family picnic", "Parent", null, IMPORTANT_NOT_URGENT, SATURDAY, true),
            activity("Long run, 7K", "Sharpen the Saw", "Run a 10K under 55 minutes", IMPORTANT_NOT_URGENT, SUNDAY, false),
            activity("Sit in on every status meeting", "Engineer", null, NOT_IMPORTANT_URGENT, THURSDAY, true));

    static final String LAST_WEEK_ACCOMPLISHMENTS =
            "Finished the offline sync design doc. Two of three runs. A picnic with no phones.";
    static final String LAST_WEEK_LESSONS =
            "Big rocks first works: the runs I put in the calendar on Monday happened. "
            + "The status meetings ate Thursday; next week I will send notes instead.";
    static final Set<RenewalDimension> LAST_WEEK_RENEWAL =
            Set.of(RenewalDimension.PHYSICAL, RenewalDimension.MENTAL, RenewalDimension.SOCIAL_EMOTIONAL);

    private DemoContent() {
    }

    private static DemoGoal goal(String title, String role, String description, int dueInWeeks, List<String> values) {
        return new DemoGoal(title, role, description, Optional.of(dueInWeeks), values, GoalStatus.ACTIVE);
    }

    private static DemoActivity activity(String title, String role, String goal, Quadrant quadrant, DayOfWeek day,
                                         boolean isCompleted) {
        return new DemoActivity(title, role, Optional.ofNullable(goal), quadrant, Optional.ofNullable(day), isCompleted);
    }
}
