package org.ai.carp.controller;

import org.ai.carp.model.Dummy;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/dummy")
public class DummyController {

    @GetMapping
    public Dummy get() {
        return new Dummy(1);
    }

    @PostMapping
    public Dummy post(@RequestBody Dummy dummy) {
        return new Dummy(dummy.getId());
    }

}
