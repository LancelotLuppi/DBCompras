package br.com.dbc.vemser.dbcompras.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity(name = "item")
public class ItemEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQ_ITEM")
    @SequenceGenerator(name = "SEQ_ITEM", sequenceName = "seq_id_item", allocationSize = 1)
    @Column(name = "id_item")
    private Integer idItem;

    @Column(name = "name")
    private String nome;

    @Column(name = "amount")
    private Integer quantidade;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY,cascade = CascadeType.ALL)
    @JoinColumn(name = "id_purchase",
            referencedColumnName = "id_purchase")
    private CompraEntity compra;

    @JsonIgnore
    @ManyToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinTable(
            name = "quotation_item",
            joinColumns = @JoinColumn(name = "id_item"),
            inverseJoinColumns = @JoinColumn(name = "id_quotation")
    )
    private Set<CotacaoEntity> cotacoes;

    @JsonIgnore
    @OneToMany(mappedBy = "cotacao")
    Set<CotacaoItemEntity> valores;

    @Column(name = "valor")
    private Double preco;

    @Override
    public String toString() {
        return "ItemEntity{" +
                "idItem=" + idItem +
                ", nome='" + nome + '\'' +
                ", quantidade=" + quantidade +
                ", compra=" + compra +
                ", cotacoes=" + cotacoes +
                ", valores=" + valores +
                '}';
    }
}
