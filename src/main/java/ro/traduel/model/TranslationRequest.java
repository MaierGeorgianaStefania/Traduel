package ro.traduel.model;

import java.util.Objects;

public final class TranslationRequest {
    private final String text;
    private final Language sourceLanguage;
    private final Language targetLanguage;

    public TranslationRequest(String text, Language sourceLanguage, Language targetLanguage) {
        Objects.requireNonNull(text, "Text must not be null.");
        this.sourceLanguage = Objects.requireNonNull(sourceLanguage, "Source language must not be null.");
        this.targetLanguage = Objects.requireNonNull(targetLanguage, "Target language must not be null.");

        if (text.trim().isEmpty()) {
            throw new IllegalArgumentException("Text must not be empty.");
        }
        if (sourceLanguage == targetLanguage) {
            throw new IllegalArgumentException("Source and target languages must be different.");
        }

        this.text = text;
    }

    public String getText() {
        return text;
    }

    public Language getSourceLanguage() {
        return sourceLanguage;
    }

    public Language getTargetLanguage() {
        return targetLanguage;
    }
}
