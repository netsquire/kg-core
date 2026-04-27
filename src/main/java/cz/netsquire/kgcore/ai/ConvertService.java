package cz.netsquire.kgcore.ai;

import cz.netsquire.kgcore.model.KnowledgeGraph;
import cz.netsquire.kgcore.model.StructuredOutput;
import org.springframework.stereotype.Service;

@Service
public class ConvertService {
    // StructuredOutput (ai DTO) -> KnowledgeGraph

//    private final ObjectMapper mapper = new ObjectMapper();

    public KnowledgeGraph fromStructuredOutput(StructuredOutput structuredOutput) {
        KnowledgeGraph kg = new KnowledgeGraph(null, null, null, null);
        kg.setName(structuredOutput.context());
        return kg;
    }
}

