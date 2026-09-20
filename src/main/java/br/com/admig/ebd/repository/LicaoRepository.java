package br.com.admig.ebd.repository;

import br.com.admig.ebd.domain.Licao;
import br.com.admig.ebd.domain.Trimestre;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LicaoRepository extends JpaRepository<Licao, Long> {
    List<Licao> findByTrimestreOrderByNumeroAsc(Trimestre trimestre);
}
