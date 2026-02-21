package com.uberpb.cli.menus;

import com.uberpb.model.*;
import com.uberpb.repository.DatabaseManager;
import com.uberpb.helpers.ValidadoresCadastro;
import com.uberpb.services.CorridaService;

import java.util.Optional;
import java.util.Scanner;

public class MenuPrincipalCLI {

    private Scanner sc;
    private DatabaseManager db;
    private User usuarioLogado;

    public MenuPrincipalCLI(Scanner sc, DatabaseManager db, User usuarioLogado) {
        this.sc = sc;
        this.db = db;
        this.usuarioLogado = usuarioLogado;
    }

    public void exibirMenu() {
        while (true) {
            System.out.println("\n=== Menu Principal ===");
            System.out.println("Usuario logado: " + usuarioLogado.getNome() + " (" + usuarioLogado.getEmail() + ")");
            System.out.println("1 - Cadastrar perfil de Passageiro");
            System.out.println("2 - Cadastrar perfil de Motorista");
            System.out.println("3 - Cadastrar perfil de Entregador");
            System.out.println("4 - Cadastrar perfil de Restaurante");
            System.out.println("5 - Menu Passageiro");
            System.out.println("6 - Menu Motorista");
            System.out.println("7 - Menu Entregador");
            System.out.println("8 - Menu Restaurante");
            System.out.println("9 - Logout");
            System.out.print("Escolha: ");
            int op = sc.nextInt();
            sc.nextLine();

            switch (op) {
                case 1 -> cadastrarPerfilPassageiro();
                case 2 -> cadastrarPerfilMotorista();
                case 3 -> cadastrarPerfilEntregador();
                case 4 -> cadastrarPerfilRestaurante();
                case 5 -> menuPassageiro();
                case 6 -> menuMotorista();
                case 7 -> menuEntregador();
                case 8 -> menuRestaurante();
                case 9 -> {
                    System.out.println("Saindo da conta...");
                    usuarioLogado = null;
                    return;
                }
                default -> System.out.println("Opcao invalida!");
            }
        }
    }

    private void cadastrarPerfilPassageiro() {
        var passageiroOpt = db.findPassageiroById(usuarioLogado.getId());
        if (passageiroOpt.isPresent()) {
            System.out.println("Voce ja possui perfil de passageiro.");
            return;
        }

        System.out.println("\n--- Cadastro de Perfil Passageiro ---");

        int idade = -1;
        while (idade == -1) {
            System.out.print("Idade: ");
            try {
                int inputIdade = sc.nextInt();
                sc.nextLine();
                if (ValidadoresCadastro.validarIdade(inputIdade)) {
                    if (inputIdade >= 18) {
                        idade = inputIdade;
                        System.out.println("✓ Idade válida!");
                    } else {
                        System.out.println("ERRO: Passageiro deve ser maior de 18 anos!");
                    }
                }
            } catch (Exception e) {
                System.out.println("ERRO: Digite uma idade válida!");
                sc.nextLine();
            }
        }

        Passageiro p = new Passageiro(usuarioLogado.getId(), "Nao definida", false);
        p.setUsername(usuarioLogado.getUsername());
        p.setSenha(usuarioLogado.getSenha());
        p.setNome(usuarioLogado.getNome());
        p.setSobrenome(usuarioLogado.getSobrenome());
        p.setEmail(usuarioLogado.getEmail());
        p.setTelefone(usuarioLogado.getTelefone());
        p.setTipo("passageiro");
        p.setDataCadastro(usuarioLogado.getDataCadastro());
        p.setIdade(idade);

        db.savePassageiro(p);
        System.out.println("\n🎉 Perfil de passageiro cadastrado com sucesso!");
    }

    private void cadastrarPerfilMotorista() {
        var motoristaOpt = db.findMotoristaById(usuarioLogado.getId());
        if (motoristaOpt.isPresent()) {
            System.out.println("Voce ja possui perfil de motorista.");
            return;
        }

        System.out.println("\n--- Cadastro de Perfil Motorista ---");

        String cnh = null;
        String validade = null;

        while (cnh == null) {
            System.out.print("CNH (11 dígitos): ");
            String input = sc.nextLine();
            if (ValidadoresCadastro.validarCNH(input)) {
                // Verificar se CNH já existe antes de aceitar
                var existingMotorista = db.findMotoristaByCnh(input);
                if (existingMotorista.isPresent()) {
                    System.out.println("❌ Erro: CNH " + input + " já está cadastrada!");
                    continue;
                }
                cnh = input;
                System.out.println("✓ CNH válida!");
            }
        }

        while (validade == null) {
            System.out.print("Validade da CNH (dd/mm/yyyy): ");
            String input = sc.nextLine();
            if (ValidadoresCadastro.validarDataValidade(input)) {
                validade = input;
                System.out.println("✓ Validade!");
            }
        }

        Motorista m = new Motorista(usuarioLogado.getId(), true, cnh, validade, 0.0, 0, true, "Nao definida",
                "Nao definida");
        m.setUsername(usuarioLogado.getUsername());
        m.setSenha(usuarioLogado.getSenha());
        m.setNome(usuarioLogado.getNome());
        m.setSobrenome(usuarioLogado.getSobrenome());
        m.setEmail(usuarioLogado.getEmail());
        m.setTelefone(usuarioLogado.getTelefone());
        m.setTipo("motorista");
        m.setDataCadastro(usuarioLogado.getDataCadastro());

        db.saveMotorista(m);
    }

