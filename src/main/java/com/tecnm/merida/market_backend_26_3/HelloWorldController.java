package com.tecnm.merida.market_backend_26_3;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController

public class HelloWorldController {
    @GetMapping("/saludo")
    public String helloWorld(){
        return "Hello World!";
    }
}
