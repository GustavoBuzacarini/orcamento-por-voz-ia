<!-- onp-spec:documentation-contract=v1 -->

# PRD: orcamento por voz acessivel

> feature: orcamento-por-voz-acessivel
> status: revisado

[ADR](adr.md) · [TDD](design.md) · [Spec](spec.md) · [Tarefas](tasks.md) · [Pesquisa](research.md)

## Pedido original do cliente

> vamos com essa ideia da 1 e um pouco da 3 (1: Orçamento por voz acessível; 3: Educação financeira e prevenção de endividamento), repositorio novo

Origem: conversa com o cliente, 2026-11-22. Contexto do pedido: desafio do curso DIO
("evoluir uma API de orçamento que usa IA para processar comandos de voz"), com entrega em
repositório próprio e README. O cliente também confirmou "2.0.1 GA" como versão do Spring AI.

## Problema, público e contexto

**Palavras do cliente:** orçamento por voz acessível, com um pouco de educação financeira.

**Interpretação da equipe (separada do pedido):**
- Público: uma pessoa que controla as próprias finanças e tem dificuldade para ler telas ou
  digitar (baixa visão, cegueira, mãos com pouca mobilidade, pouca familiaridade com apps).
- Problema: registrar e consultar gastos exige tela e teclado. A pesquisa ([research.md](research.md))
  mostra que apps financeiros têm barreiras de leitor de tela e autenticação, e que a
  inadimplência no país é alta (Serasa: cerca de 84 milhões de pessoas, ago/2026).
- A parte "educação financeira" entra como limites por categoria e avisos em linguagem simples.

## Resultados desejados e métricas

| Resultado | Linha de base | Meta | Período | Fonte |
|---|---|---|---|---|
| Registrar um gasto sem ler nem digitar | Não medida | Todos os critérios da spec exercitáveis só com áudio | Entrega do desafio | Testes `@spec:AC-xxx` |
| Valor e categoria corretos na interpretação | Não medida | Registrar a taxa em 30 frases de teste, com intervalo de Wilson | Fase de implementação | Experimento manual descrito na [pesquisa](research.md) |
| Nenhum gasto gravado sem confirmação | n/a | 100% dos critérios AC-001 a AC-004 passando | Entrega | Testes |

Impacto real sobre endividamento: **inconclusivo**. Não há dados do cliente nem experimento
com usuários; o projeto não afirma esse efeito.

## Requisitos e prioridades

| Requisito | Palavras do cliente / origem | Prioridade | Critérios na spec |
|---|---|---|---|
| Registrar gasto por voz com confirmação | "orçamento por voz acessível"; resposta ao Q de confirmação: "Confirmar sempre" | Alta | AC-001 a AC-004 |
| Validar em código antes de gravar | Constituição P-003; "validações antes de salvar" no desafio | Alta | AC-005 a AC-007 |
| Limites por categoria com aviso | "um pouco da 3"; resposta: "Usuário define por voz; só avisa" | Alta | AC-008 a AC-010 |
| Consultas por voz | Desafio: "novos tipos de consulta financeira" | Média | AC-011, AC-012 |
| Resposta em áudio e texto | "acessível"; fluxo do curso (geração de voz) | Alta | AC-013, AC-014, AC-017 |
| Áudio não é guardado | Resposta: "Descartar logo" | Alta | AC-015 |
| Falha dos serviços de IA tratada | Boa prática de API; origem: equipe | Média | AC-016 |

## Escopo e exclusões

Dentro: uma pessoa usuária (sem login), registrar gastos, definir limites mensais por
categoria, consultar totais e resumo do mês, resposta em áudio mp3 e texto, endpoint
equivalente por texto, README com como executar e testar.

Fora: login e vários usuários (resposta: "Uma pessoa só"), receitas, editar ou apagar gastos,
app ou front-end, bloqueio de gastos acima do limite, recomendações de investimento.
Alternativas propostas pela pesquisa e não escolhidas pelo cliente continuam fora.

## Regras de produto e domínios

- Domínio único: orçamento por voz. Não há PRD ou ADR anterior neste repositório.
- Referência externa, sem vínculo contratual: o projeto de agendamento de consultas usa o
  mesmo processo, mas não compartilha dados nem código.
- Constituição: P-001 prova executável, P-002 sem segredos no código, P-003 a IA interpreta
  e o código decide.
- Nada é gravado sem confirmação do cliente por voz ou texto (decisão do cliente).

## Dependências, riscos e suposições

- Dependências: conta e chave da OpenAI (custo do usuário), MySQL via Docker, Spring AI 2.0.1.
- Riscos: erro de transcrição em valores ([TDD, riscos](design.md#6-riscos-e-mitigação)),
  mudança de modelo da OpenAI, custo por comando.
- Suposições e perguntas abertas: ver [spec](spec.md) (ASM-001 a ASM-009, Q-001 a Q-003).

## Validação com o cliente

| Pergunta | Opções | Resposta literal | Origem | Data |
|---|---|---|---|---|
| Confirmação antes de gravar | sempre / direto / só acima de valor | "Confirmar sempre (Recomendado)" | AskUserQuestion, sessão | 2026-11-22 |
| Limites por categoria | só avisa / bloqueia / sem limites | "Usuário define por voz; só avisa (Recomendado)" | idem | 2026-11-22 |
| Áudio após transcrição | descartar / guardar | "Descartar logo (Recomendado)" | idem | 2026-11-22 |
| Usuários | uma pessoa / vários | "Uma pessoa só (Recomendado)" | idem | 2026-11-22 |
| Versão do Spring AI | 2.0.1 GA / 2.0.0-M4 | "2.0.1 GA" | resposta no chat | 2026-11-22 |
| Provedor de IA | Groq (gratuito) ou OpenAI | "faz com o Groq, fala opcional" | resposta no chat | 2026-11-22 |

Revisado pelo cliente em 2026-11-22: "Revisei, pode seguir" (resposta no chat).
