package com.example;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

/**
 * Created by Santosh on 12/3/16.
 */
@RestController
@RequestMapping("/api")
class ApiRestController {

    private final JmsTemplate jmsTemplate;
    private final JdbcTemplate jdbcTemplate;

    public ApiRestController(JmsTemplate jmsTemplate, JdbcTemplate jdbcTemplate) {
        this.jmsTemplate = jmsTemplate;
        this.jdbcTemplate = jdbcTemplate;
    }

    @GetMapping
    public Collection<Map<String, String>> read() {
        return jdbcTemplate.query("select * from MESSAGE",
                (rs, i) -> {
                    Map<String, String> m = new HashMap<>();
                    m.put("id", rs.getString("id"));
                    m.put("message", rs.getString("message"));
                    return m;
                });
    }

    @PostMapping
    @Transactional
    public void write(@RequestBody Map<String, String> payload,
                      @RequestParam Optional<Boolean> exception) {


        String id = UUID.randomUUID().toString();

        this.jdbcTemplate.update(
                "insert into MESSAGE(MESSAGE, id) VALUES(?,?)",
                payload.get("message"), id);

        this.jmsTemplate.convertAndSend(App.MSGS, id);

        if (exception.orElse(false)) {
            // even jdbcTemplate and jmsTemplate work correctly. rollback jdbc and jms transection done here.
            throw new RuntimeException("Nope!");
        }
    }
}
