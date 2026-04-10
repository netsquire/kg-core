package cz.netsquire.kgcore.beans;

import com.fasterxml.jackson.databind.ObjectMapper;
import cz.netsquire.kgcore.chat.EmbeddingModel;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class EmbeddingModelTest {

    private final ObjectMapper mapper = new ObjectMapper();

    @Test
    void jsonRoundTrip() throws Exception {
        EmbeddingModel original = new EmbeddingModel("id1", "test-model", List.of(0.1, 0.2, 0.3), null, Map.of("k", "v"), "hello");
        String json = mapper.writeValueAsString(original);
        EmbeddingModel parsed = mapper.readValue(json, EmbeddingModel.class);
        assertEquals(original, parsed);
    }

    @Test
    void dimensionInferredWhenNull() {
        EmbeddingModel m = new EmbeddingModel(null, "m", List.of(0.5, 0.6), null, null, null);
        assertEquals(2, m.getDimension());
    }

    @Test
    void ofFactoryCreatesInstance() {
        EmbeddingModel m = EmbeddingModel.of(List.of(1.0, 2.0, 3.0));
        assertNotNull(m);
        assertEquals(3, m.getDimension());
    }
}
