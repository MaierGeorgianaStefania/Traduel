package ro.traduel.adapter;

import com.deepl.api.DeepLClient;
import com.deepl.api.DeepLException;
import ro.traduel.model.Language;
import ro.traduel.model.TranslationProvider;
import ro.traduel.model.TranslationRequest;
import ro.traduel.model.TranslationResult;

import java.io.IOException;

public final class DeepLAdapter implements TranslationAdapter {
    private final DeepLClient client;

    public DeepLAdapter(String apiKey) {
        client = new DeepLClient(apiKey);
    }

    @Override
    public TranslationProvider getProvider() {
        return TranslationProvider.DEEPL;
    }

    @Override
    public TranslationResult translate(TranslationRequest request) throws IOException, InterruptedException {
        Language target = request.getTargetLanguage();
        String targetCode = target == Language.ENGLISH ? "en-US" : target.getCode();
        try {
            String text = client.translateText(request.getText(), request.getSourceLanguage().getCode(),
                    targetCode).getText();
            return new TranslationResult(request, getProvider(), text);
        } catch (DeepLException e) {
            throw new IOException("DeepL nu a putut traduce textul.", e);
        }
    }
}
