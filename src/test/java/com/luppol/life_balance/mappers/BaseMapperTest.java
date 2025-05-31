package com.luppol.life_balance.mappers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class BaseMapperTest {

    private final BaseMapper mapper = new BaseMapper() {};

    @Test
    void patch_callsSetter_whenFieldPresent() {
        ObjectNode json = new ObjectMapper().createObjectNode();
        json.put("foo", "bar");
        final boolean[] called = {false};
        mapper.patch(json, "foo", "baz", v -> called[0] = true);
        assertTrue(called[0]);
    }

    @Test
    void patch_doesNotCallSetter_whenFieldAbsent() {
        ObjectNode json = new ObjectMapper().createObjectNode();
        final boolean[] called = {false};
        mapper.patch(json, "foo", "baz", v -> called[0] = true);
        assertFalse(called[0]);
    }
}
