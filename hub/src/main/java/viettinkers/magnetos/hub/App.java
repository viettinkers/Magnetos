package viettinkers.magnetos.hub;

import edu.cmu.sphinx.api.Configuration;
import edu.cmu.sphinx.api.LiveSpeechRecognizer;


public class App {

    private static final String ACOUSTIC_MODEL =
        "resource:/edu/cmu/sphinx/models/en-us/en-us";
    private static final String DICTIONARY_PATH =
        "resource:/edu/cmu/sphinx/models/en-us/cmudict-en-us.dict";
    private static final String GRAMMAR_PATH =
        "resource:/grammar/";

    public static void main(String[] args) throws Exception {
        Configuration configuration = new Configuration();
        configuration.setAcousticModelPath(ACOUSTIC_MODEL);
        configuration.setDictionaryPath(DICTIONARY_PATH);
        configuration.setGrammarPath(GRAMMAR_PATH);
        configuration.setUseGrammar(true);
        configuration.setGrammarName("food.grxml");
        LiveSpeechRecognizer grxmlRecognizer =
            new LiveSpeechRecognizer(configuration);

        grxmlRecognizer.startRecognition(true);
        while (true) {
            System.out.println("Say a vegetable name:");

            String utterance = grxmlRecognizer.getResult().getHypothesis();
            System.out.println(utterance);
            
            if (utterance.contains("exit")) {
                grxmlRecognizer.stopRecognition();
                System.out.println("Bye bye");
                return;
            }
        }

    }
}
