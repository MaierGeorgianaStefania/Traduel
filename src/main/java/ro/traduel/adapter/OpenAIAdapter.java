package ro.traduel.adapter;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import ro.traduel.model.TranslationProvider;
import ro.traduel.model.TranslationRequest;
import ro.traduel.model.TranslationResult;

import java.io.IOException;

public final class OpenAIAdapter implements TranslationAdapter {
    private final String apiKey;

    public OpenAIAdapter(String apiKey) {
        this.apiKey = apiKey.strip();
    }

    @Override
    public TranslationProvider getProvider() {
        return TranslationProvider.OPENAI;
    }

    @Override
    public TranslationResult translate(TranslationRequest request) throws IOException, InterruptedException {
        JsonObject body = new JsonObject();
        body.addProperty("model", "gpt-6-luna");
        body.addProperty("reasoning_effort", "none");
        body.addProperty("store", false);
        JsonArray messages = new JsonArray();
        messages.add(JsonTranslation.message("system", JsonTranslation.instructions(request)));
        messages.add(JsonTranslation.message("user", request.getText()));
        body.add("messages", messages);

        return JsonTranslation.post(request, getProvider(), "https://api.openai.com/v1/chat/completions",
                body, response -> {
                    JsonObject choice = response.getAsJsonArray("choices").get(0).getAsJsonObject();
                    if (!"stop".equals(choice.get("finish_reason").getAsString())) {
                        throw new IllegalStateException("Incomplete translation");
                    }
                    return choice.getAsJsonObject("message").get("content").getAsString();
                }, "Authorization", "Bearer " + apiKey);
    }
}
