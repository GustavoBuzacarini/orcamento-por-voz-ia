<!-- onp-spec:documentation-contract=v1 -->

# TDD: orcamento por voz acessivel

> feature: orcamento-por-voz-acessivel
> status: revisado

[PRD](prd.md) · [ADR](adr.md) · [Spec](spec.md) · [Tarefas](tasks.md) · [Pesquisa](research.md)

## 1. Identificação e responsáveis

Autor: Claude Code, com o cliente como dono do produto. Responsável técnico: o cliente.
Data: 2026-11-22. Versão: 0.1. Revisão: pendente.

## 2. Contexto e sistema atual

Não há sistema atual: repositório novo. A referência é o projeto do curso DIO
(`05-spring-ai`): áudio → transcrição → ChatClient com Tool Calling → casos de uso → fala,
em três camadas (`domain`, `application`, `infrastructure`) com MySQL. Li o README, o
`build.gradle` e as propriedades, mas não o código das classes ([pesquisa](research.md)).
Não há PRD ou ADR anterior.

## 3. Problema e motivação

O [PRD](prd.md) pede registrar e consultar gastos só por voz. Sem confirmação e sem validação
em código, um erro de transcrição ("quarenta" por "quatorze") grava um valor errado sem que
a pessoa perceba, pois ela não lê a tela. O custo de não agir é um orçamento em que não se
pode confiar.

## 4. Escopo e não objetivos

Escopo: AC-001 a AC-017 da [spec](spec.md). Não objetivos: login, receitas, edição de gastos,
front-end, bloqueio por limite, medir qualidade do modelo por teste automático.

## 5. Solução e arquitetura

Pacote base: `br.com.orcamentovoz`.

```
infrastructure/http   VozController (POST /api/voz/comandos), TextoController
                      (POST /api/texto/comandos), ConsultaController (GET), tratamento de erros
application           ProcessarComandoUseCase, RegistrarGastoUseCase, ConfirmarGastoUseCase,
                      DefinirLimiteUseCase, ConsultarGastosUseCase, ResumoMesUseCase
                      porta AssistenteFinanceiro (interpreta o texto e escolhe a ferramenta)
domain                Categoria, Gasto, GastoPendente, LimiteCategoria, ValidadorDeGasto
infrastructure/ai     ChatClientAssistente (Spring AI + @Tool), FerramentasFinanceiras,
                      TranscricaoOpenAi, FalaOpenAi
infrastructure/persistence  repositórios JPA
```

Fluxo de voz: o controller recebe o áudio e o transcreve em memória; o texto vai ao
`AssistenteFinanceiro`; o modelo escolhe uma ferramenta; a ferramenta chama um caso de uso; o
caso de uso valida (domínio) e persiste; o texto da resposta é convertido em mp3 e devolvido.

**Contratos**

- `POST /api/voz/comandos`, multipart, campo `audio`. Sucesso: 200, `audio/mpeg`, cabeçalho
  `X-Resposta-Texto` com o texto em UTF-8 codificado por percentual. Áudio vazio: 400 JSON.
  Transcrição vazia: 200 com áudio "não entendi, pode repetir?". Falha da IA: 503 JSON
  `{"mensagem": "..."}`.
- `POST /api/texto/comandos`, JSON `{"texto": "..."}` → 200 `{"resposta": "..."}`.
- `GET /api/gastos`, `GET /api/limites`, `GET /api/resumo` → JSON para conferência.

**Ferramentas expostas ao modelo** (todas finas, sem regra):
`proporGasto(valor, categoria, descricao, data?)`, `confirmarGasto()`, `descartarGasto()`,
`definirLimite(categoria, valor)`, `totalPorCategoria(categoria)`, `resumoDoMes()`.

**Dados**

| Tabela | Campos | Observação |
|---|---|---|
| `gasto` | id, valor (decimal 12,2), categoria, descricao, data, criado_em | só gastos confirmados |
| `gasto_pendente` | id, valor, categoria, descricao, data, expira_em | no máximo uma linha |
| `limite_categoria` | categoria (chave), valor_mensal | um por categoria |

Não há tabela nem coluna com áudio. O texto transcrito pode ficar em log sem dados pessoais
além do próprio comando.

**Regras no domínio** (P-003): valor > 0 e dentro de 2 casas; categoria da lista; data não
futura (relógio injetável, fuso America/Sao_Paulo); pendente expira em 5 minutos; limite é
somado sobre gastos do mês de calendário.

**Falhas:** exceção da OpenAI na transcrição, no chat ou na fala vira `AssistenteIndisponivel`
→ 503, sem gravação (a gravação só ocorre dentro de `confirmarGasto` ou `definirLimite`).

## 6. Riscos e mitigação

| Risco | Evidência / hipótese | Probabilidade | Impacto | Mitigação | Responsável |
|---|---|---|---|---|---|
| Transcrição troca o valor | Hipótese; a medir no experimento de 30 frases | Média | Alto | Confirmação sempre repete o valor; prompt de transcrição em português com exemplos de valores | Equipe |
| Modelo chama a ferramenta errada | Hipótese | Média | Médio | Ferramentas finas; validação em código; limite de chamadas de tool (2.0.1) | Equipe |
| Propriedades da 2.0.1 diferem da base | Pesquisa: chave `.options` removida e builders imutáveis | Alta | Baixo | T-001 lê a doc da 2.0.1 e testa a subida | Equipe |
| `whisper-1` descontinuado | Só fonte de terceiros (26/02/2027) | Baixa | Médio | Modelo vem de propriedade; compara `gpt-4o-mini-transcribe` | Equipe |
| H2 esconde erro de SQL do MySQL | Hipótese | Baixa | Baixo | Só JPA, sem SQL nativo | Equipe |
| Custo por comando (3 chamadas à OpenAI) | Preços não consultados | Média | Baixo | Documentar no README; não estimar valores | Cliente |

