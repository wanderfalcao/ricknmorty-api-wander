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
