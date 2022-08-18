package br.com.dbc.vemser.dbcompras.entity.pk;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;

@Getter
@Setter
@Embeddable
public class CotacaoXItemPK implements Serializable {

    @Column(name = "id_quotation")
    private Integer idCotacao;
    @Column(name = "id_item")
    private Integer idItem;



}
