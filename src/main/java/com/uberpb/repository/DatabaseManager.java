package com.uberpb.repository;

import com.uberpb.model.*;
import com.uberpb.repository.json.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import com.uberpb.enums.CorridaStatus;

/**
 * Gerenciador principal do banco de dados JSON
 * Coordena operações entre todos os repositórios específicos
 */
public class DatabaseManager {

    private final UserRepository userRepository;
    private final PassageiroRepository passageiroRepository;
    private final MotoristaRepository motoristaRepository;
    private final VeiculoRepository veiculoRepository;
    private final CorridaRepository corridaRepository;
    private final AvaliacaoRepository avaliacaoRepository;
    private final PagamentoRepositoryJSON pagamentoRepository;
    private final HistoricoRepository historicoRepository;
    private final EntregadorRepositoryJSON entregadorRepository;
    private final RestauranteRepositoryJSON restauranteRepository;

    public DatabaseManager() {
        this.userRepository = new UserRepositoryJSON();
        this.passageiroRepository = new PassageiroRepositoryJSON();
        this.motoristaRepository = new MotoristaRepositoryJSON();
        this.veiculoRepository = new VeiculoRepositoryJSON();
        this.corridaRepository = new CorridaRepositoryJSON();
        this.avaliacaoRepository = new AvaliacaoRepository();
        this.pagamentoRepository = new PagamentoRepositoryJSON();
        this.historicoRepository = new HistoricoRepository();
        this.entregadorRepository = new EntregadorRepositoryJSON();
        this.restauranteRepository = new RestauranteRepositoryJSON();
    }

    // ===== OPERAÇÕES DE USUÁRIO =====

