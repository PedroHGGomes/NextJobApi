# Pedro Gomes - RM 553907
# Luiz Felipe Abreu - RM 555197
# Matheus Munuera - RM 557812


___________________________________________________________
# NextJob - Projeto

1.0 - Projeto NextJob – Desenvolvimento da Solução Completa

A NextJob é uma plataforma inteligente de análise de currículos, recomendação de cursos, planejamento de carreira e match com vagas, utilizando IA generativa, visão computacional e modelos preditivos para indicar ao candidato sua porcentagem de compatibilidade com cada vaga e orientar seu caminho de capacitação.

A solução responde diretamente aos pilares do desafio:

- Requalificação contínua (Upskilling e Reskilling
- IA como parceira do ser humano
- Inclusão produtiva e redução de desigualdades
- Preparação para profissões e competências emergente
1.2	– Problema e Oportunidade

O mercado de trabalho está mudando rapidamente

- Profissões serão modificadas ou extintas.
- Jovens e trabalhadores vulneráveis têm mais dificuldade em entender quais habilidades desenvolver.
- Faltam ferramentas que traduzam vagas em habilidades práticas, indicando o que aprender para conquistar aquela oportunidade.

Hoje o candidato:
- Não sabe se seu currículo está competitivo.
- Não compreende quais qualificações faltam.
- Não possui orientação estruturada de carreira.
- Não tem clareza sobre quais cursos realmente importam.



2.0 - Solução

2.1 – NextJob – Visão Geral

A NextJob é uma plataforma que utiliza IA para:

1.  Analisar automaticamente o currículo do candidato

   - Competências técnicas
   - Experiências
   - Soft skills
   - Certificações
   - Palavras-chave relevantes

2.  Analisar a vaga

   - Competências obrigatórias
   - Competências desejáveis
   - Senioridade
   - Requisitos técnicos
   - Soft skills buscadas

3.  Calcular a porcentagem de compatibilidade

   - Competências obrigatórias: 60%
   - Competências desejáveis: 30%
   - Soft skills: 10%

4.  Gerar recomendações de melhoria no currículo

   - Textos que podem ser reescritos
   - Competências não mencionadas
   - Sugestões de reorganização
   - Falhas de clareza e padronização

5.  Recomendar cursos para aumentar a compatibilidade

   - Plano de estudo automatizado
   - Lista de cursos gratuitos e pagos
   - Sugestões de trilhas profissionais

6.  Planejamento de carreira personalizado

   - Objetivo profissional recomendado
   - Mapa de competências futuras
   - Previsão de profissões emergentes até 2030

_________________________________________

## 2. Tecnologias Utilizadas

- **Linguagem:** Java 21
  
- **Framework:** Spring Boot 3.5.7
  
- **Módulos principais:**
  
  - spring-boot-starter-web
    
  - spring-boot-starter-thymeleaf
    
  - spring-boot-starter-data-jpa
    
  - spring-boot-starter-validation
    
  - spring-boot-starter-security
    
  - spring-boot-starter-oauth2-client
    
  - spring-boot-starter-amqp (RabbitMQ)
    
  - spring-boot-starter-cache
    
  - **spring-ai-starter-model-openai** (Spring AI)
    
- **Banco de dados:**
  
  - PostgreSQL (produção)
    
  - H2 (perfil `dev`)
    
- **Mensageria:** RabbitMQ

- **Build:** Gradle
  
- **Autenticação:** OAuth2 (GitHub)
  
- **Deploy em nuvem:** TODO: _descrever o provedor e links (ex: Render, Railway, etc.)_

---

## 3. Arquitetura (Visão Geral)

Camadas principais:

- `controller`  
  Ex.: `CurriculoController`, `PlanoController`, `AnaliseController`, `PerfilController`, `AiController`.
- `service`  
  Contém a regra de negócio e orquestra chamadas à IA, filas e repositórios.
- `repository`  
  Interfaces que estendem `JpaRepository` para acesso a dados.
- `model` / `dto`  
  Entidades JPA e DTOs com métodos de acesso e validações.
- `config`  
  Configurações de:
  - Cache (`CacheConfiguration`)
  - Internacionalização (`InternationalizationConfiguration`)
  - Segurança (`SecurityConfiguration`, `OAuth2Configuration`)
  - RabbitMQ (`RabbitMQConfiguration`)

---

## 4. Configuração de Ambiente

### 4.1. Pré-requisitos

- JDK 21
- Gradle Wrapper (já incluso no projeto)
- Docker (opcional, para subir PostgreSQL e RabbitMQ rapidamente)
- Conta no GitHub (para OAuth)
- Conta na Groq (para API Key de IA)

### 4.2. Variáveis de Ambiente (`.env`)

O projeto usa um arquivo `.env` baseado em `.env.example`:

```env
# Database
SPRING_DATASOURCE_URL=jdbc:postgresql://localhost:5432/nextjob
SPRING_DATASOURCE_USERNAME=nextjob_user
SPRING_DATASOURCE_PASSWORD=nextjob_pass

# Groq AI
GROQ_API_KEY=your-groq-api-key
GROQ_MODEL=llama-3.3-70b-versatile

# RabbitMQ
RABBITMQ_HOST=localhost
RABBITMQ_PORT=5672
RABBITMQ_USERNAME=guest
RABBITMQ_PASSWORD=guest

# GitHub OAuth2
GITHUB_CLIENT_ID=your-github-client-id
GITHUB_CLIENT_SECRET=your-github-client-secret
GITHUB_CALLBACK_URL=http://localhost:8080/login/oauth2/code/github

# JWT (se aplicável)
JWT_SECRET=your-jwt-secret

# Server
PORT=8080
```
