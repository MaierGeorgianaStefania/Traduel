package ro.traduel.adapter;

import ro.traduel.model.TranslationProvider;
import ro.traduel.model.TranslationRequest;
import ro.traduel.model.TranslationResult;

public final class GoogleAdapter implements TranslationAdapter {
    @Override
    public TranslationProvider getProvider() {
        return TranslationProvider.GOOGLE;
    }

    @Override
    public TranslationResult translate(TranslationRequest request) {
        throw new UnsupportedOperationException();
    }
}
