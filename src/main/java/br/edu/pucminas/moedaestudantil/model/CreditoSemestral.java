package br.edu.pucminas.moedaestudantil.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "creditos_semestrais", uniqueConstraints = @UniqueConstraint(columnNames = {"professor_id", "semestre"}))
public class CreditoSemestral {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "professor_id", nullable = false)
    private Professor professor;
    @Column(nullable = false, length = 10)
    private String semestre;
    @Column(nullable = false)
    private int quantidade;
    @Column(nullable = false)
    private LocalDateTime criadoEm;
    protected CreditoSemestral() {}
    public CreditoSemestral(Professor professor, String semestre, int quantidade) {
        this.professor = professor;
        this.semestre = semestre;
        this.quantidade = quantidade;
        this.criadoEm = LocalDateTime.now();
    }
}
