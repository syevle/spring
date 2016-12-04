package com.example;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.sql.DataSource;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;


@RestController
@RequestMapping("/api")
class ApiRestController {

    private final JdbcTemplate a, b;

    public ApiRestController(DataSource a, DataSource b) {
        this.a = new JdbcTemplate(a);
        this.b = new JdbcTemplate(b);
    }

    @GetMapping("/messages")
    public Collection<Map<String, String>> messages() {
        return b.query("select * from MESSAGE", (rs, i) -> {
            Map<String, String> cat = new HashMap<>();
            cat.put("message", rs.getString("MESSAGE"));
            cat.put("id", rs.getString("ID"));
            return cat;
        });
    }

    @GetMapping("/cats")
    public Collection<Map<String, String>> cats() {
        return a.query("select * from CAT", (rs, i) -> {
            Map<String, String> cat = new HashMap<>();
            cat.put("nickname", rs.getString("NICKNAME"));
            cat.put("id", rs.getString("ID"));
            return cat;
        });
    }

    @Transactional
    @PostMapping
    public void write(@RequestBody Map<String, String> payload,
                      @RequestParam Optional<Boolean> exception) {
        String felix = payload.get("name");

        this.a.update("INSERT INTO CAT( NICKNAME, ID ) VALUES (?,?)",
                felix, UUID.randomUUID().toString());

        this.b.update("INSERT INTO MESSAGE( MESSAGE, ID) VALUES (?,?)",
                "Hello, " + felix + "!", UUID.randomUUID().toString());

        if (exception.orElse(false)) {
            // Here is transection roll back for both database.
            throw new RuntimeException("oops!");
        }
    }
}
