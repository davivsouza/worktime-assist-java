# WorkTime Assist - API de Controle de Pausas para Ergonomia

API REST desenvolvida em Spring Boot para controle individual de pausas para ergonomia, permitindo que usuários registrem e gerenciem suas pausas durante o trabalho.

## Equipe

- **Davi Vasconcelos Souza** - RM 559906
- **Gustavo Dantas Oliveira** - RM 560685
- **Paulo Neto** - RM 560262

## Tecnologias Utilizadas

- **Java 17** - Linguagem de programação
- **Spring Boot 3.3.3** - Framework principal
- **Spring Data JPA** - Persistência de dados
- **Spring Security** - Segurança e autenticação
- **Oracle Database** - Banco de dados relacional
- **JWT (JSON Web Token)** - Autenticação baseada em tokens
- **OpenAPI/Swagger** - Documentação interativa da API
- **Lombok** - Redução de boilerplate
- **Maven** - Gerenciamento de dependências
- **Jakarta Validation** - Validação de dados de entrada
- **BCrypt** - Criptografia de senhas

## Funcionalidades

### Autenticação e Autorização
- Autenticação JWT com tokens seguros
- Proteção de rotas com Spring Security
- Criptografia de senhas com BCrypt

### Gerenciamento de Usuários
- Cadastro de novos usuários
- Consulta de perfil do usuário logado
- Busca de usuário por ID
- Validação de email único

### Gerenciamento de Pausas
- CRUD completo de pausas
- Criação de pausas em andamento (sem data de fim)
- Finalização de pausas com cálculo automático de duração
- Listagem paginada e ordenada
- Filtros e busca por ID

### Integração com Oracle Database
- Chamada de procedures para inserção de dados
- Execução de funções para cálculos e validações
- Tratamento de exceções do banco de dados
- Conversão automática entre UUID e RAW(16)

### Documentação
- Swagger UI para testes interativos
- Documentação OpenAPI completa
- Exemplos de requisições e respostas

## Pré-requisitos

- Java 17 ou superior
- Maven 3.6+
- Oracle Database 11g ou superior
- IDE (IntelliJ IDEA, Eclipse ou VS Code)

## Configuração do Banco de Dados

### 1. Criar usuário e schema no Oracle

```sql
CREATE USER worktime IDENTIFIED BY worktime123;
GRANT CONNECT, RESOURCE TO worktime;
GRANT CREATE PROCEDURE TO worktime;
GRANT CREATE TRIGGER TO worktime;
```

### 2. Executar scripts SQL

Execute os scripts na seguinte ordem:

1. `src/main/resources/db/scripts/schema.sql` - Criação das tabelas, constraints, índices e triggers
2. `src/main/resources/db/scripts/procedures.sql` - Criação das procedures e funções
3. `src/main/resources/db/scripts/insert-data.sql` - Inserção de dados de teste (opcional)

### 3. Configurar application.properties

Edite `src/main/resources/application.properties` com suas credenciais:

```properties
spring.datasource.url=jdbc:oracle:thin:@localhost:1521:XE
spring.datasource.username=worktime
spring.datasource.password=worktime123
spring.datasource.driver-class-name=oracle.jdbc.OracleDriver

jwt.secret=sua-chave-secreta-jwt-aqui
jwt.expiration=86400000
```

## Como Executar

### 1. Clone o repositório

```bash
git clone <url-do-repositorio>
cd worktime-assist-java
```

### 2. Compile o projeto

```bash
mvn clean install
```

### 3. Execute a aplicação

```bash
mvn spring-boot:run
```

A aplicação estará disponível em: `http://localhost:8080`

## Documentação da API

### Swagger UI

Acesse a documentação interativa em:
- **Swagger UI**: http://localhost:8080/swagger-ui.html
- **API Docs (JSON)**: http://localhost:8080/v3/api-docs

## Endpoints da API

### Autenticação

#### POST /api/auth/login
Autentica o usuário e retorna token JWT.

**Request:**
```json
{
  "username": "usuario@email.com",
  "password": "senha123"
}
```

