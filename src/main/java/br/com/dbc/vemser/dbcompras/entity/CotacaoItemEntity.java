package br.com.dbc.vemser.dbcompras.entity;

import br.com.dbc.vemser.dbcompras.entity.pk.CotacaoItemPk;
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
public class CotacaoItemEntity {

    @EmbeddedId
    private CotacaoItemPk cotacaoItemPk;

    @ManyToOne
    @MapsId("id_quotation")
    @JoinColumn(name = "id_quotation")
    CotacaoEntity cotacao;

    @ManyToOne
    @MapsId("id_item")
    @JoinColumn(name = "id_item")
    ItemEntity item;

    @Column(name = "value")
    private Double valorDoItem;
}
