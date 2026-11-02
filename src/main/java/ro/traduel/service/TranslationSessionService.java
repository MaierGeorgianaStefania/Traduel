package ro.traduel.service;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import ro.traduel.model.TranslationProvider;
import ro.traduel.model.TranslationRequest;
import ro.traduel.model.TranslationResult;
import ro.traduel.model.TranslationSelection;
import ro.traduel.model.TranslationSession;
import ro.traduel.repository.TranslationHistoryRepository;

import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.UUID;

@RequiredArgsConstructor
public final class TranslationSessionService {
    @NonNull
    private final TranslationHistoryRepository history;

    public TranslationSession createSession(TranslationRequest request) {
        TranslationSession session = new TranslationSession(request);
        history.save(session);
        return session;
    }

    public void addResult(UUID sessionId, TranslationResult result) {
        getSession(sessionId).addResult(result);
    }

    public TranslationSelection selectResult(UUID sessionId, TranslationProvider provider, String reason) {
        Objects.requireNonNull(provider, "Provider must not be null.");
        TranslationSession session = getSession(sessionId);
        TranslationResult result = session.getResults().stream()
                .filter(candidate -> candidate.getProvider() == provider)
                .findFirst()
                .orElseThrow(() -> new NoSuchElementException("No result from the selected provider."));
        TranslationSelection selection = new TranslationSelection(result, reason);
        session.select(selection);
        return selection;
    }

    private TranslationSession getSession(UUID id) {
        return history.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Translation session not found."));
    }
}
