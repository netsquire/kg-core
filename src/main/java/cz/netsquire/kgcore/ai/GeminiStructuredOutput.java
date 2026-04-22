package cz.netsquire.kgcore.ai;

import com.google.common.collect.ImmutableMap;
import com.google.genai.Client;
import com.google.genai.types.*;
import cz.netsquire.kgcore.util.DotenvLoader;
import org.springframework.stereotype.Service;

import java.io.File;

@Service
public class GeminiStructuredOutput {

    Client client;

    public GeminiStructuredOutput() {
        try {
            DotenvLoader.load(new File(".env"));
        } catch (Exception ignore) {
        }
        client = Client.builder()
                .apiKey(System.getProperty("GOOGLE_API_KEY"))
                .httpOptions(HttpOptions.builder()
                        .apiVersion("v1")
                        .build())
                .build();
    }

    public static void main(String[] args) {
        GeminiStructuredOutput example = new GeminiStructuredOutput();
//        example.askProperModels();
        GenerateContentResponse response = example.structuredOutput("cybersafety");
        System.out.println(response);
    }

    void askProperModels() {
        // List of models supporting structured output (as of June 2024)
        String[] modelsSupportingStructuredOutput = {
                "gemini-2.0-flash",
                "gemini-2.0-flash-001",
                "gemini-2.0-flash-lite-001",
                "gemini-2.0-flash-lite",
                "gemini-2.5-flash",
                "gemini-2.5-flash-lite",
                "gemini-2.5-pro",
                "gemini-3-flash-preview",
                "gemini-4-flash-preview"
        };
        var listModelConfig = ListModelsConfig.builder().build();
        client.models.list(listModelConfig).iterator()
                .forEachRemaining(model -> {
                    System.out.println("Model Name: " + model.name());
                    System.out.println("Supported Actions: " + model.supportedActions());
                    System.out.println("Token Limit: " + model.outputTokenLimit());
                    System.out.println("---");
                });
    }

    public GenerateContentResponse structuredOutput(String prompt) {
        String modelId = "gemini-2.5-flash-lite";

        String system_prompt ="""
              Your role is to identify context, connected concepts, related notions and links among them for the given concept.
              Every pair  of the concept/notion contains a unique integer ID and string value.
              Links contains of pairs of integer IDs of the connected concepts/notions.
              Respond with a JSON object that adheres to the following schema:
                     {
                     "context": "string",
                     "concepts": [{"integer":"string"}, {"integer":"string"}, ...],
                     "notions": [{"integer":"string"}, {"integer":"string"}, ...],
                     "links": [{"integer":"integer"}, {"integer":"integer"}, ...]
                     }
                     Limit the number of concepts, notions to 5 each and links up to 15.
                     --
                     User's input: (%s)
              """.formatted(prompt);

        GenerateContentResponse response;
        Schema structuredSchema = Schema.builder()
                .type(Type.Known.OBJECT)
                .properties(ImmutableMap.of(
                        "context", stringSchema(),
                        "concepts", arrayOfStringSchema(),
                        "notions", arrayOfStringSchema(),
                        "links", arrayOfLinksSchema()
                ))
                .required("context", "concepts", "notions", "links")
                .build();
        Schema rootSchema = Schema.builder()
                .type(Type.Known.ARRAY)
                .items(structuredSchema)
                .build();
        GenerateContentConfig config = GenerateContentConfig.builder()
                .candidateCount(1)
                .responseJsonSchema(rootSchema)          // ← keep schema to request structured JSON
                .build();
        System.out.println("---");

        response = client.models.generateContent(modelId, system_prompt, config);
        System.out.println("Structured JSON output:");
        System.out.println(response);
        return response;
    }

    private Schema arrayOfLinksSchema() {
        var node = Schema.builder()
                .type(Type.Known.INTEGER)
                .build();
        var link = Schema.builder()
                .type(Type.Known.OBJECT)
                .properties(ImmutableMap.of(
                        "from", node,
                        "to", node
                ))
                .required("from", "to")
                .build();
        return Schema.builder()
                .type(Type.Known.ARRAY)
                .items(link)
                .build();
    }

    private Schema stringSchema() {
        return Schema.builder()
                .type(Type.Known.STRING)
                .build();
    }

    private Schema arrayOfStringSchema() {
        return Schema.builder()
                .type(Type.Known.ARRAY)
                .items(Schema.builder().type(Type.Known.STRING).build())
                .build();
    }
}