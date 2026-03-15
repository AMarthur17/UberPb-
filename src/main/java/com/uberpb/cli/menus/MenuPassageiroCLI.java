package com.uberpb.cli.menus;

import com.uberpb.cli.forms.AdicionarPagamentoCLI;
import com.uberpb.cli.forms.PedidoCLI;
import com.uberpb.cli.forms.SolicitarCorridaCLI;
import com.uberpb.model.Corrida;
import com.uberpb.enums.CorridaStatus;
import com.uberpb.model.Motorista;
import com.uberpb.model.Passageiro;
import com.uberpb.repository.DatabaseManager;
import com.uberpb.services.CorridaService;
import com.uberpb.services.LocalizacaoService;
import com.uberpb.services.MetodoPagamentoService;
import com.uberpb.services.ReciboService;

import java.util.List;
import java.util.Optional;
import java.util.Scanner;

public class MenuPassageiroCLI {
    private final Scanner sc;
    private final DatabaseManager db;
    private final Passageiro passageiro;
    private final LocalizacaoService localizacaoService;
    private final MetodoPagamentoService metodoPagamentoService;
    private final CorridaService corridaService;

    public MenuPassageiroCLI(Scanner sc, DatabaseManager db, Passageiro passageiro) {
        this.sc = sc;
        this.db = db;
        this.passageiro = passageiro;
        this.localizacaoService = new LocalizacaoService();
        this.metodoPagamentoService = new MetodoPagamentoService();
        this.corridaService = new CorridaService();
    }

    public void exibirMenu() {
        while (true) {
            System.out.println("\n=== Menu Passageiro ===");
            System.out.println("1 - Cadastrar metodo de pagamento");
            System.out.println("2 - Ver metodos de pagamento");
            System.out.println("3 - Solicitar corrida");
            System.out.println("4 - Ver historico de corridas");
            System.out.println("5 - Ver localizacao atual");
            System.out.println("6 - Ver status (em corrida ou nao)");
            System.out.println("7 - Atualizar localizacao");
            System.out.println("8 - Ver informacoes do perfil");
            System.out.println("9 - Ver avaliação média");
            System.out.println("10 - Gerar recibo de corrida");
            System.out.println("11 - Fazer pedido Uber Eats");
            System.out.println("12 - Listar restaurantes disponíveis");
            System.out.println("13 - Visualizar cardápio de restaurante");
            System.out.println("14 - Acompanhar meus pedidos (Uber Eats)");
            System.out.println("15 - Voltar");
            System.out.print("Escolha: ");
            int op = sc.nextInt();
            sc.nextLine();

            switch (op) {
                case 1 -> cadastrarMetodoPagamento();
                case 2 -> verMetodosPagamento();
                case 3 -> solicitarCorrida();
                case 4 -> verHistoricoCorridas();
                case 5 -> verLocalizacaoAtual();
                case 6 -> verStatusCorrida();
                case 7 -> atualizarLocalizacao();
                case 8 -> verInformacoesPerfil();
                case 9 -> verAvaliacaoMedia();
                case 10 -> gerarReciboCorrida();
                case 11 -> fazerPedido();
                case 12 -> listarRestaurantesDisponiveis();
                case 13 -> visualizarCardapioRestaurante();
                case 14 -> acompanharPedidos();
                case 15 -> {
                    return;
                }
                case 16 -> avaliarPedidoDelivery();
                default -> System.out.println("Opcao invalida!");
            }
        }

    }
    private void acompanharPedidos() {

        System.out.println("\n=== MEUS PEDIDOS (Uber Eats) ===");

        var pedidos = db.findPedidosByCliente(passageiro.getId());

        if (pedidos.isEmpty()) {
            System.out.println("Você ainda não fez nenhum pedido.");
            System.out.println("\nPressione Enter para continuar...");
            sc.nextLine();
            return;
        }

        for (int i = 0; i < pedidos.size(); i++) {
            var p = pedidos.get(i);

            System.out.println((i + 1) + " - Pedido #" + p.getId());
            System.out.println("    Restaurante ID: " + p.getRestauranteId());
            System.out.println("    Status: " + traduzirStatusPedido(p.getStatus()));
            System.out.println("    Total: R$ " + String.format("%.2f", p.getValorTotal()));
            System.out.println("    ---------------------------");
        }

        System.out.println("\nPressione Enter para continuar...");
        sc.nextLine();
    }
    private String traduzirStatusPedido(com.uberpb.enums.StatusPedido status) {

        return switch (status) {

            case AGUARDANDO_RESTAURANTE -> "🕒 Aguardando confirmação do restaurante";
            case EM_PREPARO -> "🍳 Pedido em preparo";
            case AGUARDANDO_ENTREGADOR -> "🚚 Aguardando entregador";
            case EM_ENTREGA -> "🛵 Saiu para entrega";
            case ENTREGUE -> "✅ Pedido entregue";

            default -> status.name();
        };
    }

