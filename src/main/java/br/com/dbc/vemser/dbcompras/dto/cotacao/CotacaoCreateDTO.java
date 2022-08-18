package br.com.dbc.vemser.dbcompras.dto.cotacao;

import lombok.Data;

import java.util.List;

@Data
public class CotacaoCreateDTO {

    private Integer idCompra;
    private String nome;
    private List<CotacaoValorItensDTO> listaDeValores;
    private String anexo;

}
