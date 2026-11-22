<!-- onp-spec:documentation-contract=v1 -->

# Pesquisa de produto: orcamento por voz acessivel

> feature: orcamento-por-voz-acessivel
> status: revisado
> consulta: 2026-11-22

[PRD](prd.md) · [ADR](adr.md) · [TDD](design.md) · [Spec](spec.md) · [Tarefas](tasks.md)

## Pergunta e hipótese

**Pedido do cliente (literal):** "vamos com essa ideia da 1 e um pouco da 3, repositorio novo".
Ideia 1 = orçamento por voz acessível. Ideia 3 = educação financeira e prevenção de endividamento.

**Pergunta:** vale evoluir a API de orçamento por voz do curso (DIO, `05-spring-ai`) para
um assistente acessível, com limites por categoria e validação antes de gravar?

**Hipótese falsificável:** um fluxo só de voz (falar, confirmar, ouvir a resposta) permite a
uma pessoa registrar e consultar gastos sem ler nem digitar. Falsifica-se se algum critério
da spec não puder ser exercido só por áudio, ou se a taxa de transcrição correta de valores
for baixa nos áudios de teste.

**Decisão que a evidência informa:** versão do Spring AI, modelos de áudio, e desenho da
confirmação por voz (ver Perguntas).

## Concorrentes e alternativas

