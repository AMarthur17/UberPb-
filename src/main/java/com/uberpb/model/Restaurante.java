package com.uberpb.model;

import java.util.ArrayList;
import java.util.List;

public class Restaurante extends User {

    private String cnpj;
    private String razaoSocial;
    private String endereco;
    private boolean aberto;
    private List<Float> avaliacoes;
    private double avaliacaoMedia;
    private int totalAvaliacoes;
    private Cardapio cardapio;

    public Restaurante() {
        super();
        this.aberto = false;
        this.avaliacoes = new ArrayList<>();
        this.avaliacaoMedia = 0.0;
        this.totalAvaliacoes = 0;
        this.cardapio = new Cardapio();
    }

    public String getCnpj() { return cnpj; }
    public void setCnpj(String cnpj) { this.cnpj = cnpj; }

    public String getRazaoSocial() { return razaoSocial; }
    public void setRazaoSocial(String razaoSocial) { this.razaoSocial = razaoSocial; }

    public String getEndereco() { return endereco; }
    public void setEndereco(String endereco) { this.endereco = endereco; }

    public boolean isAberto() { return aberto; }
    public void setAberto(boolean aberto) { this.aberto = aberto; }

    public List<Float> getAvaliacoes() { return avaliacoes; }
    public void setAvaliacoes(List<Float> avaliacoes) {
        this.avaliacoes = avaliacoes;
        recalcularAvaliacaoMedia();
    }

    public Cardapio getCardapio() { return cardapio; }
    public void setCardapio(Cardapio cardapio) { this.cardapio = cardapio; }

    public double getAvaliacaoMedia() { return avaliacaoMedia; }
    public int getTotalAvaliacoes() { return totalAvaliacoes; }

    public void adicionarAvaliacao(float nota) {
        if (nota < 1 || nota > 5) throw new IllegalArgumentException("A nota deve ser entre 1 e 5.");
        if (this.avaliacoes == null) this.avaliacoes = new ArrayList<>();
        this.avaliacoes.add(nota);
        recalcularAvaliacaoMedia();
    }

    private void recalcularAvaliacaoMedia() {
        if (avaliacoes == null || avaliacoes.isEmpty()) {
            this.avaliacaoMedia = 0.0;
            this.totalAvaliacoes = 0;
            return;
        }
        double soma = 0;
        for (float n : avaliacoes) soma += n;
        this.totalAvaliacoes = avaliacoes.size();
        this.avaliacaoMedia = soma / this.totalAvaliacoes;
    }
}