package com.uberpb.model;

import com.uberpb.enums.StatusPedido;
import com.uberpb.enums.TipoEntrega;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

public class PedidoAgendamentoECalculoTest {

    private Pedido pedido;
    private Restaurante restaurante;
    private Cardapio cardapio;

    @BeforeEach
    void setUp() {
        restaurante = new Restaurante();
        restaurante.setId(1);

        cardapio = new Cardapio();
        cardapio.setTaxaEntrega(5.50);
        restaurante.setCardapio(cardapio);

        pedido = new Pedido();
        pedido.setRestauranteId(restaurante.getId());
        pedido.setTaxaEntrega(cardapio.getTaxaEntrega());
    }

    @Test
    void deveCalcularTotalCorretamenteComMultiplosItensETaxa() {
        Item item1 = new Item();
        item1.setNome("Pizza");
        item1.setPreco(40.00);

        Item item2 = new Item();
        item2.setNome("Refrigerante");
        item2.setPreco(10.00);

        pedido.adicionarItem(item1, 2);
        pedido.adicionarItem(item2, 1);

        pedido.calcularTotal();

        assertEquals(95.50, pedido.getValorTotal(), 0.01);
    }

    @Test
    void deveMudarParaAguardandoRestauranteAoConfirmar() {
        Item item1 = new Item();
        item1.setNome("Teste");
        item1.setPreco(10.0);

        pedido.adicionarItem(item1, 1);

        assertEquals(StatusPedido.CRIADO, pedido.getStatus());

        pedido.setStatus(StatusPedido.AGUARDANDO_RESTAURANTE);
        assertEquals(StatusPedido.AGUARDANDO_RESTAURANTE, pedido.getStatus());
    }

    @Test
    void agendamentoFuturoDeveSerPermitido() {
        LocalDateTime dataFutura = LocalDateTime.now().plusDays(1);
        pedido.setTipoEntrega(TipoEntrega.AGENDADO);

        assertDoesNotThrow(() -> pedido.agendarPara(dataFutura));
        assertEquals(dataFutura, pedido.getDataAgendamento());
    }

    @Test
    void agendamentoPassadoDeveLancarExcecao() {
        LocalDateTime dataPassada = LocalDateTime.now().minusHours(2);
        pedido.setTipoEntrega(TipoEntrega.AGENDADO);

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            pedido.agendarPara(dataPassada);
        });

        assertTrue(exception.getMessage().contains("passado") || exception.getMessage().toLowerCase().contains("futura"));
    }
}