    private void fazerPedido() {
        PedidoCLI pedidoCLI = new PedidoCLI(sc, db, passageiro);
        pedidoCLI.exibirMenu();
    }

    /**
     * Exibe a lista de restaurantes disponíveis (abertos) para o cliente
     */
    private void listarRestaurantesDisponiveis() {
        System.out.println("\n--- Restaurantes Disponíveis ---");
        var restaurantes = db.findRestaurantesDisponiveis();
        if (restaurantes.isEmpty()) {
            System.out.println("Nenhum restaurante disponível no momento.");
        } else {
            for (int i = 0; i < restaurantes.size(); i++) {
                var r = restaurantes.get(i);
                System.out.println((i + 1) + ". " + r.getRazaoSocial() + " | " + r.getEndereco());
                System.out.println("   Avaliação: " + String.format("%.1f", r.getAvaliacaoMedia()) + " ⭐");
                System.out.println("   CNPJ: " + r.getCnpj());
                System.out.println("   --------------------------------");
            }
            System.out.println("\nTotal: " + restaurantes.size() + " restaurante(s) disponível(is)");
        }
        System.out.println("\nPressione Enter para continuar...");
        sc.nextLine();
    }

    /**
     * Fluxo de visualização do cardápio de um restaurante disponível
     */
    private void visualizarCardapioRestaurante() {
        System.out.println("\n--- Visualizar Cardápio de Restaurante ---");
        var restaurantes = db.findRestaurantesDisponiveis();
        if (restaurantes.isEmpty()) {
            System.out.println("Nenhum restaurante disponível para visualização de cardápio.");
            System.out.println("\nPressione Enter para continuar...");
            sc.nextLine();
            return;
        }
        for (int i = 0; i < restaurantes.size(); i++) {
            var r = restaurantes.get(i);
            System.out.println((i + 1) + ". " + r.getRazaoSocial() + " | " + r.getEndereco());
        }
        System.out.print("\nEscolha o número do restaurante para ver o cardápio (0 para cancelar): ");
        int escolha = sc.nextInt();
        sc.nextLine();
        if (escolha == 0) return;
        if (escolha < 1 || escolha > restaurantes.size()) {
            System.out.println("Opção inválida!");
            System.out.println("\nPressione Enter para continuar...");
            sc.nextLine();
            return;
        }
        var restaurante = restaurantes.get(escolha - 1);
        var cardapio = restaurante.getCardapio();
        if (cardapio == null || cardapio.getItens() == null || cardapio.getItens().isEmpty()) {
            System.out.println("Este restaurante não possui cardápio cadastrado.");
        } else {
            System.out.println("\n--- Cardápio de " + restaurante.getRazaoSocial() + " ---");
            for (int i = 0; i < cardapio.getItens().size(); i++) {
                var item = cardapio.getItens().get(i);
                System.out.println((i + 1) + ". " + item.getNome() + " - R$ " + String.format("%.2f", item.getPreco()));
                if (item.getDescricao() != null && !item.getDescricao().isEmpty()) {
                    System.out.println("   " + item.getDescricao());
                }
            }
            System.out.println("\nTaxa de entrega: R$ " + String.format("%.2f", cardapio.getTaxaEntrega()));
            System.out.println("Tempo estimado de entrega: " + cardapio.getTempoEstimadoMinutos() + " min");
        }
        System.out.println("\nPressione Enter para continuar...");
        sc.nextLine();
    }

