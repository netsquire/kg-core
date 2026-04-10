package cz.netsquire.kgcore.chat;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public final class EmbeddingModel {

    private final String id;
    private final String model;
    private final List<Double> vector;
    private final Integer dimension;
    private final Map<String, String> metadata;
    private final String sourceText;

    @JsonCreator
    public EmbeddingModel(
            @JsonProperty("id") String id,
            @JsonProperty("model") String model,
            @JsonProperty(value = "vector", required = true) List<Double> vector,
            @JsonProperty("dimension") Integer dimension,
            @JsonProperty("metadata") Map<String, String> metadata,
            @JsonProperty("sourceText") String sourceText) {
        this.id = id;
        this.model = model;
        // require vector to be non-null and non-empty
        Objects.requireNonNull(vector, "vector must not be null");
        if (vector.isEmpty()) {
            throw new IllegalArgumentException("vector must not be empty");
        }
        // defensive, unmodifiable copy
        this.vector = List.copyOf(vector);
        this.dimension = (dimension == null) ? this.vector.size() : dimension;
        this.metadata = (metadata == null) ? Collections.emptyMap() : Collections.unmodifiableMap(Map.copyOf(metadata));
        this.sourceText = sourceText;
    }

    public static EmbeddingModel of(List<Double> vector) {
        return new EmbeddingModel(null, null, vector, null, null, null);
    }

    public String getId() {
        return id;
    }

    public String getModel() {
        return model;
    }

    public List<Double> getVector() {
        return vector;
    }

    public Integer getDimension() {
        return dimension;
    }

    public Map<String, String> getMetadata() {
        return metadata;
    }

    public String getSourceText() {
        return sourceText;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        EmbeddingModel that = (EmbeddingModel) o;
        return Objects.equals(id, that.id) &&
                Objects.equals(model, that.model) &&
                Objects.equals(vector, that.vector) &&
                Objects.equals(dimension, that.dimension) &&
                Objects.equals(metadata, that.metadata) &&
                Objects.equals(sourceText, that.sourceText);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, model, vector, dimension, metadata, sourceText);
    }

    @Override
    public String toString() {
        return "EmbeddingModel{" +
                "id='" + id + '\'' +
                ", model='" + model + '\'' +
                ", dimension=" + dimension +
                ", vector(size)=" + (vector == null ? 0 : vector.size()) +
                ", metadata=" + metadata +
                '}';
    }
}
