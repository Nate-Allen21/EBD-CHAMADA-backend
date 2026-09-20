package br.com.admig.ebd.controller;

import java.lang.reflect.Proxy;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;

import br.com.admig.ebd.config.JwtService;
import br.com.admig.ebd.domain.Perfil;
import br.com.admig.ebd.domain.Usuario;
import br.com.admig.ebd.dto.AuthRequest;
import br.com.admig.ebd.dto.AuthResponse;
import br.com.admig.ebd.repository.UsuarioRepository;

class AuthControllerTest {

    @Test
    void loginShouldAuthenticateAndReturnToken() {
        JwtService jwtService = new JwtService();
        ReflectionTestUtils.setField(jwtService, "secretKey", "EBDADMIGRFII2026SECRETKEY1234567890");
        ReflectionTestUtils.setField(jwtService, "jwtExpiration", 86_400_000L);

        UserDetails userDetails = User.withUsername("admin")
                .password("encoded-password")
                .authorities("ROLE_ADMIN")
                .build();

        Perfil perfil = new Perfil();
        perfil.setNome("ADMIN");

        Usuario usuario = new Usuario();
        usuario.setUsername("admin");
        usuario.setPassword("encoded-password");
        usuario.setPerfil(perfil);
        usuario.setAtivo(true);
        usuario.setUltimoAcesso(LocalDateTime.now());

        AtomicReference<Usuario> savedUser = new AtomicReference<>();
        AuthenticationManager authenticationManager = authentication -> new UsernamePasswordAuthenticationToken(
                userDetails, authentication.getCredentials(), userDetails.getAuthorities()
        );
        UsuarioRepository usuarioRepository = repositoryFor(new java.util.HashSet<>(java.util.List.of("admin")), Optional.of(usuario), savedUser);

        AuthController controller = new AuthController(authenticationManager, jwtService, usuarioRepository, new TestPasswordEncoder());
        ResponseEntity<?> response = controller.login(new AuthRequest("admin", "admin123"));

        assertThat(response.getStatusCode().value()).isEqualTo(200);
        AuthResponse body = (AuthResponse) response.getBody();
        assertThat(body.token()).isNotBlank();
        assertThat(body.username()).isEqualTo("admin");
        assertThat(body.perfil()).isEqualTo("ADMIN");
        assertThat(savedUser.get()).isNotNull();
        assertThat(savedUser.get().getUltimoAcesso()).isNotNull();
    }

    @Test
    void registerShouldRejectExistingUser() {
        AuthController controller = new AuthController(
                authentication -> authentication,
                new JwtService(),
                repositoryFor(new java.util.HashSet<>(java.util.List.of("admin")), Optional.empty(), new AtomicReference<>()),
                new TestPasswordEncoder()
        );

        ResponseEntity<?> response = controller.register(new AuthRequest("admin", "admin123"));

        assertEquals(400, response.getStatusCode().value());
        assertThat(response.getBody()).isEqualTo("Usuário já existe");
    }

    @Test
    void registerShouldCreateUserWhenUsernameIsAvailable() {
        AtomicReference<Usuario> savedUser = new AtomicReference<>();
        AuthController controller = new AuthController(
                authentication -> authentication,
                new JwtService(),
                repositoryFor(new java.util.HashSet<>(), Optional.empty(), savedUser),
                new TestPasswordEncoder()
        );

        ResponseEntity<?> response = controller.register(new AuthRequest("new-user", "secret123"));

        assertEquals(200, response.getStatusCode().value());
        assertThat(response.getBody()).isEqualTo("Usuário criado");
        assertThat(savedUser.get()).isNotNull();
        assertThat(savedUser.get().getUsername()).isEqualTo("new-user");
        assertThat(savedUser.get().getPassword()).isEqualTo("encoded:secret123");
        assertThat(savedUser.get().isAtivo()).isTrue();
        assertThat(savedUser.get().getUltimoAcesso()).isNotNull();
    }

    private UsuarioRepository repositoryFor(java.util.Set<String> existingUsernames, Optional<Usuario> user, AtomicReference<Usuario> savedUserRef) {
        return (UsuarioRepository) Proxy.newProxyInstance(
                UsuarioRepository.class.getClassLoader(),
                new Class<?>[]{UsuarioRepository.class},
                (proxy, method, args) -> {
                    switch (method.getName()) {
                        case "findByUsername":
                            return user;
                        case "existsByUsername":
                            return existingUsernames.contains(args[0]);
                        case "save":
                            Usuario saved = (Usuario) args[0];
                            existingUsernames.add(saved.getUsername());
                            savedUserRef.set(saved);
                            return saved;
                        case "toString":
                            return "UsuarioRepositoryStub";
                        case "hashCode":
                            return System.identityHashCode(proxy);
                        case "equals":
                            return proxy == args[0];
                        default:
                            return null;
                    }
                }
        );
    }

    private static class TestPasswordEncoder implements PasswordEncoder {
        @Override
        public String encode(CharSequence rawPassword) {
            return "encoded:" + rawPassword;
        }

        @Override
        public boolean matches(CharSequence rawPassword, String encodedPassword) {
            return encodedPassword.equals("encoded:" + rawPassword);
        }

        @Override
        public boolean upgradeEncoding(String encodedPassword) {
            return false;
        }
    }
}