    private void cadastrarMetodoPagamento() {
        AdicionarPagamentoCLI menuPagamento = new AdicionarPagamentoCLI(sc, passageiro);
        menuPagamento.exibirMenu();
    }

    private void verMetodosPagamento() {
        System.out.println("\n--- Métodos de Pagamento Cadastrados ---");

        int total = 0;

        var cartoes = metodoPagamentoService.listarCartoesPorUsuario(passageiro.getId());
        if (!cartoes.isEmpty()) {
            System.out.println("\nCARTÕES:");
            for (int i = 0; i < cartoes.size(); i++) {
                System.out.println((i + 1) + ". " + cartoes.get(i));
                System.out.println("   --------------------------------");
            }
            total += cartoes.size();
        }

        var pixList = metodoPagamentoService.listarPIXPorUsuario(passageiro.getId());
        if (!pixList.isEmpty()) {
            System.out.println("\nPIX:");
            for (int i = 0; i < pixList.size(); i++) {
                System.out.println((i + 1) + ". " + pixList.get(i));
                System.out.println("   --------------------------------");
            }
            total += pixList.size();
        }

        var paypalList = metodoPagamentoService.listarPayPalPorUsuario(passageiro.getId());
        if (!paypalList.isEmpty()) {
            System.out.println("\nPAYPAL:");
            for (int i = 0; i < paypalList.size(); i++) {
                System.out.println((i + 1) + ". " + paypalList.get(i));
                System.out.println("   --------------------------------");
            }
            total += paypalList.size();
        }

        if (total == 0) {
            System.out.println("Nenhum método de pagamento cadastrado ainda.");
        } else {
            System.out.println("\nTotal: " + total + " método(s) de pagamento cadastrado(s)");
        }

        System.out.println("\nPressione Enter para continuar...");
        sc.nextLine();
    }

    private void verHistoricoCorridas() {
        System.out.println("\n=== Histórico Completo de Corridas ===");
        System.out.println("======================================");

        // Usar o novo serviço de histórico integrado
        com.uberpb.services.HistoricoService historicoService =
            new com.uberpb.services.HistoricoService(new com.uberpb.repository.DatabaseManager());

        List<com.uberpb.model.HistoricoItem> historico = historicoService.gerarHistoricoPassageiro(passageiro.getId());

        if (historico.isEmpty()) {
            System.out.println("Nenhuma corrida realizada ainda.");
        } else {
            System.out.println("Deseja filtrar por categoria de carro? (S/N)");
            String resposta = sc.nextLine().trim().toLowerCase();

            List<com.uberpb.model.HistoricoItem> historicoFiltrado = historico;
            if (resposta.equals("s") || resposta.equals("sim")) {
                System.out.println("\nCategorias disponíveis:");
                historico.stream()
                    .map(item -> item.getCategoria())
                    .distinct()
                    .filter(cat -> cat != null)
                    .forEach(categoria -> System.out.println("- " + categoria.getNome()));

                System.out.print("\nDigite o nome da categoria para filtrar: ");
                String categoriaFiltro = sc.nextLine().trim();

                historicoFiltrado = historico.stream()
                    .filter(item -> item.getCategoria() != null &&
                           item.getCategoria().getNome().equalsIgnoreCase(categoriaFiltro))
                    .toList();

                if (historicoFiltrado.isEmpty()) {
                    System.out.println("\nNenhuma corrida encontrada para a categoria: " + categoriaFiltro);
                    return;
                }
                System.out.println("\nMostrando corridas da categoria: " + categoriaFiltro);
            }

            // Exibir histórico completo com dados integrados
            historicoFiltrado.forEach(item -> {
                System.out.println("\n" + item.formatarParaExibicao() + "\n");
            });

            System.out.println("========================================");
            System.out.println("Total de corridas" +
                (historicoFiltrado.size() != historico.size() ? " (filtradas)" : "") +
                ": " + historicoFiltrado.size());

            // Estatísticas resumidas
            if (!historicoFiltrado.isEmpty()) {
                double valorTotal = historicoFiltrado.stream()
                    .mapToDouble(com.uberpb.model.HistoricoItem::getValorFinal)
                    .sum();
                long corridasAvaliadas = historicoFiltrado.stream()
                    .mapToLong(item -> item.isCorridaAvaliada() ? 1 : 0)
                    .sum();

                System.out.println("\n=== RESUMO ===");
                System.out.println("Valor total gasto: R$ " + String.format("%.2f", valorTotal));
                System.out.println("Corridas avaliadas: " + corridasAvaliadas + "/" + historicoFiltrado.size());
                System.out.println("===============");
            }
        }

        System.out.println("\nPressione Enter para continuar...");
        sc.nextLine();
    }

