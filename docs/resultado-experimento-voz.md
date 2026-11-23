# Resultado do experimento: modo voz

Data: 2026-11-22 20:47  |  Base: http://localhost:8081  |  Frases testadas: 29 de 30

**Amostra reduzida:** sem audio para as frases 2. Elas ficam fora da conta, e o denominador e 29.

| Metrica | Resultado |
|---|---|
| Valor correto | 28 / 29 |
| Categoria correta | 29 / 29 |
| Valor e categoria corretos | 28 / 29 (97%), intervalo de Wilson 95%: 83% a 99% |
| Respostas sem proposta | 0 |
| Latencia por comando (s), so propostas | p50 1,2, p95 7,9 (n = 29) |

| # | Frase | Esperado | Entendido | Resultado | s | Obs |
|---|---|---|---|---|---|---|
| 1 | gastei 45 reais no mercado | R$ 45.00 / mercado | R$ 45.00 / mercado | acerto | 1,3 |  |
| 2 | paguei 12 reais e 90 de ônibus | - | - | sem audio | 0 | audio nao encontrado, fora da conta |
| 3 | comprei remédio por trinta e cinco reais | R$ 35.00 / saúde | R$ 35.00 / saúde | acerto | 1 |  |
| 4 | gastei quarenta e cinco reais com lazer | R$ 45.00 / lazer | R$ 45.00 / lazer | acerto | 1,2 |  |
| 5 | almocei fora e paguei 28 reais | R$ 28.00 / alimentação fora | R$ 28.00 / alimentação fora | acerto | 1,1 |  |
| 6 | paguei a conta de luz de 180 reais | R$ 180.00 / moradia | R$ 180.00 / moradia | acerto | 0,9 |  |
| 7 | gastei 14 reais de uber | R$ 14.00 / transporte | R$ 14.00 / transporte | acerto | 1 |  |
| 8 | paguei a mensalidade do curso de 350 reais | R$ 350.00 / educação | R$ 350.00 / educação | acerto | 0,8 |  |
| 9 | gastei sessenta reais no cinema | R$ 60.00 / lazer | R$ 60.00 / lazer | acerto | 1 |  |
| 10 | gastei setenta reais no mercado | R$ 70.00 / mercado | R$ 70.00 / mercado | acerto | 1,1 |  |
| 11 | comprei pão e leite por 18 reais e 50 | R$ 18.50 / mercado | R$ 18.50 / mercado | acerto | 0,8 |  |
| 12 | paguei 250 reais de aluguel | R$ 250.00 / moradia | R$ 200.50 / moradia | so categoria | 1,2 |  |
| 13 | gastei 90 reais na farmácia | R$ 90.00 / saúde | R$ 90.00 / saúde | acerto | 1 |  |
| 14 | paguei quarenta reais de gasolina | R$ 40.00 / transporte | R$ 40.00 / transporte | acerto | 7,9 |  |
| 15 | jantei num restaurante e gastei 120 reais | R$ 120.00 / alimentação fora | R$ 120.00 / alimentação fora | acerto | 2,1 |  |
| 16 | gastei 8 reais de passagem de metrô | R$ 8.00 / transporte | R$ 8.00 / transporte | acerto | 2,2 |  |
| 17 | comprei um livro de 65 reais | R$ 65.00 / educação | R$ 65.00 / educação | acerto | 1,2 |  |
| 18 | gastei cento e vinte reais no supermercado | R$ 120.00 / mercado | R$ 120.00 / mercado | acerto | 2,2 |  |
| 19 | paguei 33 reais e 40 de estacionamento | R$ 33.40 / transporte | R$ 33.40 / transporte | acerto | 2 |  |
| 20 | gastei 200 reais em um show | R$ 200.00 / lazer | R$ 200.00 / lazer | acerto | 2,1 |  |
| 21 | paguei a consulta médica de 300 reais | R$ 300.00 / saúde | R$ 300.00 / saúde | acerto | 1,9 |  |
| 22 | comprei material escolar de 75 reais | R$ 75.00 / educação | R$ 75.00 / educação | acerto | 1,1 |  |
| 23 | gastei 15 reais com lanche na rua | R$ 15.00 / alimentação fora | R$ 15.00 / alimentação fora | acerto | 2,9 |  |
| 24 | paguei 400 reais de condomínio | R$ 400.00 / moradia | R$ 400.00 / moradia | acerto | 1 |  |
| 25 | gastei quatorze reais no mercado | R$ 14.00 / mercado | R$ 14.00 / mercado | acerto | 3,1 |  |
| 26 | gastei quarenta reais no mercado | R$ 40.00 / mercado | R$ 40.00 / mercado | acerto | 3 |  |
| 27 | gastei sessenta e cinco reais no transporte | R$ 65.00 / transporte | R$ 65.00 / transporte | acerto | 3,2 |  |
| 28 | gastei setenta reais na farmácia | R$ 70.00 / saúde | R$ 70.00 / saúde | acerto | 1,1 |  |
| 29 | gastei 22 reais e 75 no mercado | R$ 22.75 / mercado | R$ 22.75 / mercado | acerto | 8,6 |  |
| 30 | gastei 10 reais com um presente | R$ 10.00 / outros | R$ 10.00 / outros | acerto | 3,2 |  |
