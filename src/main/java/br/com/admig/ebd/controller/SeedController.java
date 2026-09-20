package br.com.admig.ebd.controller;

import br.com.admig.ebd.domain.*;
import br.com.admig.ebd.repository.*;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class SeedController {

    private final PerfilRepository perfilRepository;
    private final UsuarioRepository usuarioRepository;
    private final ClasseRepository classeRepository;
    private final TrimestreRepository trimestreRepository;
    private final LicaoRepository licaoRepository;
    private final AlunoRepository alunoRepository;
    private final FrequenciaRepository frequenciaRepository;
    private final PasswordEncoder passwordEncoder;

    public SeedController(PerfilRepository perfilRepository, UsuarioRepository usuarioRepository,
                         ClasseRepository classeRepository, TrimestreRepository trimestreRepository,
                         LicaoRepository licaoRepository, AlunoRepository alunoRepository,
                         FrequenciaRepository frequenciaRepository, PasswordEncoder passwordEncoder) {
        this.perfilRepository = perfilRepository;
        this.usuarioRepository = usuarioRepository;
        this.classeRepository = classeRepository;
        this.trimestreRepository = trimestreRepository;
        this.licaoRepository = licaoRepository;
        this.alunoRepository = alunoRepository;
        this.frequenciaRepository = frequenciaRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping("/seed")
    public ResponseEntity<String> seed() {
        if (usuarioRepository.findByUsername("admin").isPresent()) {
            return ResponseEntity.ok("Dados já inicializados.");
        }

        Perfil adminPerfil = perfilRepository.findByNome("ADMIN")
                .orElseGet(() -> perfilRepository.save(new Perfil(null, "ADMIN")));
        Perfil secretariaPerfil = perfilRepository.findByNome("SECRETARIA")
                .orElseGet(() -> perfilRepository.save(new Perfil(null, "SECRETARIA")));

        if (!usuarioRepository.findByUsername("admin").isPresent()) {
            Usuario admin = new Usuario();
            admin.setUsername("admin");
            admin.setPassword(passwordEncoder.encode("admin123"));
            admin.setPerfil(adminPerfil);
            admin.setAtivo(true);
            admin.setUltimoAcesso(java.time.LocalDateTime.now());
            usuarioRepository.save(admin);
        }

        if (!usuarioRepository.findByUsername("cleo2026").isPresent()) {
            Usuario cleo = new Usuario();
            cleo.setUsername("cleo2026");
            cleo.setPassword(passwordEncoder.encode("ebd2026"));
            cleo.setPerfil(secretariaPerfil);
            cleo.setAtivo(true);
            cleo.setUltimoAcesso(java.time.LocalDateTime.now());
            usuarioRepository.save(cleo);
        }

        Classe gideoes = classeRepository.save(new Classe(null, "GIDEÕES", "Irmãos", true));
        Classe mensageiras = classeRepository.save(new Classe(null, "MENSAGEIRAS DE CRISTO", "Irmãs", true));
        Classe atalaias = classeRepository.save(new Classe(null, "ATALAIAS DE DEUS", "Jovens", true));
        Classe geracao = classeRepository.save(new Classe(null, "GERAÇÃO ELEITA", "Pré-adolescentes", true));
        Classe cordeiros = classeRepository.save(new Classe(null, "CORDEIRINHOS DE CRISTO", "Crianças", true));

        Trimestre trimestre = trimestreRepository.save(new Trimestre(null, 2026, "3º Trimestre", true));

        List<String[]> lições = List.of(
            new String[]{"1", "2026-07-05", "O CONCEITO BÍBLICO DE FAMÍLIA", ""},
            new String[]{"2", "2026-07-12", "A FAMÍLIA PATRIARCAL CONTEMPORÂNEA", ""},
            new String[]{"3", "2026-07-19", "PREPARANDO O FILHO PARA SER PAI", ""},
            new String[]{"4", "2026-07-26", "QUEBRANDO O CICLO TRAUMAS", ""},
            new String[]{"5", "2026-08-02", "PATERNIDADE ESPIRITUAL", ""},
            new String[]{"6", "2026-08-09", "BUSCANDO CONHECER NOSSO PAI CELESTIAL", "Em 12/08/2026"},
            new String[]{"7", "2026-08-16", "QUESTIONAMENTOS SOBRE A PATERNIDADE DIVINA", "Retiro"},
            new String[]{"8", "2026-08-23", "FUNDAMENTOS DA PATERNIDADE DIVINA", ""},
            new String[]{"9", "2026-08-30", "SENTIMENTOS DE ORFANDADE ESPIRITUAL", ""},
            new String[]{"10", "2026-09-06", "VOLTANDO À CONDIÇÃO DE FILHO", ""},
            new String[]{"11", "2026-09-12", "JESUS EXEMPLO DE FILHO", ""},
            new String[]{"12", "2026-09-20", "ESTREITANDO A RELAÇÃO COM O PAI CELESTIAL", ""}
        );

        for (String[] item : lições) {
            licaoRepository.save(new Licao(null, Integer.parseInt(item[0]), LocalDate.parse(item[1]), item[2], item[3], trimestre));
        }

        Aluno aluno1 = alunoRepository.save(new Aluno(null, "Daniel", gideoes, null, null, null, null, true, java.time.LocalDateTime.now()));
        Aluno aluno2 = alunoRepository.save(new Aluno(null, "Edvan", gideoes, null, null, null, null, true, java.time.LocalDateTime.now()));
        Aluno aluno3 = alunoRepository.save(new Aluno(null, "Eliseu", gideoes, null, null, null, null, true, java.time.LocalDateTime.now()));
        Aluno aluno4 = alunoRepository.save(new Aluno(null, "Ana Freires", mensageiras, null, null, null, null, true, java.time.LocalDateTime.now()));
        Aluno aluno5 = alunoRepository.save(new Aluno(null, "Ana Assis", mensageiras, null, null, null, null, true, java.time.LocalDateTime.now()));
        Aluno aluno6 = alunoRepository.save(new Aluno(null, "Doralice", mensageiras, null, null, null, null, true, java.time.LocalDateTime.now()));
        Aluno aluno7 = alunoRepository.save(new Aluno(null, "Rebeca", atalaias, null, null, null, null, true, java.time.LocalDateTime.now()));
        Aluno aluno8 = alunoRepository.save(new Aluno(null, "Nathan", atalaias, null, null, null, null, true, java.time.LocalDateTime.now()));
        Aluno aluno9 = alunoRepository.save(new Aluno(null, "Artur Neris", geracao, null, null, null, null, true, java.time.LocalDateTime.now()));
        Aluno aluno10 = alunoRepository.save(new Aluno(null, "Gabrielly Gomes", geracao, null, null, null, null, true, java.time.LocalDateTime.now()));
        Aluno aluno11 = alunoRepository.save(new Aluno(null, "Ana Clara Mota", cordeiros, null, null, null, null, true, java.time.LocalDateTime.now()));
        Aluno aluno12 = alunoRepository.save(new Aluno(null, "Cecília Aragão", cordeiros, null, null, null, null, true, java.time.LocalDateTime.now()));

        List<Licao> licoes = licaoRepository.findByTrimestreOrderByNumeroAsc(trimestre);
        for (Licao licao : licoes) {
            List<Aluno> alunos = List.of(aluno1, aluno2, aluno3, aluno4, aluno5, aluno6, aluno7, aluno8, aluno9, aluno10, aluno11, aluno12);
            for (Aluno aluno : alunos) {
                if (licao.getNumero() == 1 || licao.getNumero() == 2) {
                    frequenciaRepository.save(new Frequencia(null, aluno, licao, Frequencia.StatusPresenca.PRESENTE, "Registro inicial"));
                } else {
                    frequenciaRepository.save(new Frequencia(null, aluno, licao, Frequencia.StatusPresenca.AUSENTE, "Padrão"));
                }
            }
        }

        return ResponseEntity.ok("Seed executado com sucesso.");
    }
}
