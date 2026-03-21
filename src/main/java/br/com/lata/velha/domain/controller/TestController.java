package br.com.lata.velha.domain.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/teste")
public class TestController {

    @GetMapping("/user")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public String userAccess() {
        return "Acesso permitido para USER ou ADMIN";
    }

    @GetMapping("/admin")
    @PreAuthorize("hasRole('ADMIN')")
    public String adminAccess() {
        return "Acesso permitido apenas para ADMIN";
    }

    @GetMapping("/mecanico")
    @PreAuthorize("hasAnyRole('MECANICO', 'ADMIN')")
    public String mecanicoAccess() {
        return "Acesso permitido apenas para MECANICO e ADMIN";
    }

    @GetMapping("/publico")
    public String publico() {
        return "Endpoint público, sem necessidade de autenticação";
    }
}