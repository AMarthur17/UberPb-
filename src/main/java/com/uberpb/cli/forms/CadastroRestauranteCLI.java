package com.uberpb.cli.forms;

import com.uberpb.model.Restaurante;
import com.uberpb.model.User;
import com.uberpb.repository.DatabaseManager;
import com.uberpb.helpers.ValidadoresCadastro;
import com.uberpb.services.RestauranteService;
import java.util.Scanner;

public class CadastroRestauranteCLI {

    public static void exibirFormulario(Scanner sc, DatabaseManager db, User usuarioLogado) {
        System.out.println("\n=== Cadastro de Perfil: Restaurante ===");

        if (db.findRestauranteById(usuarioLogado.getId()).isPresent()) {
            System.out.println("Esta conta já possui um Restaurante associado!");
            return;
        }

        Restaurante restaurante = new Restaurante();
        restaurante.setId(usuarioLogado.getId());
        restaurante.setNome(usuarioLogado.getNome());
        restaurante.setEmail(usuarioLogado.getEmail());
        restaurante.setTelefone(usuarioLogado.getTelefone());

        System.out.print("Digite o CNPJ do Restaurante (apenas números, 14 dígitos): ");
        String cnpj = sc.nextLine().trim();

        System.out.print("Digite a Razão Social ou Nome do Restaurante: ");
        String razaoSocial = sc.nextLine().trim();

        System.out.print("Digite o Endereço completo: ");
        String endereco = sc.nextLine().trim();

        restaurante.setCnpj(cnpj);
        restaurante.setRazaoSocial(razaoSocial);
        restaurante.setEndereco(endereco);
        restaurante.setAberto(false);

        RestauranteService service = new RestauranteService();
        try {
            service.validarCadastro(restaurante);
            db.saveRestaurante(restaurante);
            System.out.println("Restaurante cadastrado com sucesso!");
        } catch (Exception e) {
            System.out.println("Erro ao cadastrar: " + e.getMessage());
        }
    }
}