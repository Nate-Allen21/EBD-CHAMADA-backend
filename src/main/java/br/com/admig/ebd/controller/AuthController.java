package br.com.admig.ebd.controller;

import br.com.admig.ebd.config.JwtService;
import br.com.admig.ebd.domain.Usuario;
import br.com.admig.ebd.dto.AuthRequest;
import br.com.admig.ebd.dto.AuthResponse;
import br.com.admig.ebd.repository.UsuarioRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthController(AuthenticationManager authenticationManager, JwtService jwtService,
                         UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder) {
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody AuthRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.username(), request.password())
        );

        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        Usuario usuario = usuarioRepository.findByUsername(userDetails.getUsername()).orElseThrow();
        usuario.setUltimoAcesso(LocalDateTime.now());
        usuarioRepository.save(usuario);

        String token = jwtService.generateToken(userDetails);
        return ResponseEntity.ok(new AuthResponse(token, userDetails.getUsername(), usuario.getPerfil().getNome()));
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody AuthRequest request) {
        if (usuarioRepository.existsByUsername(request.username())) {
            return ResponseEntity.badRequest().body("Usuário já existe");
        }

        Usuario usuario = new Usuario();
        usuario.setUsername(request.username());
        usuario.setPassword(passwordEncoder.encode(request.password()));
        usuario.setAtivo(true);
        usuario.setUltimoAcesso(LocalDateTime.now());
        usuarioRepository.save(usuario);
        return ResponseEntity.ok("Usuário criado");
    }
}
