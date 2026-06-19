package com.resume.backend.helperclass;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class StringListConverterTest {

    private final StringListConverter converter = new StringListConverter();

    @Test
    void convertToDatabaseColumn_returnsEmptyJsonForNullOrEmptyList() {
        assertEquals("[]", converter.convertToDatabaseColumn(null));
        assertEquals("[]", converter.convertToDatabaseColumn(List.of()));
    }

    @Test
    void convertToDatabaseColumn_serializesList() {
        assertEquals("[\"Java\",\"Spring\"]", converter.convertToDatabaseColumn(List.of("Java", "Spring")));
    }

    @Test
    void convertToEntityAttribute_returnsEmptyListForNullBlankOrNullString() {
        assertEquals(List.of(), converter.convertToEntityAttribute(null));
        assertEquals(List.of(), converter.convertToEntityAttribute(" "));
        assertEquals(List.of(), converter.convertToEntityAttribute("null"));
    }

    @Test
    void convertToEntityAttribute_deserializesJsonArray() {
        assertEquals(List.of("Java", "Spring"), converter.convertToEntityAttribute("[\"Java\",\"Spring\"]"));
    }

    @Test
    void convertToEntityAttribute_throwsForInvalidJson() {
        assertThrows(RuntimeException.class, () -> converter.convertToEntityAttribute("not-json"));
    }
}
