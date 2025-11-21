package cz.netsquire.kgcore.controler;

import lombok.RequiredArgsConstructor;
import lombok.val;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Objects;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class Rest {

    @GetMapping("hello")
    public String hello() {
        return "Hello, World!";
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<AuthResponse> checkAuthP(@RequestBody AuthRequest auth) {
        AuthResponse response = null;
        System.out.println("GOT (post): " + auth);
        val user = auth.user();
        val password = auth.password();

        if (Objects.equals(user, password) & Objects.equals(user, "admin")) {
            response = new AuthResponse(true, user, user);
        } else {
            response = new AuthResponse(false, "", "");
        }
        System.out.println("PRODUCED: " + ResponseEntity.ok(response));
        return ResponseEntity.ok(response);
    }

    @GetMapping(value = "/auth/{user}/{password}")
//    , produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<AuthResponse> checkAuthG(@PathVariable String user, @PathVariable String password) {
        AuthResponse response;
        System.out.println("GOT (get): " + user + " / " + password);
        if (Objects.equals(user, password) & Objects.equals(user, "admin")) {
            response = new AuthResponse(true, user, user);
        } else {
            response = new AuthResponse(false, "", "");
        }
        return ResponseEntity.ok(response);
    }
}