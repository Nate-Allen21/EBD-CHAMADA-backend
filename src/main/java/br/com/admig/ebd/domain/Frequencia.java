package br.com.admig.ebd.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "frequencias")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Frequencia {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "aluno_id", nullable = false)
    private Aluno aluno;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "licao_id", nullable = false)
    private Licao licao;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatusPresenca status = StatusPresenca.PRESENTE;

    @Column(length = 500)
    private String observacao;

    public enum StatusPresenca {
        PRESENTE,
        AUSENTE,
        JUSTIFICADO,
        VISITANTE
    }
}
