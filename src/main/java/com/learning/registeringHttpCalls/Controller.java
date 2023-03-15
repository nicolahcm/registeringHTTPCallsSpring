package com.learning.registeringHttpCalls;


import org.springframework.web.bind.annotation.*;

@RestController
public class Controller {




    @GetMapping("/noparam")
    public String noParam() {
        return "Hello";
    }


    @PostMapping(value = "/body", produces = "application/json")
    public Student getStudent(@RequestBody Student student) {
        return student;
    }

    @GetMapping("/queryparam")
    public String queryParam(@RequestParam String name) {
        return name;
    }
}
