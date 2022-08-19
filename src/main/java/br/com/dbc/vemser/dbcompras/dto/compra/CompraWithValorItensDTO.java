package br.com.dbc.vemser.dbcompras.dto.compra;

import br.com.dbc.vemser.dbcompras.dto.item.ItemValorizadoDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CompraWithValorItensDTO {
    private Integer idCompra;
    private String name;
    private String descricao;
    private LocalDateTime dataCompra;
    private Double valorTotal;
    private List<ItemValorizadoDTO> itens;
    private String status;
}