package cz.netsquire.kgcore.chat;

import com.google.genai.Client;
import com.google.genai.types.GenerateContentResponse;
import org.springframework.stereotype.Service;

@Service
public class GeminiService {

    public static final String SYSTEM_SET =
            """
            You are a helpful, friendly, and witty AI assistant named Grok-Gemini.
            """;

    Client client = Client.builder().apiKey("GOOGLE_API_KEY").build();

    public GenerateContentResponse answer(String prompt) {
        return client.models.generateContent("gemini-2.0-flash", prompt, null);

    }
}