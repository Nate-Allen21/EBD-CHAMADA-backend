package br.com.admig.ebd.repository;

import br.com.admig.ebd.domain.Aluno;
import br.com.admig.ebd.domain.Classe;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AlunoRepository extends JpaRepository<Aluno, Long> {
    List<Aluno> findByClasseOrderByNomeCompletoAsc(Classe classe);
    List<Aluno> findAllByOrderByNomeCompletoAsc();
}
