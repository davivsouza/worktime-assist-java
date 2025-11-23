# WorkTime Assist - API de Controle de Pausas para Ergonomia

API REST desenvolvida em Spring Boot para controle individual de pausas para ergonomia, permitindo que usuários registrem e gerenciem suas pausas durante o trabalho.

## Tecnologias Utilizadas

- **Java 17**
- **Spring Boot 3.3.3**
- **Spring Data JPA** - Persistência de dados
- **Spring Security** - Segurança e autenticação
- **Oracle Database** - Banco de dados relacional
- **JWT** - Autenticação baseada em tokens
- **OpenAPI/Swagger** - Documentação da API
- **Lombok** - Redução de boilerplate
- **Maven** - Gerenciamento de dependências
- **Jakarta Validation** - Validação de dados

## Funcionalidades

- CRUD completo de pausas
- Paginação e ordenação nas listagens
- Cálculo automático de duração
- Integração com procedures Oracle
- Autenticação JWT
- Documentação Swagger
- Tratamento global de exceções
- Validações de entrada

## Pré-requisitos

- Java 17 ou superior
- Maven 3.6+
- Oracle Database (configurado)
- IDE (IntelliJ IDEA, Eclipse ou VS Code)

## Configuração do Banco de Dados

### 1. Criar usuário e schema

```sql
CREATE USER worktime IDENTIFIED BY worktime123;
GRANT CONNECT, RESOURCE TO worktime;
GRANT CREATE PROCEDURE TO worktime;
```

### 2. Executar scripts SQL

Execute os scripts na seguinte ordem:

1. `src/main/resources/db/scripts/schema.sql` - Criação da tabela
2. `src/main/resources/db/scripts/procedures.sql` - Criação das procedures

### 3. Configurar application.properties

Edite `src/main/resources/application.properties` com suas credenciais:

```properties
spring.datasource.url=jdbc:oracle:thin:@localhost:1521:XE
spring.datasource.username=worktime
spring.datasource.password=worktime123
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

## Endpoints

### Autenticação

#### POST /api/auth/login
Autentica o usuário e retorna token JWT.

**Request:**
```json
{
  "username": "usuario",
  "password": "senha"
}
```

**Response:**
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "type": "Bearer"
}
```

### Pausas

#### POST /api/pausas
Cria uma nova pausa.

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
  "duracao": null
}
```

#### GET /api/pausas
Lista todas as pausas com paginação.

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
Busca uma pausa específica por ID.

**Headers:**
```
Authorization: Bearer {token}
```

#### PUT /api/pausas/{id}
Atualiza uma pausa existente.

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
Remove uma pausa.

**Headers:**
```
Authorization: Bearer {token}
```

## Estrutura do Projeto

```
src/
├── main/
│   ├── java/com/worktime/assist/
│   │   ├── config/          # Configurações (Security, Swagger, JWT)
│   │   ├── controller/      # Controllers REST
│   │   ├── dto/             # Data Transfer Objects
│   │   ├── entity/          # Entidades JPA
│   │   ├── exception/       # Tratamento de exceções
│   │   ├── mapper/          # Mappers DTO <-> Entity
│   │   ├── repository/      # Repositories JPA
│   │   └── service/         # Lógica de negócio
│   └── resources/
│       ├── db/scripts/      # Scripts SQL
│       └── application.properties
└── test/                    # Testes unitários e de integração
```

## Integração com Oracle Procedures

A aplicação integra com duas procedures Oracle:

### iniciar_pausa
Chamada automaticamente ao criar uma nova pausa.

**Parâmetros:**
- `p_inicio` (TIMESTAMP) - Data/hora de início

### encerrar_pausa
Chamada automaticamente ao atualizar uma pausa com data de fim.

**Parâmetros:**
- `p_fim` (TIMESTAMP) - Data/hora de fim
- `p_duracao` (NUMBER) - Duração em segundos

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

## Testes

Execute os testes com:

```bash
mvn test
```

## Tratamento de Erros

A API retorna erros padronizados:

```json
{
  "timestamp": "2024-01-15T10:30:00",
  "status": 404,
  "error": "Not Found",
  "message": "pausa não encontrada com id: ...",
  "path": "/api/pausas/..."
}
```

## Licença

Este projeto é parte de um trabalho acadêmico.

## Contato

Para dúvidas ou sugestões, entre em contato através do repositório.

