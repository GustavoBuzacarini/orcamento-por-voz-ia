# Orçamento por voz acessível

API inteligente em Java com Spring Boot e Spring AI para controlar gastos **só falando**. A pessoa
manda um áudio, a IA entende o que ela quer, a aplicação executa uma função real (registrar um
gasto, definir um limite, consultar o mês) e a resposta volta **em áudio e em texto**.

Projeto do desafio DIO "Spring Boot + IA". Evolui a API de orçamento por voz do curso
([`05-spring-ai`](https://github.com/digitalinnovationone/dio-spring-boot-learning-track/blob/main/05-spring-ai/README.md))
com foco em acessibilidade e em um pouco de educação financeira.

## O que o projeto faz

Fluxo principal:

1. Recebe um arquivo de áudio.
2. Transcreve o áudio em texto (OpenAI, via Spring AI).
3. Um modelo de linguagem entende a intenção e escolhe uma ferramenta (Tool Calling).
4. A ferramenta chama um caso de uso real da aplicação, que valida e grava no banco.
5. A resposta é convertida em áudio mp3 e devolvida junto com o texto.

Exemplos do que dá para falar:

| Você diz | O que acontece |
|---|---|
| "gastei 45 reais no mercado" | O assistente repete "R$ 45,00 em mercado" e pergunta se pode gravar |
| "sim" | Grava o gasto (e avisa quanto resta do limite, ou que ele foi ultrapassado) |
| "não" | Descarta o gasto proposto |
| "meu limite de lazer é 300 reais" | Define o limite mensal da categoria |
| "quanto gastei com mercado este mês?" | Diz o total da categoria no mês |
| "como está meu mês?" | Diz o total, o total por categoria e as categorias acima do limite |

## Melhoria implementada

O projeto do curso registra gastos direto. Aqui foram acrescentados:

- **Confirmação por voz antes de gravar.** Um erro de transcrição ("quarenta" por "quatorze") não vira
  registro errado, e quem não enxerga a tela consegue conferir o que será gravado.
- **Validação em código.** Valor maior que zero, categoria conhecida e data que não seja futura são
  verificados pelo código, nunca pelo prompt. A IA interpreta, o código decide.
- **Limites mensais por categoria**, definidos por voz. O limite só avisa; nunca bloqueia.
- **Novas consultas**: total por categoria e resumo do mês.
- **Resposta acessível**: em áudio (`audio/mpeg`) com o mesmo texto no cabeçalho `X-Resposta-Texto`
  (percent-encoded em UTF-8), para leitores de tela. A fala é opcional (`FALA_HABILITADA=false`): sem ela,
  a resposta vem em JSON com o texto. Isso permite usar um provedor gratuito que não gera voz em português.
- **Privacidade**: o áudio é processado em memória e descartado. O banco guarda só valores,
  categorias e datas.
- **Endpoint equivalente por texto**, para usar sem microfone.
- **Falhas da IA tratadas**: se a OpenAI estiver fora do ar, a API responde 503 com uma mensagem
  clara e nada é gravado.

A especificação completa (PRD, ADR, TDD, spec com 21 critérios de aceite e pesquisa) está em
[`.spec/features/orcamento-por-voz-acessivel`](.spec/features/orcamento-por-voz-acessivel).

## Tecnologias

Java 25 · Spring Boot 4.1 · Spring AI 2.0.1 (ChatClient, `@Tool`, transcrição e fala) · Groq
(gratuito) ou OpenAI (`gpt-4o-mini`, `whisper-1`, `gpt-4o-mini-tts`) · Spring Data JPA · MySQL 9 (Docker) ·
H2 nos testes · Gradle · JUnit 5 e MockMvc.

## Como executar

Pré-requisitos: JDK 25, Docker e uma chave de um provedor de IA compatível com a API da OpenAI.

**Provedores** (escolha no `.env`, sem mudar o código):

| | Groq (gratuito) | OpenAI (pago) |
|---|---|---|
| Chat com ferramentas | sim | sim |
| Transcrição | sim (Whisper) | sim |
| Resposta falada em português | **não** (a fala fica desligada e a resposta vem em texto) | sim |

A Groq tem plano gratuito, com limites que você confere no painel dela. Veja a decisão em
[ADR D5](.spec/features/orcamento-por-voz-acessivel/adr.md). O uso com a Groq depende de ela aceitar o
cliente da OpenAI do Spring AI; isso foi documentado, mas só se confirma rodando com uma chave.

1. Copie `.env.example` para `.env`, escolha o bloco do provedor (Groq vem ligado) e preencha
   `AI_API_KEY`, `MYSQL_PASSWORD` e `MYSQL_ROOT_PASSWORD`. O `.env` não vai para o Git.
2. Suba o MySQL:

   ```powershell
   docker compose --env-file .env up -d
   ```

3. Carregue as variáveis e rode a aplicação (PowerShell):

   ```powershell
   Get-Content .env | Where-Object { $_ -match '^[^#].+=' } | ForEach-Object { $n,$v = $_ -split '=',2; Set-Item "env:$n" $v }
   ./gradlew.bat bootRun
   ```

A API sobe em `http://localhost:8080`.

> **Atenção:** esta versão não tem login. Use só na sua máquina e não exponha a porta na internet.
> O áudio e o texto dos comandos são enviados à OpenAI para serem processados.

## Como testar o fluxo principal

Sem chave da OpenAI, rode a suíte automática. Ela troca a IA e a voz por versões falsas e
determinísticas, então não custa nada e não usa a rede:

```powershell
./gradlew.bat test
```

Para ver os 21 critérios da spec com prova:

```powershell
node .claude/skills/onp-spec-driven/scripts/onp-spec.mjs verify orcamento-por-voz-acessivel
```

Com a chave configurada e a aplicação rodando, teste pelo texto:

```powershell
Invoke-RestMethod -Method Post -Uri http://localhost:8080/api/texto/comandos -ContentType "application/json; charset=utf-8" -Body ([Text.Encoding]::UTF8.GetBytes('{"texto":"gastei 45 reais no mercado"}'))
```

E pela voz, enviando um arquivo de áudio seu (grave "gastei 45 reais no mercado" em `comando.m4a`):

```powershell
curl.exe -X POST http://localhost:8080/api/voz/comandos -F "audio=@comando.m4a" -D -
```

Com a fala ligada, a resposta é um mp3 (use `-o resposta.mp3`) e o cabeçalho `X-Resposta-Texto` traz o
mesmo texto. Com a fala desligada (Groq), a resposta é um JSON com o texto. Mais exemplos em
[`docs/exemplos-requisicoes.md`](docs/exemplos-requisicoes.md).

### Endpoints

| Método e caminho | Para quê |
|---|---|
| `POST /api/voz/comandos` (multipart, campo `audio`) | Comando por voz; devolve mp3 + cabeçalho com o texto |
| `POST /api/texto/comandos` (JSON `{"texto": "..."}`) | O mesmo comando por texto; devolve `{"resposta": "..."}` |
| `GET /api/gastos`, `/api/limites`, `/api/resumo` | Conferir o que foi gravado |

## O que os testes provam, e o que não provam

Os 21 critérios provam as **regras do nosso código**: confirmar antes de gravar, recusar valores
inválidos, avisar do limite, não guardar áudio e tratar falhas. Eles **não provam** que o modelo
entende bem o seu português: para isso há um experimento de 30 frases, descrito em
[`docs/experimento-30-frases.md`](docs/experimento-30-frases.md), com um script (`tools/rodar-experimento.ps1`).

Resultados (Groq, `openai/gpt-oss-20b` e `whisper-large-v3-turbo`, valor e categoria corretos):

| Rodada | Acertos | Intervalo de Wilson 95% | Latência p50 / p95 |
|---|---|---|---|
| Texto (sem transcrição), 30 frases | 29 / 30 (97%) | 83% a 99% | 2,7 s / 9,7 s |
| Voz, 29 áudios gravados pelo autor | 28 / 29 (97%) | 83% a 99% | 1,2 s / 7,9 s |

Limites honestos: uma só voz, um só microfone, ambiente silencioso, frases lidas de uma lista e uma rodada
de cada. Na voz, o único erro foi "duzentos e cinquenta" lido como R$ 200,50; a confirmação por voz diria o
valor antes de gravar. Relatórios completos em [`docs/`](docs).

## O que aprendi

Achei muito interessante, e diferente do que eu esperava, a forma como tratei as regras de negócio. Isso me
fez raciocinar mais a fundo e pensar no bom desempenho e na confiabilidade do projeto: em vez de deixar a IA
decidir tudo, deixei que ela apenas **entenda o pedido**, e é o código que valida valor, categoria, data e
limite. A IA interpreta, o código decide.

Também foi incrível ver a IA com voz funcionando de verdade: eu falo uma frase, ela é transcrita, entendida,
validada e respondida. Além de reforçar os fundamentos de Java e Spring Boot que o curso ensina, aprendi a
usar a IA como aceleradora do trabalho sem abrir mão do cuidado com o projeto: eu orientei o assistente de
IA e validei tudo com testes automáticos e com o experimento de frases.

Para saber se funcionava de verdade, testei dezenas de frases: 30 por texto e 29 por voz, com a minha própria
voz. O resultado foi de 97% de acertos em valor e categoria nas duas rodadas (veja a tabela acima, com os
limites dessa medida).

### Dificuldades que enfrentei e resolvi
- **Docker e portas:** eram coisas novas para mim. A porta 8080 estava ocupada pelo banco Oracle, então rodei
  a aplicação na 8081.
- **Provedor de IA:** tive erros de autenticação (a chave de um provedor enviada para outro), de modelo que
  não existia na minha conta e de incompatibilidade com o modelo de raciocínio. Aprendi a ler o erro no log
  e a corrigir pela configuração.
- **Um defeito que só o experimento mostrou:** às vezes a IA reescrevia a pergunta de confirmação sem dizer o
  valor. Passei a deixar o código montar essa frase, e o teste mostrou que a pergunta sempre traz valor e
  categoria.

No começo foi difícil, o que é normal quando tudo é novo, mas tudo foi resolvido.

### Lições técnicas
- Tool Calling funciona melhor com ferramentas finas: a ferramenta só repassa para um caso de uso, e a regra
  mora no código, onde dá para testar sem IA.
- Colocar a IA atrás de uma interface (`AssistenteFinanceiro`) permite testar tudo de forma determinística,
  sem chave e sem custo.
- Teste automático com IA falsa não substitui o teste com a IA real: foi o experimento que achou o defeito
  da confirmação sem valor.
- A versão do curso usa uma versão de testes (milestone) do Spring AI; existe versão final, e as propriedades
  e builders mudaram entre elas.
- Acessibilidade muda o desenho: confirmar por voz e devolver o texto junto do áudio não são detalhes, são
  requisitos.

Fiquei muito feliz com o resultado e totalmente satisfeito com o que construí.

## Estrutura

```
src/main/java/br/com/orcamentovoz
  domain/          regras: categorias, gasto, limite, validador
  application/     casos de uso e portas (assistente, transcrição, fala)
  infrastructure/  ai (Spring AI e ferramentas), http (controllers), persistence (JPA)
.spec/             constituição, PRD, ADR, TDD, spec, tarefas e provas
```
