package com.storemates.auth.service;

import com.storemates.auth.dto.AuthRequestDTO;
import com.storemates.auth.dto.AuthResponseDTO;
import com.storemates.security.jwt.JwtService;
import com.storemates.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    /**
     * -crea el primero ADMIN o users
    public AuthResponseDTO register(RegisterRequestDTO request) {
     if (userRepository.findByEmail(request.getEmail()).isPresent())
     throw new RuntimeException("El email ya está registrado");

        var user = UserEntity.builder()
                .firstname(request.getFirstname())
                .lastname(request.getLastname())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(Role.ADMIN)
                .build();

        userRepository.save(user);
        var jwtToken = jwtService.generateToken(user);
        return AuthResponseDTO.builder().token(jwtToken).build();
    }*/

    public AuthResponseDTO login(AuthRequestDTO request) {
        try {
            // INTENTO DE AUTENTICACIÓN
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getEmail(),
                            request.getPassword()
                    )
            );

            var user = userRepository.findByEmail(request.getEmail())
                    .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado sistema interno"));

            Map<String, Object> extraClaims = new HashMap<>();
            extraClaims.put("role", user.getRole().name());
            extraClaims.put("name", user.getFirstname());

            String jwtToken = jwtService.generateToken(extraClaims, user);

            log.info("Login exitoso para usuario: {}", request.getEmail());

            return AuthResponseDTO.builder()
                    .token(jwtToken)
                    .build();

        } catch (AuthenticationException e) {
            log.warn("Intento de login fallido para el email: {}", request.getEmail());
            throw new BadCredentialsException("credenciales invalidas");
        }
    }
}