    public User saveUser(User user) {
        // Definir data de cadastro se não estiver definida
        if (user.getDataCadastro() == null) {
            user.setDataCadastro(LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")));
        }

        return userRepository.save(user);
    }

    public Optional<User> findUserById(int id) {
        return userRepository.findById(id);
    }

    public Optional<User> findUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public List<User> findAllUsers() {
        return userRepository.findAll();
    }

    public User updateUser(User user) {
        return userRepository.update(user);
    }

    public boolean deleteUser(int id) {
        return userRepository.deleteById(id);
    }

    // ===== OPERAÇÕES DE PASSAGEIRO =====

    public Passageiro savePassageiro(Passageiro passageiro) {
        // Se o passageiro já tem ID, significa que o usuário já existe
        if (passageiro.getId() > 0) {
            // Apenas salvar como passageiro
            return passageiroRepository.save(passageiro);
        } else {
            // Primeiro salvar como usuário
            User user = new User();
            user.setNome(passageiro.getNome());
            user.setSobrenome(passageiro.getSobrenome());
            user.setEmail(passageiro.getEmail());
            user.setTelefone(passageiro.getTelefone());
            user.setTipo("passageiro");
            user.setDataCadastro(LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")));

            User savedUser = saveUser(user);
            passageiro.setId(savedUser.getId());

            // Depois salvar como passageiro
            return passageiroRepository.save(passageiro);
        }
    }

    public Optional<Passageiro> findPassageiroById(int id) {
        return passageiroRepository.findById(id);
    }

    public List<Passageiro> findAllPassageiros() {
        return passageiroRepository.findAll();
    }

    public Passageiro updatePassageiro(Passageiro passageiro) {
        // Atualizar dados do usuário também
        Optional<User> userOpt = findUserById(passageiro.getId());
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            user.setNome(passageiro.getNome());
            user.setSobrenome(passageiro.getSobrenome());
            user.setEmail(passageiro.getEmail());
            user.setTelefone(passageiro.getTelefone());
            updateUser(user);
        }

        return passageiroRepository.update(passageiro);
    }

    public boolean deletePassageiro(int id) {
        // Deletar do repositório de passageiros
        boolean deleted = passageiroRepository.deleteById(id);

        // Deletar do repositório de usuários também
        if (deleted) {
            deleteUser(id);
        }

        return deleted;
    }

    public List<Passageiro> findPassageirosByLocalizacao(String localizacao) {
        return passageiroRepository.findByLocalizacao(localizacao);
    }

    public List<Passageiro> findPassageirosEmCorrida() {
        return passageiroRepository.findEmCorrida();
    }

    // ===== OPERAÇÕES DE MOTORISTA =====

    public Motorista saveMotorista(Motorista motorista) {
        // Se o motorista já tem ID, significa que o usuário já existe
        if (motorista.getId() > 0) {
            // Apenas salvar como motorista
            return motoristaRepository.save(motorista);
        } else {
            // Primeiro salvar como usuário
            User user = new User();
            user.setNome(motorista.getNome());
            user.setSobrenome(motorista.getSobrenome());
            user.setEmail(motorista.getEmail());
            user.setTelefone(motorista.getTelefone());
            user.setTipo("motorista");
            user.setDataCadastro(LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")));

            User savedUser = saveUser(user);
            motorista.setId(savedUser.getId());

            System.out.println("Perfil de motorista criado com sucesso!");
            // Depois salvar como motorista
            return motoristaRepository.save(motorista);
        }
    }

    public Optional<Motorista> findMotoristaById(int id) {
        return motoristaRepository.findById(id);
    }

    public List<Motorista> findAllMotoristas() {
        return motoristaRepository.findAll();
    }

    public Motorista updateMotorista(Motorista motorista) {
        // Atualizar dados do usuário também
        Optional<User> userOpt = findUserById(motorista.getId());
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            user.setNome(motorista.getNome());
            user.setSobrenome(motorista.getSobrenome());
            user.setEmail(motorista.getEmail());
            user.setTelefone(motorista.getTelefone());
            updateUser(user);
        }

        return motoristaRepository.update(motorista);
    }

    public boolean deleteMotorista(int id) {
        // Deletar do repositório de motoristas
        boolean deleted = motoristaRepository.deleteById(id);

        // Deletar do repositório de usuários também
        if (deleted) {
            deleteUser(id);
        }

        return deleted;
    }

    public List<Motorista> findMotoristasByLocalizacao(String localizacao) {
        return motoristaRepository.findByLocalizacao(localizacao);
    }

    public List<Motorista> findMotoristasDisponiveis() {
        return motoristaRepository.findDisponiveis();
    }

    public List<Motorista> findMotoristasAtivos() {
        return motoristaRepository.findAtivos();
    }

    public Optional<Motorista> findMotoristaByCnh(String cnh) {
        return motoristaRepository.findByCnh(cnh);
    }
    
    /**
     * Obtém motorista com média de avaliações atualizada (T16.4)
     * Útil para garantir que sempre temos a média mais recente
     */
    public Optional<Motorista> findMotoristaComMediaAtualizada(int id) {
        Optional<Motorista> motoristaOpt = findMotoristaById(id);
        
        if (motoristaOpt.isPresent()) {
            Motorista motorista = motoristaOpt.get();
            // Garantir que a média está atualizada
            double mediaAtual = calcularMediaAvaliacoes(id);
            int totalAtual = contarAvaliacoes(id);
            
            motorista.setAvaliacaoMedia(mediaAtual);
            motorista.setTotalAvaliacoes(totalAtual);
        }
        
        return motoristaOpt;
    }

    // ===== OPERAÇÕES DE VEÍCULO =====

    public Veiculo saveVeiculo(Veiculo veiculo) {
        return veiculoRepository.save(veiculo);
    }

    public Optional<Veiculo> findVeiculoById(int id) {
        return veiculoRepository.findById(id);
    }

    public List<Veiculo> findAllVeiculos() {
        return veiculoRepository.findAll();
    }

    public Veiculo updateVeiculo(Veiculo veiculo) {
        return veiculoRepository.update(veiculo);
    }

    public boolean deleteVeiculo(int id) {
        return veiculoRepository.deleteById(id);
    }

    public Optional<Veiculo> findVeiculoByPlaca(String placa) {
        return veiculoRepository.findByPlaca(placa);
    }

    public List<Veiculo> findVeiculosByCategoria(String categoria) {
        return veiculoRepository.findByCategoria(categoria);
    }

    public List<Veiculo> findVeiculosByMarca(String marca) {
        return veiculoRepository.findByMarca(marca);
    }

    // ===== OPERAÇÕES DE CORRIDAS =====
    public List<Corrida> findAllCorridas() {
        return corridaRepository.findAll();
    }

    public Optional<Corrida> findCorridaById(int id) {
        return corridaRepository.findById(id);
    }

    public Corrida saveCorrida(Corrida corrida) {
        return corridaRepository.save(corrida);
    }

    public Corrida updateCorrida(Corrida corrida) {
        return corridaRepository.update(corrida);
    }

    public List<Corrida> listarCorridasPendentesPorCategoria(String categoria) {
        return findAllCorridas().stream()
                .filter(c -> c.getStatus() == CorridaStatus.PENDENTE)
                .filter(c -> c.getCategoria() != null && c.getCategoria().getNome().equalsIgnoreCase(categoria))
                .sorted((c1, c2) -> c2.getDataHoraSolicitacao().compareTo(c1.getDataHoraSolicitacao()))
                .collect(Collectors.toList());
    }

    // ===== MÉTODOS DE UTILIDADE =====

    /**
     * Recalcula e atualiza a média de avaliações de TODOS os motoristas (T16.4)
     * Útil para sincronizar dados após importação ou migração
     */
    public void recalcularTodasAsMedias() {
        List<Motorista> todosMotoristas = findAllMotoristas();
        int atualizados = 0;
        
        for (Motorista motorista : todosMotoristas) {
            double mediaAtual = calcularMediaAvaliacoes(motorista.getId());
            int totalAtual = contarAvaliacoes(motorista.getId());
            
            motorista.setAvaliacaoMedia(mediaAtual);
            motorista.setTotalAvaliacoes(totalAtual);
            updateMotorista(motorista);
            atualizados++;
        }
        
        System.out.println("✅ Recalculadas médias de " + atualizados + " motoristas");
    }

    /**
     * Limpa todos os dados do banco
     * Útil para testes
     */
    public void clearAllData() {
        // Implementar limpeza se necessário
        System.out.println("Limpeza de dados não implementada ainda");
    }

    /**
     * Retorna estatísticas do banco de dados
     */
    public String getDatabaseStats() {
        int totalUsers = findAllUsers().size();
        int totalPassageiros = findAllPassageiros().size();
        int totalMotoristas = findAllMotoristas().size();
        int totalVeiculos = findAllVeiculos().size();
        int totalAvaliacoes = findAllAvaliacoes().size();

        return String.format(
                "=== ESTATÍSTICAS DO BANCO DE DADOS ===\n" +
                        "Total de Usuários: %d\n" +
                        "Total de Passageiros: %d\n" +
                        "Total de Motoristas: %d\n" +
                        "Total de Veículos: %d\n" +
                        "Total de Avaliações: %d\n" +
                        "=====================================",
                totalUsers, totalPassageiros, totalMotoristas, totalVeiculos, totalAvaliacoes);
    }

    // ===== OPERAÇÕES DE AVALIAÇÃO =====

    public Avaliacao saveAvaliacao(Avaliacao avaliacao) {
        // Salvar a avaliação
        Avaliacao avaliacaoSalva = avaliacaoRepository.save(avaliacao);
        
        // Atualizar automaticamente a média do avaliado (T16.4)
        atualizarMediaAutomatica(avaliacao.getAvaliadoId());
        
        return avaliacaoSalva;
    }
    
    /**
     * Atualiza automaticamente a média de avaliações do motorista/passageiro (T16.4)
     * Chamado sempre que uma nova avaliação é salva ou atualizada
     */
    private void atualizarMediaAutomatica(int avaliadoId) {
        // Calcular nova média e total
        double novaMedia = calcularMediaAvaliacoes(avaliadoId);
        int novoTotal = contarAvaliacoes(avaliadoId);
        
        // Tentar atualizar como motorista primeiro
        Optional<Motorista> motoristaOpt = findMotoristaById(avaliadoId);
        if (motoristaOpt.isPresent()) {
            Motorista motorista = motoristaOpt.get();
            motorista.setAvaliacaoMedia(novaMedia);
            motorista.setTotalAvaliacoes(novoTotal);
            updateMotorista(motorista);
            return;
        }
        
        // Se não for motorista, tentar como passageiro
        Optional<Passageiro> passageiroOpt = findPassageiroById(avaliadoId);
        if (passageiroOpt.isPresent()) {
            Passageiro passageiro = passageiroOpt.get();
            // Passageiro também pode ter média de avaliações
            // (caso futuramente motoristas avaliem passageiros)
            updatePassageiro(passageiro);
        }
    }

    public Optional<Avaliacao> findAvaliacaoById(int id) {
        return avaliacaoRepository.findById(id);
    }

    public List<Avaliacao> findAllAvaliacoes() {
        return avaliacaoRepository.findAll();
    }

    public List<Avaliacao> findAvaliacoesByCorridaId(int corridaId) {
        return avaliacaoRepository.findByCorridaId(corridaId);
    }

    public List<Avaliacao> findAvaliacoesByAvaliadorId(int avaliadorId) {
        return avaliacaoRepository.findByAvaliadorId(avaliadorId);
    }

    public List<Avaliacao> findAvaliacoesByAvaliadoId(int avaliadoId) {
        return avaliacaoRepository.findByAvaliadoId(avaliadoId);
    }

    public List<Avaliacao> findAvaliacoesDeMotoristas() {
        return avaliacaoRepository.findAvaliacoesDeMotoristas();
    }

    public List<Avaliacao> findAvaliacoesDePassageiros() {
        return avaliacaoRepository.findAvaliacoesDePassageiros();
    }

    public boolean corridaJaAvaliadaPor(int corridaId, int avaliadorId, String tipoAvaliador) {
        return avaliacaoRepository.corridaJaAvaliadaPor(corridaId, avaliadorId, tipoAvaliador);
    }

    public double calcularMediaAvaliacoes(int avaliadoId) {
        return avaliacaoRepository.calcularMediaAvaliacoes(avaliadoId);
    }

    public int contarAvaliacoes(int avaliadoId) {
        return avaliacaoRepository.contarAvaliacoes(avaliadoId);
    }

    public boolean deleteAvaliacaoById(int id) {
        // Antes de deletar, guardar o ID do avaliado para recalcular média depois
        Optional<Avaliacao> avaliacaoOpt = findAvaliacaoById(id);
        int avaliadoId = avaliacaoOpt.map(Avaliacao::getAvaliadoId).orElse(-1);
        
        boolean deletado = avaliacaoRepository.deleteById(id);
        
        // Recalcular média após deletar (T16.4)
        if (deletado && avaliadoId != -1) {
            atualizarMediaAutomatica(avaliadoId);
        }
        
        return deletado;
    }

    public Optional<Avaliacao> updateAvaliacao(Avaliacao avaliacao) {
        Optional<Avaliacao> avaliacaoAtualizada = avaliacaoRepository.update(avaliacao);
        
        // Recalcular média após atualizar (T16.4)
        if (avaliacaoAtualizada.isPresent()) {
            atualizarMediaAutomatica(avaliacao.getAvaliadoId());
        }
        
        return avaliacaoAtualizada;
    }

    // ===== OPERAÇÕES DE PAGAMENTO =====

    public List<com.uberpb.model.pagamento.Pagamento> findAllPagamentos() {
        return pagamentoRepository.listarTodos();
    }

    public Optional<com.uberpb.model.pagamento.Pagamento> findPagamentoById(int id) {
        return pagamentoRepository.buscarPorId(id);
    }

    public List<com.uberpb.model.pagamento.Pagamento> findPagamentosByCorridaId(int corridaId) {
        return pagamentoRepository.listarTodos().stream()
                .filter(p -> p.getCorridaId() == corridaId)
                .collect(Collectors.toList());
    }

    public com.uberpb.model.pagamento.Pagamento savePagamento(com.uberpb.model.pagamento.Pagamento pagamento) {
        return pagamentoRepository.salvar(pagamento);
    }

    public void updatePagamento(com.uberpb.model.pagamento.Pagamento pagamento) {
        pagamentoRepository.atualizar(pagamento);
    }

    // ===== OPERAÇÕES DE HISTÓRICO =====
    // T18.4: Garantir persistência do histórico no banco de dados

    /**
     * Salva o histórico em cache para melhor performance
     */
    public void salvarHistoricoCache(List<HistoricoItem> historico) {
        historicoRepository.salvarHistorico(historico);
    }

    /**
     * Carrega o histórico do cache
     */
    public List<HistoricoItem> carregarHistoricoCache() {
        return historicoRepository.carregarHistorico();
    }

    /**
     * Limpa o cache do histórico (útil quando há mudanças nas corridas)
     */
    public void limparHistoricoCache() {
        historicoRepository.limparHistorico();
    }

    // ===== OPERAÇÕES DE ENTREGADOR =====
    public Entregador saveEntregador(Entregador entregador) {
        return entregadorRepository.save(entregador);
    }

    public Optional<Entregador> findEntregadorById(int id) {
        return entregadorRepository.findById(id);
    }
    public void updateEntregador(com.uberpb.model.Entregador entregador) {
        entregadorRepository.update(entregador);
    }

    // ===== OPERAÇÕES DE RESTAURANTE =====
    public Restaurante saveRestaurante(Restaurante restaurante) {
        return restauranteRepository.save(restaurante);
    }

    public Optional<Restaurante> findRestauranteById(int id) {
        return restauranteRepository.findById(id);
    }

    public void updateRestaurante(com.uberpb.model.Restaurante restaurante) {
        restauranteRepository.update(restaurante);
    }
}