# Experimento das 30 frases (docs/experimento-30-frases.md).
# Envia cada frase a API, le o valor e a categoria da PROPOSTA de gasto e compara com o esperado.
# Nunca confirma gasto: depois de cada frase manda "nao" para limpar a pendencia.
#
# Modo texto (sem transcricao):  .\tools\rodar-experimento.ps1 -Modo texto
# Modo voz (com transcricao):    .\tools\rodar-experimento.ps1 -Modo voz -Pasta C:\caminho\audios
#   Os audios se chamam f01.m4a, f02.m4a ... f30.m4a (qualquer extensao aceita pela API).
# Este arquivo e ASCII de proposito: o PowerShell 5.1 le script sem BOM como ANSI.

param(
    [ValidateSet("texto", "voz")] [string] $Modo = "texto",
    [string] $Pasta = "",
    [string] $Base = "http://localhost:8081",
    [string] $Rotulo = "",
    [string] $Csv = (Join-Path $PSScriptRoot "..\docs\frases-experimento.csv")
)

$ErrorActionPreference = "Stop"
[Console]::OutputEncoding = [Text.Encoding]::UTF8
$inv = [Globalization.CultureInfo]::InvariantCulture

function Normalizar([string] $s) {
    $d = $s.Normalize([Text.NormalizationForm]::FormD)
    ([regex]::Replace($d, "\p{Mn}", "")).ToLowerInvariant().Trim()
}

function LerCorpo($resp) {
    $ms = New-Object IO.MemoryStream
    $resp.RawContentStream.CopyTo($ms)
    [Text.Encoding]::UTF8.GetString($ms.ToArray())
}

