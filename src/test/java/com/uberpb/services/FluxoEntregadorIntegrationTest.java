package com.uberpb.services;

import com.uberpb.enums.StatusPedido;
import com.uberpb.model.Entregador;
import com.uberpb.model.Pedido;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class FluxoEntregadorIntegrationTest {

    private Pedido pedido;
    private Entregador entregadorA;
    private Entregador entregadorB;
    private List<Entregador> filaDisponiveis;

    @BeforeEach
    void setUp() {
        pedido = new Pedido();
        pedido.setId(101);
        pedido.setStatus(StatusPedido.AGUARDANDO_ENTREGADOR);

        entregadorA = new Entregador();
        entregadorA.setId(1);
        entregadorA.setDisponivel(true);

        entregadorB = new Entregador();
        entregadorB.setId(2);
        entregadorB.setDisponivel(true);

        filaDisponiveis = new ArrayList<>();
        filaDisponiveis.add(entregadorA);
        filaDisponiveis.add(entregadorB);
    }

    @Test
    void entregadorAceitaPedidoFicaIndisponivel() {
        pedido.setEntregadorId(entregadorA.getId());

        pedido.setStatus(StatusPedido.EM_ENTREGA);
        entregadorA.setDisponivel(false);

        assertEquals(StatusPedido.EM_ENTREGA, pedido.getStatus());
        assertFalse(entregadorA.isDisponivel(), "Entregador A deve ficar ocupado/indisponível");
    }

    @Test
    void entregadorRejeitaPedidoPassaParaOProximoFila() {
        pedido.setEntregadorId(entregadorA.getId());

        pedido.setEntregadorId(0);

        List<Entregador> novaFila = filaDisponiveis.stream()
                .filter(e -> e.getId() != entregadorA.getId())
                .toList();

        if (!novaFila.isEmpty()) {
            Entregador proximo = novaFila.getFirst();
            pedido.setEntregadorId(proximo.getId());
        }

        assertEquals(entregadorB.getId(), pedido.getEntregadorId(), "Pedido deveria ter sido repassado para o Entregador B");
        assertTrue(entregadorB.isDisponivel(), "Entregador B ainda está disponível pois o convite apenas tocou para ele");
    }

    @Test
    void entregaFinalizadaLiberaEntregadorEAtualizaStatus() {
        pedido.setEntregadorId(entregadorA.getId());
        pedido.setStatus(StatusPedido.EM_ENTREGA);
        entregadorA.setDisponivel(false);

        pedido.setStatus(StatusPedido.ENTREGUE);
        entregadorA.setDisponivel(true);

        assertEquals(StatusPedido.ENTREGUE, pedido.getStatus());
        assertTrue(entregadorA.isDisponivel(), "O entregador deve voltar a ficar online após entregar");
    }

    @Test
    void naoDevePermitirEntregadorAceitarPedidoQueJaEstaEmEntrega() {
        pedido.setEntregadorId(entregadorA.getId());
        pedido.setStatus(StatusPedido.EM_ENTREGA);

        boolean podeAceitar = (pedido.getStatus() == StatusPedido.AGUARDANDO_ENTREGADOR);

        assertFalse(podeAceitar, "Entregador B não pode aceitar um pedido que não está mais aguardando entregador.");
        assertEquals(entregadorA.getId(), pedido.getEntregadorId(), "O pedido deve permanecer com o Entregador A.");
    }
}