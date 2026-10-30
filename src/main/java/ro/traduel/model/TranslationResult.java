package ro.traduel.model;

import java.util.Objects;

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

    public TranslationRequest getRequest() {
        return request;
    }

    public TranslationProvider getProvider() {
        return provider;
    }

    public String getTranslatedText() {
        return translatedText;
    }
}
