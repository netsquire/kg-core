package cz.netsquire.kgcore.util;

import com.google.genai.Client;
import com.google.genai.types.GenerateContentResponse;

public class VanillaAiCurl {
    public static void main(String[] args) {

        GenerateContentResponse response;

        try (Client client = Client.builder().apiKey("AIzaSyBLA3I4kEb9ngM31G7fVZuzmt79vasvcN4").build()) {
            response = client.models.generateContent(
                    "gemini-3-flash-preview",
                    "Explain how Gemini API keys work, and how to use them in Java.",
                    null);
        }

        System.out.println(response.text());
    }
}