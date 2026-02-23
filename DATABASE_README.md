# Sistema de Banco de Dados JSON Organizado

Este projeto implementa um sistema de banco de dados JSON bem organizado, com pastas separadas para cada tipo de entidade.

## Estrutura do Banco de Dados

```
database/
├── users/
│   └── users.json          # Usuários básicos
├── passageiros/
│   └── passageiros.json    # Passageiros
├── motoristas/
│   └── motoristas.json     # Motoristas
├── entregadores/
│   └── entregadores.json   # Entregadores (Delivery)
├── restaurantes/
│   └── restaurantes.json   # Restaurantes (Delivery)
├── pedidos/
│   └── pedidos.json        # Pedidos e Agendamentos do carrinho (Delivery)
├── veiculos/
│   └── veiculos.json       # Veículos
├── corridas/
│   └── corridas.json       # Corridas
└── id_counter.json         # Contador global de IDs
```

## Como Funciona

### 1. Cadastro de Usuário
Quando você cadastra um usuário, ele é salvo em:
- `database/users/users.json` - Dados básicos do usuário

### 2. Cadastro de Passageiro
Quando você cadastra um passageiro, ele é salvo em:
- `database/users/users.json` - Dados básicos (nome, email, telefone)
- `database/passageiros/passageiros.json` - Dados específicos do passageiro

### 3. Cadastro de Motorista
Quando você cadastra um motorista, ele é salvo em:
- `database/users/users.json` - Dados básicos (nome, email, telefone)
- `database/motoristas/motoristas.json` - Dados específicos do motorista

### 4. Cadastro de Veículo
Quando você cadastra um veículo, ele é salvo em:
- `database/veiculos/veiculos.json` - Dados do veículo

### 5. Registro de Pedidos (Delivery)
Quando um passageiro finaliza o seu carrinho de compras de comida, o sistema persiste o estado em:
- `database/pedidos/pedidos.json` - Salva a estrutura completa (restaurante, itens, quantidades e o status).

## Exemplo de Uso

```java
import com.uberpb.repository.DatabaseManager;
import com.uberpb.model.Passageiro;
import com.uberpb.model.Motorista;
import com.uberpb.model.Veiculo;

// Criar instância do gerenciador
DatabaseManager db = new DatabaseManager();

// Cadastrar um passageiro
Passageiro passageiro = new Passageiro(0, "São Paulo - Centro", false);
passageiro.setNome("Maria");
passageiro.setSobrenome("Santos");
passageiro.setEmail("maria@email.com");
passageiro.setTelefone("(11) 99999-9999");

Passageiro savedPassageiro = db.savePassageiro(passageiro);
// Isso salva em users.json E passageiros.json

// Cadastrar um motorista
Motorista motorista = new Motorista(0, true, "12345678901", "2025-12-31", 4.8, 150, true, "São Paulo - Zona Sul");
motorista.setNome("Carlos");
motorista.setSobrenome("Oliveira");
motorista.setEmail("carlos@email.com");
motorista.setTelefone("(11) 88888-8888");

Motorista savedMotorista = db.saveMotorista(motorista);
// Isso salva em users.json E motoristas.json

// Cadastrar um veículo
Veiculo veiculo = new Veiculo(0, "Civic", "Honda", "2020", "Prata", "ABC-1234", "Carro", "Econômico");
Veiculo savedVeiculo = db.saveVeiculo(veiculo);
// Isso salva em veiculos.json
```

## Vantagens do Sistema

1. **Organização**: Cada tipo de entidade tem sua própria pasta e arquivo JSON
2. **Consistência**: Dados básicos são sempre salvos em `users.json`
3. **Flexibilidade**: Fácil de adicionar novos tipos de entidade
4. **Manutenção**: Cada repositório é responsável por sua própria entidade
5. **IDs Únicos**: Sistema global de contadores de ID evita conflitos

## Repositórios Disponíveis

- `UserRepository` - Gerencia usuários básicos
- `PassageiroRepository` - Gerencia passageiros
- `MotoristaRepository` - Gerencia motoristas
- `VeiculoRepository` - Gerencia veículos
- `DatabaseManager` - Coordena todos os repositórios

## Executar Exemplo

Para testar o sistema, execute a classe `DatabaseExample`:

```java
com.uberpb.examples.DatabaseExample
```

Isso criará dados de exemplo em todos os arquivos JSON e mostrará as estatísticas do banco.

## Estrutura dos Arquivos JSON

### users.json
```json
[
  {
    "id": 1,
    "nome": "João",
    "sobrenome": "Silva",
    "email": "joao@email.com",
    "telefone": "(11) 99999-9999",
    "tipo": "usuario",
    "dataCadastro": "2024-01-15T10:30:00"
  }
]
```

### passageiros.json
```json
[
  {
    "id": 2,
    "nome": "Maria",
    "sobrenome": "Santos",
    "email": "maria@email.com",
    "telefone": "(11) 88888-8888",
    "tipo": "passageiro",
    "dataCadastro": "2024-01-15T10:35:00",
    "avaliacaoMedia": 0.0,
    "historicoCorridas": [],
    "localizacaoAtual": "São Paulo - Centro",
    "emCorrida": false,
    "metodosPagamento": ["Cartão de Crédito", "PIX"],
    "idade": 25
  }
]
```

### motoristas.json
```json
[
  {
    "id": 3,
    "nome": "Carlos",
    "sobrenome": "Oliveira",
    "email": "carlos@email.com",
    "telefone": "(11) 77777-7777",
    "tipo": "motorista",
    "dataCadastro": "2024-01-15T10:40:00",
    "ativo": true,
    "cnh": "12345678901",
    "validadeCnh": "2025-12-31",
    "avaliacaoMedia": 4.8,
    "totalAvaliacoes": 150,
    "disponivel": true,
    "localizacaoAtual": "São Paulo - Zona Sul"
  }
]
```

### veiculos.json
```json
[
  {
    "id": 1,
    "modelo": "Civic",
    "marca": "Honda",
    "ano": "2020",
    "cor": "Prata",
    "placa": "ABC-1234",
    "tipo": "Carro",
    "categoria": "Econômico"
  }
]
```

### id_counter.json
```json
{
  "users": 4,
  "passageiros": 2,
  "motoristas": 2,
  "veiculos": 2,
  "corridas": 1
}
```

### entregadores.json
```json
[
  {
    "id": 4,
    "nome": "Pedro",
    "email": "pedro@email.com",
    "tipo": "entregador",
    "ativo": true,
    "cnh": "12345678900",
    "tipoVeiculo": "MOTO",
    "disponivel": true,
    "localizacaoAtual": "Centro",
    "avaliacaoMedia": 0.0,
    "totalAvaliacoes": 0
  }
]
```

### restaurantes.json
```json
[
  {
    "id": 5,
    "nome": "João",
    "email": "joao@email.com",
    "tipo": "restaurante",
    "cnpj": "12345678901234",
    "razaoSocial": "Pizzaria do João",
    "endereco": "Rua Principal, 123 - Centro",
    "aberto": true,
    "avaliacaoMedia": 0.0,
    "totalAvaliacoes": 0
  }
]
```
