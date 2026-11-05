package ro.traduel.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Language {
    ROMANIAN("ro", "Română"),
    ENGLISH("en", "Engleză"),
    GERMAN("de", "Germană");

    private final String code;
    private final String displayName;
}
