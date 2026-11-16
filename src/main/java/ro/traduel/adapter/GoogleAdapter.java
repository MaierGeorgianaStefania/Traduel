package ro.traduel.adapter;

import com.google.gson.JsonObject;
import org.apache.commons.text.StringEscapeUtils;
import ro.traduel.model.TranslationProvider;
import ro.traduel.model.TranslationRequest;
import ro.traduel.model.TranslationResult;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

public final class GoogleAdapter implements TranslationAdapter {
    private final String apiKey;

    public GoogleAdapter(String apiKey) {
        this.apiKey = apiKey.strip();
    }

    @Override
    public TranslationProvider getProvider() {
        return TranslationProvider.GOOGLE;
    }

    @Override
    public TranslationResult translate(TranslationRequest request) throws IOException, InterruptedException {
        JsonObject body = new JsonObject();
        body.addProperty("q", request.getText());
        body.addProperty("source", request.getSourceLanguage().getCode());
        body.addProperty("target", request.getTargetLanguage().getCode());
        body.addProperty("format", "text");

        String key = URLEncoder.encode(apiKey, StandardCharsets.UTF_8);
        return JsonTranslation.post(request, getProvider(),
                "https://translation.googleapis.com/language/translate/v2?key=" + key,
                body, response -> StringEscapeUtils.unescapeHtml4(response
                        .getAsJsonObject("data").getAsJsonArray("translations")
                        .get(0).getAsJsonObject().get("translatedText").getAsString()));
    }
}
