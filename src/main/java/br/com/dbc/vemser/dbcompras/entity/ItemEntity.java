package br.com.dbc.vemser.dbcompras.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity(name = "item")
public class ItemEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQ_ITEM")
    @SequenceGenerator(name = "SEQ_ITEM", sequenceName = "seq_id_item", allocationSize = 1)
    @Column(name = "id_item", insertable = false, updatable = false)
    private Integer idItem;

    @Column(name = "name")
    private String nome;

    @Column(name = "amount")
    private Integer quantidade;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_purchase",
            referencedColumnName = "id_purchase")
    private CompraEntity compra;

    public ItemEntity(String nome, Integer quantidade) {
        this.nome = nome;
        this.quantidade = quantidade;
    }
}
