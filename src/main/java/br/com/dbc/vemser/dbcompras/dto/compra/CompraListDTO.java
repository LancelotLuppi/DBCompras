package br.com.dbc.vemser.dbcompras.dto.compra;

import br.com.dbc.vemser.dbcompras.dto.item.ItemDTO;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class CompraListDTO {
    private Integer idCompra;
    private String name;
    private String descricao;
    private LocalDate dataCompra;
    private Double valorTotal;
    private List<ItemDTO> itens;
    private String status;
}