**Response (200):**
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "type": "Bearer"
}
```

### Usuários

#### POST /api/usuarios
Cria um novo usuário (público, não requer autenticação).

**Request:**
```json
{
  "nome": "João Silva",
  "email": "joao@email.com",
  "senha": "senha123"
}
```

**Response (201):**
```json
{
  "id": "123e4567-e89b-12d3-a456-426614174000",
  "nome": "João Silva",
  "email": "joao@email.com",
  "dataCriacao": "2024-01-15T10:00:00",
  "dataAtualizacao": "2024-01-15T10:00:00"
}
```

#### GET /api/usuarios/me
Retorna os dados do usuário logado (requer autenticação).

**Headers:**
```
Authorization: Bearer {token}
```

#### GET /api/usuarios/{id}
Busca um usuário específico por ID (requer autenticação).

**Headers:**
```
Authorization: Bearer {token}
```

### Pausas

#### POST /api/pausas
Cria uma nova pausa (requer autenticação).

**Headers:**
```
Authorization: Bearer {token}
```

**Request:**
```json
{
  "inicio": "2024-01-15T10:00:00",
  "fim": null
}
```

**Response (201):**
```json
{
  "id": "123e4567-e89b-12d3-a456-426614174000",
  "inicio": "2024-01-15T10:00:00",
  "fim": null,
  "duracao": null,
  "usuarioId": "123e4567-e89b-12d3-a456-426614174001"
}
```

#### GET /api/pausas
Lista todas as pausas com paginação (requer autenticação).

**Headers:**
```
Authorization: Bearer {token}
```

**Query Parameters:**
- `page` - Número da página (padrão: 0)
- `size` - Tamanho da página (padrão: 10)
- `sort` - Campo para ordenação (padrão: inicio,DESC)

**Exemplo:**
```
GET /api/pausas?page=0&size=20&sort=inicio,DESC
```

#### GET /api/pausas/{id}
Busca uma pausa específica por ID (requer autenticação).

**Headers:**
```
Authorization: Bearer {token}
```

#### PUT /api/pausas/{id}
Atualiza uma pausa existente (requer autenticação).

**Headers:**
```
Authorization: Bearer {token}
```

**Request:**
```json
{
  "inicio": "2024-01-15T10:00:00",
  "fim": "2024-01-15T10:15:00"
}
```

**Nota:** Ao informar `fim`, a duração é calculada automaticamente.

#### DELETE /api/pausas/{id}
Remove uma pausa (requer autenticação).

**Headers:**
```
Authorization: Bearer {token}
```

### Procedures e Funções do Banco de Dados

#### POST /api/procedures/inserir-usuario
Chama a procedure `inserir_usuario` do Oracle para inserir um novo usuário.

**Request:**
```json
{
  "nome": "Maria Santos",
  "email": "maria@email.com",
  "senha": "senha123"
}
```

**Response (201):**
```json
{
  "id": "123e4567-e89b-12d3-a456-426614174000",
  "mensagem": "usuário inserido via procedure inserir_usuario",
  "nome": "Maria Santos",
  "email": "maria@email.com"
}
```

#### POST /api/procedures/inserir-pausa
Chama a procedure `inserir_pausa` do Oracle para inserir uma nova pausa.

**Request:**
```json
{
  "inicio": "2024-01-15T10:00:00",
  "fim": "2024-01-15T10:15:00",
  "usuarioId": "123e4567-e89b-12d3-a456-426614174001"
}
```

**Response (201):**
```json
{
  "id": "123e4567-e89b-12d3-a456-426614174002",
  "mensagem": "pausa inserida via procedure inserir_pausa",
  "inicio": "2024-01-15T10:00:00",
  "fim": "2024-01-15T10:15:00",
  "usuarioId": "123e4567-e89b-12d3-a456-426614174001"
}
```

#### POST /api/procedures/calcular-duracao
Chama a função `calcular_duracao_pausa` do Oracle para calcular a duração entre duas datas.

**Request:**
```json
{
  "inicio": "2024-01-15T10:00:00",
  "fim": "2024-01-15T10:15:30"
}
```

**Response (200):**
```json
{
  "duracaoSegundos": 930,
  "duracaoMinutos": 15.5,
  "duracaoHoras": 0.25833333333333336,
  "mensagem": "duração calculada via função calcular_duracao_pausa",
  "inicio": "2024-01-15T10:00:00",
  "fim": "2024-01-15T10:15:30"
}
```

#### GET /api/procedures/validar-email?email=usuario@email.com
Chama a função `validar_email_usuario` do Oracle para validar se um email é válido e está disponível.

**Response (200):**
```json
{
  "email": "usuario@email.com",
  "valido": true,
  "mensagem": "email válido e disponível (via função validar_email_usuario)"
}
```

#### GET /api/procedures/contar-pausas/{usuarioId}
Chama a função `contar_pausas_usuario` do Oracle para contar o total de pausas de um usuário.

**Query Parameters (opcionais):**
- `dataInicio` - Data inicial para filtrar (formato ISO: 2024-01-15T00:00:00)
- `dataFim` - Data final para filtrar (formato ISO: 2024-01-15T23:59:59)

**Exemplo:**
```
GET /api/procedures/contar-pausas/123e4567-e89b-12d3-a456-426614174001?dataInicio=2024-01-01T00:00:00&dataFim=2024-01-31T23:59:59
```

**Response (200):**
```json
{
  "usuarioId": "123e4567-e89b-12d3-a456-426614174001",
  "totalPausas": 25,
  "mensagem": "contagem realizada via função contar_pausas_usuario",
  "dataInicio": "2024-01-01T00:00:00",
  "dataFim": "2024-01-31T23:59:59"
}
```

## Estrutura do Projeto

```
src/
├── main/
│   ├── java/com/worktime/assist/
│   │   ├── config/              # Configurações
│   │   │   ├── JwtAuthenticationFilter.java
│   │   │   ├── JwtTokenProvider.java
│   │   │   ├── PasswordEncoderConfig.java
│   │   │   ├── SecurityConfig.java
│   │   │   └── SwaggerConfig.java
│   │   ├── controller/          # Controllers REST
│   │   │   ├── AuthController.java
│   │   │   ├── PausaController.java
│   │   │   ├── ProcedureController.java
│   │   │   └── UsuarioController.java
│   │   ├── dto/                 # Data Transfer Objects
│   │   │   ├── LoginRequest.java
│   │   │   ├── LoginResponse.java
│   │   │   ├── PausaRequest.java
│   │   │   ├── PausaResponse.java
│   │   │   ├── UsuarioRequest.java
│   │   │   └── UsuarioResponse.java
│   │   ├── entity/              # Entidades JPA
│   │   │   ├── Pausa.java
│   │   │   └── Usuario.java
│   │   ├── exception/           # Tratamento de exceções
│   │   │   ├── ErrorResponse.java
│   │   │   ├── GlobalExceptionHandler.java
│   │   │   ├── ResourceConflictException.java
│   │   │   └── ResourceNotFoundException.java
│   │   ├── mapper/              # Mappers DTO <-> Entity
│   │   │   ├── PausaMapper.java
│   │   │   └── UsuarioMapper.java
│   │   ├── repository/          # Repositories JPA
│   │   │   ├── PausaRepository.java
│   │   │   └── UsuarioRepository.java
│   │   ├── service/             # Lógica de negócio
│   │   │   ├── AuthService.java
│   │   │   ├── OracleProcedureService.java
│   │   │   ├── PausaService.java
│   │   │   └── UsuarioService.java
│   │   └── WorktimeAssistApplication.java
│   └── resources/
│       ├── db/scripts/          # Scripts SQL
│       │   ├── schema.sql
│       │   ├── procedures.sql
│       │   ├── insert-data.sql
│       │   └── insert-usuario-teste.sql
│       └── application.properties
└── test/                        # Testes unitários e de integração
```

## Integração com Oracle Database

A aplicação integra com procedures e funções Oracle através do serviço `OracleProcedureService`:

### Procedures

#### inserir_usuario
Insere um novo usuário no banco de dados com validações.

**Parâmetros:**
- `p_id` (OUT RAW(16)) - ID gerado do usuário
- `p_nome` (IN VARCHAR2) - Nome do usuário
- `p_email` (IN VARCHAR2) - Email do usuário
- `p_senha` (IN VARCHAR2) - Senha do usuário (será criptografada)
- `p_resultado` (OUT VARCHAR2) - Mensagem de resultado

#### inserir_pausa
Insere uma nova pausa no banco de dados com validações.

**Parâmetros:**
- `p_id` (OUT RAW(16)) - ID gerado da pausa
- `p_inicio` (IN TIMESTAMP) - Data/hora de início
- `p_usuario_id` (IN RAW(16)) - ID do usuário
- `p_fim` (IN TIMESTAMP DEFAULT NULL) - Data/hora de fim (opcional)
- `p_resultado` (OUT VARCHAR2) - Mensagem de resultado

### Funções

#### calcular_duracao_pausa
Calcula a duração entre duas datas em segundos.

**Parâmetros:**
- `p_inicio` (TIMESTAMP) - Data/hora de início
- `p_fim` (TIMESTAMP) - Data/hora de fim

**Retorno:** NUMBER - Duração em segundos

#### validar_email_usuario
Valida se um email é válido e está disponível.

**Parâmetros:**
- `p_email` (VARCHAR2) - Email a ser validado

**Retorno:** NUMBER - 1 se válido e disponível, 0 caso contrário

#### contar_pausas_usuario
Conta o total de pausas de um usuário, opcionalmente filtrando por período.

**Parâmetros:**
- `p_usuario_id` (RAW(16)) - ID do usuário
- `p_data_inicio` (TIMESTAMP DEFAULT NULL) - Data inicial (opcional)
- `p_data_fim` (TIMESTAMP DEFAULT NULL) - Data final (opcional)

**Retorno:** NUMBER - Total de pausas

## Testes

### Executar testes unitários

```bash
mvn test
```

### Testar com Postman

Importe a coleção do Postman localizada em:
- `postman/WorkTime-Assist.postman_collection.json`

Configure as variáveis de ambiente:
- `baseUrl`: http://localhost:8080
- `token`: (será preenchido após login)

## Tratamento de Erros

A API retorna erros padronizados no formato:

```json
{
  "timestamp": "2024-01-15T10:30:00",
  "status": 404,
  "error": "Not Found",
  "message": "pausa não encontrada com id: ...",
  "path": "/api/pausas/..."
}
```

### Códigos de Status HTTP

- `200` - Sucesso
- `201` - Criado com sucesso
- `400` - Dados inválidos
- `401` - Não autenticado
- `404` - Recurso não encontrado
- `409` - Conflito (ex: email já cadastrado)
- `500` - Erro interno do servidor

## Deploy

### Railway

1. Crie uma conta no [Railway](https://railway.app)
2. Conecte seu repositório GitHub
3. Configure as variáveis de ambiente:
   - `SPRING_DATASOURCE_URL`
   - `SPRING_DATASOURCE_USERNAME`
   - `SPRING_DATASOURCE_PASSWORD`
   - `JWT_SECRET`
   - `JWT_EXPIRATION`
4. Deploy automático será realizado

### Azure VM Linux

1. Provisione uma VM Linux no Azure
2. Instale Java 17 e Maven
3. Clone o repositório
4. Configure o banco de dados Oracle
5. Execute: `mvn spring-boot:run` ou configure como serviço systemd

## Segurança

- Senhas são criptografadas com BCrypt antes do armazenamento
- Tokens JWT com expiração configurável
- Rotas protegidas com Spring Security
- Validação de entrada em todos os endpoints
- Tratamento de exceções para evitar vazamento de informações

## Licença

Este projeto é parte de um trabalho acadêmico.

## Contato

Para dúvidas ou sugestões, entre em contato através do repositório.
