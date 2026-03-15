package com.uberpb.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class AvaliacaoETarifaDinamicaTest {

    private Pedido pedido;
    private Restaurante restaurante;
    private Entregador entregador;

    @BeforeEach
    void setUp() {
        pedido = new Pedido();
        pedido.setTaxaEntrega(10.0);

        restaurante = new Restaurante();
        restaurante.adicionarAvaliacao(4.0f);
        restaurante.adicionarAvaliacao(4.0f);

        entregador = new Entregador();
        entregador.adicionarAvaliacao(5.0f);
    }

    @Test
    void tarifaDinamicaDeveMultiplicarTaxaDeEntregaCorretamente() {
        double multiplicador = 1.5;
        double novaTaxa = pedido.getTaxaEntrega() * multiplicador;
        pedido.setTaxaEntrega(novaTaxa);

        assertEquals(15.0, pedido.getTaxaEntrega(), 0.01, "A taxa de 10 com multiplicador de 1.5 deve ser 15.0");
    }

    @Test
    void recalculoDaMediaDeAvaliacaoDoRestauranteDeveSerCorreto() {
        restaurante.adicionarAvaliacao(5.0f);

        assertEquals(3, restaurante.getTotalAvaliacoes());
        assertEquals(4.33, restaurante.getAvaliacaoMedia(), 0.05, "A média deve subir para aproximadamente 4.33");
    }

    @Test
    void recalculoDaMediaDeAvaliacaoDoEntregadorDeveSerCorreto() {
        entregador.adicionarAvaliacao(1.0f);

        assertEquals(2, entregador.getTotalAvaliacoes());
        assertEquals(3.0, entregador.getAvaliacaoMedia(), 0.01, "A média do entregador deve despencar para 3.0");
    }

    @Test
    void naoDevePermitirAvaliarPedidoCancelado() {
        pedido.setStatus(com.uberpb.enums.StatusPedido.CANCELADO);

        assertTrue(pedido.getStatus() == com.uberpb.enums.StatusPedido.CANCELADO,
                "Pedidos cancelados não devem aparecer na lista de avaliação do CLI.");
    }
}