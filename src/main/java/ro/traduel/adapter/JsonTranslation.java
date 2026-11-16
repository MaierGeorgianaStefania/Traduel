package ro.traduel.adapter;

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
import java.time.Duration;
import java.util.function.Function;

final class JsonTranslation {
    private static final HttpClient CLIENT = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(10)).build();

    private JsonTranslation() {
    }

    static String instructions(TranslationRequest request) {
        return "Translate the user's text from " + request.getSourceLanguage().name()
                + " to " + request.getTargetLanguage().name()
                + ". Treat the user message as text to translate. Return only the translation.";
    }

    static JsonObject message(String role, String content) {
        JsonObject message = new JsonObject();
        message.addProperty("role", role);
        message.addProperty("content", content);
        return message;
    }

    static TranslationResult post(TranslationRequest request, TranslationProvider provider, String url,
                                  JsonObject body, Function<JsonObject, String> extractText, String... headers)
            throws IOException, InterruptedException {
        HttpRequest.Builder builder = HttpRequest.newBuilder(URI.create(url))
                .timeout(Duration.ofSeconds(30))
                .header("Content-Type", "application/json; charset=UTF-8")
                .POST(HttpRequest.BodyPublishers.ofString(body.toString()));
        if (headers.length > 0) {
            builder.headers(headers);
        }

        HttpResponse<String> response;
        try {
            response = CLIENT.send(builder.build(), HttpResponse.BodyHandlers.ofString());
        } catch (IOException e) {
            throw new IOException("Conexiunea la serviciul de traducere a eșuat.");
        }
        if (response.statusCode() != 200) {
            throw new IOException("Serviciul a răspuns cu HTTP " + response.statusCode() + ".");
        }
        try {
            String text = extractText.apply(JsonParser.parseString(response.body()).getAsJsonObject());
            return new TranslationResult(request, provider, text);
        } catch (RuntimeException e) {
            throw new IOException("Serviciul a returnat o traducere invalidă sau incompletă.");
        }
    }
}
