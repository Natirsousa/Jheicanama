package com.example.cadastro.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.cadastro.model.Contractor;
import com.example.cadastro.security.JwtUtil;
import com.example.cadastro.service.ContractorService;

@RestController
@RequestMapping("/contratantes")
@CrossOrigin(origins="*")
public class ContractorController {
    
    @Autowired
    private ContractorService contractorService;
    
    @Autowired
    private JwtUtil jwtUtil;

    @PostMapping("/register")
    public ResponseEntity<?> cadastrar(@RequestBody Contractor user){
        try{
            Contractor novo = contractorService.register(user);
            return ResponseEntity.ok(novo);
        }catch (RuntimeException e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest){
        String identificador = loginRequest.getIdentificador();
        String senha = loginRequest.getSenha();
        
        boolean valido = contractorService.validarlogin(identificador, senha);

        if(valido){
            Contractor user = identificador.matches("\\d+") ?
                contractorService.searchByCnpj(identificador) :
                contractorService.searchByEmail(identificador);
            String token = jwtUtil.generateToken(identificador);
            return ResponseEntity.ok(Map.of(
                    "token",token,
                    "tipo", "contractor"
                ));
                
        }

        return ResponseEntity.status(401).body("Email/Cnpj ou senha incorreta");
    }

    @GetMapping("/me")
    public ResponseEntity<?> getUserData(@RequestHeader("Authorization") String token){
        if(token.startsWith("Bearer ")){
            token = token.substring(7);
        }
        if(!jwtUtil.validateToken(token)){
            return ResponseEntity.status(401).body("Token inválido ou expirado");
        }

        String cnpj = jwtUtil.extractIdentifier(token);
        cnpj = cnpj.replaceAll("[^\\d]", "");
        Contractor user = contractorService.searchByCnpj(cnpj);

        return ResponseEntity.ok(user);
    }

}