    private void menuPassageiro() {
        var passageiroOpt = db.findPassageiroById(usuarioLogado.getId());
        if (passageiroOpt.isEmpty()) {
            System.out.println("Voce ainda nao possui perfil de passageiro. Cadastre primeiro.");
            return;
        }

        Passageiro passageiro = passageiroOpt.get();

        // Lógica de avaliação de corrida pendente (deve ser antes do menu)
        CorridaService corridaService = new CorridaService();
        Optional<Corrida> corridaAvaliacao = corridaService.obterCorridaAtivaPassageiro(passageiro.getId())
                .filter(c -> c.getStatus() == com.uberpb.enums.CorridaStatus.AVALIACAO && !c.isAvaliada_passageiro());
        if (corridaAvaliacao.isPresent()) {
            Corrida corrida = corridaAvaliacao.get();
            System.out.println("\nVocê possui uma corrida anterior aguardando avaliação do motorista!");
            System.out.println("Origem: " + corrida.getOrigem() + " | Destino: " + corrida.getDestino());
            System.out.print("Deseja avaliar o motorista agora? (s/n): ");
            String resp = sc.nextLine().trim().toLowerCase();
            if (resp.equals("s") || resp.equals("sim")) {
                com.uberpb.cli.forms.AvaliarMotoristaCLI avaliarMenu = new com.uberpb.cli.forms.AvaliarMotoristaCLI(sc,
                        corrida, db);
                avaliarMenu.exibirMenu();
                corridaService.updateCorrida(corrida);
                // Se ambos avaliaram, finalizar
                if (corrida.isAvaliada_passageiro() && corrida.isAvaliada_motorista()) {
                    corrida.setStatus(com.uberpb.enums.CorridaStatus.FINALIZADA);
                    corridaService.updateCorrida(corrida);
                }
            }
        }

        MenuPassageiroCLI menuPassageiro = new MenuPassageiroCLI(sc, db, passageiro);
        menuPassageiro.exibirMenu();
    }

    private void menuMotorista() {
        var motoristaOpt = db.findMotoristaById(usuarioLogado.getId());
        if (motoristaOpt.isEmpty()) {
            System.out.println("Voce ainda nao possui perfil de motorista. Cadastre primeiro.");
            return;
        }

        Motorista motorista = motoristaOpt.get();

        // Lógica de avaliação de corrida pendente (deve ser antes do menu)
        CorridaService corridaService = new CorridaService();
        Optional<Corrida> corridaAvaliacao = corridaService.obterCorridaAtivaMotorista(motorista.getId())
                .filter(c -> c.getStatus() == com.uberpb.enums.CorridaStatus.AVALIACAO && !c.isAvaliada_motorista());
        if (corridaAvaliacao.isPresent()) {
            Corrida corrida = corridaAvaliacao.get();
            System.out.println("\nVocê possui uma corrida anterior aguardando avaliação do passageiro!");
            System.out.println("Origem: " + corrida.getOrigem() + " | Destino: " + corrida.getDestino());
            System.out.print("Deseja avaliar o passageiro agora? (s/n): ");
            String resp = sc.nextLine().trim().toLowerCase();
            if (resp.equals("s") || resp.equals("sim")) {
                com.uberpb.cli.forms.AvaliarPassageiroCLI avaliarMenu = new com.uberpb.cli.forms.AvaliarPassageiroCLI(sc, corrida, db);
                avaliarMenu.exibirMenu();
                corridaService.updateCorrida(corrida);
                // Se ambos avaliaram, finalizar
                if (corrida.isAvaliada_passageiro() && corrida.isAvaliada_motorista()) {
                    corrida.setStatus(com.uberpb.enums.CorridaStatus.FINALIZADA);
                    corridaService.updateCorrida(corrida);
                }
            }
        }

        MenuMotoristaCLI menuMotorista = new MenuMotoristaCLI(sc, db, motorista);
        menuMotorista.exibirMenu();
    }

