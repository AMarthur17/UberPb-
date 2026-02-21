package com.uberpb.model;

import java.util.ArrayList;
import java.util.List;

public class Entregador extends User {

    private boolean ativo;
    private String cnh;
    private String validadeCnh;
    private String tipoVeiculo;
    private List<Float> avaliacoes;
    private double avaliacaoMedia;
    private int totalAvaliacoes;
    private boolean disponivel;
    private String localizacaoAtual;

    public Entregador() {
        super();
        this.ativo = false;
        this.avaliacoes = new ArrayList<>();
        this.avaliacaoMedia = 0.0;
        this.totalAvaliacoes = 0;
        this.disponivel = false;
        this.localizacaoAtual = "Não definida";
    }

    public boolean isAtivo() { return ativo; }
    public void setAtivo(boolean ativo) { this.ativo = ativo; }

    public String getCnh() { return cnh; }
    public void setCnh(String cnh) { this.cnh = cnh; }

    public String getValidadeCnh() { return validadeCnh; }
    public void setValidadeCnh(String validadeCnh) { this.validadeCnh = validadeCnh; }

    public String getTipoVeiculo() { return tipoVeiculo; }
    public void setTipoVeiculo(String tipoVeiculo) { this.tipoVeiculo = tipoVeiculo; }

    public List<Float> getAvaliacoes() { return avaliacoes; }
    public void setAvaliacoes(List<Float> avaliacoes) {
        this.avaliacoes = avaliacoes;
        recalcularAvaliacaoMedia();
    }

    public double getAvaliacaoMedia() { return avaliacaoMedia; }
    public int getTotalAvaliacoes() { return totalAvaliacoes; }

    public boolean isDisponivel() { return disponivel; }
    public void setDisponivel(boolean disponivel) { this.disponivel = disponivel; }

    public String getLocalizacaoAtual() { return localizacaoAtual; }
    public void setLocalizacaoAtual(String localizacaoAtual) { this.localizacaoAtual = localizacaoAtual; }

    public void adicionarAvaliacao(float nota) {
        if (nota < 1 || nota > 5) {
            throw new IllegalArgumentException("A nota deve ser entre 1 e 5.");
        }
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