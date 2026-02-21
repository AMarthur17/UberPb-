package com.uberpb.model;

import java.util.ArrayList;
import java.util.List;

public class Cardapio {
    private List<Item> itens;
    private double taxaEntrega;
    private int tempoEstimadoMinutos;

    public Cardapio() {
        this.itens = new ArrayList<>();
        this.taxaEntrega = 0.0;
        this.tempoEstimadoMinutos = 0;
    }

    public List<Item> getItens() { return itens; }
    public void setItens(List<Item> itens) { this.itens = itens; }

    public void adicionarItem(Item item) { this.itens.add(item); }

    public double getTaxaEntrega() { return taxaEntrega; }
    public void setTaxaEntrega(double taxaEntrega) { this.taxaEntrega = taxaEntrega; }

    public int getTempoEstimadoMinutos() { return tempoEstimadoMinutos; }
    public void setTempoEstimadoMinutos(int tempoEstimadoMinutos) { this.tempoEstimadoMinutos = tempoEstimadoMinutos; }
}
