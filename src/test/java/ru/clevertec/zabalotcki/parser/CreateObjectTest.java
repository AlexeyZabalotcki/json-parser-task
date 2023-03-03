package ru.clevertec.zabalotcki.parser;

import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CreateObjectTest {

    @Test
    void testCreateObjectFromMap() throws Exception {
        Map<String, Object> map = new HashMap<>();
        map.put("name", "Alice");
        map.put("age", 30);
        map.put("isAlive", true);

        Person person = CreateObject.createObjectFromMap(map, Person.class);

        assertEquals("Alice", person.getName());
        assertEquals(30, person.getAge());
        assertTrue(person.isAlive());
    }
}
