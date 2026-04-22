package cz.netsquire.kgcore.http;

import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/graph")
public class GraphController {

    @GetMapping("/nodes")
    public List<Map<String, String>> getNodes() {
        return List.of(Map.of("id", "Node1"), Map.of("id", "Node2"));
    }

    @PostMapping("/nodes")
    public void addNode(@RequestBody Map<String, String> node) {
        // Persistence logic
    }
}