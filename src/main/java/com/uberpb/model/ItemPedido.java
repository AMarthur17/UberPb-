package com.uberpb.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ItemPedido {

    private Item item;
    private int quantidade;

    public ItemPedido() {}

    public ItemPedido(Item item, int quantidade) {
        this.item = item;
        this.quantidade = quantidade;
    }

    public Item getItem() { return item; }
    public void setItem(Item item) { this.item = item; }

    public int getQuantidade() { return quantidade; }
    public void setQuantidade(int quantidade) { this.quantidade = quantidade; }

    public double getSubtotal() {
        return item.getPreco() * quantidade;
    }
}