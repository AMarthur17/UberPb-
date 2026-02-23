package com.uberpb.model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class RestaurantTest {
    @Test
    void deveIniciarComMediaZeroETotalZero() {
        Restaurante rest = new Restaurante();
        assertEquals(0.0, rest.getAvaliacaoMedia());
        assertEquals(0, rest.getTotalAvaliacoes());
    }

    @Test
    void deveCalcularMediaDeVariasAvaliacoes() {
        Restaurante rest = new Restaurante();
        rest.adicionarAvaliacao(4.0f);
        rest.adicionarAvaliacao(5.0f);
        rest.adicionarAvaliacao(3.0f);

        assertEquals(4.0, rest.getAvaliacaoMedia());
        assertEquals(3, rest.getTotalAvaliacoes());
    }

    @Test
    void deveMudarStatusAberto() {
        Restaurante rest = new Restaurante();
        assertFalse(rest.isAberto());

        rest.setAberto(true);
        assertTrue(rest.isAberto());
    }
}
