# Sistema UberPB - Documentação Completa

## 📋 Visão Geral

O **UberPB** é um sistema multiplataforma de mobilidade e delivery desenvolvido em Java com interface CLI. O sistema simula o funcionamento de aplicativos como Uber e UberEats, integrando passageiros, motoristas, restaurantes e entregadores em um único ecossistema, com atribuição baseada em proximidade e gestão por JSON.

## 🏗️ Arquitetura do Sistema

### Estrutura de Packages
- **`com.uberpb.model`**: Entidades principais (User, Passageiro, Motorista, Corrida, Categoria, Veiculo)
- **`com.uberpb.repository`**: Camada de persistência com padrão Repository
- **`com.uberpb.services`**: Serviços de negócio (LocalizacaoService, EstimativaService, CorridaService)
- **`com.uberpb.cli.menus`**: Interfaces de usuário via linha de comando
- **`com.uberpb.cli.forms`**: Formulários para cadastros e validações

### Padrões de Design Utilizados
- **Repository Pattern**: Para abstração da camada de dados
- **Service Layer**: Para lógica de negócio
- **Observer Pattern**: Para notificações de mudanças em categorias
- **Strategy Pattern**: Para diferentes tipos de veículos e cálculos de preço

## 🚀 Principais Funcionalidades

### 1. Sistema de Autenticação e Perfis
- **Login/Logout**: Autenticação por email e senha
- **Cadastro de Usuários**: Base comum para passageiros e motoristas
- **Perfil Duplo**: Usuários podem ser passageiros E motoristas simultaneamente
- **Validações**: CPF, email, idade, CNH, etc.
- ### Expansão Delivery (UberEats)
- **Cadastro de Entregador**: Validação estrita de documentos (CNH/RG) e tipo de veículo (Moto ou Bicicleta).
- **Cadastro de Restaurante**: Validação de CNPJ (14 dígitos) e gestão de status de funcionamento (Aberto/Fechado).
- **Menus Específicos**: Gestão de disponibilidade e localização em tempo real para entregadores.
- **Gestão de Pedidos e Carrinho**: Criação das entidades `Pedido`, `ItemPedido`, `Cardapio` e `Item`, permitindo a adição e remoção de produtos, com cálculo automático do valor total e taxa de entrega.
- **Agendamento de Pedidos**: Suporte nativo para flexibilidade de entregas através do enumerador `TipoEntrega` (`IMEDIATO` ou `AGENDADO`). O sistema possui blindagem de regras de negócio, lançando exceções caso o usuário tente agendar uma entrega para uma data/hora no passado.
- **Tratamento de Erros e Resiliência**: Os menus interativos (CLI) foram blindados com blocos `try-catch` para converter entradas de texto com segurança, evitando quebras (como `InputMismatchException` ou `DateTimeParseException`) durante a digitação do usuário.
- **Qualidade de Software**: Cobertura de testes superior a 80% utilizando JUnit nas regras de negócio e validações das entidades de Delivery.

### 2. Gestão de Passageiros
- ✅ Cadastro de métodos de pagamento
- ✅ Atualização de localização
- ✅ Histórico de corridas
- ✅ Visualização de status atual
- ✅ Informações do perfil

### 3. Gestão de Motoristas
- ✅ Cadastro de CNH com validação de validade
- ✅ Cadastro de veículos por categoria
- ✅ Controle de disponibilidade
- ✅ Sistema de avaliações
- ✅ Notificações de corridas
- ✅ Aceitar/recusar corridas
- ✅ Finalizar corridas

### 4. Sistema de Corridas Inteligente

#### Categorias Disponíveis
- **UberX**: Categoria básica (multiplicador 1.0x)
- **Comfort**: Categoria confortável (multiplicador 1.2x)
- **Black**: Categoria premium (multiplicador 1.5x)
- **Bag**: Categoria para entregas (multiplicador 0.8x)

#### Sistema de Localização
- **Coordenadas Baseadas em JSON**: Sistema próprio de localizações
- **Localizações Pré-definidas**: Aeroporto, Shopping, Hospital, Universidade, Centro, Parque, Praia
- **Cálculo de Distância**: Baseado em diferença de coordenadas
- **Atribuição Aleatória**: Novas localizações são atribuídas aleatoriamente

## 🎯 Fluxo Completo de Solicitação de Corrida

### 1. **Iniciação pelo Passageiro**
```
Passageiro acessa Menu Passageiro → Opção "2 - Solicitar corrida"
```

