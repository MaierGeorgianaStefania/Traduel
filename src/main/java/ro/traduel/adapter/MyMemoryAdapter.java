package ro.traduel.adapter;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import ro.traduel.model.TranslationProvider;
import ro.traduel.model.TranslationRequest;
import ro.traduel.model.TranslationResult;

import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;

public final class MyMemoryAdapter implements TranslationAdapter {
    private final HttpClient client;

    public MyMemoryAdapter() {
        this(HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(10)).build());
    }

    MyMemoryAdapter(HttpClient client) {
        this.client = client;
    }

    @Override
    public TranslationProvider getProvider() {
        return TranslationProvider.MYMEMORY;
    }

    @Override
    public TranslationResult translate(TranslationRequest request) throws IOException, InterruptedException {
        if (request.getText().getBytes(StandardCharsets.UTF_8).length > 500) {
            throw new IOException("MyMemory acceptă maximum 500 de bytes UTF-8 per text.");
        }
        String text = URLEncoder.encode(request.getText(), StandardCharsets.UTF_8);
        String pair = request.getSourceLanguage().getCode() + "|" + request.getTargetLanguage().getCode();
        HttpRequest httpRequest = HttpRequest.newBuilder()
                .uri(URI.create("https://api.mymemory.translated.net/get?q=" + text
                        + "&langpair=" + URLEncoder.encode(pair, StandardCharsets.UTF_8)))
                .timeout(Duration.ofSeconds(30))
                .GET()
                .build();
        HttpResponse<String> response = client.send(httpRequest,
                HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
        if (response.statusCode() != 200) {
            throw new IOException("MyMemory a răspuns cu HTTP " + response.statusCode() + ".");
        }

        JsonObject data;
        String translatedText;
        int status;
        try {
            data = JsonParser.parseString(response.body()).getAsJsonObject();
            status = data.get("responseStatus").getAsInt();
            if (status != 200) {
                throw new IOException("MyMemory nu a putut traduce textul. Cod " + status + ".");
            }
            translatedText = data.getAsJsonObject("responseData").get("translatedText").getAsString();
            return new TranslationResult(request, getProvider(), translatedText);
        } catch (RuntimeException e) {
            throw new IOException("Răspuns MyMemory invalid.");
        }
    }
}
