package cz.netsquire.kgcore.controler;

import lombok.RequiredArgsConstructor;
import lombok.val;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
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
    @ResponseStatus(HttpStatus.OK)
    public AuthResponse checkAuth(@RequestBody AuthRequest auth) {
        AuthResponse response = null;
        System.out.println("--\nGOT (post): " + auth);
        val user = auth.user();
        val password = auth.password();

        if (Objects.equals(user, password) & Objects.equals(user, "admin")) {
            response = new AuthResponse(user, user);
        } else {
            response = new AuthResponse("__UNAUTHENTICATED__", "__UNKNOWN__");
        }
        System.out.println("PRODUCED: " + response);
        return response;
    }
}