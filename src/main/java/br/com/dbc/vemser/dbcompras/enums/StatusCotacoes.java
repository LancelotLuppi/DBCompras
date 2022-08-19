package br.com.dbc.vemser.dbcompras.enums;

public enum StatusCotacoes {

    APROVADO("APROVADO"),
    REPROVADO("REPROVADO"),
    EM_ABERTO("EM_ABERTO");

    private String situacaoCompra;

    public String getSituacaoCompra() {
        return situacaoCompra;
    }

    StatusCotacoes(String situacaoCompra) {
        this.situacaoCompra = situacaoCompra;
    }
}
