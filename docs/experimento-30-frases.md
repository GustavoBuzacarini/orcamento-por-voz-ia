# Experimento das 30 frases

**Status: NÃO EXECUTADO.** Ele precisa de uma chave da OpenAI e de áudios gravados pela própria
pessoa. Nenhum resultado abaixo foi inventado: a tabela está em branco de propósito.

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
