package br.com.dbc.vemser.dbcompras.enums;

public enum StatusCompra {

    APROVADO("APROVADO"),
    REPROVADO("REPROVADO");

    private String statusCompra;

    public String getSituacaoCompra() {
        return statusCompra;
    }

    StatusCompra(String statusCompra) {
        this.statusCompra = statusCompra;
    }
}
