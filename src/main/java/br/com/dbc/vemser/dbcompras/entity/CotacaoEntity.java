package br.com.dbc.vemser.dbcompras.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity(name = "quotation")
public class CotacaoEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQ_COTACAO")
    @SequenceGenerator(name = "SEQ_COTACAO", sequenceName = "seq_id_quotation", allocationSize = 1)
    @Column(name = "id_quotation")
    private Integer idCotacao;

    @Column(name = "name")
    private String nome;

    @Column(name = "data")
    private LocalDateTime localDate;

    @Column(name = "file")
    private byte[] anexo;

    @Column(name = "status")
    private String status;

    @Column(name = "valor")
    private Double valor;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_user",
            referencedColumnName = "id_user")
    private UsuarioEntity usuario;

    @JsonIgnore
    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private Set<CotacaoXItemEntity> itens;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_purchase",
            referencedColumnName = "id_purchase")
    private CompraEntity compra;
}
