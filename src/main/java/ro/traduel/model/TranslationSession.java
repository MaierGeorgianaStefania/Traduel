package ro.traduel.model;

import lombok.AccessLevel;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

@Getter
public final class TranslationSession {
    private final UUID id;
    private final TranslationRequest request;
    private final LocalDateTime createdAt;
    @Getter(AccessLevel.NONE)
    private final List<TranslationResult> results = new ArrayList<>();
    @Getter(AccessLevel.NONE)
    private final List<TranslationRevision> revisions = new ArrayList<>();
    @Getter(AccessLevel.NONE)
    private TranslationSelection selection;

    public TranslationSession(TranslationRequest request) {
        this.request = Objects.requireNonNull(request, "Request must not be null.");
        this.id = UUID.randomUUID();
        this.createdAt = LocalDateTime.now();
    }

    public void addResult(TranslationResult result) {
        Objects.requireNonNull(result, "Result must not be null.");
        TranslationRequest resultRequest = result.getRequest();
        if (!request.getText().equals(resultRequest.getText())
                || request.getSourceLanguage() != resultRequest.getSourceLanguage()
                || request.getTargetLanguage() != resultRequest.getTargetLanguage()) {
            throw new IllegalArgumentException("Result does not match the session request.");
        }
        if (results.stream().anyMatch(existing -> existing.getProvider() == result.getProvider())) {
            throw new IllegalArgumentException("A result from this provider already exists.");
        }
        results.add(result);
    }

    public void select(TranslationSelection selection) {
        Objects.requireNonNull(selection, "Selection must not be null.");
        if (!results.contains(selection.getResult())) {
            throw new IllegalArgumentException("Selected result must belong to this session.");
        }
        this.selection = selection;
    }

    public void addRevision(TranslationRevision revision) {
        Objects.requireNonNull(revision, "Revision must not be null.");
        if (selection == null || revision.getSelection() != selection) {
            throw new IllegalArgumentException("Revision must refer to the current selection.");
        }
        revisions.add(revision);
    }

    public List<TranslationResult> getResults() {
        return List.copyOf(results);
    }

    public Optional<TranslationSelection> getSelection() {
        return Optional.ofNullable(selection);
    }

    public List<TranslationRevision> getRevisions() {
        return List.copyOf(revisions);
    }
}
