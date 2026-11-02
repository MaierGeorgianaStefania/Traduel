package ro.traduel.service;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import ro.traduel.model.TranslationRevision;
import ro.traduel.model.TranslationSelection;
import ro.traduel.model.TranslationSession;
import ro.traduel.repository.TranslationHistoryRepository;

import java.util.NoSuchElementException;
import java.util.UUID;

@RequiredArgsConstructor
public final class PostEditingService {
    @NonNull
    private final TranslationHistoryRepository history;

    public TranslationRevision saveRevision(UUID sessionId, String editedText) {
        TranslationSession session = history.findById(sessionId)
                .orElseThrow(() -> new NoSuchElementException("Translation session not found."));
        TranslationSelection selection = session.getSelection()
                .orElseThrow(() -> new IllegalStateException("Select a translation before editing."));
        TranslationRevision revision = new TranslationRevision(selection, editedText);
        session.addRevision(revision);
        return revision;
    }
}
