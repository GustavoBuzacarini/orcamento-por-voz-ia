<!-- onp-spec:documentation-contract=v1 -->

# Spec: Orcamento por voz acessivel

> feature: orcamento-por-voz-acessivel
> status: em-implementacao

## Contexto

Uma pessoa que tem dificuldade para ler telas ou digitar registra e consulta seus gastos só
falando, com confirmação antes de gravar, limites mensais por categoria e resposta também em
áudio. Categorias aceitas (ASM-001): mercado, alimentação fora, transporte, moradia, saúde,
educação, lazer e outros.

Convenção de teste: o interpretador de linguagem é substituído por uma versão falsa e
determinística (ver [TDD](design.md)); estes critérios provam as regras do nosso código.

## Histórias

### US-001 — Registrar um gasto por voz com confirmação

Como pessoa que controla seus gastos, quero falar um gasto e confirmar antes de ele ser
gravado, para que um erro de compreensão não vire um registro errado.

#### AC-001 — O gasto falado é repetido para confirmação e ainda não é gravado

- **Dado** que não há gasto pendente
- **Quando** a pessoa envia o áudio "gastei 45 reais no mercado"
- **Então** a resposta repete "R$ 45,00 em mercado" e pergunta se pode gravar
- **E** nenhum gasto foi gravado (a lista de gastos continua vazia)

#### AC-002 — Dizer "sim" grava o gasto

- **Dado** um gasto pendente de R$ 45,00 em mercado
- **Quando** a pessoa envia o áudio "sim"
- **Então** a resposta informa que o gasto foi gravado
- **E** a lista de gastos passa a ter esse gasto, com a data de hoje

#### AC-003 — Dizer "não" descarta o gasto pendente

- **Dado** um gasto pendente de R$ 45,00 em mercado
- **Quando** a pessoa envia o áudio "não"
- **Então** a resposta informa que o gasto foi descartado
- **E** nenhum gasto foi gravado

#### AC-004 — Confirmar depois de muito tempo não grava nada

- **Dado** um gasto pendente criado há mais de 5 minutos
- **Quando** a pessoa envia o áudio "sim"
- **Então** a resposta avisa que não há gasto para confirmar e pede para falar o gasto de novo
- **E** nenhum gasto foi gravado

### US-002 — Só gastos válidos são aceitos

Como pessoa usuária, quero que valores e datas absurdos sejam recusados, para que o
orçamento não seja contaminado por um erro de interpretação da IA.

#### AC-005 — Valor zero ou negativo é recusado

- **Dado** que não há gasto pendente
- **Quando** o interpretador pede para registrar um gasto de R$ 0,00 ou de R$ -10,00
- **Então** a resposta explica que o valor precisa ser maior que zero
- **E** nenhum gasto fica pendente nem é gravado (o código decide, não o modelo)

#### AC-006 — Categoria desconhecida é recusada com as opções

- **Dado** que não há gasto pendente
- **Quando** o interpretador pede um gasto na categoria "cassino"
- **Então** a resposta diz que a categoria não existe e lista as categorias disponíveis
- **E** nenhum gasto fica pendente nem é gravado

#### AC-007 — Data no futuro é recusada

- **Dado** que hoje é 2026-11-22
- **Quando** o interpretador pede um gasto com data 2026-12-01
- **Então** a resposta explica que não dá para registrar gasto em data futura
- **E** nenhum gasto fica pendente nem é gravado

### US-003 — Limites por categoria com aviso

Como pessoa que quer evitar apertos no fim do mês, quero definir limites por categoria e ser
avisada quando eles forem ultrapassados, para que eu decida com calma o que fazer.

#### AC-008 — Definir um limite mensal por voz

- **Dado** que não há limite para a categoria lazer
- **Quando** a pessoa envia o áudio "meu limite de lazer é 300 reais"
- **Então** a resposta confirma "limite de lazer: R$ 300,00 por mês"
- **E** o limite fica guardado, e um novo pedido para a mesma categoria substitui o anterior

#### AC-009 — Gasto que ultrapassa o limite é gravado e a pessoa é avisada

- **Dado** o limite de lazer de R$ 300,00 e R$ 280,00 já gastos em lazer neste mês
- **Quando** a pessoa confirma um gasto de R$ 50,00 em lazer
- **Então** o gasto é gravado
- **E** a resposta avisa que o limite de lazer foi ultrapassado em R$ 30,00

#### AC-010 — Gasto dentro do limite informa quanto ainda resta

- **Dado** o limite de lazer de R$ 300,00 e R$ 100,00 já gastos em lazer neste mês
- **Quando** a pessoa confirma um gasto de R$ 50,00 em lazer
- **Então** o gasto é gravado
- **E** a resposta diz que ainda restam R$ 150,00 no limite de lazer, sem aviso de excesso

### US-004 — Consultar gastos por voz

Como pessoa usuária, quero perguntar quanto gastei, para saber como está o mês sem ler nada.

#### AC-011 — Total por categoria no mês

- **Dado** gastos neste mês de R$ 40,00 e R$ 60,00 em mercado, e R$ 30,00 em lazer
- **Quando** a pessoa envia o áudio "quanto gastei com mercado este mês?"
- **Então** a resposta diz que o total em mercado é R$ 100,00
- **E** gastos de outras categorias e de outros meses não entram na conta

#### AC-012 — Resumo do mês com categorias acima do limite

