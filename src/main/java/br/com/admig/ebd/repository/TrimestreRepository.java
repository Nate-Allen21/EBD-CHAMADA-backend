package br.com.admig.ebd.repository;

import br.com.admig.ebd.domain.Trimestre;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TrimestreRepository extends JpaRepository<Trimestre, Long> {
    Optional<Trimestre> findByAnoAndNome(Integer ano, String nome);
}
