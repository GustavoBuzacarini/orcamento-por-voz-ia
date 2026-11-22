<!-- onp-spec:documentation-contract=v1 -->

# Tasks: Orcamento por voz acessivel

> feature: orcamento-por-voz-acessivel

<!--
  T-xxx = tarefa. `Refs:` aponta US/AC; `Arquivos:` decide o paralelismo.
  Status: pendente | em-andamento | concluida
  Atalho: `onp-spec tarefa <feature> <T-xxx> <status>`
  Pacote base: br.com.orcamentovoz (src/main/java/br/com/orcamentovoz)
-->

## T-001 — Esqueleto Gradle, Spring Boot, Spring AI 2.0.1 e configuração [concluida]

- Refs: US-001, AC-016
- Arquivos: build.gradle, settings.gradle, gradlew, gradlew.bat, gradle/wrapper/gradle-wrapper.properties, gradle/wrapper/gradle-wrapper.jar, compose.yml, .env.example, src/main/resources/application.properties, src/test/resources/application-test.properties, src/main/java/br/com/orcamentovoz/OrcamentoVozApplication.java, src/main/java/br/com/orcamentovoz/config/RelogioConfig.java
- Notas: ler a doc da 2.0.1 antes de fixar propriedades (chave `.options` removida); confirmar ASM-008 (Java e Boot); sem segredos (P-002).

## T-002 — Domínio: categorias, gasto, limite e validador [concluida]

- Refs: US-002, AC-005, AC-006, AC-007
- Arquivos: src/main/java/br/com/orcamentovoz/domain/Categoria.java, src/main/java/br/com/orcamentovoz/domain/Gasto.java, src/main/java/br/com/orcamentovoz/domain/GastoPendente.java, src/main/java/br/com/orcamentovoz/domain/LimiteCategoria.java, src/main/java/br/com/orcamentovoz/domain/ValidadorDeGasto.java, src/main/java/br/com/orcamentovoz/domain/RegraDeGastoException.java
- Notas: relógio injetável; fuso America/Sao_Paulo (ASM-004). Cumpre P-003.

## T-003 — Persistência JPA dos gastos, pendências e limites [concluida]

- Refs: US-001, US-003, AC-002, AC-008
- Arquivos: src/main/java/br/com/orcamentovoz/infrastructure/persistence/GastoRepository.java, src/main/java/br/com/orcamentovoz/infrastructure/persistence/GastoPendenteRepository.java, src/main/java/br/com/orcamentovoz/infrastructure/persistence/LimiteRepository.java
- Notas: depende de T-001 e T-002; sem SQL nativo (risco H2 x MySQL).

## T-004 — Casos de uso de proposta e confirmação de gasto [concluida]

- Refs: US-001, AC-001, AC-002, AC-003, AC-004
- Arquivos: src/main/java/br/com/orcamentovoz/application/RegistrarGastoUseCase.java, src/main/java/br/com/orcamentovoz/application/ConfirmarGastoUseCase.java, src/main/java/br/com/orcamentovoz/application/Calendario.java, src/main/java/br/com/orcamentovoz/application/Moeda.java
- Notas: gravação só em confirmar; pendência expira em 5 minutos (ASM-003).

## T-005 — Casos de uso de limites e aviso [concluida]

- Refs: US-003, AC-008, AC-009, AC-010
- Arquivos: src/main/java/br/com/orcamentovoz/application/DefinirLimiteUseCase.java, src/main/java/br/com/orcamentovoz/application/AvaliarLimiteService.java
- Notas: o aviso é consumido por T-004 na resposta de confirmação; combinar a interface `AvaliarLimiteService` antes (TDD §5).

## T-006 — Casos de uso de consulta e resumo do mês [concluida]

- Refs: US-004, AC-011, AC-012
- Arquivos: src/main/java/br/com/orcamentovoz/application/ConsultarGastosUseCase.java, src/main/java/br/com/orcamentovoz/application/ResumoMesUseCase.java
- Notas: mês de calendário (ASM-004).

## T-007 — Porta do assistente, ferramentas @Tool e ChatClient [concluida]

- Refs: US-001, US-004, AC-001, AC-011
- Arquivos: src/main/java/br/com/orcamentovoz/application/AssistenteFinanceiro.java, src/main/java/br/com/orcamentovoz/application/ProcessarComandoUseCase.java, src/main/java/br/com/orcamentovoz/infrastructure/ai/FerramentasFinanceiras.java, src/main/java/br/com/orcamentovoz/infrastructure/ai/ChatClientAssistente.java, src/main/resources/prompts/assistente.st
- Notas: ferramentas finas, sem regra (P-003); depende de T-004 a T-006.

## T-008 — Endpoint de voz, transcrição, fala e tratamento de erros [concluida]

- Refs: US-005, US-006, US-007, AC-013, AC-014, AC-015, AC-016
- Arquivos: src/main/java/br/com/orcamentovoz/infrastructure/ai/TranscricaoOpenAi.java, src/main/java/br/com/orcamentovoz/infrastructure/ai/FalaOpenAi.java, src/main/java/br/com/orcamentovoz/infrastructure/http/VozController.java, src/main/java/br/com/orcamentovoz/infrastructure/http/ErrosHandler.java, src/main/java/br/com/orcamentovoz/infrastructure/http/ComandoInvalidoException.java, src/main/java/br/com/orcamentovoz/application/Transcritor.java, src/main/java/br/com/orcamentovoz/application/Sintetizador.java, src/main/java/br/com/orcamentovoz/application/AssistenteIndisponivelException.java
- Notas: áudio só em memória (AC-015); cabeçalho `X-Resposta-Texto`.

## T-009 — Endpoint de texto e consultas REST [concluida]

- Refs: US-005, AC-017
- Arquivos: src/main/java/br/com/orcamentovoz/infrastructure/http/TextoController.java, src/main/java/br/com/orcamentovoz/infrastructure/http/ConsultaController.java
- Notas: depende de T-007.

## T-010 — README, exemplos de requisição e experimento das 30 frases [em-andamento]

- Refs: US-005, AC-013
- Arquivos: README.md, docs/exemplos-requisicoes.md, docs/experimento-30-frases.md
- Notas: explicar o que o projeto faz, como executar, a melhoria, tecnologias, como testar, aprendizados (exigência do curso); registrar o resultado real do experimento, nunca inventado.

## T-011 — Provedor de IA configurável (Groq) e fala opcional [concluida]

- Refs: US-005, AC-018
- Arquivos: src/main/resources/application.properties, src/main/java/br/com/orcamentovoz/infrastructure/http/VozController.java, src/test/java/br/com/orcamentovoz/FalaDesligadaTest.java, .env.example, README.md
- Notas: ADR D5; URL base, chave e modelos por variável de ambiente; AC-018 prova a fala desligada; a compatibilidade real com a Groq só se prova com uma chave de teste (ASM-010).

## T-012 — Proposta recusada cancela a pendência anterior [concluida]

- Refs: US-002, AC-019
- Arquivos: src/main/java/br/com/orcamentovoz/application/RegistrarGastoUseCase.java, src/test/java/br/com/orcamentovoz/PendenciaAposRecusaTest.java
- Notas: achado em teste real (2026-11-22). A pendência é apagada antes de validar, e a transação não pode desfazer isso quando a validação falha (`noRollbackFor`).

## Documentos relacionados

[PRD](prd.md) · [ADR](adr.md) · [TDD](design.md) · [Spec](spec.md) · [Pesquisa](research.md)
