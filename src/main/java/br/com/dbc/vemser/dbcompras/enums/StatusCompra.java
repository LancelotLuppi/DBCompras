package br.com.dbc.vemser.dbcompras.enums;

public enum StatusCompra {
    ABERTO("ABERTO"),
    EM_COTACAO("EM_COTACAO"),
    COTADO("COTADO"),
    APROVADO_GESTOR("APROVADO_GESTOR"),
    REPROVADO_GESTOR("REPROVADO_GESTOR"),
    APROVADO_FINANCEIRO("APROVADO_FINANCEIRO"),
    REPROVADO_FINANCEIRO("REPROVADO_FINANCEIRO"),
    FECHADO("FECHADO");

    private String statusCompra;

    StatusCompra(String statusCompra) {
        this.statusCompra = statusCompra;
    }

    public String getStatusCompra() {
        return statusCompra;
    }
}
