package br.com.admig.ebd.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "alunos")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Aluno {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nomeCompleto;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "classe_id", nullable = false)
    private Classe classe;

    private LocalDate dataNascimento;
    private String telefone;
    private String responsavel;
    @Column(length = 1000)
    private String observacoes;

    @Column(nullable = false)
    private Boolean ativo = true;

    @Column(nullable = false)
    private LocalDateTime dataCadastro = LocalDateTime.now();
}
