package com.uberpb.cli.menus;

import com.uberpb.enums.StatusPedido;
import com.uberpb.model.Cardapio;
import com.uberpb.model.Item;
import com.uberpb.model.Pedido;
import com.uberpb.model.Restaurante;
import com.uberpb.repository.DatabaseManager;

import java.util.Scanner;

public class MenuRestauranteCLI {
    private final Scanner sc;
    private final DatabaseManager db;
    private final Restaurante restaurante;

    public MenuRestauranteCLI(Scanner sc, DatabaseManager db, Restaurante restaurante) {
        this.sc = sc;
        this.db = db;
        this.restaurante = restaurante;
    }

    public void exibirMenu() {
        while (true) {
            System.out.println("\n=== Menu Restaurante (" + restaurante.getRazaoSocial() + ") ===");
            System.out.println("Status: " + (restaurante.isAberto() ? "🟢 ABERTO" : "🔴 FECHADO"));
            System.out.println("Avaliação: " + String.format("%.1f", restaurante.getAvaliacaoMedia()) + " ⭐ (" + restaurante.getTotalAvaliacoes() + " avaliações)");
            System.out.println("-------------------------");
            System.out.println("1 - Abrir / Fechar Restaurante");
            System.out.println("2 - Gerenciar Cardápio");
            System.out.println("3 - Ver pedidos recebidos");
            System.out.println("9 - Voltar ao Menu Principal");
            System.out.print("Escolha: ");

            int op = sc.nextInt();
            sc.nextLine();

            switch (op) {
                case 1 -> alternarStatusFuncionamento();
                case 2 -> gerenciarCardapio();
                case 3 -> gerenciarPedidos();
                case 9 -> { return; }
                default -> System.out.println("Opção inválida!");
            }
        }
    }

    private void gerenciarPedidos() {

        var pedidos = db.findPedidosByRestaurante(restaurante.getId());

        if (pedidos.isEmpty()) {
            System.out.println("Nenhum pedido para este restaurante.");
            return;
        }

        System.out.println("\n=== PEDIDOS ===");

        for (int i = 0; i < pedidos.size(); i++) {
            Pedido p = pedidos.get(i);

            System.out.println((i + 1) + " - Pedido #" + p.getId()
                    + " | Status: " + p.getStatus()
                    + " | Total: R$ "
                    + String.format("%.2f", p.getValorTotal()));
        }

        System.out.print("\nEscolha o número do pedido (0 para voltar): ");
        int escolha = sc.nextInt();
        sc.nextLine();

        if (escolha == 0) return;

        if (escolha < 1 || escolha > pedidos.size()) {
            System.out.println("Opção inválida.");
            return;
        }

        Pedido pedidoSelecionado = pedidos.get(escolha - 1);

        gerenciarPedidoIndividual(pedidoSelecionado);
    }

    private void gerenciarPedidoIndividual(Pedido pedido) {

        System.out.println("\n=== Gerenciar Pedido #" + pedido.getId() + " ===");
        System.out.println("Status atual: " + pedido.getStatus());

        System.out.println("1 - Confirmar (aceitar) pedido");
        System.out.println("2 - Rejeitar pedido");
        System.out.println("3 - Finalizar preparo");
        System.out.println("9 - Voltar");

        int op = sc.nextInt();
        sc.nextLine();

        switch (op) {
            case 1 -> {
                if (pedido.getStatus() == StatusPedido.AGUARDANDO_RESTAURANTE) {
                    pedido.setStatus(StatusPedido.EM_PREPARO);
                    db.updatePedido(pedido);
                    System.out.println("✅ Pedido confirmado. Agora está EM_PREPARO.");
                } else {
                    System.out.println("Pedido não pode ser confirmado neste status.");
                }
            }
            case 2 -> {
                if (pedido.getStatus() == StatusPedido.AGUARDANDO_RESTAURANTE) {
                    pedido.setStatus(StatusPedido.CANCELADO);
                    db.updatePedido(pedido);
                    System.out.println("❌ Pedido rejeitado e cancelado.");
                } else {
                    System.out.println("Pedido não pode ser rejeitado neste status.");
                }
            }
            case 3 -> {
                if (pedido.getStatus() == StatusPedido.EM_PREPARO) {
                    pedido.setStatus(StatusPedido.AGUARDANDO_ENTREGADOR);
                    db.updatePedido(pedido);
                    System.out.println("🍳 Preparo finalizado. Aguardando entregador.");
                } else {
                    System.out.println("Pedido ainda não está em preparo.");
                }
            }
            case 9 -> { return; }
            default -> System.out.println("Opção inválida.");
        }
    }

