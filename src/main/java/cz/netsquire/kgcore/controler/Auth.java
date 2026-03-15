package cz.netsquire.kgcore.controler;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.Objects;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class Auth {

    @GetMapping({"/hello", "/hello/"})
    String hello() {
        System.out.println("--\nGOT (get) hello");
        return "AUTH entry point";
    }

    private AuthResponse checkAuth(String user, String password) {
        if (Objects.equals(user, password) & Objects.equals(user, "admin")) {
            return new AuthResponse("OK", "ADMIN_ROLE");
        } else {
            return new AuthResponse("NO_OK", "__UNKNOWN__");
        }
    }

    @GetMapping("/v1/{user}/{password}")
    AuthResponse basicAuth(@PathVariable String user, @PathVariable String password) {
        System.out.println("-- Came: " + user + " w " + password + "\n Basic AUTH response%n");
        return checkAuth(user, password);
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    AuthResponse checkAuth(@RequestBody AuthRequest auth) {
        AuthResponse response;
        System.out.println("--\nGOT (post): " + auth);
        response = checkAuth(auth.user(), auth.password());
        System.out.println("PRODUCED: " + response);
        return response;
    }
}

record AuthResponse(String status, String role) {}
record AuthRequest(String user, String password) {}