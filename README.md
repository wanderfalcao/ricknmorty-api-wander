![CI](https://github.com/wanderfalcao/ricknmorty-api-wander/actions/workflows/ci.yml/badge.svg)

# Rick and Morty API

API REST desenvolvida com Spring Boot e Java 17, que serve dados de personagens
da série Rick and Morty a partir de um banco PostgreSQL containerizado.

---

## Pré-requisitos

- Docker
- Docker Compose

---

## Como rodar localmente

```bash
# Subir banco e API juntos
docker-compose up -d --build

# Verificar se os containers estão rodando
docker ps

# Parar tudo
docker-compose down
```

---

## Validação do banco (Questão 1)

```bash
# Exibir logs do banco para confirmar inicialização dos dados
docker logs ricknmorty-db

# Listar tabelas
docker exec ricknmorty-db psql -U postgres -d postgres -c "\dt"

# Executar SELECT para validar dados carregados
docker exec ricknmorty-db psql -U postgres -d postgres -c "SELECT id, name, status FROM characters LIMIT 5;"
```

---

## Validação da API (Questão 2)

Após subir os containers, acesse no navegador ou via curl:

```bash
curl http://localhost:8080/api/characters
```

A resposta deve listar os personagens vindos do banco PostgreSQL.

---

## Estrutura dos containers

| Container        | Imagem base              | Porta exposta | Descrição                        |
|------------------|--------------------------|---------------|----------------------------------|
| ricknmorty-db    | postgres:17.8-alpine3.23 | —             | Banco de dados (sem porta no host) |
| ricknmorty-api   | eclipse-temurin:17-jre   | 8080          | API Spring Boot                  |

O banco **não expõe a porta 5432 para o host**. A API conecta ao banco pelo nome
do serviço `db` dentro da rede Docker `ricknmorty-net`.

---

## Persistência de dados

Os dados do PostgreSQL são persistidos no volume `ricknmorty-db-data`.
O script `dados/data.sql` roda automaticamente apenas na primeira inicialização
do container, via `/docker-entrypoint-initdb.d/`.

---

## Pipeline de CI

O workflow principal fica em `.github/workflows/ci.yml` e roda em dois cenários:

- **Push na main**: executa automaticamente lint, testes e análise de segurança
- **Execução manual**: disponível na aba Actions > CI > Run workflow, com opção de
  escolher se quer rodar os testes e/ou o lint

### Jobs

| Job                   | O que faz                                                                  |
|-----------------------|----------------------------------------------------------------------------|
| Lint                  | Valida o POM e compila o projeto para garantir que não há erros de código  |
| Testes                | Sobe um container PostgreSQL e executa `mvn test`                          |
| Analise de Segurança  | Roda o CodeQL para identificar vulnerabilidades no código Java             |

O lint e os testes vivem em um workflow separado (`lint-and-test.yml`) chamado
pelo `ci.yml` via `workflow_call`, mantendo o pipeline organizado e reutilizável.

---

## Depuração de pipeline

Para exercitar a depuração, o step de compilação no job de lint foi alterado
temporariamente para `run: exit 1`, simulando uma falha proposital. Após o push,
a execução apareceu com status de falha na aba Actions. Ao expandir o step com
o ícone vermelho, o log exibiu `Process completed with exit code 1`, indicando
exatamente onde o pipeline parou. Após reverter para `run: mvn compile -q` e
novo push, o pipeline voltou ao verde.

---

## Execução manual vs automática

No push automático, o workflow é disparado imediatamente após o commit chegar na
branch main, sem nenhuma intervenção — lint, testes e análise de segurança rodam
juntos com os valores padrão.

Na execução manual via botão "Run workflow", é possível ativar ou desativar os
inputs `run_tests` e `run_lint` antes de iniciar, útil para validar só uma parte
do pipeline. A execução manual aparece com o gatilho `workflow_dispatch` nos logs
da aba Actions, facilitando a distinção entre os dois modos no histórico.

---

## Endpoint de estatísticas

`GET /api/characters/stats` retorna a contagem total de personagens agrupada por status:

```bash
curl http://localhost:8080/api/characters/stats
```

Resposta esperada:

```json
{
  "total": 826,
  "byStatus": {
    "Alive": 439,
    "Dead": 262,
    "unknown": 125
  }
}
```

---

## Workflows adicionais (TP3)

| Workflow               | Arquivo                   | Descrição                                          |
|------------------------|---------------------------|----------------------------------------------------|
| Env Context Demo       | `env-context-demo.yml`    | Demonstra contextos e escopos de variáveis         |
| Vars and Secrets Demo  | `vars-demo.yml`           | Demonstra uso de variáveis e secrets do repositório |
| Self-hosted Demo       | `self-hosted-demo.yml`    | Executa jobs no runner auto-hospedado              |

O `ci.yml` também foi estendido com:

- Permissões explícitas via `permissions:`
- Criação automática de issue via `GITHUB_TOKEN` quando a análise de segurança falha
- Jobs de deploy condicionais para os ambientes `dev` (branch dev) e `prod` (branch master)
