package ru.job4j.grabber.utils;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.*;

class HabrCareerDateTimeParserTest {

    @Test
    void parse() {
        HabrCareerDateTimeParser parser = new HabrCareerDateTimeParser();
        LocalDateTime result = parser.parse("2024-04-26T18:27:36");
        LocalDateTime expected = LocalDateTime.of(2024, 4, 26, 18, 27, 36);
        assertThat(expected).isEqualTo(result);
    }
}