    private void verLocalizacaoAtual() {
        System.out.println("\n--- Localização Atual ---");
        System.out.println("Localização: " + passageiro.getLocalizacaoAtual());
    }

    private void verStatusCorrida() {
        System.out.println("\n--- Status da Corrida ---");

        Optional<Corrida> corridaAtiva = corridaService.obterCorridaAtivaPassageiro(passageiro.getId());

        if (corridaAtiva.isPresent()) {
            Corrida corrida = corridaAtiva.get();
            System.out.println("Status: Em corrida");
            System.out.println("===============================");
            System.out.println("Origem: " + corrida.getOrigem());
            System.out.println("Destino: " + corrida.getDestino());
            System.out.println("Categoria: " + corrida.getCategoria().getNome());
            System.out.println("Status: " + corrida.getStatusString());
            System.out.println("Preço: R$ " + String.format("%.2f", corrida.getPrecoEstimado()));
            if (corrida.getDistancia() > 0) {
                System.out.println("Distância: " + String.format("%.1f km", corrida.getDistancia()));
            }

            if (corrida.getMotoristaId() > 0 && corrida.getStatus() != CorridaStatus.PENDENTE) {
                Optional<Motorista> motoristaOpt = db.findMotoristaById(corrida.getMotoristaId());
                if (motoristaOpt.isPresent()) {
                    Motorista motorista = motoristaOpt.get();
                    System.out.println("\nMotorista Atribuído:");
                    System.out.println("Nome: " + motorista.getNome() + " " + motorista.getSobrenome());
                    System.out.println("Avaliação: " + String.format("%.1f", motorista.getAvaliacaoMedia()));
                } else {
                    System.out.println("\nMotorista não encontrado (ID: " + corrida.getMotoristaId() + ")");
                }
            } else {
                System.out.println("\nAguardando atribuição de motorista...");
            }

            if (corrida.getDataHoraSolicitacao() != null) {
                System.out.println("Solicitada em: " + corrida.getDataHoraSolicitacao()
                        .format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")));
            }
            if (corrida.getDataHoraAceito() != null) {
                System.out.println("Aceita em: " + corrida.getDataHoraAceito()
                        .format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")));

                int tempoRestanteReal = corrida.calcularTempoRestanteReal();
                if (tempoRestanteReal > 0) {
                    System.out.println("Tempo restante: " + tempoRestanteReal + " min");
                } else if (corrida.getStatus() == CorridaStatus.EM_ANDAMENTO) {
                    System.out.println("Tempo esgotado - chegando em breve!");
                }
            }
            if (corrida.getDataHoraFim() != null) {
                System.out.println("Finalizada em: " + corrida.getDataHoraFim()
                        .format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")));
            }
            System.out.println("===============================");
        } else {
            System.out.println("Status: Disponível");
            System.out.println("Você não possui nenhuma corrida ativa no momento.");
        }
    }

    private void atualizarLocalizacao() {
        System.out.println("\n--- Atualizar Localização ---");

        String localizacaoAleatoria = localizacaoService.getLocalizacaoAleatoria();

        if (localizacaoAleatoria != null) {
            passageiro.setLocalizacaoAtual(localizacaoAleatoria);
            db.updatePassageiro(passageiro);
            System.out.println("Nova localização: " + localizacaoAleatoria);
            System.out.println("Localização atualizada com sucesso!");
        } else {
            System.out.println("Erro: Não foi possível obter uma localização aleatória!");
        }
    }

    private void verInformacoesPerfil() {
        System.out.println("\n--- Informações do Perfil ---");
        System.out.println("Nome: " + passageiro.getNome() + " " + passageiro.getSobrenome());
        System.out.println("Email: " + passageiro.getEmail());
        System.out.println("Telefone: " + passageiro.getTelefone());
        System.out.println("Idade: " + passageiro.getIdade() + " anos");
        System.out.println("Localização: " + passageiro.getLocalizacaoAtual());
        System.out.println("Avaliação média: " + passageiro.getAvaliacaoMedia());
        System.out.println("Status: " + (passageiro.isEmCorrida() ? "Em corrida" : "Disponível"));
    }

    private void verAvaliacaoMedia() {
        System.out.println("\n--- Avaliação Média ---");
        System.out.println("Avaliação média: " + String.format("%.1f", passageiro.getAvaliacaoMedia()));
        System.out.println("Total de avaliações: " + passageiro.getTotalAvaliacoes());

        if (passageiro.getTotalAvaliacoes() == 0) {
            System.out.println("\nVocê ainda não recebeu avaliações.");
        } else {
            System.out.println("\nContinue mantendo um bom histórico de viagens!");
        }

        System.out.println("\nPressione Enter para continuar...");
        sc.nextLine();
    }

    private void solicitarCorrida() {
        SolicitarCorridaCLI menuSolicitar = new SolicitarCorridaCLI(sc, passageiro);
        menuSolicitar.exibirMenu();
    }

    private void avaliarPedidoDelivery() {
        System.out.println("\n=== AVALIAR PEDIDO UBER EATS ===");

        var pedidosEntregues = db.findPedidosByCliente(passageiro.getId()).stream()
                .filter(p -> p.getStatus() == com.uberpb.enums.StatusPedido.ENTREGUE)
                .toList();

        if (pedidosEntregues.isEmpty()) {
            System.out.println("Você não possui pedidos finalizados para avaliar no momento.");
            return;
        }

        for (int i = 0; i < pedidosEntregues.size(); i++) {
            var p = pedidosEntregues.get(i);
            System.out.println((i + 1) + " - Pedido #" + p.getId() + " | Restaurante ID: " + p.getRestauranteId() + " | Entregador ID: " + p.getEntregadorId());
        }

        System.out.print("\nEscolha o número do pedido para avaliar (0 para cancelar): ");
        int escolha = sc.nextInt();
        sc.nextLine();

        if (escolha < 1 || escolha > pedidosEntregues.size()) return;

        com.uberpb.model.Pedido pedidoEscolhido = pedidosEntregues.get(escolha - 1);

        // 1. Avaliando o Restaurante
        var restauranteOpt = db.findRestauranteById(pedidoEscolhido.getRestauranteId());
        if (restauranteOpt.isPresent()) {
            var restaurante = restauranteOpt.get();
            System.out.print("Dê uma nota de 1 a 5 para a comida do restaurante " + restaurante.getRazaoSocial() + ": ");
            double notaRestaurante = sc.nextDouble();
            sc.nextLine();

            restaurante.adicionarAvaliacao((float) notaRestaurante);
            db.updateRestaurante(restaurante);
            System.out.println("✅ Restaurante avaliado com sucesso!");
        }

        if (pedidoEscolhido.getEntregadorId() != null && pedidoEscolhido.getEntregadorId() > 0) {
            var entregadorOpt = db.findEntregadorById(pedidoEscolhido.getEntregadorId());
            if (entregadorOpt.isPresent()) {
                var entregador = entregadorOpt.get();
                System.out.print("Dê uma nota de 1 a 5 para o motoboy " + entregador.getNome() + ": ");
                double notaEntregador = sc.nextDouble();
                sc.nextLine();

                entregador.adicionarAvaliacao((float) notaEntregador);
                db.updateEntregador(entregador);
                System.out.println("✅ Entregador parceiro avaliado com sucesso!");
            }
        }
        System.out.println("\nObrigado por ajudar a manter a qualidade do Uber Eats!");
    }

    private void gerarReciboCorrida() {
        System.out.println("\n=== Gerar Recibo de Corrida ===");

        ReciboService reciboService = new ReciboService();
        var corridasFinalizadas = reciboService.listarCorridasFinalizadasDoPassageiro(passageiro.getId());

        if (corridasFinalizadas.isEmpty()) {
            System.out.println("Você não possui corridas finalizadas para gerar recibo.");
            System.out.println("\nPressione Enter para voltar...");
            sc.nextLine();
            return;
        }

        System.out.println("\nCorridas Finalizadas Disponíveis:");
        System.out.println("===============================");

        for (int i = 0; i < corridasFinalizadas.size(); i++) {
            Corrida corrida = corridasFinalizadas.get(i);
            System.out.println((i + 1) + ". " + corrida.getOrigem() + " -> " + corrida.getDestino());
            System.out.println("   Categoria: " + corrida.getCategoria());
            System.out.println("   Valor: R$ " + String.format("%.2f", corrida.getPrecoEstimado()));
            if (corrida.getDataHoraFim() != null) {
                System.out.println("   Data: " + corrida.getDataHoraFim()
                    .format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")));
            }
            System.out.println("   --------------------------------");
        }

        System.out.print("\nEscolha o número da corrida para gerar o recibo (0 para cancelar): ");
        int escolha = sc.nextInt();
        sc.nextLine();

        if (escolha == 0) {
            return;
        }

        if (escolha < 1 || escolha > corridasFinalizadas.size()) {
            System.out.println("Opção inválida!");
            System.out.println("\nPressione Enter para voltar...");
            sc.nextLine();
            return;
        }

        Corrida corridaEscolhida = corridasFinalizadas.get(escolha - 1);

        System.out.println("\nGerando recibo da corrida " + corridaEscolhida.getOrigem() + " -> " + corridaEscolhida.getDestino());
        System.out.println("===============================");

        boolean sucesso = reciboService.exibirRecibo(corridaEscolhida.getId());

        if (sucesso) {
            System.out.println("\nDeseja salvar o recibo em arquivo? (s/n): ");
            String resposta = sc.nextLine().trim().toLowerCase();

            if (resposta.equals("s") || resposta.equals("sim")) {
                System.out.print("Digite o nome do arquivo (deixe vazio para nome padrão): ");
                String nomeArquivo = sc.nextLine().trim();

                if (nomeArquivo.isEmpty()) {
                    nomeArquivo = null;
                }

                boolean salvo = reciboService.salvarReciboEmArquivo(corridaEscolhida.getId(), nomeArquivo);

                if (salvo) {
                    System.out.println("Recibo salvo com sucesso!");
                } else {
                    System.out.println("Erro ao salvar recibo em arquivo.");
                }
            }
        } else {
            System.out.println("Erro ao gerar recibo.");
        }

        System.out.println("\nPressione Enter para voltar...");
        sc.nextLine();
    }
}