### 2. **Verificações Preliminares**
- ❌ **Bloqueio por Corrida Ativa**: Sistema verifica se passageiro já possui corrida em andamento
- ✅ **Liberação**: Apenas passageiros sem corrida ativa podem solicitar

### 3. **Seleção de Origem**
```
Opções disponíveis:
1 - Usar localização atual do passageiro
2 - Escolher outra localização da lista disponível
```

### 4. **Seleção de Destino**
- Exibição de todas as localizações disponíveis
- Validação se localização existe no sistema
- Cálculo automático de distância entre origem e destino

### 5. **Apresentação de Categorias e Preços**
```
Sistema exibe:
- Tempo estimado da viagem (≈ X min)
- Lista de categorias com preços em tempo real
- Cálculo: Distância × Preço base × Multiplicador da categoria
```

### 6. **Algoritmo de Atribuição Automática**

#### Critérios de Seleção do Motorista:
1. **Disponibilidade**: `motorista.isDisponivel() == true`
2. **Status Ativo**: `motorista.isAtivo() == true`
3. **Sem Corrida Ativa**: Não possui corrida em andamento/pendente
4. **Categoria Compatível**: Mesma categoria do veículo solicitado
5. **Proximidade**: Menor distância euclidiana da origem

#### Processo de Seleção:
```java
1. Filtrar motoristas disponíveis na categoria
2. Calcular distância de cada motorista até origem
3. Ordenar por proximidade (menor distância primeiro)
4. Selecionar o mais próximo
5. Atribuir automaticamente
```

### 7. **Confirmação e Criação da Corrida**
```
Se motorista encontrado:
✅ Corrida criada com status PENDENTE
✅ Motorista automaticamente atribuído
✅ Passageiro recebe confirmação com dados do motorista
✅ Preço final calculado e exibido
```

```
Se nenhum motorista disponível:
❌ Mensagem de erro
❌ Sugestão para tentar outra categoria
```

## 🔄 Sistema de Reatribuição Inteligente

### Quando Motorista Recusa Corrida:
1. **Busca Automática**: Sistema procura próximo motorista mais próximo
2. **Exclusão do Anterior**: Motorista que recusou é excluído da nova busca
3. **Reatribuição**: Corrida automaticamente transferida
4. **Notificação**: Novo motorista recebe notificação
5. **Cancelamento Automático**: Se nenhum motorista disponível, corrida é cancelada

### Fluxo de Reatribuição:
```
Motorista A recusa → Sistema busca Motorista B (mais próximo)
Se Motorista B existe → Reatribui corrida
Se não existe → Cancela corrida e libera passageiro
```

## ⏱️ Sistema Temporal e Estados

### Estados da Corrida:
- **PENDENTE**: Aguardando aceitação do motorista
- **EM_ANDAMENTO**: Motorista aceitou e está realizando a corrida
- **FINALIZADA**: Corrida concluída com sucesso
- **CANCELADA**: Corrida cancelada

### Controle Temporal:
- **dataHoraSolicitacao**: Timestamp da solicitação
- **dataHoraAceito**: Quando motorista aceitou
- **tempoRestante**: Tempo estimado restante (calculado em tempo real)
- **Finalização Automática**: Corridas expiradas são finalizadas automaticamente

### Gestão de Status dos Usuários:
```
Ao INICIAR corrida:
- Passageiro: emCorrida = true
- Motorista: disponivel = false

Ao FINALIZAR/CANCELAR corrida:
- Passageiro: emCorrida = false  
- Motorista: disponivel = true
```

## 🎮 Interface de Usuário (CLI)

### Menu Principal
```
1 - Cadastrar perfil de Passageiro
2 - Cadastrar perfil de Motorista  
3 - Menu Passageiro
4 - Menu Motorista
9 - Logout
```

### Menu Passageiro
```
1 - Cadastrar método de pagamento
2 - Solicitar corrida ⭐
3 - Ver histórico de corridas
4 - Ver localização atual
5 - Ver status (em corrida ou não)
6 - Atualizar localização
7 - Ver informações do perfil
8 - Voltar
```

### Menu Motorista
```
1 - Ver status ativo
2 - Ver avaliação média
3 - Ver total de avaliações
4 - Ver localização atual
5 - Atualizar localização
6 - Ver informações do perfil
7 - Ver CNH e validade
8 - Ver status disponibilidade
9 - Cadastrar veículo
10 - Notificações de corrida ⭐
11 - Finalizar corrida atual ⭐
12 - Voltar
```

## 💾 Persistência de Dados

