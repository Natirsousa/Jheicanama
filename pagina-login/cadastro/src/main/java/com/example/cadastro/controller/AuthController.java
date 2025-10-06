package com.example.cadastro.controller;

import com.example.cadastro.model.Candidate;
import com.example.cadastro.service.UserService;
import com.example.cadastro.security.JwtUtil;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

@RestController
@RequestMapping("/usuarios")
@CrossOrigin(origins = "*") 
public class AuthController {
  
    @Autowired
    private  UserService userService;

    @Autowired
    private JwtUtil jwtUtil;

    @PostMapping("/register")
    public ResponseEntity<?> cadastrar(@RequestBody Candidate usuario){
        try{
            Candidate novo = userService.register(usuario);
            return ResponseEntity.ok(novo);
            
        }catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        boolean valido = userService.validarLogin(request.getIdentificador(), request.getSenha());
        Candidate user;

        if (valido) {
            if(request.getIdentificador().matches("\\d+")){
                user = userService.searchByCPF(request.getIdentificador());
            }else{
                user = userService.searchByEmail(request.getIdentificador());
            }
            String token = jwtUtil.generateToken(user.getCpf());
            return ResponseEntity.ok(Map.of(
                "token", token,
                "tipo", "candidate"
            ));
        }
        return ResponseEntity.status(401).body("Email ou senha inválidos");
    }

    @GetMapping("/{cpf}")
    public ResponseEntity<?> searchByCpf(@PathVariable String cpf){
        cpf = cpf.replaceAll("[^\\d]","");
        Candidate user = userService.searchByCPF(cpf);
        if(user != null){
            return ResponseEntity.ok(user);
        }
        return ResponseEntity.notFound().build();
    }

    @GetMapping("/me")
    public ResponseEntity<?> getUserData(@RequestHeader("Authorization") String token){
        if(token.startsWith("Bearer ")){
            token = token.substring(7);
        }
        if(!jwtUtil.validateToken(token)){
            return ResponseEntity.status(401).body("Token inválido ou expirado");
        }
        String cpf = jwtUtil.extractIdentifier(token);
        cpf = cpf.replaceAll("[^\\d]", "");
        Candidate user = userService.searchByCPF(cpf);

        return ResponseEntity.ok(user);
    }
    
}
