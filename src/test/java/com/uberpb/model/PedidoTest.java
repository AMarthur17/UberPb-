package com.uberpb.model;

import com.uberpb.enums.StatusPedido;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class PedidoTest {

    @Test
    void pedidoDeveIniciarComStatusCriado() {

        Pedido pedido = new Pedido();

        assertEquals(StatusPedido.CRIADO, pedido.getStatus());
    }

    @Test
    void deveAdicionarItemAoPedido() {

        Pedido pedido = new Pedido();
        Item item = new Item("Hamburguer", 20.0, "Artesanal");

        pedido.adicionarItem(item, 2);

        assertEquals(1, pedido.getItens().size());

        double subtotal = pedido.getItens().get(0).getSubtotal();
        assertEquals(40.0, subtotal);
    }

    @Test
    void deveCalcularTotalComTaxaEntrega() {

        Pedido pedido = new Pedido();
        Item item = new Item("Pizza", 50.0, "Grande");

        pedido.adicionarItem(item, 1);
        pedido.setTaxaEntrega(10.0);

        pedido.calcularTotal();

        assertEquals(60.0, pedido.getValorTotal());
    }

    @Test
    void deveRemoverItemDoPedido() {

        Pedido pedido = new Pedido();
        Item item = new Item("Batata", 15.0, "Frita");

        pedido.adicionarItem(item, 1);
        pedido.removerItem(0);

        assertTrue(pedido.getItens().isEmpty());
    }

    @Test
    void devePermitirAlterarStatusDoPedido() {

        Pedido pedido = new Pedido();

        pedido.setStatus(StatusPedido.EM_PREPARO);

        assertEquals(StatusPedido.EM_PREPARO, pedido.getStatus());
    }
}