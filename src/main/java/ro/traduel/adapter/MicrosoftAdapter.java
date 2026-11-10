package ro.traduel.adapter;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import ro.traduel.model.TranslationProvider;
import ro.traduel.model.TranslationRequest;
import ro.traduel.model.TranslationResult;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;

public final class MicrosoftAdapter implements TranslationAdapter {
    private final String apiKey;
    private final String region;
    private final HttpClient client;

    public MicrosoftAdapter(String apiKey, String region) {
        this(apiKey, region, HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(10)).build());
    }

    MicrosoftAdapter(String apiKey, String region, HttpClient client) {
        if (apiKey == null || apiKey.isBlank()) {
            throw new IllegalArgumentException("Microsoft API key is required.");
        }
        this.apiKey = apiKey.strip();
        this.region = region == null ? "" : region.strip();
        this.client = client;
    }

    @Override
    public TranslationProvider getProvider() {
        return TranslationProvider.MICROSOFT;
    }

    @Override
    public TranslationResult translate(TranslationRequest request) throws IOException, InterruptedException {
        JsonObject item = new JsonObject();
        item.addProperty("Text", request.getText());
        JsonArray body = new JsonArray();
        body.add(item);

        HttpRequest.Builder builder = HttpRequest.newBuilder()
                .uri(URI.create("https://api.cognitive.microsofttranslator.com/translate?api-version=3.0"
                        + "&from=" + request.getSourceLanguage().getCode()
                        + "&to=" + request.getTargetLanguage().getCode()))
                .timeout(Duration.ofSeconds(30))
                .header("Ocp-Apim-Subscription-Key", apiKey)
                .header("Content-Type", "application/json; charset=UTF-8")
                .POST(HttpRequest.BodyPublishers.ofString(body.toString(), StandardCharsets.UTF_8));
        if (!region.isBlank()) {
            builder.header("Ocp-Apim-Subscription-Region", region);
        }
        HttpResponse<String> response = client.send(builder.build(),
                HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
        if (response.statusCode() != 200) {
            throw new IOException("Microsoft a răspuns cu HTTP " + response.statusCode() + ".");
        }
        try {
            String text = JsonParser.parseString(response.body()).getAsJsonArray()
                    .get(0).getAsJsonObject().getAsJsonArray("translations")
                    .get(0).getAsJsonObject().get("text").getAsString();
            return new TranslationResult(request, getProvider(), text);
        } catch (RuntimeException e) {
            throw new IOException("Răspuns Microsoft invalid.");
        }
    }
}
