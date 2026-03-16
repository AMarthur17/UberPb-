package com.uberpb.cli.menus;

import com.uberpb.model.Entregador;
import com.uberpb.model.Passageiro;
import com.uberpb.model.Pedido;
import com.uberpb.model.Restaurante;
import com.uberpb.enums.StatusPedido;
import com.uberpb.repository.DatabaseManager;
import com.uberpb.services.LocalizacaoService;

import java.util.List;
import java.util.Scanner;

public class MenuEntregadorCLI {

    private final Scanner sc;
    private final DatabaseManager db;
    private final Entregador entregador;
    private final LocalizacaoService localizacaoService;

    public MenuEntregadorCLI(Scanner sc, DatabaseManager db, Entregador entregador) {
        this.sc = sc;
        this.db = db;
        this.entregador = entregador;
        this.localizacaoService = new LocalizacaoService();
    }

    public void exibirMenu() {
        while (true) {
            System.out.println("\n=== Menu Entregador ===");
            System.out.println("Veículo: " + entregador.getTipoVeiculo());
            System.out.println("Status: " + (entregador.isDisponivel() ? "🟢 ONLINE" : "🔴 OFFLINE"));
            System.out.println("Localização Atual: " + entregador.getLocalizacaoAtual());
            System.out.println("Avaliação: " + String.format("%.1f", entregador.getAvaliacaoMedia()) +
                    " ⭐ (" + entregador.getTotalAvaliacoes() + " avaliações)");
            System.out.println("-------------------------");

            System.out.println("1 - Alternar disponibilidade (Ficar Online/Offline)");
            System.out.println("2 - Atualizar Localização");
            System.out.println("3 - Ver pedidos disponíveis");
            System.out.println("4 - Ver meu pedido atual");
            System.out.println("9 - Voltar ao Menu Principal");

            System.out.print("Escolha: ");
            int op = sc.nextInt();
            sc.nextLine();

            switch (op) {
                case 1 -> alternarDisponibilidade();
                case 2 -> atualizarLocalizacao();
                case 3 -> verPedidosDisponiveis();
                case 4 -> verPedidoAtual();
                case 9 -> { return; }
                default -> System.out.println("Opção inválida!");
            }
        }
    }

    private void alternarDisponibilidade() {
        if (entregador.getLocalizacaoAtual().equals("Não definida") || entregador.getLocalizacaoAtual().equals("Nao definida")) {
            System.out.println("Você precisa definir sua localização antes de ficar online!");
            return;
        }

        entregador.setDisponivel(!entregador.isDisponivel());
        db.updateEntregador(entregador);

        System.out.println("Status atualizado para: " +
                (entregador.isDisponivel() ? "ONLINE" : "OFFLINE"));
    }

    private void atualizarLocalizacao() {
        System.out.println("\n--- Atualizar Localização ---");
        localizacaoService.exibirLocalizacoes();

        System.out.print("Digite o nome da sua localização atual: ");
        String novaLocalizacao = sc.nextLine().trim();

        if (localizacaoService.isLocalizacaoValida(novaLocalizacao)) {
            entregador.setLocalizacaoAtual(
                    localizacaoService.getNome(novaLocalizacao)
            );
            db.updateEntregador(entregador);
            System.out.println("Localização atualizada com sucesso!");
        } else {
            System.out.println("Localização inválida.");
        }
    }

