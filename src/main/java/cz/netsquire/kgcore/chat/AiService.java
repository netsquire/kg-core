package cz.netsquire.kgcore.chat;

import com.google.genai.Client;
import com.google.genai.types.GenerateContentResponse;
import org.springframework.stereotype.Service;

@Service
public class AiService {

    public final String SYSTEM_SET = "You are a helpful, friendly, and witty AI assistant named Grok-Gemini.";

    public final String API_KEY = "AIzaSyBWxhUk1nYuMZHfCrrPoPpl-z1blLjqujI";
    private final Client client = Client.builder().apiKey(API_KEY).build();

    public GenerateContentResponse askAi(String prompt) {
        return client.models.generateContent("gemini-flash-latest", prompt, null);
    }
}