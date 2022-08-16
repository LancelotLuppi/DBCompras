package br.com.dbc.vemser.dbcompras.entity;

import br.com.dbc.vemser.dbcompras.enums.StatusCotacoes;
import com.fasterxml.jackson.annotation.JsonIgnore;
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
    @Column(name = "id_quotation", insertable = false, updatable = false)
    private Integer idCotacao;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_purchase",
            referencedColumnName = "id_purchase")
    private CompraEntity compras;

    @Column(name = "name")
    private String nome;

    @Column(name = "data")
    private LocalDateTime localDate;

    @Column(name = "file")
    private String anexo;

    @Column(name = "status")
    private Boolean status;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_user",
            referencedColumnName = "id_user")
    private UsuarioEntity usuario;

    @JsonIgnore
    @ManyToMany(fetch = FetchType.LAZY,cascade = CascadeType.ALL)
    @JoinTable(
            name = "quotation_item",
            joinColumns = @JoinColumn(name = "id_quotation"),
            inverseJoinColumns = @JoinColumn(name = "id_item")
    )
    private Set<ItemEntity> itens;

}
