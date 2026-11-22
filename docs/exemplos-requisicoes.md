# Exemplos de requisições

Todos os exemplos usam `curl.exe` no PowerShell, com a aplicação em `http://localhost:8080`.
As respostas abaixo são **exemplos do formato**; com um modelo real, a redação pode variar um pouco.

## Registrar um gasto (texto)

```powershell
curl.exe -X POST http://localhost:8080/api/texto/comandos -H "Content-Type: application/json" -d '{\"texto\":\"gastei 45 reais no mercado\"}'
```

```json
{"resposta": "Vou gravar R$ 45,00 em mercado, hoje. Posso gravar? Diga sim ou não."}
```

## Confirmar

```powershell
curl.exe -X POST http://localhost:8080/api/texto/comandos -H "Content-Type: application/json" -d '{\"texto\":\"sim\"}'
```

```json
{"resposta": "Gasto de R$ 45,00 em mercado gravado."}
```

## Definir limite e gastar acima dele

```powershell
curl.exe -X POST http://localhost:8080/api/texto/comandos -H "Content-Type: application/json" -d '{\"texto\":\"meu limite de lazer é 300 reais\"}'
```

```json
{"resposta": "Limite de lazer: R$ 300,00 por mês."}
```

Se já houver R$ 280,00 em lazer e você gravar mais R$ 50,00, a confirmação avisa antes
("isso ultrapassa o limite de lazer em R$ 30,00") e, depois do "sim", diz que o limite foi
ultrapassado em R$ 30,00. O gasto é gravado mesmo assim: o limite só avisa.

## Valor inválido

```powershell
curl.exe -X POST http://localhost:8080/api/texto/comandos -H "Content-Type: application/json" -d '{\"texto\":\"gastei 0 reais no mercado\"}'
```

Com a IA real, o modelo chama a ferramenta e o código recusa: "O valor precisa ser maior que zero."
Nada fica pendente nem é gravado.

## Voz

```powershell
curl.exe -X POST http://localhost:8080/api/voz/comandos -F "audio=@comando.mp3" -D - -o resposta.mp3
```

Cabeçalhos da resposta (trecho):

```
HTTP/1.1 200
Content-Type: audio/mpeg
X-Resposta-Texto: Vou%20gravar%20R%24%2045%2C00%20em%20mercado%2C%20hoje.%20Posso%20gravar%3F%20Diga%20sim%20ou%20n%C3%A3o.
```

Para decodificar o texto:

```powershell
[uri]::UnescapeDataString("Vou%20gravar%20R%24%2045%2C00%20em%20mercado")
```

## Erros

| Situação | Resposta |
|---|---|
| Áudio vazio | `400` com `{"mensagem": "O áudio veio vazio. Grave de novo e envie."}` |
| Áudio maior que 5 MB | `413` com a mensagem do limite |
| OpenAI fora do ar | `503` com `{"mensagem": "o assistente está indisponível, tente de novo em instantes"}` |
| Áudio sem fala reconhecível | `200`, em áudio: "Não entendi, pode repetir?" |

## Conferir o que foi gravado

```powershell
curl.exe http://localhost:8080/api/gastos
curl.exe http://localhost:8080/api/limites
curl.exe http://localhost:8080/api/resumo
```
