package cz.netsquire.kgcore.ai;

import com.google.genai.Client;
import com.google.genai.types.GenerateContentResponse;
import org.springframework.stereotype.Service;

@Service
public class ChatService {

    private final Client client = Client.builder().apiKey(System.getProperty("GOOGLE_API_KEY")).build();

    public GenerateContentResponse askAi(String prompt) {
        return client.models.generateContent("gemini-flash-latest", prompt, null);
    }
}