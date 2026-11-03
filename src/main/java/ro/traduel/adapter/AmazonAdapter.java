package ro.traduel.adapter;

import ro.traduel.model.TranslationProvider;
import ro.traduel.model.TranslationRequest;
import ro.traduel.model.TranslationResult;

public final class AmazonAdapter implements TranslationAdapter {
    @Override
    public TranslationProvider getProvider() {
        return TranslationProvider.AMAZON;
    }

    @Override
    public TranslationResult translate(TranslationRequest request) {
        throw new UnsupportedOperationException();
    }
}
