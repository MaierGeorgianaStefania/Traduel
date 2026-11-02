package ro.traduel.model;

import lombok.Getter;

import java.time.LocalDateTime;
import java.util.Objects;

@Getter
public final class TranslationSelection {
    private final TranslationResult result;
    private final String reason;
    private final LocalDateTime selectedAt;

    public TranslationSelection(TranslationResult result, String reason) {
        this.result = Objects.requireNonNull(result, "Result must not be null.");
        this.reason = reason == null ? "" : reason.trim();
        this.selectedAt = LocalDateTime.now();
    }
}
