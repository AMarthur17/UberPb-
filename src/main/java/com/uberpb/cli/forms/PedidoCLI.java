package com.uberpb.cli.forms;

import com.uberpb.model.*;
import com.uberpb.repository.DatabaseManager;

import java.util.List;
import java.util.Scanner;

import com.uberpb.enums.StatusPedido;

public class PedidoCLI {

    private final Scanner sc;
    private final DatabaseManager db;
    private final Passageiro passageiro;

    public PedidoCLI(Scanner sc, DatabaseManager db, Passageiro passageiro) {
        this.sc = sc;
        this.db = db;
        this.passageiro = passageiro;
    }

    public void exibirMenu() {

        List<Restaurante> restaurantes = db.findRestaurantesDisponiveis();

        if (restaurantes.isEmpty()) {
            System.out.println("Nenhum restaurante disponível.");
            return;
        }

        System.out.println("\n=== Escolha um Restaurante ===");
        for (int i = 0; i < restaurantes.size(); i++) {
            System.out.println((i + 1) + " - " + restaurantes.get(i).getRazaoSocial());
        }

        System.out.print("Escolha: ");
        int escolhaRest = sc.nextInt();
        sc.nextLine();

        if (escolhaRest < 1 || escolhaRest > restaurantes.size()) {
            System.out.println("Opção inválida.");
            return;
        }

        Restaurante restaurante = restaurantes.get(escolhaRest - 1);

        if (restaurante.getCardapio() == null ||
                restaurante.getCardapio().getItens().isEmpty()) {
            System.out.println("Este restaurante não possui itens.");
            return;
        }

        Pedido pedido = new Pedido();
        pedido.setPassageiroId(passageiro.getId());
        pedido.setRestauranteId(restaurante.getId());
        pedido.setTaxaEntrega(restaurante.getCardapio().getTaxaEntrega());

        while (true) {

            System.out.println("\n=== Cardápio ===");
            List<Item> itens = restaurante.getCardapio().getItens();

            for (int i = 0; i < itens.size(); i++) {
                Item item = itens.get(i);
                System.out.println((i + 1) + " - " + item.getNome() +
                        " | R$ " + String.format("%.2f", item.getPreco()));
            }

            System.out.println("0 - Finalizar Pedido");
            System.out.print("Escolha item: ");

            int escolhaItem = sc.nextInt();
            sc.nextLine();

            if (escolhaItem == 0)
                break;

            if (escolhaItem < 1 || escolhaItem > itens.size()) {
                System.out.println("Item inválido.");
                continue;
            }

            Item itemSelecionado = itens.get(escolhaItem - 1);

            System.out.print("Quantidade: ");
            int quantidade = sc.nextInt();
            sc.nextLine();

            pedido.adicionarItem(itemSelecionado, quantidade);

            System.out.println("Item adicionado!");
        }

        if (pedido.getItens().isEmpty()) {
            System.out.println("Pedido vazio. Cancelado.");
            return;
        }

        pedido.calcularTotal();

        System.out.println("\n=== Resumo do Pedido ===");
        for (ItemPedido ip : pedido.getItens()) {
            System.out.println(ip.getItem().getNome() +
                    " x" + ip.getQuantidade() +
                    " = R$ " + String.format("%.2f", ip.getSubtotal()));
        }

        System.out.println("Taxa entrega: R$ " +
                String.format("%.2f", pedido.getTaxaEntrega()));

        System.out.println("Total: R$ " +
                String.format("%.2f", pedido.getValorTotal()));

        System.out.print("Confirmar pedido? (s/n): ");
        String confirmar = sc.nextLine().toLowerCase();

        if (confirmar.equals("s") || confirmar.equals("sim")) {

            pedido.setStatus(StatusPedido.AGUARDANDO_RESTAURANTE);
            db.savePedido(pedido);

            System.out.println("📦 Pedido enviado ao restaurante!");

            notificarRestaurante(restaurante, pedido);

        } else {
            System.out.println("Pedido cancelado.");
        }
    }

    private void notificarRestaurante(Restaurante restaurante, Pedido pedido) {

        System.out.println("\n🔔 NOVO PEDIDO PARA O RESTAURANTE!");
        System.out.println("Restaurante: " + restaurante.getRazaoSocial());
        System.out.println("Pedido #" + pedido.getId());
        System.out.println("Total: R$ " +
                String.format("%.2f", pedido.getValorTotal()));
        System.out.println("Status: " + pedido.getStatus());
    }

    private void selecionarEntregador(Pedido pedido) {

        List<Entregador> entregadores =
                db.findEntregadoresDisponiveis();

        if (entregadores.isEmpty()) {
            System.out.println("❌ Nenhum entregador disponível.");
            return;
        }

        Entregador entregador = entregadores.get(0);

        pedido.setEntregadorId(entregador.getId());
        pedido.setStatus(StatusPedido.EM_PREPARO);

        // 🔥 MARCAR COMO INDISPONÍVEL
        entregador.setDisponivel(false);
        db.updateEntregador(entregador);

        db.savePedido(pedido);

        System.out.println("🚴 Entregador atribuído: " +
                entregador.getNome());
    }
}