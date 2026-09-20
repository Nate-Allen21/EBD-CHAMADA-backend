package br.com.admig.ebd.service;

import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import br.com.admig.ebd.domain.Perfil;
import br.com.admig.ebd.domain.Usuario;
import br.com.admig.ebd.repository.UsuarioRepository;

class UserDetailsServiceImplTest {

    @Test
    void loadUserByUsernameShouldReturnUserDetailsForActiveUser() {
        Perfil perfil = new Perfil();
        perfil.setNome("ADMIN");

        Usuario usuario = new Usuario();
        usuario.setUsername("admin");
        usuario.setPassword("encoded-password");
        usuario.setPerfil(perfil);
        usuario.setAtivo(true);

        UsuarioRepository repository = buildRepository(Map.of("admin", usuario));
        UserDetailsServiceImpl service = new UserDetailsServiceImpl(repository);

        UserDetails result = service.loadUserByUsername("admin");

        assertThat(result.getUsername()).isEqualTo("admin");
        assertThat(result.getPassword()).isEqualTo("encoded-password");
        assertThat(result.getAuthorities()).anyMatch(authority -> authority.getAuthority().equals("ROLE_ADMIN"));
        assertThat(result.isEnabled()).isTrue();
    }

    @Test
    void loadUserByUsernameShouldThrowWhenUserDoesNotExist() {
        UsuarioRepository repository = buildRepository(Map.of());
        UserDetailsServiceImpl service = new UserDetailsServiceImpl(repository);

        assertThrows(UsernameNotFoundException.class, () -> service.loadUserByUsername("missing"));
    }

    @Test
    void loadUserByUsernameShouldThrowWhenUserIsInactive() {
        Perfil perfil = new Perfil();
        perfil.setNome("ADMIN");

        Usuario usuario = new Usuario();
        usuario.setUsername("admin");
        usuario.setPassword("encoded-password");
        usuario.setPerfil(perfil);
        usuario.setAtivo(false);

        UsuarioRepository repository = buildRepository(Map.of("admin", usuario));
        UserDetailsServiceImpl service = new UserDetailsServiceImpl(repository);

        assertThrows(UsernameNotFoundException.class, () -> service.loadUserByUsername("admin"));
    }

    private UsuarioRepository buildRepository(Map<String, Usuario> users) {
        Map<String, Usuario> store = new HashMap<>(users);

        return (UsuarioRepository) Proxy.newProxyInstance(
                UsuarioRepository.class.getClassLoader(),
                new Class<?>[]{UsuarioRepository.class},
                (proxy, method, args) -> {
                    switch (method.getName()) {
                        case "findByUsername":
                            return Optional.ofNullable(store.get(args[0]));
                        case "existsByUsername":
                            return store.containsKey(args[0]);
                        case "save":
                            Usuario saved = (Usuario) args[0];
                            store.put(saved.getUsername(), saved);
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
}