- **Dado** gastos neste mês em mercado e em lazer, e o limite de lazer ultrapassado
- **Quando** a pessoa envia o áudio "como está meu mês?"
- **Então** a resposta traz o total do mês, o total por categoria e cita lazer como acima do limite

### US-005 — Resposta acessível, em áudio e em texto

Como pessoa que não lê a tela, quero ouvir a resposta e, se precisar, ter também o texto, para
usar o assistente com leitor de tela ou sem microfone.

#### AC-013 — Toda resposta de voz volta em áudio e com o texto equivalente

- **Dado** qualquer comando de voz válido
- **Quando** a resposta é enviada
- **Então** o corpo é áudio mp3 com a fala da resposta
- **E** um cabeçalho traz o mesmo texto da fala, para leitores de tela

#### AC-014 — Áudio que não foi entendido pede para repetir

- **Dado** um áudio cuja transcrição vem vazia
- **Quando** a pessoa o envia
- **Então** a resposta em áudio pede "não entendi, pode repetir?"
- **E** nenhum gasto ou limite é criado

#### AC-017 — O mesmo comando pode ser enviado por texto

- **Dado** que não há gasto pendente
- **Quando** a pessoa envia o texto "gastei 45 reais no mercado" ao endpoint de texto
- **Então** recebe a mesma pergunta de confirmação, em texto
- **E** nenhum áudio é gerado

### US-006 — Privacidade do áudio

Como pessoa usuária, quero que meu áudio seja apagado depois de entendido, para que minha voz
não fique guardada.

#### AC-015 — O áudio não fica guardado

- **Dado** um comando de voz enviado e processado
- **Quando** o processamento termina
- **Então** nenhum arquivo de áudio permanece em disco
- **E** o banco de dados guarda só o texto transcrito do comando, nunca os bytes do áudio

### US-007 — Falha dos serviços de IA

Como pessoa usuária, quero uma mensagem clara quando a IA estiver fora do ar, para saber que
nada foi gravado e que posso tentar de novo.

#### AC-016 — Falha da IA devolve aviso claro e não grava nada

- **Dado** que o serviço de transcrição ou de chat está indisponível
- **Quando** a pessoa envia um comando
- **Então** a resposta é um erro 503 com a mensagem "o assistente está indisponível, tente de novo em instantes"
- **E** nenhum gasto ou limite é criado

## Fora de escopo

- Login e vários usuários. Receitas. Editar ou apagar gastos. Bloquear gasto acima do limite.
- Front-end ou app. Recomendações de investimento ou crédito.
- Medir a qualidade da interpretação do modelo como critério automático (vira experimento
  manual, ASM-007).

## Suposições

| ID | Suposição | Status | Resolução |
|---|---|---|---|
| ASM-001 | Categorias fixas: mercado, alimentação fora, transporte, moradia, saúde, educação, lazer, outros | confirmada | Cliente: "Confirmo todas (Recomendado)" (AskUserQuestion), 2026-11-22 |
| ASM-002 | Só gastos (despesas); sem receitas nesta versão | confirmada | Cliente: "Confirmo todas (Recomendado)" (AskUserQuestion), 2026-11-22 |
| ASM-003 | Um gasto pendente por vez, que expira em 5 minutos; um novo gasto substitui o pendente | confirmada | Cliente: "Confirmo todas (Recomendado)" (AskUserQuestion), 2026-11-22 |
| ASM-004 | "Mês" é o mês de calendário no fuso America/Sao_Paulo | confirmada | Cliente: "Confirmo todas (Recomendado)" (AskUserQuestion), 2026-11-22 |
| ASM-005 | Valores em reais com 2 casas decimais | confirmada | Cliente: "Confirmo todas (Recomendado)" (AskUserQuestion), 2026-11-22 |
| ASM-006 | O limite é mensal, por categoria; redefinir substitui o anterior | confirmada | Cliente: "Confirmo todas (Recomendado)" (AskUserQuestion), 2026-11-22 |
| ASM-007 | A qualidade da interpretação do modelo não é critério automático; é medida no experimento de 30 frases | confirmada | Cliente: "Confirmo todas (Recomendado)" (AskUserQuestion), 2026-11-22 |
| ASM-008 | Java 25 e Spring Boot 4.1.1 funcionam com Spring AI 2.0.1 | confirmada | Build e 19 testes passaram com Java 25, Boot 4.1.1 e Spring AI 2.0.1 em 2026-11-22 |
| ASM-009 | A pessoa usa a própria chave da OpenAI e paga o consumo | confirmada | Cliente: "Confirmo todas (Recomendado)" (AskUserQuestion), 2026-11-22 |

## Perguntas em aberto

| ID | Pergunta | Status | Resposta |
|---|---|---|---|
| Q-001 | Há um valor máximo por gasto acima do qual o assistente recusa ou pede confirmação extra? | respondida | Cliente: "Sem máximo de negócio (Recomendado)"; vale só o limite técnico do banco (AskUserQuestion), 2026-11-22 |
| Q-002 | Qual modelo de transcrição usar (whisper-1 ou gpt-4o-mini-transcribe)? Depende do experimento | aberta | — |
| Q-003 | Editar ou apagar o último gasto por voz entra numa versão futura? | respondida | Cliente: "Fica para depois (Recomendado)"; fora desta versão (AskUserQuestion), 2026-11-22 |

## Documentos relacionados

[PRD](prd.md) · [ADR](adr.md) · [TDD](design.md) · [Tarefas](tasks.md) · [Pesquisa](research.md)