### Estrutura de Arquivos JSON:
```
database/
├── users/users.json          # Usuários base
├── passageiros/passageiros.json  # Perfis de passageiros
├── motoristas/motoristas.json    # Perfis de motoristas
├── corridas/corridas.json        # Corridas do sistema
├── veiculos/veiculos.json        # Veículos cadastrados
└── localizacoes.json             # Mapa de localizações
```

### Recursos de Persistência:
- **Auto-save**: Dados salvos automaticamente após cada operação
- **Estrutura Organizada**: Separação por tipo de entidade
- **IDs Únicos**: Geração automática de identificadores
- **Relacionamentos**: Via IDs (passageiroId, motoristaId, etc.)

## 🚨 Recursos Especiais

### 1. **Sistema de Notificações para Motoristas**
- Lista corridas atribuídas especificamente ao motorista
- Informações detalhadas: origem, destino, preço, distância
- Tempo estimado de duração
- Aceitar/recusar com feedback em tempo real

### 2. **Algoritmo de Proximidade**
- Cálculo baseado em coordenadas
- Busca sempre pelo motorista mais próximo
- Sistema inteligente de fallback

### 3. **Controle de Estado Consistente**
- Validações para evitar corridas duplicadas
- Sincronização de status entre passageiros e motoristas
- Liberação automática de recursos

### 4. **Estimativa de Preços Dinâmica**
- Cálculo em tempo real baseado em:
  - Distância real entre pontos
  - Multiplicador da categoria
  - Preço base configurável

## 🔧 Casos de Uso Principais

### Caso de Uso 1: Solicitação de Corrida Bem-Sucedida
```
1. Passageiro solicita corrida
2. Sistema encontra motorista disponível
3. Corrida atribuída automaticamente
4. Motorista recebe notificação
5. Motorista aceita corrida
6. Status atualizado para EM_ANDAMENTO
7. Motorista finaliza corrida
8. Status atualizado para FINALIZADA
9. Ambos ficam disponíveis para novas corridas
```

### Caso de Uso 2: Reatribuição por Recusa
```
1. Passageiro solicita corrida
2. Motorista A recebe atribuição
3. Motorista A recusa corrida
4. Sistema busca Motorista B
5. Corrida reatribuída para Motorista B
6. Processo continua normalmente
```

### Caso de Uso 3: Cancelamento por Falta de Motoristas
```
1. Passageiro solicita corrida
2. Nenhum motorista disponível na categoria
3. Sistema retorna erro imediatamente
OU
1. Motorista recusa corrida
2. Nenhum outro motorista disponível
3. Sistema cancela corrida automaticamente
4. Passageiro liberado para nova solicitação
```

## ✨ Diferenciais do Sistema

1. **Atribuição Automática Inteligente**: Elimina necessidade de busca manual
2. **Sistema de Reatribuição**: Garantia de service level
3. **Controle Temporal**: Gestão automatizada do ciclo de vida das corridas
4. **Interface Intuitiva**: CLI organizada e user-friendly
5. **Arquitetura Limpa**: Separação clara de responsabilidades
6. **Persistência Robusta**: Sistema de arquivos JSON estruturado
7. **Validações Completas**: Prevenção de estados inconsistentes

Este sistema representa uma implementação completa e funcional de uma plataforma de transporte por aplicativo, com algoritmos inteligentes de matching e uma arquitetura preparada para escalabilidade.
## 🍔 Expansão UberEats – Módulo de Delivery

### 📋 Visão Geral

O sistema **UberPB** foi expandido para incluir um módulo completo de delivery, semelhante ao UberEats.

Agora passageiros podem:

- Realizar pedidos em restaurantes cadastrados
- Acompanhar o status da preparação
- Visualizar quando saiu para entrega
- Receber o pedido por entregadores cadastrados
- Efetuar pedidos de forma imediata ou agendada

A expansão mantém os mesmos princípios arquiteturais do módulo de corridas:

- Persistência em JSON
- Controle de estados via Enum
- Separação por camadas (Model, Repository, CLI)
- Regras de negócio centralizadas nas entidades


## 🆕 Novas Entidades

### 📦 Pedido

Representa um pedido realizado por um passageiro.

#### Principais Atributos

- id
- passageiroId
- restauranteId
- entregadorId
- itens
- taxaEntrega
- valorTotal
- status
- dataCriacao
- tipo de entrega


### 🔄 Estados do Pedido (StatusPedido)

