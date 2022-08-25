package br.com.dbc.vemser.dbcompras.entity;

import br.com.dbc.vemser.dbcompras.entity.pk.CotacaoXItemPK;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity(name = "quotation_item")
public class CotacaoXItemEntity {

    @EmbeddedId
    private CotacaoXItemPK cotacaoXItemPK;

    @Column(name = "unitary_value")
    private Double valorDoItem;

    @Column(name = "total_value")
    private Double valorTotal;

    // FIXME privates?
    @ManyToOne
    @MapsId("idCotacao")
    @JoinColumn(name = "id_quotation")
    CotacaoEntity cotacao;

    @ManyToOne
    @MapsId("idItem")
    @JoinColumn(name = "id_item")
    ItemEntity item;
}
