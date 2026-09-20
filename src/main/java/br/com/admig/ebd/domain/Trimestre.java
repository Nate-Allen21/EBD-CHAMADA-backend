package br.com.admig.ebd.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "trimestres")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Trimestre {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Integer ano;

    @Column(nullable = false)
    private String nome;

    @Column(nullable = false)
    private boolean ativo = true;
}