- CRIADO
- AGUARDANDO_RESTAURANTE
- EM_PREPARO
- AGUARDANDO_ENTREGADOR
- EM_ENTREGA
- ENTREGUE
- CANCELADO


### 📌 Regras Implementadas no Pedido

- Pedido inicia com status `CRIADO`
- Só é salvo após confirmação do cliente
- Pedido não pode ser vazio
- Valor total é calculado automaticamente
- Status seguem fluxo controlado
- Cliente só pode ter um pedido ativo por vez
- O cliente pode optar pelo envio imediato ou agendado


### 🧾 ItemPedido

Relaciona um item do cardápio com sua quantidade dentro do pedido.

#### Cálculo do Subtotal

```
subtotal = precoItem × quantidade
```


### 🛵 Entregador

Novo perfil de usuário voltado para o módulo de delivery.

#### Características

- Tipo de veículo (Moto ou Bicicleta)
- Controle de disponibilidade (Online/Offline)
- Localização obrigatória para ficar online
- Atribuição automática de pedidos
- Sistema de avaliação


## 🔄 Fluxo Completo do Pedido

### 1️⃣ Criação do Pedido

```
Passageiro escolhe restaurante
→ Seleciona itens
→ Confirma pedido
```

Após confirmação:

```
Status → AGUARDANDO_RESTAURANTE
```


### 2️⃣ Aceite pelo Restaurante

Quando o restaurante aceita:

```
Status → EM_PREPARO
```


### 3️⃣ Busca de Entregador

Após preparo:

```
Status → AGUARDANDO_ENTREGADOR
```

#### Critérios de Seleção

1. Entregador disponível
2. Possui localização válida
3. Está ativo no sistema

#### Processo

```java
1. Buscar entregadores disponíveis
2. Selecionar o primeiro elegível
3. Atribuir entregador ao pedido
4. Marcar entregador como indisponível
```


### 4️⃣ Entrega

Quando o entregador sai para entrega:

```
Status → EM_ENTREGA
```


### 5️⃣ Finalização

Ao concluir a entrega:

```
Status → ENTREGUE
Entregador.disponivel = true
```


## 👤 Acompanhamento pelo Cliente

### Novo recurso no Menu Passageiro

```
15 - Acompanhar meus pedidos
```

O cliente pode visualizar:

- ID do pedido
- Restaurante
- Status atual
- Valor total

#### Exibição Descritiva dos Status

- Aguardando confirmação
- Em preparo
- Aguardando entregador
- Saiu para entrega
- Pedido entregue


## 🛵 Menu Entregador

### Funcionalidades Disponíveis

- Alternar disponibilidade (Online/Offline)
- Atualizar localização
- Visualizar pedidos disponíveis
- Aceitar pedido
- Finalizar entrega

### Regras

- Não pode ficar online sem localização definida
- Ao aceitar pedido → fica indisponível
- Ao finalizar → volta a ficar disponível


## 💾 Persistência de Dados (Delivery)

```
database/
├── pedidos/pedidos.json
├── restaurantes/restaurantes.json
├── entregadores/entregadores.json
```

### Relacionamentos por ID

- pedido.passageiroId
- pedido.restauranteId
- pedido.entregadorId


## 📏 Regras de Negócio Garantidas

- Pedido não pode ser criado sem itens
- Status seguem fluxo controlado
- Entregador precisa de localização para operar
- Entregador fica indisponível durante entrega
- Cliente só visualiza seus próprios pedidos
- Pedido não pode pular estados


## 🧪 Testes Unitários

### Classe Criada

```
PedidoTest.java
```

### Testes Implementados

- Status inicial do pedido
- Adição de itens
- Remoção de itens
- Cálculo de total com taxa de entrega
- Alteração de status válida
- Bloqueio de alteração inválida
- Agendamento de entrega futuro
- Bloqueio de agendamentos no passado
- Validação de informações dos cardápios 
- Validações de requisitos para entregadores e serviços para delivery


## 🔧 Novo Caso de Uso – Pedido Completo

```
1. Passageiro cria pedido
2. Restaurante recebe notificação
3. Restaurante aceita
4. Sistema busca entregador disponível
5. Entregador aceita
6. Pedido entra em entrega
7. Entrega finalizada
8. Entregador volta a ficar disponível
```


## ✅ Resultado da Expansão

O UberPB agora funciona como:

- Plataforma de mobilidade
- Plataforma de delivery
- Sistema com controle de estados robusto
- Persistência estruturada em JSON
- Arquitetura organizada por camadas
- Cobertura básica com testes unitários
