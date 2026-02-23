package com.uberpb.services;

import com.uberpb.model.Entregador;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class EntregadorServiceTest {
    private EntregadorService service;

    @BeforeEach
    void setUp() {
        service = new EntregadorService();
    }

    @Test
    void deveLancarExcecaoSeEntregadorNulo() {
        Exception exception = assertThrows(Exception.class, () -> service.validarCadastro(null));
        assertEquals("O entregador não pode ser nulo!", exception.getMessage());
    }

    @Test
    void deveLancarExcecaoSeCnhInvalida() {
        Entregador entregador = new Entregador();
        entregador.setCnh("");

        Exception exception = assertThrows(Exception.class, () -> service.validarCadastro(entregador));
        assertEquals("Falha na validação da CNH.", exception.getMessage());
    }

    @Test
    void deveLancarExcecaoSeVeiculoInvalido() {
        Entregador entregador = new Entregador();
        entregador.setCnh("12345678901");
        entregador.setTipoVeiculo("PATINETE");

        Exception exception = assertThrows(Exception.class, () -> service.validarCadastro(entregador));
        assertEquals("Falha na validação do tipo de veículo.", exception.getMessage());
    }
}