    private void verPedidosDisponiveis() {
        if (!entregador.isDisponivel()) {
            System.out.println("❌ Você precisa estar ONLINE para ver pedidos.");
            return;
        }

        if (temPedidoEmEntrega()) {
            System.out.println("⚠️ Você já está com um pedido em entrega.");
            return;
        }

        List<Pedido> pedidos = db.findAllPedidos().stream()
                .filter(p -> p.getStatus() == StatusPedido.AGUARDANDO_ENTREGADOR)
                .filter(p -> p.getEntregadorId() == null || p.getEntregadorId() == 0)
                .toList();

        if (pedidos.isEmpty()) {
            System.out.println("❌ Nenhum pedido disponível no momento.");
            return;
        }

        System.out.println("\n=== PEDIDOS DISPONÍVEIS ===");

        for (int i = 0; i < pedidos.size(); i++) {
            Pedido p = pedidos.get(i);
            System.out.println((i + 1) + " - Pedido #" + p.getId()
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

        Pedido pedido = pedidos.get(escolha - 1);

        System.out.println("\n--- Ação ---");
        System.out.println("1 - Aceitar pedido");
        System.out.println("2 - Recusar (Voltar à lista)");
        System.out.print("Escolha: ");
        int acao = sc.nextInt();
        sc.nextLine();

        if (acao == 1) {
            pedido.setEntregadorId(entregador.getId());
            pedido.setStatus(StatusPedido.EM_ENTREGA);
            entregador.setDisponivel(false);

            db.updateEntregador(entregador);
            db.updatePedido(pedido);

            System.out.println("🚚 Pedido aceito com sucesso! Agora está EM_ENTREGA.");
            System.out.println("Vá para a opção '4 - Ver meu pedido atual' para detalhes.");
        } else if (acao == 2) {
            System.out.println("❌ Pedido ignorado.");
        } else {
            System.out.println("Opção inválida.");
        }
    }

    private void verPedidoAtual() {
        Pedido pedido = db.findAllPedidos()
                .stream()
                .filter(p ->
                        p.getEntregadorId() != null &&
                                p.getEntregadorId() == entregador.getId() &&
                                p.getStatus() == StatusPedido.EM_ENTREGA
                )
                .findFirst()
                .orElse(null);

        if (pedido == null) {
            System.out.println("Você não tem pedido em entrega.");
            return;
        }

        System.out.println("\n=== MEU PEDIDO ATUAL ===");
        System.out.println("Pedido #" + pedido.getId()
                + " | Total: R$ "
                + String.format("%.2f", pedido.getValorTotal()));

        // ===== VISUALIZAÇÃO DA ROTA =====
        Restaurante restaurante = db.findRestauranteById(pedido.getRestauranteId()).orElse(null);
        Passageiro cliente = db.findPassageiroById(pedido.getPassageiroId()).orElse(null);

        if (restaurante != null && cliente != null) {
            System.out.println("\n=== ROTA DA ENTREGA ===");
            System.out.println("📍 Entregador: " + entregador.getLocalizacaoAtual());
            System.out.println("   ↓");
            System.out.println("🏪 Restaurante: " + restaurante.getEndereco());
            System.out.println("   ↓");
            System.out.println("👤 Cliente: " + cliente.getLocalizacaoAtual());
        }

        System.out.println("\n1 - Finalizar entrega");
        System.out.println("9 - Voltar");

        int op = sc.nextInt();
        sc.nextLine();

        if (op == 1) {
            pedido.setStatus(StatusPedido.ENTREGUE);
            entregador.setDisponivel(true);
            db.updateEntregador(entregador);
            db.updatePedido(pedido);

            System.out.println("✅ Pedido entregue com sucesso!");
            System.out.println("\n========================================");
            System.out.println("🧾 RECIBO DE DELIVERY - UBER EATS");
            System.out.println("Pedido #" + pedido.getId());
            System.out.println("Status Final: " + pedido.getStatus());
            System.out.println("Total da Compra: R$ " + String.format("%.2f", pedido.getValorTotal()));
            System.out.println("Entregador Parceiro: " + entregador.getNome());
            System.out.println("Data/Hora: " + java.time.LocalDateTime.now().format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")));
            System.out.println("========================================\n");
        }
    }

    private boolean temPedidoEmEntrega() {
        return db.findAllPedidos()
                .stream()
                .anyMatch(p ->
                        p.getEntregadorId() != null &&
                                p.getEntregadorId() == entregador.getId() &&
                                p.getStatus() == StatusPedido.EM_ENTREGA
                );
    }
}