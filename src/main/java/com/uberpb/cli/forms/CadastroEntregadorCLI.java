package com.uberpb.cli.forms;

import com.uberpb.model.Entregador;
import com.uberpb.model.User;
import com.uberpb.repository.DatabaseManager;
import com.uberpb.helpers.ValidadoresCadastro;
import com.uberpb.services.EntregadorService;
import java.util.Scanner;

public class CadastroEntregadorCLI {

    public static void exibirFormulario(Scanner sc, DatabaseManager db, User usuarioLogado) {
        System.out.println("\n=== Cadastro de Perfil: Entregador ===");

        if (db.findEntregadorById(usuarioLogado.getId()).isPresent()) {
            System.out.println("Você já possui um perfil de Entregador ativo!");
            return;
        }

        Entregador entregador = new Entregador();
        entregador.setId(usuarioLogado.getId());
        entregador.setNome(usuarioLogado.getNome());
        entregador.setSobrenome(usuarioLogado.getSobrenome());
        entregador.setEmail(usuarioLogado.getEmail());
        entregador.setTelefone(usuarioLogado.getTelefone());

        System.out.print("Digite o número da sua CNH (ou RG se for bicicleta): ");
        String cnh = sc.nextLine().trim();

        System.out.print("Qual será o veículo de entrega? (MOTO ou BICICLETA): ");
        String tipoVeiculo = sc.nextLine().trim().toUpperCase();

        entregador.setCnh(cnh);
        entregador.setTipoVeiculo(tipoVeiculo);
        entregador.setAtivo(true);
        entregador.setDisponivel(false);

        EntregadorService service = new EntregadorService();
        try {
            service.validarCadastro(entregador);
            db.saveEntregador(entregador);
            System.out.println("Perfil de Entregador cadastrado com sucesso!");
        } catch (Exception e) {
            System.out.println("Erro ao cadastrar: " + e.getMessage());
        }
    }
}