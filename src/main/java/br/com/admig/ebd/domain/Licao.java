package br.com.admig.ebd.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Table(name = "licoes")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Licao {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Integer numero;

    @Column(nullable = false)
    private LocalDate data;

    @Column(nullable = false)
    private String tema;

    @Column(length = 500)
    private String observacao;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "trimestre_id", nullable = false)
    private Trimestre trimestre;
}