| Produto / alternativa | Comportamento observado | Fonte primária | Data da consulta | Limitações |
|---|---|---|---|---|
| Base do curso (DIO `05-spring-ai`) | Áudio → transcrição → ChatClient com Tool Calling → casos de uso → resposta em áudio. Java 25, Spring Boot 4.0.5, Spring AI 2.0.0-M4, OpenAI (gpt-4o-mini, whisper-1, gpt-4o-mini-tts), MySQL | [README](https://github.com/digitalinnovationone/dio-spring-boot-learning-track/blob/main/05-spring-ai/README.md), build.gradle, application.properties | 2026-11-22 | Li README, build e propriedades. **Não li o código** de controllers, tools e entidades: endpoints não confirmados |
| App de banco com leitor de tela | Barreiras relatadas: autenticação (18 estudos) e incompatibilidade com leitor de tela (17 estudos) | [JMIR 2026, revisão de escopo](https://jmir.org/2026/1/e82315) | 2026-11-22 | Revisão sobre bancos, não sobre apps de orçamento; não mede eficácia de voz |
| FinSight (UC Berkeley, protótipo voz-first para cegos) | Protótipo de banco móvel por voz | [Berkeley MIMS 2026](https://www.ischool.berkeley.edu/projects/2026/finsight-inclusive-mobile-banking-blind-and-low-vision-users) | 2026-11-22 | Protótipo acadêmico, sem avaliação de resultado; li só o resumo da busca |
| Fazer nada (planilha ou caderno) | Exige ler e digitar; sem acessibilidade nativa | n/a | n/a | Alternativa real para o público-alvo |

Não comparei apps de orçamento comerciais por voz: a busca não retornou fonte primária
adequada. Isso fica como lacuna, sem tabela inventada.

## Evidências e dados

| Dado | Valor | Fonte | Limite |
|---|---|---|---|
| Inadimplentes no Brasil | 83,98 milhões em ago/2026 (51,0% dos adultos, segundo a Serasa) | [Serasa, Mapa da Inadimplência](https://www.serasa.com.br/imprensa/10-anos-do-mapa-de-inadimplencia/) | Os números variam entre publicações (81,7 mi em fev; 83,3 mi em abr). Vi o valor de agosto só em notícia que cita a Serasa |
| Inadimplentes que ganham até 1 salário mínimo | 48% | Serasa (via resumo de busca) | Não vi o relatório original |
| Pessoas com deficiência visual | 6,978 milhões, 3,4% da população de 2 anos ou mais (PNS 2019) | [IBGE](https://agenciadenoticias.ibge.gov.br/agencia-sala-de-imprensa/31445-pns-2019-pais-tem-17-3-milhoes-de-pessoas-com-algum-tipo-de-deficiencia.html) | Pesquisa amostral de 2019, não censo |
| Deficiência visual em idosos | 9,2% | IBGE PNS 2019 | idem |
| Spring AI atual | 2.0.0 GA em 12/06/2026; 2.0.1 em 21/08/2026; projetado para Spring Boot 4.0 e 4.1 | [GA](https://spring.io/blog/2026/06/12/spring-ai-2-0-0-GA-available-now/), [2.0.1](https://spring.io/blog/2026/08/21/spring-ai-2-0-1-available-now) | Versão mínima de Java **não consta** nessas páginas |
| Modelos OpenAI na doc do Spring AI (doc da 2.1.0) | Fala padrão gpt-4o-mini-tts; transcrição padrão whisper-1, com gpt-4o-transcribe e gpt-4o-mini-transcribe como alternativas | [fala](https://docs.spring.io/spring-ai/reference/api/audio/speech/openai-speech.html), [transcrição](https://docs.spring.io/spring-ai/reference/api/audio/transcriptions/openai-transcriptions.html) | A página é da 2.1.0, não da 2.0.x |
| Descontinuação do whisper-1 | Um guia de terceiros cita desligamento em 26/02/2027 | howtodoinjava (via busca) | **Não confirmado** em fonte oficial da OpenAI |

Estes números mostram que o problema é grande, mas não provam que um assistente por voz
reduz endividamento. Sobre isso: **inconclusivo**.

### Pontos técnicos que mudam o projeto

- A base usa Spring AI **2.0.0-M4** (milestone). Existe GA (2.0.0) e 2.0.1.
- Em 2.0 o `ChatClient` registra um `ToolCallingAdvisor` automaticamente; a chave
  `.options` foi removida das propriedades; opções viraram imutáveis por builder.
- 2.0.1: o strict mode das tools OpenAI passou a ser `false` por padrão (parâmetro opcional
  causava erro 400). Há limite de chamadas de tool (`ToolCallLimitExceededException`).
- Propriedades de áudio: `spring.ai.openai.audio.speech.*` e
  `spring.ai.openai.audio.transcription.*` (incluindo `language` e `prompt`).

## Viabilidade matemática e estatística

Sem dados do cliente, não há benefício líquido a estimar. Custo de operação: cada comando
por voz faz até três chamadas à OpenAI (transcrição, chat com tools, fala). Não consultei
preços atuais e **não vou estimar valores sem fonte**. Experimento proposto, a executar na
fase de implementação:

- **Métrica primária:** proporção de comandos de teste em que valor e categoria saem
  corretos. Numerador = comandos corretos; denominador = comandos testados; reportar com
  intervalo de Wilson.
- **Amostra mínima sugerida:** 30 frases em português, com valores, centavos e números por
  extenso ("quarenta e cinco reais"). Com 30 casos o intervalo é largo; é só um teste de
  fumaça, não prova de qualidade.
- **Latência:** medir p50 e p95 do fluxo completo em 10 repetições, informando a rede.

## Proposta e alternativas ao pedido

Recomendação (conecta a ideia 1 com um pedaço da 3):

1. **Confirmação antes de gravar** (acessibilidade): o assistente repete valor, categoria
   e descrição e só grava após "sim". Evidência de apoio: a revisão JMIR e o projeto
   Berkeley apontam confirmação como ponto crítico, mas nenhum mede o efeito. Isto é
   inferência minha.
2. **Limite por categoria** com aviso quando o gasto o ultrapassa (parte da ideia 3).
3. **Validação em código** (P-003): valor positivo, categoria conhecida, limite checado
   pelo código, não pelo prompt.
4. **Respostas curtas e em linguagem simples**, sempre também em áudio.
5. **Atualizar para Spring AI 2.0.1 GA**, em vez de seguir a 2.0.0-M4 da base. Alternativa:
   manter M4 para ficar idêntico ao curso. Risco da alternativa: usar milestone com GA
   disponível.
6. **Transcrição:** testar `whisper-1` (padrão da base) e `gpt-4o-mini-transcribe`, e
   escolher pelo experimento acima.

## Decisão e experimento

Decisões de produto respondidas pelo cliente. Falta ainda: aprovar a versão do Spring AI
(recomendado 2.0.1 GA) e o modelo de transcrição, que dependem do experimento.

| Pergunta | Resposta literal | Origem | Data |
|---|---|---|---|
| Confirmação por voz antes de gravar | "Confirmar sempre (Recomendado)" | Ferramenta de perguntas do harness (AskUserQuestion), sessão atual | 2026-11-22 |
| Limite por categoria: quem define e o que acontece ao estourar | "Usuário define por voz; só avisa (Recomendado)" | idem | 2026-11-22 |
| Retenção do áudio | "Descartar logo (Recomendado)" | idem | 2026-11-22 |
| Um usuário ou vários com login | "Uma pessoa só (Recomendado)" | idem | 2026-11-22 |
