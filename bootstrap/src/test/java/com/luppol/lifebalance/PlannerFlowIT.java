package com.luppol.lifebalance;

import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.request.RequestPostProcessor;

import java.util.UUID;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class PlannerFlowIT extends IntegrationTest {
    private static final String WEEK = "/api/weeks/2026-09-21";

    private final RequestPostProcessor member = person("member-" + UUID.randomUUID(), "USER");
    private final RequestPostProcessor stranger = person("stranger-" + UUID.randomUUID(), "USER");

    @Test
    void newMember_startsWithSharpenTheSaw() throws Exception {
        mvc.perform(get("/api/roles").with(member))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].kind").value("SHARPEN_THE_SAW"));
        mvc.perform(get("/api/mission").with(member)).andExpect(status().isNotFound());
    }

    @Test
    void member_plansAWeekRootedInRolesAndValues() throws Exception {
        String family = create("/api/values", "{\"name\":\"Family\"}");
        String parent = create("/api/roles", "{\"name\":\"Parent\"}");
        String goal = create("/api/goals", "{\"roleId\":\"%s\",\"title\":\"Teach cycling\",\"valueIds\":[\"%s\"]}"
                .formatted(parent, family));
        String activity = create(WEEK + "/activities", """
                {"roleId":"%s","goalId":"%s","title":"Bike practice","quadrant":"IMPORTANT_NOT_URGENT",
                 "scheduledOn":"2026-09-26"}""".formatted(parent, goal));

        mvc.perform(put("/api/activities/{id}/completion", activity).with(member)).andExpect(status().isOk());
        mvc.perform(get(WEEK + "/scorecard").with(member))
                .andExpect(jsonPath("$.bigRocks.planned").value(1))
                .andExpect(jsonPath("$.bigRocks.completed").value(1))
                .andExpect(jsonPath("$.byRole[0].roleId").value(parent));
        mvc.perform(put(WEEK + "/review").with(member).contentType(MediaType.APPLICATION_JSON)
                        .content("{\"accomplishments\":\"Mia rode alone\",\"renewedDimensions\":[\"PHYSICAL\"]}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.renewedDimensions[0]").value("PHYSICAL"));
    }

    @Test
    void member_ranksValuesAndCannotDuplicateThem() throws Exception {
        String integrity = create("/api/values", "{\"name\":\"Integrity\"}");
        String growth = create("/api/values", "{\"name\":\"Growth\"}");

        mvc.perform(put("/api/values/order").with(member).contentType(MediaType.APPLICATION_JSON)
                .content("{\"ids\":[\"%s\",\"%s\"]}".formatted(growth, integrity))).andExpect(status().isNoContent());
        mvc.perform(get("/api/values").with(member))
                .andExpect(jsonPath("$[0].name").value("Growth"))
                .andExpect(jsonPath("$[1].name").value("Integrity"));
        mvc.perform(post("/api/values").with(member).contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\":\"integrity\"}")).andExpect(status().isConflict());
    }

    @Test
    void roles_inUseOrBuiltIn_cannotBeRemoved() throws Exception {
        String parent = create("/api/roles", "{\"name\":\"Parent\"}");
        create("/api/goals", "{\"roleId\":\"%s\",\"title\":\"Teach cycling\"}".formatted(parent));
        String saw = JsonPath.read(mvc.perform(get("/api/roles").with(member)).andReturn().getResponse()
                .getContentAsString(), "$[0].id");

        mvc.perform(delete("/api/roles/{id}", parent).with(member)).andExpect(status().isConflict());
        mvc.perform(delete("/api/roles/{id}", saw).with(member)).andExpect(status().isConflict());
    }

    @Test
    void strangers_cannotReachSomeoneElsesPlanner() throws Exception {
        String parent = create("/api/roles", "{\"name\":\"Parent\"}");
        String goal = create("/api/goals", "{\"roleId\":\"%s\",\"title\":\"Teach cycling\"}".formatted(parent));

        mvc.perform(get("/api/goals/{id}", goal).with(stranger)).andExpect(status().isNotFound());
        mvc.perform(put("/api/goals/{id}", goal).with(stranger).contentType(MediaType.APPLICATION_JSON)
                .content("{\"roleId\":\"%s\",\"title\":\"Mine\"}".formatted(parent))).andExpect(status().isNotFound());
        mvc.perform(delete("/api/goals/{id}", goal).with(stranger)).andExpect(status().isNotFound());
        mvc.perform(post("/api/goals").with(stranger).contentType(MediaType.APPLICATION_JSON)
                        .content("{\"roleId\":\"%s\",\"title\":\"Borrowed\"}".formatted(parent)))
                .andExpect(status().isUnprocessableContent());
        mvc.perform(get("/api/goals").with(stranger)).andExpect(jsonPath("$", hasSize(0)));
        mvc.perform(get("/api/goals/{id}", goal).with(member)).andExpect(jsonPath("$.title").value("Teach cycling"));
    }

    @Test
    void activities_mustStayInsideTheirWeek() throws Exception {
        String parent = create("/api/roles", "{\"name\":\"Parent\"}");

        mvc.perform(post(WEEK + "/activities").with(member).contentType(MediaType.APPLICATION_JSON)
                        .content("{\"roleId\":\"%s\",\"title\":\"Late\",\"quadrant\":\"IMPORTANT_URGENT\",\"scheduledOn\":\"2026-09-28\"}"
                                .formatted(parent)))
                .andExpect(status().isUnprocessableContent())
                .andExpect(jsonPath("$.detail").value("2026-09-28 is outside the week starting 2026-09-21"));
    }

    private String create(String path, String body) throws Exception {
        String response = mvc.perform(post(path).with(member).contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        return JsonPath.read(response, "$.id");
    }
}
