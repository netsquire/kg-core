package cz.netsquire.kgcore.ai;

import com.google.common.collect.ImmutableMap;
import com.google.genai.Client;
import com.google.genai.types.*;
import cz.netsquire.kgcore.util.DotenvLoader;
import org.springframework.stereotype.Service;

import java.io.File;

@Service
public class AiStructuredService {

    final String MODEL_ID = "gemini-2.5-flash-lite";

    // Don't mention the given concept in the output, only related concepts and links among them. (1)
    // If case found concepts have strict hierarchical relationship, use "is a" link.
    //                If case found concepts have strict part-whole relationship, use "part of" link.
    //                If case found concepts have strict cause-effect relationship, use "causes" link.
    //                If case found concepts have strict temporal relationship, use "before/after" link. 
    //                If case found concepts have strict spatial relationship, use "located in" link. 
    //                If case found concepts have strict functional relationship, use "used for" link. 
    //                If case found concepts have strict similarity relationship, use "similar to" link. 
    //                Otherwise, use "related to" link.
    //    If the given concept is too broad, you can narrow it down by identifying more specific sub-concepts.
    //    If the given concept is too narrow, you can broaden it by identifying more general super-concepts.
    //    If the given concept is too vague, you can clarify it by identifying more specific concepts or by providing additional context.
    //    Respond with a JSON object that adheres to the following schema:
    //                       {
    //                       "contexts": ["string", ...],
    //                       "graph": [{"concept1":"link1"}, {"concept2":"link2"}, ...],
    //                       }
    //                    where all {id, concept_id, context_id} are unique integers among contexts, links and concepts,
    //                   and "link" is a string describing the relationship between two concepts in {concept_id, link, concept_id, weight},
    //                   and "weight" is a float number between 0 and 1 representing the strength of the relationship.

    final String STRUCTURED_PROMPT = """
            Your role is to discover knowledge graph around given concept identifying its possible contexts,
            connected/related concepts and semantic links among them.
            Then combine found concepts and links to bound pairs, up to 5.
            Links have weight according Cognitive Distance between adjacent concepts, in float 0-1.
            If found concepts also have mutual relationship strong enough, provide also links among them.
            Context is a broader area, domain or field, or a specific situation, scenario or use case where the concept+link(s) are relevant.
            
            Format the output using dense JSON arrays. Use an array of positional values (tuples) for each entry instead of key-value pairs to minimize token usage.
            
            Schema Mapping:
            contexts: [id, name]
            concepts: [concept_id, label, context_id]
            links: [source_id, relation, target_id, weight]
            --
            Given: (%s)
            """;
    
    public static void main(String[] args) {
        GenerateContentResponse response = new AiStructuredService().output("car");
        System.out.println(response);
    }

    Client client;

    private final GenerateContentConfig contentConfig = buildConceptSchema();

    public AiStructuredService() {
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
        //  {
        //  "contexts": [{"id_1":"context1"}, {"id_2":"context2"}, ...],
        //  "concepts": [{"id_1","concept1","id_of_context"}, {"id_2":"concept2","id_of_context"}, ...],
        //  "links": [{"id_1", "link1", "id_2", "weight_1"}, {"id_3", "link2", "id_4", "weight_2"}, ...]
        //  }
        Schema contextSchema = Schema.builder()
                .type(Type.Known.OBJECT)
                .properties(ImmutableMap.of(
                        "id", intSchema(),
                        "context", stringSchema()
                ))
                .required("id", "context")
                .build();
        
        Schema conceptSchema = Schema.builder()
                .type(Type.Known.OBJECT)
                .properties(ImmutableMap.of(
                        "id", intSchema(),
                        "concept", stringSchema(),
                        "contextId", intSchema()
                ))
                .required("id", "concept", "contextId")
                .build();
        
        Schema linkSchema = Schema.builder()
                .type(Type.Known.OBJECT)
                .properties(ImmutableMap.of(
                        "id1", intSchema(),
                        "link", stringSchema(),
                        "id2", intSchema(),
                        "weight", numberSchema()
                ))
                .required("id1", "link", "id2", "weight")
                .build();
        var schema = Schema.builder()
                .type(Type.Known.OBJECT)
                .properties(ImmutableMap.of(
                        "contexts", Schema.builder().type(Type.Known.ARRAY).items(contextSchema).build(),
                        "concepts", Schema.builder().type(Type.Known.ARRAY).items(conceptSchema).build(),
                        "links", Schema.builder().type(Type.Known.ARRAY).items(linkSchema).build()
                ))
                .required("contexts", "concepts", "links")
                .build();
        return GenerateContentConfig.builder()
                .candidateCount(1)
                .responseJsonSchema(schema)
                .build();
    }

    private GenerateContentConfig buildConceptSchema0() {
        Schema structuredSchema = Schema.builder()
                .type(Type.Known.OBJECT)
                .properties(ImmutableMap.of(
                        "contexts", arrayOfStringSchema(),
                        "graph", auxLinksArraySchema()
                ))
                .required("contexts", "graph")
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
        return client.models.generateContent(MODEL_ID, 
                STRUCTURED_PROMPT.formatted(prompt), 
                contentConfig);
        // number of used tokens ???
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

    private Schema numberSchema() {
        return Schema.builder()
                .type(Type.Known.NUMBER)
                .build();
    }

    private Schema intSchema() {
        return Schema.builder()
                .type(Type.Known.INTEGER)
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
