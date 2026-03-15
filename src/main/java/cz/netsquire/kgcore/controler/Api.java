package cz.netsquire.kgcore.controler;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class Api {

    @GetMapping({"/hello", "/hello/"})
    String hello() {
        return "General API: News, etc";
    }

    @GetMapping({"/status", "/status/"})
    Status status() {
        return new Status("SOME_STATE","API is running", 150);
    }

    @GetMapping("news")
    @ResponseStatus(HttpStatus.OK)
    List<NewsArticle> news() {
        System.out.println("-- Newswire --");
        var list = List.of(
                new NewsArticle("News 1", "-- This is the text of news 1 --"),
//                        List.of(new Tag("tag1"), new Tag("tag2")), List.of()),
                new NewsArticle("News 2", "-- This is the text of news 2 --")
//                        List.of(new Tag("tag3"), new Tag("newTag", 0.2f)), List.of())
        );
        System.out.println("PRODUCED news: " + list);
        return list;
    }
}

record Status(String state, String message, int code) {}

record NewsArticle(String title, String text  /*, List<Tag> tags, List<NewsArticle> related*/) {}

record Tag(String concept, float relevance) {
//    static int num;
    public Tag(String concept) {
        this(concept, 1.0f);
    }
}