## 7. Plano de implementação

Etapas ligadas às [tarefas](tasks.md): T-001 esqueleto e configuração → T-002 domínio e
validação → T-003 persistência → T-004 a T-006 casos de uso (arquivos disjuntos, podem rodar
em paralelo depois de T-003) → T-007 ferramentas e ChatClient → T-008 voz e erros → T-009
texto e consultas → T-010 README e experimento. T-004 a T-006 só dependem de T-002 e T-003.

## 8. Segurança e privacidade

- Autenticação: não aplicável nesta versão (uma pessoa, uso local); a API não deve ser exposta
  à internet. Registrado como limitação no README.
- Dados pessoais: valores e categorias de gasto. O áudio é processado em memória e não é
  gravado em disco nem no banco (AC-015). O áudio e o texto saem para a OpenAI: o README avisa.
- Segredos: `OPENAI_API_KEY` só por variável de ambiente; `.env` fora do Git (P-002).
- Abuso: tamanho máximo do upload configurável (padrão 5 MB); só `audio/*`.
- Pagamentos: não aplicável.

## 9. Estratégia de testes

Mapa AC → teste (título com `@spec:AC-xxx`): todos os AC-001 a AC-017. Testes de API com
`MockMvc`, banco H2, `AssistenteFinanceiro`, `TranscriptionModel` e `TextToSpeechModel`
falsos. A fala falsa devolve bytes conhecidos para provar AC-013. O princípio P-003 tem um
teste próprio (`@principle:P-003`): um assistente falso tenta gravar valor negativo e
categoria inválida, e o código recusa. Teste pulado não conta como prova. A qualidade real do
modelo é medida à parte no experimento de 30 frases, fora do gate.

## 10. Operação e observabilidade

Logs: comando transcrito, ferramenta chamada, resultado da validação, tempo de cada etapa.
Sem chave, sem bytes de áudio. `logging.level.org.springframework.ai=DEBUG` só em
desenvolvimento. Saúde: `/actuator/health` se o Actuator for adicionado (opcional).

## 11. Reversão e recuperação

Projeto novo, sem produção. Reversão = voltar o commit; dados em volume MySQL com backup
manual (`mysqldump`) descrito no README. Gatilho: gasto gravado sem confirmação em qualquer
teste.

## 12. Métricas de sucesso

Produto: taxa de valor e categoria corretos em 30 frases (Wilson). Técnica: 17 ACs com PASS;
latência p50 e p95 do fluxo completo em 10 repetições. Limite: com 30 amostras o intervalo é
largo; é teste de fumaça.

## 13. Glossário e domínios

- **Gasto pendente:** gasto proposto que espera "sim" ou "não".
- **Limite:** teto mensal por categoria; só avisa.
- **Ferramenta (tool):** função Java que o modelo pode chamar.
- Domínio único: orçamento por voz, responsável: cliente.

## 14. Alternativas e decisões

Ver [ADR](adr.md): D1 (aceita), D2, D3, D4 (propostas, pendentes de revisão do cliente).

## 15. Dependências e compatibilidade

| Dependência | Versão | Observação |
|---|---|---|
| Spring AI | 2.0.1 GA | decisão do cliente (ADR D1) |
| Spring Boot | 4.1.1 (confirmado no build, 2026-11-22) | Spring AI 2.0 foi feito para Boot 4.0 e 4.1 |
| Java | 25 (confirmado no build; ASM-008) | a versão mínima não consta nos posts consultados |
| OpenAI | chat `gpt-4o-mini`; transcrição `whisper-1` ou `gpt-4o-mini-transcribe`; fala `gpt-4o-mini-tts` | modelos por propriedade |
| MySQL | 9.x via Docker compose | testes usam H2 |

Sem chave da OpenAI a aplicação sobe, mas os comandos devolvem 503 (AC-016).

## 16. Performance e capacidade

Uso de uma pessoa; sem meta de carga. Medir p50 e p95 do fluxo completo (voz → resposta em
áudio) em 10 repetições, informando rede e modelo. Não há resultado ainda.

## 17. Migração e implantação

Não aplicável: projeto novo. Esquema criado por `ddl-auto=update` no desenvolvimento, como
na base; o README avisa que isso não serve para produção.

## 18. Perguntas e suposições

Ver [spec](spec.md): ASM-001 a ASM-009 (abertas) e Q-001 a Q-003 (abertas). Q-002 depende do
experimento. Nenhuma interfere em domínio anterior, pois não há outro.

## 19. Marcos e acompanhamento

M1 esqueleto sobe com Spring AI 2.0.1 (T-001). M2 regras de gasto e limite com testes
(T-002 a T-006). M3 fluxo de voz completo (T-007 a T-009). M4 README e experimento (T-010).
Acompanhamento pela lista de tarefas e por `onp-spec status`. Sem estimativas de prazo.

## 20. Revisão e rastreabilidade

Revisores: cliente, 2026-11-22 ("Revisei, pode seguir"). Cadeia: [PRD](prd.md) → [ADR](adr.md) → este TDD →
[spec](spec.md) → [tarefas](tasks.md) → testes `@spec:AC-xxx`.

Estrutura baseada no [Technical Design Doc Creator, Tech Leads Club](https://github.com/tech-leads-club/agent-skills/blob/main/packages/skills-catalog/skills/%28creation%29/create-technical-design-doc/SKILL.md); texto e requisitos de rastreabilidade adaptados para este projeto.
