package ro.traduel;

import ro.traduel.adapter.DeepLAdapter;
import ro.traduel.adapter.TranslationAdapter;
import ro.traduel.model.Language;
import ro.traduel.model.TranslationRequest;
import ro.traduel.model.TranslationResult;
import ro.traduel.model.TranslationRevision;
import ro.traduel.model.TranslationSession;
import ro.traduel.repository.TranslationHistoryRepository;
import ro.traduel.service.PostEditingService;
import ro.traduel.service.TranslationSessionService;

import java.io.IOException;
import java.util.List;
import java.util.Scanner;

public class TraduelConsole {
    public static void main(String[] args) {
        String apiKey = System.getenv("DEEPL_API_KEY");
        if (apiKey == null || apiKey.isBlank()) {
            System.out.println("Configurează variabila de mediu DEEPL_API_KEY.");
            return;
        }

        TranslationHistoryRepository history = new TranslationHistoryRepository();
        TranslationAdapter adapter = new DeepLAdapter(apiKey.strip());
        try (Scanner input = new Scanner(System.in)) {
            while (true) {
                System.out.println("\n1. Traducere nouă\n2. Istoric\n0. Ieșire");
                System.out.print("Alege o opțiune: ");
                if (!input.hasNextLine()) {
                    return;
                }
                String choice = input.nextLine().strip();
                switch (choice) {
                    case "1" -> {
                        try {
                            translate(input, adapter, history);
                        } catch (IOException e) {
                            System.out.println(e.getMessage() + " Verifică cheia și conexiunea.");
                        }
                    }
                    case "2" -> showHistory(history);
                    case "0" -> {
                        return;
                    }
                    default -> System.out.println("Opțiune invalidă.");
                }
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.out.println("Traducerea a fost întreruptă.");
        }
    }

    private static void translate(Scanner input, TranslationAdapter adapter,
                                  TranslationHistoryRepository history) throws IOException, InterruptedException {
        Language sourceLanguage = readLanguage(input, "Alege limba sursă");
        if (sourceLanguage == null) {
            return;
        }

        Language targetLanguage;
        do {
            targetLanguage = readLanguage(input, "Alege limba țintă");
            if (targetLanguage == null) {
                return;
            }
            if (targetLanguage == sourceLanguage) {
                System.out.println("Alege o limbă țintă diferită de limba sursă.");
            }
        } while (targetLanguage == sourceLanguage);

        System.out.print("Introdu textul: ");
        if (!input.hasNextLine()) {
            return;
        }
        String text = input.nextLine();
        if (text.isBlank()) {
            System.out.println("Introdu un text pentru traducere.");
            return;
        }

        TranslationRequest request = new TranslationRequest(text, sourceLanguage, targetLanguage);
        TranslationResult result = adapter.translate(request);

        TranslationSessionService sessions = new TranslationSessionService(history);
        PostEditingService editing = new PostEditingService(history);
        TranslationSession session = sessions.createSession(request);
        sessions.addResult(session.getId(), result);

        System.out.println("Traducere DeepL: " + result.getTranslatedText());
        System.out.print("Motivul alegerii (opțional, Enter pentru a omite): ");
        String reason = input.hasNextLine() ? input.nextLine() : "";
        sessions.selectResult(session.getId(), result.getProvider(), reason);

        System.out.print("Scrie versiunea ta sau apasă Enter: ");
        if (input.hasNextLine()) {
            String editedText = input.nextLine();
            if (!editedText.isBlank()) {
                TranslationRevision revision = editing.saveRevision(session.getId(), editedText);
                System.out.println("Versiunea ta: " + revision.getEditedText());
            }
        }
    }

    private static void showHistory(TranslationHistoryRepository history) {
        List<TranslationSession> sessions = history.findAll();
        if (sessions.isEmpty()) {
            System.out.println("Istoricul este gol.");
            return;
        }
        for (int index = 0; index < sessions.size(); index++) {
            TranslationSession session = sessions.get(index);
            TranslationRequest request = session.getRequest();
            System.out.println("\n" + (index + 1) + ". " + request.getSourceLanguage().getDisplayName()
                    + " → " + request.getTargetLanguage().getDisplayName());
            System.out.println("Text original: " + request.getText());
            for (TranslationResult result : session.getResults()) {
                System.out.println(result.getProvider() + ": " + result.getTranslatedText());
            }
            session.getSelection()
                    .filter(selection -> !selection.getReason().isBlank())
                    .ifPresent(selection -> System.out.println("Motivul alegerii: " + selection.getReason()));
            for (TranslationRevision revision : session.getRevisions()) {
                System.out.println("Versiunea ta: " + revision.getEditedText());
            }
        }
    }

    private static Language readLanguage(Scanner input, String prompt) {
        Language[] languages = Language.values();
        for (int index = 0; index < languages.length; index++) {
            System.out.println((index + 1) + ". " + languages[index].getDisplayName());
        }
        System.out.print(prompt + ": ");
        while (input.hasNextLine()) {
            String choice = input.nextLine().strip();
            for (int index = 0; index < languages.length; index++) {
                if (choice.equals(String.valueOf(index + 1))) {
                    return languages[index];
                }
            }
            System.out.print("Opțiune invalidă. " + prompt + ": ");
        }
        return null;
    }
}
