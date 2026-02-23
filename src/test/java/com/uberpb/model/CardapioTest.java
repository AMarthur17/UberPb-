package com.uberpb.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class CardapioTest {
    private Cardapio cardapio;

    @BeforeEach
    void setUp() {
        cardapio = new Cardapio();
    }

    @Test
    void deveAdicionarItemAoCardapio() {
        Item item = new Item("Hambúrguer", 25.0, "Pão, carne e queijo");
        cardapio.adicionarItem(item);

        assertEquals(1, cardapio.getItens().size());
        assertEquals("Hambúrguer", cardapio.getItens().get(0).getNome());
    }

    @Test
    void deveRemoverItemDoCardapio() {
        Item item1 = new Item("Refrigerante", 5.0, "Lata");
        Item item2 = new Item("Suco", 7.0, "Copo");

        cardapio.adicionarItem(item1);
        cardapio.adicionarItem(item2);

        cardapio.removerItem(item1);

        assertEquals(1, cardapio.getItens().size());
        assertEquals("Suco", cardapio.getItens().get(0).getNome());
    }

    @Test
    void deveSetarEObterTaxaDeEntrega() {
        cardapio.setTaxaEntrega(12.50);
        assertEquals(12.50, cardapio.getTaxaEntrega());
    }
}
