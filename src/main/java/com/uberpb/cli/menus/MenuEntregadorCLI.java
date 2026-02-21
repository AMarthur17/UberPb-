package com.uberpb.cli.menus;

import com.uberpb.model.Entregador;
import com.uberpb.repository.DatabaseManager;
import com.uberpb.services.LocalizacaoService;

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
            System.out.println("Avaliação: " + String.format("%.1f", entregador.getAvaliacaoMedia()) + " ⭐ (" + entregador.getTotalAvaliacoes() + " avaliações)");
            System.out.println("-------------------------");
            System.out.println("1 - Alternar disponibilidade (Ficar Online/Offline)");
            System.out.println("2 - Atualizar Localização");
            System.out.println("9 - Voltar ao Menu Principal");
            System.out.print("Escolha: ");

            int op = sc.nextInt();
            sc.nextLine();

            switch (op) {
                case 1 -> alternarDisponibilidade();
                case 2 -> atualizarLocalizacao();
                case 9 -> { return; }
                default -> System.out.println("Opção inválida!");
            }
        }
    }

    private void alternarDisponibilidade() {
        if (entregador.getLocalizacaoAtual().equals("Não definida")) {
            System.out.println("Você precisa definir sua localização antes de ficar online!");
            return;
        }
        entregador.setDisponivel(!entregador.isDisponivel());
        db.updateEntregador(entregador);
        System.out.println("Status atualizado para: " + (entregador.isDisponivel() ? "ONLINE" : "OFFLINE"));
    }

    private void atualizarLocalizacao() {
        System.out.println("\n--- Atualizar Localização ---");
        localizacaoService.exibirLocalizacoes();
        System.out.print("Digite o nome da sua localização atual: ");
        String novaLocalizacao = sc.nextLine().trim();

        if (localizacaoService.isLocalizacaoValida(novaLocalizacao)) {
            entregador.setLocalizacaoAtual(localizacaoService.getNome(novaLocalizacao));
            db.updateEntregador(entregador);
            System.out.println("Localização atualizada com sucesso!");
        } else {
            System.out.println("Localização inválida.");
        }
    }
}