package com.uberpb.cli.menus;

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
            System.out.println("9 - Voltar ao Menu Principal");
            System.out.print("Escolha: ");

            int op = sc.nextInt();
            sc.nextLine();

            switch (op) {
                case 1 -> alternarStatusFuncionamento();
                case 9 -> { return; }
                default -> System.out.println("Opção inválida!");
            }
        }
    }

    private void alternarStatusFuncionamento() {
        restaurante.setAberto(!restaurante.isAberto());
        db.updateRestaurante(restaurante); // Assumindo que você criou o updateRestaurante no DatabaseManager
        System.out.println("✅ Status atualizado para: " + (restaurante.isAberto() ? "ABERTO" : "FECHADO"));
    }
}