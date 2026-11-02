package ro.traduel.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Language {
    ROMANIAN("ro"),
    ENGLISH("en"),
    GERMAN("de");

    private final String code;
}
