package ro.traduel.adapter;

import ro.traduel.model.TranslationProvider;
import ro.traduel.model.TranslationRequest;
import ro.traduel.model.TranslationResult;

import java.io.IOException;

public interface TranslationAdapter {
    TranslationProvider getProvider();

    TranslationResult translate(TranslationRequest request) throws IOException, InterruptedException;
}
