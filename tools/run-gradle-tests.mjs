#!/usr/bin/env node
// Ponte entre o Gradle/JUnit e o onp-spec (reporter "jest-json").
// 1) roda `gradlew cleanTest test`; 2) lê build/test-results/test/*.xml;
// 3) grava .spec/verification/raw.json no formato testResults[].assertionResults[].
// O título de cada teste (@DisplayName) deve conter a tag @spec:AC-xxx.
import { spawnSync } from 'node:child_process';
import { readdirSync, readFileSync, writeFileSync, mkdirSync, existsSync } from 'node:fs';
import { join, dirname } from 'node:path';
import { fileURLToPath } from 'node:url';

const root = join(dirname(fileURLToPath(import.meta.url)), '..');
const resultsDir = join(root, 'build', 'test-results', 'test');
const outFile = join(root, '.spec', 'verification', 'raw.json');

const decode = (s) =>
  s
    .replace(/&#x([0-9a-fA-F]+);/g, (_, h) => String.fromCodePoint(parseInt(h, 16)))
    .replace(/&#(\d+);/g, (_, d) => String.fromCodePoint(Number(d)))
    .replace(/&lt;/g, '<')
    .replace(/&gt;/g, '>')
    .replace(/&quot;/g, '"')
    .replace(/&apos;/g, "'")
    .replace(/&amp;/g, '&');

export function parseJUnitXml(xml) {
  const tests = [];
  const re = /<testcase\b([^>]*?)(?:\/>|>([\s\S]*?)<\/testcase>)/g;
  for (const m of xml.matchAll(re)) {
    const name = decode((m[1].match(/\bname="([^"]*)"/) || [])[1] || '');
    const body = m[2] || '';
    let status = 'passed';
    if (/<(failure|error)\b/.test(body)) status = 'failed';
    else if (/<skipped\b/.test(body)) status = 'skipped';
    tests.push({ fullName: name, title: '', status });
  }
  return tests;
}

function main() {
  const isWin = process.platform === 'win32';
  // caminho absoluto: no Windows o cmd não acha o .bat só pelo cwd em alguns ambientes
  const gradlew = join(root, isWin ? 'gradlew.bat' : 'gradlew');
  const proc = spawnSync(isWin ? `"${gradlew}"` : gradlew, ['cleanTest', 'test', '--console=plain'], {
    cwd: root,
    shell: isWin,
    stdio: ['ignore', 'inherit', 'inherit'],
  });

  const assertionResults = [];
  if (existsSync(resultsDir)) {
    for (const f of readdirSync(resultsDir).filter((n) => n.endsWith('.xml'))) {
      assertionResults.push(...parseJUnitXml(readFileSync(join(resultsDir, f), 'utf-8')));
    }
  }
  mkdirSync(dirname(outFile), { recursive: true });
  writeFileSync(outFile, JSON.stringify({ testResults: [{ assertionResults }] }, null, 2));
  process.exit(proc.status ?? 1);
}

if (process.argv[1] && fileURLToPath(import.meta.url) === process.argv[1]) main();
