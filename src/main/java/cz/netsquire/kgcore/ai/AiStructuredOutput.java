package cz.netsquire.kgcore.ai;

import com.google.common.collect.ImmutableMap;
import com.google.genai.Client;
import com.google.genai.types.*;
import com.google.gson.Gson;
import cz.netsquire.kgcore.util.DotenvLoader;
import org.springframework.stereotype.Service;

import java.io.File;

@Service
public class AiStructuredOutput { // extract to Interface and implement in GeminiStructuredOutputService

    Client client;
    private final Gson gson = new Gson();
    private final GenerateContentConfig contentConfig = buildConceptSchema();

    public AiStructuredOutput() {
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
        GenerateContentResponse response = new AiStructuredOutput().output("car");
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

    private GenerateContentConfig buildConceptSchema() {
        Schema structuredSchema = Schema.builder()
                .type(Type.Known.OBJECT)
                .properties(ImmutableMap.of(
                        "context", stringSchema(),
                        "graph", auxLinksArraySchema()
                ))
                .required("context", "graph")
                .build();
        Schema rootSchema = Schema.builder()
                .type(Type.Known.ARRAY)
                .items(structuredSchema)
                .build();
        return GenerateContentConfig.builder()
                .candidateCount(1)
                .responseJsonSchema(rootSchema)
                .build();
    }

    public GenerateContentResponse output(String prompt) {
        String modelId = "gemini-2.5-flash-lite";
        String system_prompt = """
                Your role is to identify context, connected/related concepts
                and semantic links among them according their Cognitive Distance.
                Then combine found concepts and links to bound pairs, up to 5.
                
                Don't mention the given concept in the output, only related concepts and links among them.
                Respond with a JSON object that adheres to the following schema:
                       {
                       "context": "string",
                       "graph": [{"concept1":"link1"}, {"concept2":"link2"}, ...],
                       }
                       --
                       Given: (%s)
                """.formatted(prompt);
        return client.models.generateContent(modelId, system_prompt, contentConfig);
        // used tokens ???
    }

    private Schema auxLinksArraySchema() {
        var node = Schema.builder()
                .type(Type.Known.STRING)
                .build();
        var edge = Schema.builder()
                .type(Type.Known.STRING)
                .build();
        var link = Schema.builder()
                .type(Type.Known.OBJECT)
                .properties(ImmutableMap.of("node", node, "edge", edge))
                .required("node", "edge")
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
                .items(Schema.builder()
                        .type(Type.Known.STRING)
                        .build())
                .build();
    }
}