function EnviarTexto([string] $texto) {
    $json = (@{ texto = $texto } | ConvertTo-Json -Compress)
    $r = Invoke-WebRequest -Method Post -Uri "$Base/api/texto/comandos" -UseBasicParsing -TimeoutSec 90 `
        -ContentType "application/json; charset=utf-8" -Body ([Text.Encoding]::UTF8.GetBytes($json))
    (LerCorpo $r | ConvertFrom-Json).resposta
}

function EnviarVoz([string] $arquivo) {
    $saida = Join-Path $env:TEMP "exp-voz-corpo.json"
    $codigo = & curl.exe -s -S -m 90 -o $saida -w "%{http_code}" -X POST "$Base/api/voz/comandos" -F "audio=@$arquivo"
    if ($codigo -ne "200") { throw "HTTP $codigo" }
    $texto = [IO.File]::ReadAllText($saida, [Text.Encoding]::UTF8)
    ($texto | ConvertFrom-Json).resposta
}

function Wilson([int] $x, [int] $n) {
    if ($n -eq 0) { return @(0.0, 0.0) }
    $z = 1.96; $p = $x / $n
    $den = 1 + ($z * $z) / $n
    $centro = ($p + ($z * $z) / (2 * $n)) / $den
    $margem = $z * [Math]::Sqrt($p * (1 - $p) / $n + ($z * $z) / (4 * $n * $n)) / $den
    @([Math]::Max(0.0, $centro - $margem), [Math]::Min(1.0, $centro + $margem))
}

function Percentil([double[]] $v, [double] $q) {
    if ($v.Count -eq 0) { return 0 }
    $o = $v | Sort-Object
    $i = [Math]::Ceiling($q * $o.Count) - 1
    $o[[Math]::Max(0, [Math]::Min($o.Count - 1, $i))]
}

if ($Modo -eq "voz" -and -not (Test-Path $Pasta)) { throw "Informe -Pasta com os audios f01..f30." }
$frases = Import-Csv -Path $Csv -Delimiter ";" -Encoding UTF8
$linhas = @(); $tempos = @(); $acertosValor = 0; $acertosCat = 0; $acertosAmbos = 0; $semProposta = 0; $ausentes = @()

foreach ($f in $frases) {
    $id = [int]$f.id
    $esperadoValor = [decimal]::Parse($f.valor_esperado, $inv)
    $entendidoValor = $null; $entendidoCat = ""; $obs = ""; $resposta = ""
    $arq = $null
    if ($Modo -eq "voz") {
        # aceita f01.m4a, f010.m4a, f01.m4a.m4a...: o numero e o que vem depois do "f" e dos zeros
        $arq = Get-ChildItem -Path $Pasta -File | Where-Object { $_.Name -match '^f0*(\d+)\b' -and [int]$Matches[1] -eq $id } | Select-Object -First 1
        if (-not $arq) {
            $ausentes += $id
            $linhas += [pscustomobject]@{ Id = $id; Frase = $f.frase; Esperado = "-"; Entendido = "-"; Resultado = "sem audio"; Segundos = 0; Obs = "audio nao encontrado, fora da conta" }
            Write-Host ("[{0,2}/30] {1,-45} sem audio (fora da conta)" -f $id, $f.frase)
            continue
        }
    }
    $t0 = Get-Date
    try {
        if ($Modo -eq "texto") {
            $resposta = EnviarTexto $f.frase
        } else {
            $resposta = EnviarVoz $arq.FullName
        }
    } catch {
        $obs = "ERRO: " + $_.Exception.Message
        Start-Sleep -Seconds 5
    }
    $seg = ((Get-Date) - $t0).TotalSeconds
    if ($resposta -match 'R\$\s*([\d\.]+,\d{2}) em ([^,\.]+)') {
        $entendidoValor = [decimal]::Parse(($Matches[1] -replace '\.', '' -replace ',', '.'), $inv)
        $entendidoCat = $Matches[2]
        $tempos += $seg
    } else {
        $semProposta++
        if (-not $obs) { $obs = "sem proposta: " + $resposta }
    }
    $okValor = ($null -ne $entendidoValor) -and ($entendidoValor -eq $esperadoValor)
    $okCat = (Normalizar $entendidoCat) -eq (Normalizar $f.categoria_esperada)
    if ($okValor) { $acertosValor++ }
    if ($okCat) { $acertosCat++ }
    if ($okValor -and $okCat) { $acertosAmbos++ }
    $linhas += [pscustomobject]@{
        Id = $id; Frase = $f.frase
        Esperado = ("R$ {0} / {1}" -f $esperadoValor.ToString("0.00", $inv), $f.categoria_esperada)
        Entendido = $(if ($null -ne $entendidoValor) { "R$ {0} / {1}" -f $entendidoValor.ToString("0.00", $inv), $entendidoCat } else { "-" })
        Resultado = $(if ($okValor -and $okCat) { "acerto" } elseif ($okValor) { "so valor" } elseif ($okCat) { "so categoria" } else { "erro" })
        Segundos = [Math]::Round($seg, 1); Obs = $obs
    }
    # limpa a pendencia: o experimento nunca grava gasto
    try { [void](EnviarTexto "nao") } catch { }
    Write-Host ("[{0,2}/30] {1,-45} {2}" -f $id, $f.frase, $linhas[-1].Resultado)
}

$n = $frases.Count - $ausentes.Count
$w = Wilson $acertosAmbos $n
$p50 = Percentil $tempos 0.50; $p95 = Percentil $tempos 0.95
$nome = if ($Rotulo) { $Rotulo } else { "modo $Modo" }
$arquivoSaida = Join-Path $PSScriptRoot ("..\docs\resultado-experimento-{0}.md" -f $Modo)

$md = @()
$md += "# Resultado do experimento: $nome"
$md += ""
$md += "Data: $(Get-Date -Format 'yyyy-MM-dd HH:mm')  |  Base: $Base  |  Frases testadas: $n de $($frases.Count)"
if ($ausentes.Count -gt 0) { $md += ""; $md += "**Amostra reduzida:** sem audio para as frases $($ausentes -join ', '). Elas ficam fora da conta, e o denominador e $n." }
if ($Modo -eq "texto") { $md += ""; $md += "**Sem transcricao:** a frase entra como texto. Mede so o modelo de chat com as ferramentas, nao o Whisper." }
$md += ""
$md += "| Metrica | Resultado |"
$md += "|---|---|"
$md += "| Valor correto | $acertosValor / $n |"
$md += "| Categoria correta | $acertosCat / $n |"
$md += "| Valor e categoria corretos | $acertosAmbos / $n ({0:P0}), intervalo de Wilson 95%: {1:P0} a {2:P0} |" -f ($acertosAmbos / $n), $w[0], $w[1]
$md += "| Respostas sem proposta | $semProposta |"
$md += "| Latencia por comando (s), so propostas | p50 {0:N1}, p95 {1:N1} (n = {2}) |" -f $p50, $p95, $tempos.Count
$md += ""
$md += "| # | Frase | Esperado | Entendido | Resultado | s | Obs |"
$md += "|---|---|---|---|---|---|---|"
foreach ($l in $linhas) { $md += "| {0} | {1} | {2} | {3} | {4} | {5} | {6} |" -f $l.Id, $l.Frase, $l.Esperado, $l.Entendido, $l.Resultado, $l.Segundos, $l.Obs }
[IO.File]::WriteAllLines($arquivoSaida, $md, (New-Object Text.UTF8Encoding($true)))

Write-Host ""
Write-Host ("Valor e categoria corretos: {0}/{1} ({2:P0}), Wilson 95%: {3:P0} a {4:P0}" -f $acertosAmbos, $n, ($acertosAmbos / $n), $w[0], $w[1])
Write-Host ("Latencia p50 {0:N1} s, p95 {1:N1} s" -f $p50, $p95)
Write-Host "Relatorio: $arquivoSaida"
