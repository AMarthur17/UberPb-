package com.uberpb.services;

import com.uberpb.model.Restaurante;
import com.uberpb.helpers.ValidadoresCadastro;

public class RestauranteService {

    public void validarCadastro(Restaurante restaurante) throws Exception {
        if (restaurante == null) {
            throw new Exception("O restaurante não pode ser nulo!");
        }
        if (!ValidadoresCadastro.validarCNPJ(restaurante.getCnpj())) {
            throw new Exception("Falha na validação do CNPJ.");
        }
        if (!ValidadoresCadastro.validarCampoObrigatorio(restaurante.getRazaoSocial(), "Razão Social")) {
            throw new Exception("A Razão Social é obrigatória.");
        }
        if (!ValidadoresCadastro.validarCampoObrigatorio(restaurante.getEndereco(), "Endereço")) {
            throw new Exception("O Endereço é obrigatório.");
        }
    }
}