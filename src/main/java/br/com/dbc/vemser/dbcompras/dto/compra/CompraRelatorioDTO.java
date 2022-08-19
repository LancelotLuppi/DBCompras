package br.com.dbc.vemser.dbcompras.dto.compra;

import br.com.dbc.vemser.dbcompras.dto.item.ItemDTO;
import br.com.dbc.vemser.dbcompras.entity.ItemEntity;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Set;

@Data
@NoArgsConstructor
public class CompraRelatorioDTO {
    private Integer idCompra;
    private String name;
    private String descricao;
    @JsonFormat(pattern = "dd/MM/yyyy HH:mm")
    private LocalDateTime dataCompra;
    private Double valorTotal;
    private String status;
    private Integer idUser;
    private String nome;

    public CompraRelatorioDTO(Integer idCompra, String name, String descricao, LocalDateTime dataCompra, Double valorTotal, String status, Integer idUser, String nome) {
        this.idCompra = idCompra;
        this.name = name;
        this.descricao = descricao;
        this.dataCompra = dataCompra;
        this.valorTotal = valorTotal;
        this.status = status;
        this.idUser = idUser;
        this.nome = nome;
    }
}
