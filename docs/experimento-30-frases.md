# Experimento das 30 frases

**Status (2026-11-22):** modo texto executado com sucesso, 29/30 (Wilson 83% a 99%), veja `resultado-experimento-texto.md`. Modo voz executado com 29 áudios do cliente (a frase 2 se perdeu): 28/29 (Wilson 83% a 99%), veja `resultado-experimento-voz.md`. O script e as 30 frases estão prontos ([`frases-experimento.csv`](frases-experimento.csv) e
[`tools/rodar-experimento.ps1`](../tools/rodar-experimento.ps1)). Uma primeira rodada em modo texto revelou
um defeito real do produto (a IA reescrevia a pergunta de confirmação sem dizer o valor), corrigido na
tarefa T-013. Depois da correção, a rodada em modo texto foi refeita e é a válida. A rodada **por voz**
precisa dos 30 áudios gravados. Nenhum resultado foi inventado: o relatório é gerado pelo script em
`docs/resultado-experimento-texto.md` e `docs/resultado-experimento-voz.md`.

## Como rodar com o script

```powershell
# 1) Sem transcrição (mede só o modelo de chat com as ferramentas)
.\tools\rodar-experimento.ps1 -Modo texto

# 2) Com transcrição: grave f01.m4a ... f30.m4a lendo as frases do CSV e aponte a pasta
.\tools\rodar-experimento.ps1 -Modo voz -Pasta C:\caminho\dos\audios
```

O script nunca grava gasto: depois de cada frase ele diz "não" para limpar a pendência. Ele mostra a
taxa de acerto de valor e categoria com intervalo de Wilson e a latência (p50 e p95).

## Objetivo

Medir quanto o fluxo completo (transcrição + modelo + ferramenta) acerta **valor e categoria** em
português falado. É um teste de fumaça, não uma prova de qualidade: com 30 amostras o intervalo de
confiança é largo. Também ajuda a decidir a pergunta Q-002 da spec (`whisper-1` ou
`gpt-4o-mini-transcribe`).

## Como executar

1. Grave 30 frases em mp3, uma por arquivo (`f01.mp3` ... `f30.mp3`), com vozes e ritmos variados.
2. Para cada frase, escreva na tabela o valor e a categoria **esperados**.
3. Envie cada áudio a `POST /api/voz/comandos` e leia o cabeçalho `X-Resposta-Texto`: ele mostra o
   valor e a categoria que a IA entendeu (a pergunta de confirmação).
4. Marque acerto se valor **e** categoria estão corretos. Limpe o gasto pendente (diga "não") entre as frases.
5. Repita com o outro modelo de transcrição, trocando `spring.ai.openai.audio.transcription.model`.
6. Para a latência, repita 10 vezes a mesma frase e anote o tempo total (p50 e p95), informando a rede.

## Variedade sugerida de frases

- Valores inteiros, com centavos e por extenso: "45 reais", "12 e 90", "quarenta e cinco reais".
- Valores parecidos que se confundem: "quarenta" e "quatorze", "sessenta" e "setenta".
- Todas as 8 categorias, incluindo "alimentação fora" ("almocei fora").
- Datas: hoje, "ontem", "anteontem".
- Ruído de fundo e fala rápida.

## Resultados

| # | Frase dita | Esperado (valor, categoria) | Entendido | whisper-1 | gpt-4o-mini-transcribe |
|---|---|---|---|---|---|
| 1 | | | | | |

Resumo (preencher depois): acertos / 30 por modelo, com intervalo de Wilson de 95%; latência p50 e p95.

Intervalo de Wilson para `p = x/n`, com `z = 1,96`:
`centro = (p + z²/2n) / (1 + z²/n)` e `margem = z·√(p(1−p)/n + z²/4n²) / (1 + z²/n)`.
