package com.example.cadastro.service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.example.cadastro.model.Contractor;
import com.example.cadastro.repository.ContractorRepository;
import com.example.cadastro.security.JwtUtil;
@Service
public class ContractorService {

    @Autowired
    private ContractorRepository contractorRepository;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private PasswordEncoder passwordEnconder;

    public Contractor register(Contractor user){
       String cnpjLimpo = user.getCnpj().replaceAll("\\D", "");
        user.setCnpj(cnpjLimpo);

        if(contractorRepository.findByCnpj(cnpjLimpo) != null){
            throw new RuntimeException("CNPJ já cadastrado!");
        }
        if(contractorRepository.findByEmail(user.getEmail())!=null){
            throw new RuntimeException("Email já cadastrado!");
        }

        user.setPassWord(passwordEnconder.encode(user.getPassWord()));
        return contractorRepository.save(user);
    }

    public Contractor searchByEmail(String email){
        return contractorRepository.findByEmail(email);
    }

    public Contractor searchByCnpj(String cnpj){
         cnpj = cnpj.replaceAll("[^\\d]", "");
        return contractorRepository.findByCnpj(cnpj);
    }

    public boolean validarlogin(String identificador, String senha){
         Contractor user;

        if(identificador.matches("\\d+")){ // é CNPJ
            String cnpjLimpo = identificador.replaceAll("[^\\d]", "");
            user = contractorRepository.findByCnpj(cnpjLimpo);
        } else { // é email
            user = contractorRepository.findByEmail(identificador);
        }
        if(user == null){
            return false;
        }

        return passwordEnconder.matches(senha, user.getPassWord());
    }

    public String gerarToken(Contractor user){
        return jwtUtil.generateToken(user.getCnpj());
    }
}
