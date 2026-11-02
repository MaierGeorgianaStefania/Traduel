package ro.traduel.model;

import lombok.Getter;

import java.util.Objects;

@Getter
public final class TranslationResult {
    private final TranslationRequest request;
    private final TranslationProvider provider;
    private final String translatedText;

    public TranslationResult(TranslationRequest request, TranslationProvider provider, String translatedText) {
        this.request = Objects.requireNonNull(request, "Request must not be null.");
        this.provider = Objects.requireNonNull(provider, "Provider must not be null.");
        Objects.requireNonNull(translatedText, "Translated text must not be null.");

        if (translatedText.trim().isEmpty()) {
            throw new IllegalArgumentException("Translated text must not be empty.");
        }

        this.translatedText = translatedText;
    }
}
