package br.edu.fatecpg.backend_pulseira.controller;

import br.edu.fatecpg.backend_pulseira.config.JwtService;
import br.edu.fatecpg.backend_pulseira.dto.LoginRequestDTO;
import br.edu.fatecpg.backend_pulseira.dto.LoginResponseDTO;
import br.edu.fatecpg.backend_pulseira.model.Cuidador;
import br.edu.fatecpg.backend_pulseira.repository.CuidadorRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final CuidadorRepository cuidadorRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody @Valid LoginRequestDTO dto) {
        Cuidador cuidador = cuidadorRepository.findByEmail(dto.email()).orElse(null);

        if (cuidador == null || !passwordEncoder.matches(dto.senha(), cuidador.getSenhaHash())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("E-mail ou senha incorretos.");
        }

        String role ="RESPONSAVEL";
        String token = jwtService.gerarToken(cuidador.getEmail(), role, cuidador.getId());

        LoginResponseDTO response = new LoginResponseDTO(
                token,
                "Bearer",
                cuidador.getId(),
                cuidador.getNome(),
                cuidador.getEmail(),
                role
        );

        return ResponseEntity.ok(response);
    }
}