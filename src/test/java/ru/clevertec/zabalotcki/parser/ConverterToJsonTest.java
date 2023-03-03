package ru.clevertec.zabalotcki.parser;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ConverterToJsonTest {

    @Test
    void toJson() {
        Person person = new Person(12, "Bob", "address");
        String expected = "{\"age\":12,\"name\":\"Bob\",\"address\":\"address\"}";
        String actual = ConverterToJson.toJson(person);

        assertEquals(expected, actual);
    }
}
