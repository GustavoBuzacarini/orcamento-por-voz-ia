<!-- onp-spec:documentation-contract=v1 -->

# ADR: orcamento por voz acessivel

> feature: orcamento-por-voz-acessivel
> status: revisado

[PRD](prd.md) · [TDD](design.md) · [Spec](spec.md) · [Tarefas](tasks.md) · [Pesquisa](research.md)

## Identificação e contexto

- Domínio: orçamento por voz. Data: 2026-11-22. Autoria: equipe (Claude Code) com o cliente.
- Atende os requisitos do [PRD](prd.md) e os critérios AC-001 a AC-017 da [spec](spec.md).
- Decisões neste ADR: **D1** versão do Spring AI; **D2** onde guardar a confirmação pendente;
  **D3** como testar sem chamar a OpenAI; **D4** organização do código e banco de dados.

## Restrições e regras anteriores

- Constituição P-001, P-002 e P-003 continuam válidas e nenhuma é substituída.
- Não há ADR anterior neste repositório.
- A base do curso usa Spring AI 2.0.0-M4, Boot 4.0.5 e Java 25. Ela é referência, não contrato.

## Alternativas consideradas

**D1 — Versão do Spring AI**

| Opção | Benefício | Custo / risco | Reversível |
|---|---|---|---|
| 2.0.1 GA (escolhida) | Versão estável; strict mode das tools corrigido; limite de chamadas de tool | Pode diferir da base do curso em propriedades e builders | Sim |
| 2.0.0-M4 (curso) | Idêntica às aulas | Milestone com GA disponível; erro 400 em tool com parâmetro opcional (corrigido na 2.0.1) | Sim |

**D2 — Confirmação pendente**

| Opção | Benefício | Custo / risco |
|---|---|---|
| Tabela no banco com validade (proposta) | Sobrevive a reinício; simples de testar | Mais uma tabela |
| Memória do processo | Zero persistência | Perde a pendência ao reiniciar; difícil de auditar |
| Memória de conversa do ChatClient | Natural para o modelo | Deixa uma regra financeira dependente do modelo (fere P-003) |

**D3 — Testes**

| Opção | Benefício | Custo / risco |
|---|---|---|
| Porta `AssistenteFinanceiro` com implementação real (ChatClient) e uma falsa nos testes (proposta) | Testes determinísticos, sem custo nem rede | Não prova a qualidade do modelo (medida à parte) |
| Chamar a OpenAI nos testes | Mais realista | Custo, flakiness, exige chave; teste pulado não é prova |

**D4 — Código e banco**

| Opção | Benefício | Custo / risco |
|---|---|---|
| Camadas `domain`, `application`, `infrastructure` como no curso; MySQL em execução, H2 nos testes (proposta) | Mesmo desenho das aulas; testes rápidos | Diferença MySQL/H2 pode esconder um erro de SQL |
| Tudo com MySQL via Testcontainers | Mais fiel | Exige Docker nos testes; mais lento |

## Decisão e justificativa

- **D1: aceita pelo cliente** ("2.0.1 GA", 2026-11-22). Justificativa: [pesquisa](research.md).
- **D2, D3, D4: aceitas** com a revisão do cliente em 2026-11-22 ("Revisei, pode seguir"). Justificativa: P-003 exige que a regra
  de confirmação viva no código, e P-001 exige provas repetíveis sem rede.

## Consequências e riscos

- Ganho: toda regra de gravação é testável sem IA. Perda: a qualidade de interpretação não
  entra nos critérios automáticos e vira experimento manual.
- Risco: propriedades da 2.0.1 diferirem da base. Mitigação: ler a doc da versão no T-001.
- Risco: H2 diferente de MySQL. Mitigação: SQL só via JPA, sem consultas nativas.
- Ver [TDD](design.md#6-riscos-e-mitigação).

## Validade, revisão e substituição

Revisar se: o cliente optar por vários usuários (muda dados e segurança); o `whisper-1` for
descontinuado; a doc da 2.0.x tornar uma tool incompatível. Nenhuma decisão anterior é
substituída. Mudanças devem criar um ADR novo.

## Respostas e aprovação

| Pergunta | Resposta literal | Origem | Data | Documentos |
|---|---|---|---|---|
| Versão do Spring AI | "2.0.1 GA" | Chat | 2026-11-22 | este ADR (D1), [TDD §15](design.md#15-dependências-e-compatibilidade) |
| D2, D3, D4 | "Revisei, pode seguir" (resposta à revisão dos documentos; o cliente não escreveu ressalvas) | Chat | 2026-11-22 | este ADR |
