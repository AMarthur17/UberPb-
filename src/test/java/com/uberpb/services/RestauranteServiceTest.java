package com.uberpb.services;

import com.uberpb.model.Restaurante;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class RestauranteServiceTest {
    private RestauranteService service;

    @BeforeEach
    void setUp() {
        service = new RestauranteService();
    }

    @Test
    void deveLancarExcecaoSeRestauranteNulo() {
        Exception exception = assertThrows(Exception.class, () -> service.validarCadastro(null));
        assertEquals("O restaurante não pode ser nulo!", exception.getMessage());
    }

    @Test
    void deveLancarExcecaoSeCnpjInvalido() {
        Restaurante restaurante = new Restaurante();
        restaurante.setCnpj("123");

        Exception exception = assertThrows(Exception.class, () -> service.validarCadastro(restaurante));
        assertEquals("Falha na validação do CNPJ.", exception.getMessage());
    }

    @Test
    void deveCalcularAvaliacaoMediaCorretamente() {
        Restaurante restaurante = new Restaurante();
        restaurante.adicionarAvaliacao(4.0f);
        restaurante.adicionarAvaliacao(5.0f);

        assertEquals(2, restaurante.getTotalAvaliacoes());
        assertEquals(4.5, restaurante.getAvaliacaoMedia());
    }

    @Test
    void naoDevePermitirAvaliacaoForaDoLimite() {
        Restaurante restaurante = new Restaurante();

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            restaurante.adicionarAvaliacao(6.0f);
        });

        assertEquals("A nota deve ser entre 1 e 5.", exception.getMessage());
    }
}
