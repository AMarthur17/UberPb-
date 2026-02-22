package com.uberpb.model;

import com.uberpb.enums.StatusPedido;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Pedido {

    private int id;
    private int passageiroId;
    private int restauranteId;
    private Integer entregadorId;

    private List<ItemPedido> itens;
    private double taxaEntrega;
    private double valorTotal;

    private StatusPedido status; // usando enum correto

    private LocalDateTime dataCriacao;

    public Pedido() {
        this.itens = new ArrayList<>();
        this.status = StatusPedido.CRIADO;
        this.dataCriacao = LocalDateTime.now();
    }

    public void adicionarItem(Item item, int quantidade) {
        itens.add(new ItemPedido(item, quantidade));
    }

    public void removerItem(int index) {
        if (index >= 0 && index < itens.size()) {
            itens.remove(index);
        }
    }

    public void calcularTotal() {
        double soma = 0;
        for (ItemPedido ip : itens) {
            soma += ip.getSubtotal();
        }
        this.valorTotal = soma + taxaEntrega;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getPassageiroId() {
        return passageiroId;
    }

    public void setPassageiroId(int passageiroId) {
        this.passageiroId = passageiroId;
    }

    public int getRestauranteId() {
        return restauranteId;
    }

    public void setRestauranteId(int restauranteId) {
        this.restauranteId = restauranteId;
    }

    public Integer getEntregadorId() {
        return entregadorId;
    }

    public void setEntregadorId(Integer entregadorId) {
        this.entregadorId = entregadorId;
    }

    public void setTaxaEntrega(double taxaEntrega) {
        this.taxaEntrega = taxaEntrega;
    }

    public double getTaxaEntrega() {
        return taxaEntrega;
    }

    public double getValorTotal() {
        return valorTotal;
    }

    public void setValorTotal(double valorTotal) {
        this.valorTotal = valorTotal;
    }

    public StatusPedido getStatus() {
        return status;
    }

    public void setStatus(StatusPedido status) {
        this.status = status;
    }

    public LocalDateTime getDataCriacao() {
        return dataCriacao;
    }

    public void setDataCriacao(LocalDateTime dataCriacao) {
        this.dataCriacao = dataCriacao;
    }

    public List<ItemPedido> getItens() {
        return itens;
    }
}