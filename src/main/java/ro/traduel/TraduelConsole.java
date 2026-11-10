package ro.traduel;

import ro.traduel.adapter.DeepLAdapter;
import ro.traduel.adapter.MicrosoftAdapter;
import ro.traduel.adapter.MyMemoryAdapter;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class TraduelConsole {
    public static void main(String[] args) {
        List<TranslationAdapter> adapters = createAdapters();

        TranslationHistoryRepository history = new TranslationHistoryRepository();
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
                        translate(input, adapters, history);
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

    private static void translate(Scanner input, List<TranslationAdapter> adapters,
                                  TranslationHistoryRepository history) throws InterruptedException {
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
        List<TranslationResult> results = new ArrayList<>();
        for (TranslationAdapter adapter : adapters) {
            try {
                results.add(adapter.translate(request));
            } catch (IOException e) {
                System.out.println(adapter.getProvider() + ": " + e.getMessage());
            }
        }
        if (results.isEmpty()) {
            System.out.println("Niciun motor nu a returnat o traducere.");
            return;
        }

        TranslationSessionService sessions = new TranslationSessionService(history);
        PostEditingService editing = new PostEditingService(history);
        TranslationSession session = sessions.createSession(request);
        for (int index = 0; index < results.size(); index++) {
            TranslationResult result = results.get(index);
            sessions.addResult(session.getId(), result);
            System.out.println((index + 1) + ". " + result.getProvider() + ": " + result.getTranslatedText());
        }

        TranslationResult selected = readResult(input, results);
        if (selected == null) {
            return;
        }
        System.out.print("Motivul alegerii (opțional, Enter pentru a omite): ");
        String reason = input.hasNextLine() ? input.nextLine() : "";
        sessions.selectResult(session.getId(), selected.getProvider(), reason);

        System.out.print("Scrie versiunea ta sau apasă Enter: ");
        if (input.hasNextLine()) {
            String editedText = input.nextLine();
            if (!editedText.isBlank()) {
                TranslationRevision revision = editing.saveRevision(session.getId(), editedText);
                System.out.println("Versiunea ta: " + revision.getEditedText());
            }
        }
    }

    private static List<TranslationAdapter> createAdapters() {
        List<TranslationAdapter> adapters = new ArrayList<>();
        String deepLKey = System.getenv("DEEPL_API_KEY");
        if (deepLKey != null && !deepLKey.isBlank()) {
            adapters.add(new DeepLAdapter(deepLKey.strip()));
        }
        String microsoftKey = System.getenv("MICROSOFT_API_KEY");
        if (microsoftKey != null && !microsoftKey.isBlank()) {
            adapters.add(new MicrosoftAdapter(microsoftKey, System.getenv("MICROSOFT_REGION")));
        }
        adapters.add(new MyMemoryAdapter());
        return adapters;
    }

    private static TranslationResult readResult(Scanner input, List<TranslationResult> results) {
        System.out.print("Alege traducerea preferată (1-" + results.size() + "): ");
        while (input.hasNextLine()) {
            String choice = input.nextLine().strip();
            for (int index = 0; index < results.size(); index++) {
                if (choice.equals(String.valueOf(index + 1))) {
                    return results.get(index);
                }
            }
            System.out.print("Opțiune invalidă. Alege o traducere: ");
        }
        return null;
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