    private void alternarStatusFuncionamento() {
        restaurante.setAberto(!restaurante.isAberto());
        db.updateRestaurante(restaurante); // Assumindo que você criou o updateRestaurante no DatabaseManager
        System.out.println("✅ Status atualizado para: " + (restaurante.isAberto() ? "ABERTO" : "FECHADO"));
    }

    private void gerenciarCardapio() {
        Cardapio cardapio = restaurante.getCardapio();

        while (true) {
            System.out.println("\n=== Gerenciar Cardápio ===");
            System.out.println("1 - Adicionar item");
            System.out.println("2 - Remover item");
            System.out.println("3 - Definir taxa de entrega");
            System.out.println("4 - Definir tempo estimado");
            System.out.println("5 - Listar itens");
            System.out.println("9 - Voltar");
            System.out.print("Escolha: ");

            int op = sc.nextInt();
            sc.nextLine();

            switch (op) {
                case 1 -> adicionarItem(cardapio);
                case 2 -> removerItem(cardapio);
                case 3 -> definirTaxa(cardapio);
                case 4 -> definirTempo(cardapio);
                case 5 -> listarItens(cardapio);
                case 9 -> {
                    db.updateRestaurante(restaurante);
                    return;
                }
                default -> System.out.println("Opção inválida!");
            }
        }
    }
    private void adicionarItem(Cardapio cardapio) {
        System.out.print("Nome do item: ");
        String nome = sc.nextLine();

        System.out.print("Descrição: ");
        String descricao = sc.nextLine();

        System.out.print("Preço: ");
        double preco = sc.nextDouble();
        sc.nextLine();

        Item item = new Item();
        item.setNome(nome);
        item.setDescricao(descricao);
        item.setPreco(preco);

        cardapio.adicionarItem(item);

        System.out.println("Item adicionado com sucesso!");
    }
    private void definirTaxa(Cardapio cardapio) {
        System.out.print("Nova taxa de entrega: ");
        double taxa = sc.nextDouble();
        sc.nextLine();

        cardapio.setTaxaEntrega(taxa);
        System.out.println("Taxa atualizada!");
    }
    private void definirTempo(Cardapio cardapio) {
        System.out.print("Tempo estimado (minutos): ");
        int tempo = sc.nextInt();
        sc.nextLine();

        cardapio.setTempoEstimadoMinutos(tempo);
        System.out.println("Tempo atualizado!");
    }
    private void listarItens(Cardapio cardapio) {
        if (cardapio.getItens().isEmpty()) {
            System.out.println("Cardápio vazio.");
            return;
        }

        for (int i = 0; i < cardapio.getItens().size(); i++) {
            Item item = cardapio.getItens().get(i);
            System.out.println((i + 1) + ". " + item.getNome() +
                    " - R$ " + String.format("%.2f", item.getPreco()));
        }
    }
    private void removerItem(Cardapio cardapio) {
        listarItens(cardapio);

        System.out.print("Digite o número do item para remover: ");
        int index = sc.nextInt();
        sc.nextLine();

        if (index < 1 || index > cardapio.getItens().size()) {
            System.out.println("Opção inválida!");
            return;
        }

        cardapio.getItens().remove(index - 1);
        System.out.println("Item removido com sucesso!");
    }


}