package cz.netsquire.kgcore.util;

import com.google.genai.Client;
import com.google.genai.types.GenerateContentResponse;
import com.google.genai.types.Model;

public class VanillaAiCurl {
    public static void main(String[] args) {
        VanillaAiCurl curl = new VanillaAiCurl();
        GenerateContentResponse response = curl.generateContentResponse();
        System.out.println(response);
    }

//    public static void modelList() {
//        // Initialize the client (ensure GEMINI_API_KEY is set in environment variables)
//        Client client = Client.builder().build();
//
//        System.out.println("Available Models and Supported Methods:");
//
//        // Iterate through the list of models
//        for (Model model : client.models().list(null)) {
//            System.out.println("Model Name: " + model.name());
//            System.out.println("Supported Methods: " + model.supportedGenerationMethods());
//            System.out.println("-----------------------------------");
//        }
//    }

    GenerateContentResponse generateContentResponse() {
        GenerateContentResponse response;
        try (Client client = Client.builder().apiKey("AIzaSyBWxhUk1nYuMZHfCrrPoPpl-z1blLjqujI").build()) {

            response = client.models.generateContent(
                    "gpt-4.0-mini",
                    "Explain how Structured Output work, and how to use them in Java.",
                    null);
        }
        return response;
    }
}