    private void cadastrarPerfilEntregador() {
        var entregadorOpt = db.findEntregadorById(usuarioLogado.getId());
        if (entregadorOpt.isPresent()) {
            System.out.println("Voce ja possui perfil de entregador.");
            return;
        }

        System.out.println("\n--- Cadastro de Perfil Entregador ---");

        String cnh = null;
        String tipoVeiculo = null;

        while (cnh == null) {
            System.out.print("CNH (ou RG para bicicleta): ");
            String input = sc.nextLine();
            if (ValidadoresCadastro.validarCampoObrigatorio(input, "Documento")) {
                cnh = input;
                System.out.println("✓ Documento válido!");
            }
        }

        while (tipoVeiculo == null) {
            System.out.print("Veículo de entrega (MOTO ou BICICLETA): ");
            String input = sc.nextLine().trim().toUpperCase();
            if (ValidadoresCadastro.validarTipoVeiculoEntregador(input)) {
                tipoVeiculo = input;
                System.out.println("✓ Veículo válido!");
            }
        }

        Entregador e = new Entregador();
        e.setId(usuarioLogado.getId());
        e.setUsername(usuarioLogado.getUsername());
        e.setSenha(usuarioLogado.getSenha());
        e.setNome(usuarioLogado.getNome());
        e.setSobrenome(usuarioLogado.getSobrenome());
        e.setEmail(usuarioLogado.getEmail());
        e.setTelefone(usuarioLogado.getTelefone());
        e.setTipo("entregador");
        e.setDataCadastro(usuarioLogado.getDataCadastro());

        e.setCnh(cnh);
        e.setTipoVeiculo(tipoVeiculo);
        e.setAtivo(true);
        e.setDisponivel(false);

        db.saveEntregador(e);
        System.out.println("\n🎉 Perfil de entregador cadastrado com sucesso!");
    }

    private void cadastrarPerfilRestaurante() {
        var restauranteOpt = db.findRestauranteById(usuarioLogado.getId());
        if (restauranteOpt.isPresent()) {
            System.out.println("Voce ja possui perfil de restaurante.");
            return;
        }

        System.out.println("\n--- Cadastro de Perfil Restaurante ---");

        String cnpj = null;
        String razaoSocial = null;
        String endereco = null;

        while (cnpj == null) {
            System.out.print("CNPJ (14 dígitos): ");
            String input = sc.nextLine();
            if (ValidadoresCadastro.validarCNPJ(input)) {
                cnpj = input;
                System.out.println("CNPJ válido!");
            }
        }

        while (razaoSocial == null) {
            System.out.print("Razão Social / Nome do Restaurante: ");
            String input = sc.nextLine();
            if (ValidadoresCadastro.validarCampoObrigatorio(input, "Razão Social")) {
                razaoSocial = input;
                System.out.println("✓ Razão Social válida!");
            }
        }

        while (endereco == null) {
            System.out.print("Endereço completo: ");
            String input = sc.nextLine();
            if (ValidadoresCadastro.validarCampoObrigatorio(input, "Endereço")) {
                endereco = input;
                System.out.println("✓ Endereço válido!");
            }
        }

        Restaurante r = new Restaurante();
        r.setId(usuarioLogado.getId());
        r.setUsername(usuarioLogado.getUsername());
        r.setSenha(usuarioLogado.getSenha());
        r.setNome(usuarioLogado.getNome());
        r.setSobrenome(usuarioLogado.getSobrenome());
        r.setEmail(usuarioLogado.getEmail());
        r.setTelefone(usuarioLogado.getTelefone());
        r.setTipo("restaurante");
        r.setDataCadastro(usuarioLogado.getDataCadastro());

        r.setCnpj(cnpj);
        r.setRazaoSocial(razaoSocial);
        r.setEndereco(endereco);
        r.setAberto(false);

        db.saveRestaurante(r);
        System.out.println("\n🎉 Perfil de restaurante cadastrado com sucesso!");
    }

    private void menuEntregador() {
        var entregadorOpt = db.findEntregadorById(usuarioLogado.getId());
        if (entregadorOpt.isEmpty()) {
            System.out.println("Você ainda não possui perfil de entregador. Cadastre primeiro (Opção 3).");
            return;
        }

        Entregador entregador = entregadorOpt.get();
        MenuEntregadorCLI menuEntregador = new MenuEntregadorCLI(sc, db, entregador);
        menuEntregador.exibirMenu();
    }

    private void menuRestaurante() {
        var restauranteOpt = db.findRestauranteById(usuarioLogado.getId());
        if (restauranteOpt.isEmpty()) {
            System.out.println("Você ainda não possui perfil de restaurante. Cadastre primeiro (Opção 4).");
            return;
        }

        Restaurante restaurante = restauranteOpt.get();
        MenuRestauranteCLI menuRestaurante = new MenuRestauranteCLI(sc, db, restaurante);
        menuRestaurante.exibirMenu();
    }
}
