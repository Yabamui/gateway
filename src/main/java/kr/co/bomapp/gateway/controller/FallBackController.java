package kr.co.bomapp.gateway.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class FallBackController {
    @GetMapping("/fallback/1")
    public ResponseEntity<String> getFallBackOne() {
        return new ResponseEntity<>("fallback-1", HttpStatus.OK);
    }

    @GetMapping("/fallback/2")
    public ResponseEntity<String> getFallBackTwo() {
        return new ResponseEntity<>("fallback-2", HttpStatus.OK);
    }
}
