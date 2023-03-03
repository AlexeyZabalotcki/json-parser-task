package ru.clevertec.zabalotcki.parser;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

class JsonParserTest {

    private String json;
    private Map<String, Object> expected;

    @BeforeEach
    void setUp() {
        json = "{\"age\":12,\"name\":\"Alex\"}";
        expected = new HashMap<>();
        expected.put("age", 12L);
        expected.put("name", "Alex");
    }

    @Test
    public void checkParseShouldReturnMap() {
        JsonParser parser = new JsonParser(json);

        HashMap<String, Object> actual = (HashMap<String, Object>) parser.parse();
        assertEquals(expected.get("age"), actual.get("age"));
        assertEquals(expected.get("name"), actual.get("name"));
    }
}
