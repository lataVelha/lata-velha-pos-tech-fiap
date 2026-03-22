package br.com.lata.velha.presentation.api;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/teste")
public class TestController {

    @GetMapping("/user")
    public String userAccess() {
        return "Acesso permitido para USER ou ADMIN";
    }

    @GetMapping("/admin")
    public String adminAccess() {
        return "Acesso permitido apenas para ADMIN";
    }

    @GetMapping("/mecanico")
    public String mecanicoAccess() {
        return "Acesso permitido apenas para MECANICO e ADMIN";
    }

    @GetMapping("/publico")
    public String publico() {
        return "Endpoint público, sem necessidade de autenticação";
    }
}