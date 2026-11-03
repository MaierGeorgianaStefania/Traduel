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
import java.util.Scanner;

public class TraduelConsole {
    public static void main(String[] args) {
        String apiKey = System.getenv("DEEPL_API_KEY");
        if (apiKey == null || apiKey.isBlank()) {
            System.out.println("Configurează variabila de mediu DEEPL_API_KEY.");
            return;
        }

        try (Scanner input = new Scanner(System.in)) {
            System.out.print("Text în engleză: ");
            if (!input.hasNextLine()) {
                return;
            }
            String text = input.nextLine();
            if (text.isBlank()) {
                System.out.println("Introdu un text pentru traducere.");
                return;
            }

            TranslationAdapter adapter = new DeepLAdapter(apiKey.strip());
            TranslationRequest request = new TranslationRequest(text, Language.ENGLISH, Language.ROMANIAN);
            TranslationResult result = adapter.translate(request);

            TranslationHistoryRepository history = new TranslationHistoryRepository();
            TranslationSessionService sessions = new TranslationSessionService(history);
            PostEditingService editing = new PostEditingService(history);
            TranslationSession session = sessions.createSession(request);
            sessions.addResult(session.getId(), result);
            sessions.selectResult(session.getId(), result.getProvider(), "");

            System.out.println("Traducere DeepL: " + result.getTranslatedText());
            System.out.print("Scrie versiunea ta sau apasă Enter: ");
            if (input.hasNextLine()) {
                String editedText = input.nextLine();
                if (!editedText.isBlank()) {
                    TranslationRevision revision = editing.saveRevision(session.getId(), editedText);
                    System.out.println("Versiunea ta: " + revision.getEditedText());
                }
            }
        } catch (IOException e) {
            System.out.println(e.getMessage() + " Verifică cheia și conexiunea.");
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.out.println("Traducerea a fost întreruptă.");
        }
    }
}
