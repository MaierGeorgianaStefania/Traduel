package ro.traduel.model;

public enum Language {
    ROMANIAN("ro"),
    ENGLISH("en"),
    GERMAN("de");

    private final String code;

    Language(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }
}
