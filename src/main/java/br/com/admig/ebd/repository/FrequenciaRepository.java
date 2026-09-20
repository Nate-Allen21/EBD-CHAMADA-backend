package br.com.admig.ebd.repository;

import br.com.admig.ebd.domain.Aluno;
import br.com.admig.ebd.domain.Frequencia;
import br.com.admig.ebd.domain.Licao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FrequenciaRepository extends JpaRepository<Frequencia, Long> {
    Optional<Frequencia> findByAlunoAndLicao(Aluno aluno, Licao licao);
    List<Frequencia> findByLicao(Licao licao);
    List<Frequencia> findByAluno(Aluno aluno);
}
