package com.example.cadastro.service;

import com.example.cadastro.model.Candidate;
import com.example.cadastro.model.Contractor;
import com.example.cadastro.repository.ContractorRepository;
import com.example.cadastro.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ContractorRepository contractorRepository;

    @Override
    public UserDetails loadUserByUsername(String identifier) throws UsernameNotFoundException {
        // Primeiro tenta candidato
        Candidate candidate = userRepository.findByCpf(identifier);
        if (candidate != null) {
            return org.springframework.security.core.userdetails.User
                    .withUsername(candidate.getCpf())
                    .password(candidate.getPassWord())
                    .roles("USER")
                    .build();
        }

        // Depois tenta contratante
        Contractor contractor = contractorRepository.findByCnpj(identifier);
        if (contractor != null) {
            return org.springframework.security.core.userdetails.User
                    .withUsername(contractor.getCnpj())
                    .password(contractor.getPassWord())
                    .roles("CONTRACTOR")
                    .build();
        }

        throw new UsernameNotFoundException("Usuário não encontrado: " + identifier);
    }
}
