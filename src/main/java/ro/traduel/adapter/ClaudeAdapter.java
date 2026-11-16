package ro.traduel.adapter;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import ro.traduel.model.TranslationProvider;
import ro.traduel.model.TranslationRequest;
import ro.traduel.model.TranslationResult;

import java.io.IOException;

public final class ClaudeAdapter implements TranslationAdapter {
    private final String apiKey;

    public ClaudeAdapter(String apiKey) {
        this.apiKey = apiKey.strip();
    }

    @Override
    public TranslationProvider getProvider() {
        return TranslationProvider.CLAUDE;
    }

    @Override
    public TranslationResult translate(TranslationRequest request) throws IOException, InterruptedException {
        JsonObject body = new JsonObject();
        body.addProperty("model", "claude-haiku-4-5-20251001");
        body.addProperty("max_tokens", 4096);
        body.addProperty("system", JsonTranslation.instructions(request));
        JsonArray messages = new JsonArray();
        messages.add(JsonTranslation.message("user", request.getText()));
        body.add("messages", messages);

        return JsonTranslation.post(request, getProvider(), "https://api.anthropic.com/v1/messages",
                body, response -> {
                    if (!"end_turn".equals(response.get("stop_reason").getAsString())) {
                        throw new IllegalStateException("Incomplete translation");
                    }
                    StringBuilder translation = new StringBuilder();
                    for (var item : response.getAsJsonArray("content")) {
                        JsonObject block = item.getAsJsonObject();
                        if ("text".equals(block.get("type").getAsString())) {
                            translation.append(block.get("text").getAsString());
                        }
                    }
                    return translation.toString();
                }, "x-api-key", apiKey, "anthropic-version", "2023-06-01");
    }
}
