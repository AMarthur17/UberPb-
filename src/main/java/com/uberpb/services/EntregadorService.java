package com.uberpb.services;

import com.uberpb.model.Entregador;
import com.uberpb.helpers.ValidadoresCadastro;

public class EntregadorService {

    public void validarCadastro(Entregador entregador) throws Exception {
        if (entregador == null) {
            throw new Exception("O entregador não pode ser nulo!");
        }
        if (!ValidadoresCadastro.validarCampoObrigatorio(entregador.getCnh(), "CNH")) {
            throw new Exception("Falha na validação da CNH.");
        }
        if (!ValidadoresCadastro.validarTipoVeiculoEntregador(entregador.getTipoVeiculo())) {
            throw new Exception("Falha na validação do tipo de veículo.");
        }
    }
}