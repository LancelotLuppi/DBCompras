package br.com.dbc.vemser.dbcompras.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity(name = "quotation_item")
public class CotacaoXItemEntity {

    @Id
    @Column(name = "id_item")
    private Integer idItem;



    @Column(name = "value")
    private Double valorDoItem;
}
