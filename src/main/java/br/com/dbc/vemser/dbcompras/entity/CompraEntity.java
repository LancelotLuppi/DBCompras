package br.com.dbc.vemser.dbcompras.entity;

import br.com.dbc.vemser.dbcompras.enums.SituacaoCompra;
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
@Entity(name = "purchase")
public class CompraEntity {


    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQ_COMPRA")
    @SequenceGenerator(name = "SEQ_COMPRA", sequenceName = "seq_id_purchase", allocationSize = 1)
    @Column(name = "id_purchase", insertable = false, updatable = false)
    private Integer idCompra;

    @Column(name = "name")
    private String name;

    @Column(name = "data")
    private LocalDateTime dataCompra;

    @Column(name = "status")
    private SituacaoCompra status;

    @Column(name = "total_value")
    private Double valor;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_user",
            referencedColumnName = "id_user")
    private UsuarioEntity usuario;

    @JsonIgnore
    @OneToMany(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_purchase",
    referencedColumnName = "id_purchase")
    private Set<ItemEntity> itens;

}
