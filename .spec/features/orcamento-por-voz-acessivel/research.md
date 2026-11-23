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

## Adendo (2026-11-22): provedor gratuito

Motivo: o cliente não consegue pagar a OpenAI. Fontes consultadas em 2026-11-22; a observação vem das
páginas, a decisão é do cliente (ADR D5).

| Item | Observado | Fonte | Limite |
|---|---|---|---|
| URL base da Groq | `https://api.groq.com/openai/v1` (inclui `/v1`) | [Groq, speech-to-text](https://console.groq.com/docs/speech-to-text), [Spring AI, Groq](https://docs.spring.io/spring-ai/reference/2.0/api/chat/groq-chat.html) | — |
| Modelos de chat de produção com tool use | `openai/gpt-oss-20b`, `openai/gpt-oss-120b`, `llama-3.3-70b-versatile`, `llama-3.1-8b-instant` | [Groq, modelos](https://console.groq.com/docs/models) | A página de tool use diz que todos os modelos hospedados suportam; `gpt-oss` não faz chamadas de ferramenta em paralelo (o projeto não precisa) |
| Transcrição | `whisper-large-v3-turbo` (custo e desempenho) e `whisper-large-v3` (precisão); mp3, m4a, wav, webm, ogg, flac aceitos; 25 MB no plano gratuito | [Groq, speech-to-text](https://console.groq.com/docs/speech-to-text) | Limites de requisições do plano gratuito **não constam** nas páginas lidas |
| Compatibilidade | "A API da Groq não é totalmente compatível com a da OpenAI"; não suporta mensagens multimodais | [Spring AI, Groq](https://docs.spring.io/spring-ai/reference/2.0/api/chat/groq-chat.html) | Esta página do Spring AI é da 2.0.0-RC2 e cita IDs de modelo antigos; usei os IDs da página de modelos da Groq |
| Voz | Anunciada só em inglês e árabe | [Groq, anúncio](https://community.groq.com/groq-updates-2/announcements-115/index3.html) | Por isso a fala é opcional (spec AC-018) |
| Transcrição via Spring AI | O Spring AI documenta transcrição só para OpenAI e Azure OpenAI | docs.spring.io | Usar o cliente da OpenAI contra a URL da Groq é uma suposição **ainda não testada** |

Hipótese a validar com uma chave gratuita da Groq: o cliente OpenAI do Spring AI 2.0.1 funciona
contra a Groq para chat com ferramentas e para transcrição. Se falhar, o ADR D5 deve ser revisto.

### Achado em teste real (2026-11-22)

Rodando a aplicação com a Groq, a primeira chamada com ferramenta falhou com `400: property
'reasoning_content' is unsupported` ao usar `openai/gpt-oss-20b`. Interpretação: o modelo de raciocínio
devolve `reasoning_content`, o Spring AI reenvia a mensagem do assistente com esse campo depois da
chamada da ferramenta, e a Groq o recusa. O erro confirma a ressalva da Groq de que a API não é totalmente
compatível com a da OpenAI. Mitigação adotada: usar um modelo que não raciocina, `llama-3.3-70b-versatile`
(produção, com tool use segundo a página de modelos). Resultado da troca: ainda não confirmado.

Resultado em teste real (2026-11-22, relato do cliente): com `openai/gpt-oss-20b` e `include_reasoning: false`
(`AI_SEM_RACIOCINIO=true`), o comando "gastei 45 reais no mercado" por `POST /api/texto/comandos` devolveu a
pergunta de confirmação. O `llama-3.3-70b-versatile` citado na página de modelos da Groq retornou 404 na conta do
cliente; a lista de modelos varia por conta (`GET /openai/v1/models`). Transcrição de áudio na Groq: ainda não testada.

Transcrição na Groq (2026-11-22, 1 amostra): o áudio do cliente "gastei trinta reais no transporte" (m4a, 5 s,
Gravador de Som do Windows), enviado a `POST /api/voz/comandos` com `whisper-large-v3-turbo`, gerou a proposta
"R$ 30,00 em transporte, hoje" com resposta em JSON (fala desligada), em cerca de 1,7 s no total. É uma única
amostra, sem intervalo de confiança: não prova taxa de acerto. O experimento das 30 frases segue pendente.

### Achado do experimento em modo texto (2026-11-22, primeira rodada)

Rodada de 30 frases por `POST /api/texto/comandos` (sem transcrição) com `openai/gpt-oss-20b` na Groq. O
leitor automático do resultado só reconhecia frases iniciadas por "Vou gravar", então os números dessa rodada
**não valem como taxa de acerto** (o intervalo de Wilson também saiu errado por um defeito do script, já
corrigido). O que ela mostrou de verdade, lendo as respostas uma a uma:

- Em 6 das 30 respostas, o modelo reescreveu a pergunta de confirmação **sem dizer o valor** ("Aguardando sua
  confirmação.", "Posso gravar? Diga sim ou não."). Isso derruba a proteção principal do projeto para quem não
  enxerga a tela. Os testes com IA falsa não pegavam isso, porque a frase era a do código.
- "33 reais e 40" foi lido como dois números, e outras respostas citaram um limite com valor trocado.
- Latência por comando de 1 a 13 s, com duas chamadas à IA por comando.

Mitigação (spec AC-020, tarefa T-013): as ferramentas passam a devolver a frase do código **direto à pessoa**
(`returnDirect` com um conversor de texto puro), sem o modelo reescrever. O modelo só escolhe a ferramenta e os
argumentos. Efeito esperado: a confirmação sempre diz valor e categoria, e cai de duas para uma chamada à IA por
comando. Provado com um modelo falso no teste `FraseDoCodigoTest`; **ainda não medido com a IA real**.

### Resultado válido do experimento em modo texto (2026-11-22, depois da correção T-013)

30 frases por `POST /api/texto/comandos` (sem transcrição), `openai/gpt-oss-20b` na Groq, aplicação com
`returnDirect`. Relatório gerado pelo script: `docs/resultado-experimento-texto.md`.

| Métrica | Resultado |
|---|---|
| Valor e categoria corretos | 29 / 30 (97%), intervalo de Wilson 95%: 83% a 99% |
| Latência por comando (n = 29 propostas) | p50 2,7 s; p95 9,7 s |
| Único erro | caso 30: o modelo enviou a categoria "outras" e o código recusou ("A categoria outras não existe"), listando as categorias |

Limites desta medida: 30 frases escritas pela equipe, uma só rodada, um só modelo, sem ruído e **sem
transcrição** (a frase entra como texto). Não mede o Whisper. A rodada por voz continua pendente e é a que
decide a pergunta Q-002. Comparação com a rodada anterior: ela não é válida (leitor do script com defeito),
então não há "antes e depois" numérico; o antes é só a leitura manual de 6 confirmações sem valor.

### Resultado do experimento por voz (2026-11-22)

29 áudios gravados pelo próprio cliente (m4a, 3 a 5 s, Gravador de Som do Windows, microfone do notebook),
lendo as frases de `docs/frases-experimento.csv`; o áudio da frase 2 se perdeu e ficou **fora da conta**.
Transcrição `whisper-large-v3-turbo`, chat `openai/gpt-oss-20b` (Groq), aplicação com `returnDirect`. Relatório
gerado pelo script: `docs/resultado-experimento-voz.md`.

| Métrica | Resultado |
|---|---|
| Valor e categoria corretos | 28 / 29 (97%), intervalo de Wilson 95%: 83% a 99% |
| Categoria correta | 29 / 29 |
| Valor correto | 28 / 29 |
| Latência por comando (n = 29) | p50 1,2 s; p95 7,9 s |
| Único erro | frase 12, "paguei 250 reais de aluguel": entendido R$ 200,50. A confirmação por voz diria "R$ 200,50" antes de gravar |

Interpretação do erro (hipótese, sem prova): o número "duzentos e cinquenta" foi lido como "200 e 50" (reais e
centavos). O prompt ensina "doze e noventa" como 12,90, e essa regra pode ter induzido a leitura. O script não
guarda a transcrição, então não dá para separar erro do Whisper de erro do modelo de chat.

Limites desta medida: uma voz (a do cliente), um microfone, ambiente silencioso, 29 frases lidas de uma lista
(leitura, não fala espontânea), uma rodada, um modelo de transcrição. Com n = 29 o intervalo é largo. Não compara
`whisper-large-v3` com `whisper-large-v3-turbo`, e não compara com o `whisper-1` da OpenAI, então a pergunta
Q-002 (qual modelo de transcrição) só tem evidência para o `whisper-large-v3-turbo`.

### Decisão Q-002 (2026-11-22)

Resposta literal do cliente no chat: "feche o Q-002 com whisper-large-v3-turbo". Escolhido para a transcrição com
a Groq (`AI_TRANSCRIPTION_MODEL=whisper-large-v3-turbo`, que já é o valor do `.env.example`). Evidência: 28/29
no experimento por voz. Não há comparação com `whisper-large-v3` nem com `whisper-1`; a decisão pode ser
revista se um novo experimento mostrar diferença.
