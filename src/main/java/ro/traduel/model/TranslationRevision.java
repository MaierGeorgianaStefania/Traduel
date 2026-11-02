package ro.traduel.model;

import lombok.Getter;

import java.time.LocalDateTime;
import java.util.Objects;

@Getter
public final class TranslationRevision {
    private final TranslationSelection selection;
    private final String editedText;
    private final LocalDateTime createdAt;

    public TranslationRevision(TranslationSelection selection, String editedText) {
        this.selection = Objects.requireNonNull(selection, "Selection must not be null.");
        Objects.requireNonNull(editedText, "Edited text must not be null.");
        if (editedText.isBlank()) {
            throw new IllegalArgumentException("Edited text must not be empty.");
        }
        this.editedText = editedText;
        this.createdAt = LocalDateTime.now();
    }
}
