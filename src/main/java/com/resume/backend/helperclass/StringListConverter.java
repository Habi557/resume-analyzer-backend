package com.resume.backend.helperclass;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import com.fasterxml.jackson.core.type.TypeReference;

import java.io.IOException;
import java.util.List;
@Converter
public class StringListConverter
        implements AttributeConverter<List<String>, String> {

    private static final ObjectMapper mapper = new ObjectMapper();

    @Override
    public String convertToDatabaseColumn(List<String> attribute) {
        try {
            // ✅ NULL or empty list → empty JSON array
            if (attribute == null || attribute.isEmpty()) {
                return "[]";
            }
            return mapper.writeValueAsString(attribute);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Conversion error", e);
        }
    }

    @Override
    public List<String> convertToEntityAttribute(String dbData) {
        try {
            // ✅ NULL / blank / "null" → empty list
            if (dbData == null || dbData.isBlank() || dbData.equalsIgnoreCase("null")) {
                return List.of();
            }
            return mapper.readValue(dbData, new TypeReference<List<String>>() {});
        } catch (IOException e) {
            throw new RuntimeException("Conversion error", e);
        }
    }
}
