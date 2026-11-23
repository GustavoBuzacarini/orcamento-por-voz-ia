# Resultado do experimento: modo texto

Data: 2026-11-22 20:24  |  Base: http://localhost:8081  |  Frases: 30

**Sem transcricao:** a frase entra como texto. Mede so o modelo de chat com as ferramentas, nao o Whisper.

| Metrica | Resultado |
|---|---|
| Valor correto | 29 / 30 |
| Categoria correta | 29 / 30 |
| Valor e categoria corretos | 29 / 30 (97%), intervalo de Wilson 95%: 83% a 99% |
| Respostas sem proposta | 1 |
| Latencia por comando (s), so propostas | p50 2,7, p95 9,7 (n = 29) |

| # | Frase | Esperado | Entendido | Resultado | s | Obs |
|---|---|---|---|---|---|---|
| 1 | gastei 45 reais no mercado | R$ 45.00 / mercado | R$ 45.00 / mercado | acerto | 2,7 |  |
| 2 | paguei 12 reais e 90 de ônibus | R$ 12.90 / transporte | R$ 12.90 / transporte | acerto | 0,6 |  |
| 3 | comprei remédio por trinta e cinco reais | R$ 35.00 / saúde | R$ 35.00 / saúde | acerto | 0,7 |  |
| 4 | gastei quarenta e cinco reais com lazer | R$ 45.00 / lazer | R$ 45.00 / lazer | acerto | 0,6 |  |
| 5 | almocei fora e paguei 28 reais | R$ 28.00 / alimentação fora | R$ 28.00 / alimentação fora | acerto | 6,7 |  |
| 6 | paguei a conta de luz de 180 reais | R$ 180.00 / moradia | R$ 180.00 / moradia | acerto | 9,9 |  |
| 7 | gastei 14 reais de uber | R$ 14.00 / transporte | R$ 14.00 / transporte | acerto | 2,6 |  |
| 8 | paguei a mensalidade do curso de 350 reais | R$ 350.00 / educação | R$ 350.00 / educação | acerto | 2,7 |  |
| 9 | gastei sessenta reais no cinema | R$ 60.00 / lazer | R$ 60.00 / lazer | acerto | 8,6 |  |
| 10 | gastei setenta reais no mercado | R$ 70.00 / mercado | R$ 70.00 / mercado | acerto | 1,7 |  |
| 11 | comprei pão e leite por 18 reais e 50 | R$ 18.50 / mercado | R$ 18.50 / mercado | acerto | 1,7 |  |
| 12 | paguei 250 reais de aluguel | R$ 250.00 / moradia | R$ 250.00 / moradia | acerto | 0,5 |  |
| 13 | gastei 90 reais na farmácia | R$ 90.00 / saúde | R$ 90.00 / saúde | acerto | 2,1 |  |
| 14 | paguei quarenta reais de gasolina | R$ 40.00 / transporte | R$ 40.00 / transporte | acerto | 9,7 |  |
| 15 | jantei num restaurante e gastei 120 reais | R$ 120.00 / alimentação fora | R$ 120.00 / alimentação fora | acerto | 0,6 |  |
| 16 | gastei 8 reais de passagem de metrô | R$ 8.00 / transporte | R$ 8.00 / transporte | acerto | 8,5 |  |
| 17 | comprei um livro de 65 reais | R$ 65.00 / educação | R$ 65.00 / educação | acerto | 1,5 |  |
| 18 | gastei cento e vinte reais no supermercado | R$ 120.00 / mercado | R$ 120.00 / mercado | acerto | 3,5 |  |
| 19 | paguei 33 reais e 40 de estacionamento | R$ 33.40 / transporte | R$ 33.40 / transporte | acerto | 7,6 |  |
| 20 | gastei 200 reais em um show | R$ 200.00 / lazer | R$ 200.00 / lazer | acerto | 3,6 |  |
| 21 | paguei a consulta médica de 300 reais | R$ 300.00 / saúde | R$ 300.00 / saúde | acerto | 8,7 |  |
| 22 | comprei material escolar de 75 reais | R$ 75.00 / educação | R$ 75.00 / educação | acerto | 2,5 |  |
| 23 | gastei 15 reais com lanche na rua | R$ 15.00 / alimentação fora | R$ 15.00 / alimentação fora | acerto | 2,5 |  |
| 24 | paguei 400 reais de condomínio | R$ 400.00 / moradia | R$ 400.00 / moradia | acerto | 8,7 |  |
| 25 | gastei quatorze reais no mercado | R$ 14.00 / mercado | R$ 14.00 / mercado | acerto | 2,7 |  |
| 26 | gastei quarenta reais no mercado | R$ 40.00 / mercado | R$ 40.00 / mercado | acerto | 7,6 |  |
| 27 | gastei sessenta e cinco reais no transporte | R$ 65.00 / transporte | R$ 65.00 / transporte | acerto | 2,7 |  |
| 28 | gastei setenta reais na farmácia | R$ 70.00 / saúde | R$ 70.00 / saúde | acerto | 1,5 |  |
| 29 | gastei 22 reais e 75 no mercado | R$ 22.75 / mercado | R$ 22.75 / mercado | acerto | 2,5 |  |
| 30 | gastei 10 reais com um presente | R$ 10.00 / outros | - | erro | 0,7 | sem proposta: A categoria outras não existe. As categorias são: mercado, alimentação fora, transporte, moradia, saúde, educação, lazer, outros. |
