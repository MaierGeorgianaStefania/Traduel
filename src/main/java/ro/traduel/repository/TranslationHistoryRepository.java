package ro.traduel.repository;

import lombok.NoArgsConstructor;

import ro.traduel.model.TranslationSession;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

@NoArgsConstructor
public final class TranslationHistoryRepository {
    private final Map<UUID, TranslationSession> sessions = new LinkedHashMap<>();

    public void save(TranslationSession session) {
        Objects.requireNonNull(session, "Session must not be null.");
        sessions.put(session.getId(), session);
    }

    public Optional<TranslationSession> findById(UUID id) {
        Objects.requireNonNull(id, "Session ID must not be null.");
        return Optional.ofNullable(sessions.get(id));
    }

    public List<TranslationSession> findAll() {
        return List.copyOf(sessions.values());
    }
}
