# Permission Auditor

> Aplicativo Android que escaneia todos os apps instalados no dispositivo, classifica suas permissões por nível de risco e calcula um score de segurança individual e global — ajudando o usuário a identificar apps potencialmente perigosos.

![Android](https://img.shields.io/badge/Android-minSdk%2024-3DDC84?logo=android&logoColor=white)
![Java](https://img.shields.io/badge/Java-11-ED8B00?logo=openjdk&logoColor=white)
![Architecture](https://img.shields.io/badge/Architecture-MVVM-blueviolet)
![License](https://img.shields.io/badge/License-MIT-blue)

---

## Demonstração


> **📹 Vídeo do app **

<video 
  src="https://github.com/user-attachments/assets/52b7f75b-5c64-4527-bac7-0f0c669f8567" 
  controls 
  width="320">
</video>


---

## Índice

1. [Visão Geral](#visão-geral)
2. [Funcionalidades](#funcionalidades)
3. [Arquitetura](#arquitetura)
4. [Sistema de Classificação de Risco](#sistema-de-classificação-de-risco)
5. [Algoritmo de Pontuação](#algoritmo-de-pontuação)
6. [Design System](#design-system)
7. [Estrutura do Projeto](#estrutura-do-projeto)
8. [Stack Tecnológico](#stack-tecnológico)
9. [Pré-requisitos](#pré-requisitos)
10. [Como Executar](#como-executar)
11. [Testes](#testes)
12. [Catálogo de Permissões](#catálogo-de-permissões)

---

## Visão Geral

O **Permission Auditor** varre o `PackageManager` do Android para listar todos os aplicativos de usuário instalados e inspecionar cada permissão declarada no `AndroidManifest.xml` deles. Cada permissão é classificada em um de quatro níveis de risco (`EXTREME`, `HIGH`, `MEDIUM`, `LOW`), e o app calcula:

- **Score individual** (0–100) por aplicativo, onde 100 é totalmente seguro.
- **Score global do dispositivo** (0–100), ponderado pelo nível de risco de cada app.
- **Categoria EXTREME** quando um app acumula tantas permissões perigosas que seu score chega a **0**.

O objetivo é tornar acessível ao usuário leigo informações que normalmente exigem conhecimento técnico avançado: quais apps estão pedindo permissões perigosas, o que essas permissões acessam e o que um app malicioso poderia fazer com elas.

---

## Funcionalidades

### Tela de Scan
- Animação de radar personalizada (`RadarView`) com arcos ciano giratórios e anéis concêntricos.
- Progresso em tempo real mostrando o nome do app sendo analisado.
- Escaneamento assíncrono via `ExecutorService` — UI nunca trava.

### Tela de Resultados
- **Score global** do dispositivo com label contextual (Seguro / Moderado / Vulnerável).
- Lista de apps ordenados do mais perigoso para o mais seguro.
- **Multi-ViewType RecyclerView**: apps com score 0 recebem o layout especial `item_app_critical` com borda vermelha e banner de alerta.
- **Filtros por chip**: Todos / Alto Risco / Médio / Seguros — chips respondem ao estado selecionado via `ColorStateList` (ciano quando ativo).
- Apps EXTREME aparecem no filtro "Alto Risco".
- Expansão inline de cada card para prévia das permissões com explicações.
- Botão "Ver detalhes" navega para a tela de detalhe do app.
- **Dialog de re-scan** ao pressionar voltar: pergunta se deseja iniciar uma nova análise.

### Tela de Detalhes
- Ícone, nome e package name do app.
- **Score normal** (colorido por faixa: verde / amarelo / vermelho).
- **Bloco CRÍTICO** substituindo o score quando app é EXTREME: badge vermelho + "0" em 64sp + "Risco máximo detectado".
- Lista completa de permissões com **itens expansíveis**:
  - **Collapsed**: nome da permissão + badge de risco + chevron ▼.
  - **Expanded**: o que a permissão acessa + seção "EM UM APP MALICIOSO:" com 1 exemplo concreto do dano que pode ser causado.
- Botão "Abrir Configurações" abre as configurações do app diretamente via `Intent`.

---

## Arquitetura

O projeto segue o padrão **MVVM** com separação clara de responsabilidades:

```
UI Layer
├── ScanFragment         → observa ScanViewModel (Activity-scoped)
├── ResultFragment       → observa ScanViewModel + ResultViewModel
└── DetailFragment       → recebe AppInfo via Bundle (Serializable)

ViewModel Layer
├── ScanViewModel        → AndroidViewModel, gerencia estado de scan e dados
└── ResultViewModel      → ViewModel, filtro MediatorLiveData sobre a lista de apps

Data Layer
├── PermissionRepository → ExecutorService + Handler, lê PackageManager
├── PermissionClassifier → mapa estático de 41 permissões catalogadas
└── RiskCalculator       → fórmulas de score individual e global

Model
├── AppInfo              → Serializable, inclui scoreSet flag para categoria EXTREME
└── PermissionInfo       → Serializable, enum RiskLevel {EXTREME, HIGH, MEDIUM, LOW}
```

### Fluxo de Dados

```
[Button] → ScanViewModel.startScan()
         → PermissionRepository.scanInstalledApps()   [background thread]
              → PackageManager.getInstalledPackages()
              → PermissionClassifier.classify()         por permissão
              → RiskCalculator.calculateAppScore()      por app
              → RiskCalculator.calculateGlobalScore()   device-wide
         → postValue() → LiveData → ResultFragment → AppAdapter
```

### Navegação

Usa **Navigation Component** (sem SafeArgs — serialização manual via `Bundle.putSerializable`).

```
scanFragment ──action_scan_to_result──► resultFragment ──action_result_to_detail──► detailFragment
```

---

## Sistema de Classificação de Risco

### Níveis de Risco de Permissão

| Nível | Cor | Critério |
|-------|-----|----------|
| `EXTREME` | 🔴 Vermelho vivo | App-level: score computado = 0 |
| `HIGH` | 🌹 Rosa/Rose | Acesso a dados altamente sensíveis (SMS, localização GPS, microfone, contatos, acessibilidade...) |
| `MEDIUM` | 🟡 Âmbar | Acesso moderado (câmera, Bluetooth, localização aproximada, contas...) |
| `LOW` | 🟢 Esmeralda | Acesso de baixo impacto (internet, vibração, alarmes, lanterna...) |

### Categoria EXTREME vs. Risco HIGH

- **HIGH** é determinado pelas permissões individuais (`getDominantRisk()`).
- **EXTREME** é determinado pelo score do app (`getAppCategory()`): somente quando `setRiskScore(0)` foi chamado explicitamente, usando o flag `scoreSet` para não confundir objetos recém-criados (cujo score padrão também é 0) com apps genuinamente zerados.

---

## Algoritmo de Pontuação

### Score Individual (0–100)

```
score = 100
       − min(qtd_HIGH   × 20,  60)   ← penalidade por HIGH   (cap 60)
       − min(qtd_MEDIUM ×  8,  24)   ← penalidade por MEDIUM (cap 24)
       −     qtd_LOW    ×  1         ← penalidade por LOW (sem cap)

score = max(0, score)                 ← floor em 0
```

**Exemplos:**

| Permissões | Cálculo | Score |
|-----------|---------|-------|
| Nenhuma | 100 | **100** |
| 1 HIGH | 100 − 20 | **80** |
| 3 HIGH | 100 − 60 (cap) | **40** |
| 1 MEDIUM | 100 − 8 | **92** |
| 3 HIGH + 3 MEDIUM | 100 − 60 − 24 | **16** |
| 100 LOW | 100 − 100 = 0 | **0 → EXTREME** |

### Score Global do Dispositivo

Média ponderada onde apps mais perigosos têm maior peso:

```
           Σ (score_app × peso_app)
global = ─────────────────────────────
                Σ peso_app

peso = 4  se categoria = EXTREME
peso = 3  se categoria = HIGH
peso = 1  caso contrário
```

Divisão inteira (truncamento, sem arredondamento).

---

## Design System

O projeto usa um sistema de design próprio (Claude Design System) aplicado via tokens semânticos:

### Paleta de Cores

| Token | Valor | Uso |
|-------|-------|-----|
| `bg_0` | `#08090C` | Canvas / scrim |
| `bg_1` | `#0F1218` | Superfície base |
| `bg_2` | `#181C25` | Cards |
| `bg_3` | `#232834` | Raised / hover |
| `bg_4` | `#2D3343` | Inputs / chips |
| `brand_500` | `#22D3EE` | Ciano principal |
| `risk_high` | `#F43F5E` | Alto risco |
| `risk_medium` | `#F59E0B` | Médio risco |
| `risk_low` | `#34D399` | Baixo risco |
| `risk_critical` | `#EF4444` | Score zero |

### Escala de Espaçamento (4pt)

`s_1=4dp` · `s_2=8dp` · `s_3=12dp` · `s_4=16dp` · `s_5=20dp` · `s_6=24dp` · `s_7=32dp` → `s_10=64dp`

### Tipografia

| Style | Tamanho | Uso |
|-------|---------|-----|
| `TextAppearance.PA.Display` | 34sp bold | Score numérico |
| `TextAppearance.PA.H1` | 24sp bold | Títulos de tela |
| `TextAppearance.PA.H2` | 20sp bold | Nome do app |
| `TextAppearance.PA.H3` | 17sp | Subtítulos |
| `TextAppearance.PA.Body` | 15sp | Corpo de texto |
| `TextAppearance.PA.Small` | 13sp | Labels secundários |
| `TextAppearance.PA.Caption` | 11sp bold caps | Badges / cabeçalhos |
| `TextAppearance.PA.Mono` | 12sp | Package name |

### Componentes Estilizados

- `Widget.PA.Button.Primary` — botão preenchido ciano
- `Widget.PA.Button.Secondary` — botão com borda sutil
- `Widget.PA.Button.Ghost` — botão texto
- `Widget.PA.Card` — card escuro com borda sutil e cantos arredondados
- `Widget.PA.Chip` — chip com `ColorStateList`: ciano quando selecionado, `bg_3` no estado normal

---

## Estrutura do Projeto

```
app/src/
├── main/
│   ├── java/com/gitproject/getorpermition/
│   │   ├── data/
│   │   │   ├── model/
│   │   │   │   ├── AppInfo.java             # Modelo principal (Serializable, scoreSet flag)
│   │   │   │   └── PermissionInfo.java      # Modelo de permissão (enum RiskLevel, maliciousUse)
│   │   │   └── repository/
│   │   │       └── PermissionRepository.java # PackageManager + ExecutorService
│   │   ├── ui/
│   │   │   ├── MainActivity.java            # Host do NavHostFragment
│   │   │   ├── scan/
│   │   │   │   ├── ScanFragment.java        # Tela inicial
│   │   │   │   ├── ScanViewModel.java       # Estado IDLE/SCANNING/DONE/ERROR + reset()
│   │   │   │   └── RadarView.java           # View customizada animada
│   │   │   ├── result/
│   │   │   │   ├── ResultFragment.java      # Lista + filtros + dialog de re-scan
│   │   │   │   ├── ResultViewModel.java     # MediatorLiveData com filtro ativo
│   │   │   │   └── AppAdapter.java          # Multi-ViewType: TYPE_NORMAL / TYPE_CRITICAL
│   │   │   └── detail/
│   │   │       ├── DetailFragment.java      # Score / bloco CRÍTICO + lista de permissões
│   │   │       └── PermissionAdapter.java   # Itens expansíveis com maliciousUse
│   │   └── utils/
│   │       ├── PermissionClassifier.java    # 41 permissões catalogadas
│   │       └── RiskCalculator.java          # Fórmulas de score
│   └── res/
│       ├── color/
│       │   ├── chip_background_color.xml    # Selector: ciano (checked) / bg_3 (default)
│       │   └── chip_text_color.xml          # Selector: inverso (checked) / secondary (default)
│       ├── drawable/
│       │   ├── bg_risk_badge_high/medium/low/critical.xml
│       │   ├── bg_critical_banner.xml       # Banner do item crítico na lista
│       │   └── bg_critical_details_banner.xml # Banner do detalhe
│       ├── layout/
│       │   ├── fragment_scan.xml
│       │   ├── fragment_result.xml
│       │   ├── fragment_detail.xml
│       │   ├── item_app.xml                 # Card normal
│       │   ├── item_app_critical.xml        # Card EXTREME (score = 0)
│       │   └── item_permission.xml          # Item expansível de permissão
│       └── values/
│           ├── colors.xml                   # Tokens de cor do design system
│           ├── dimens.xml                   # Escala 4pt + radii + tipografia
│           ├── strings.xml                  # Todas as strings do app
│           └── themes.xml                   # Tema PA + estilos de componentes
└── test/
    └── java/com/gitproject/getorpermition/
        ├── data/model/
        │   ├── AppInfoTest.java             # 22+ testes: contagens, dominantRisk, getAppCategory
        │   ├── PermissionInfoTest.java      # 11 testes: enum, construtores, maliciousUse
        │   └── AppInfoSerializationTest.java # 8 testes: round-trip Serializable
        ├── utils/
        │   ├── RiskCalculatorTest.java      # 35+ testes: score individual e global, EXTREME peso ×4
        │   └── PermissionClassifierTest.java # 46 testes: todas as 41 permissões
        ├── ui/
        │   ├── result/ResultViewModelTest.java # 27 testes: filtros ALL/HIGH/MEDIUM/LOW + EXTREME
        │   └── scan/ScanStateTest.java         # 7 testes: enum ScanState
        └── ScoringIntegrationTest.java         # 12 testes: pipeline completo end-to-end
```

---

## Stack Tecnológico

| Tecnologia | Versão | Uso |
|-----------|--------|-----|
| Java | 11 | Linguagem principal |
| Android SDK | minSdk 24 / targetSdk 36 | Suporte Android 7.0+ |
| AndroidX AppCompat | — | Compatibilidade retroativa |
| Material Components | — | Chips, Buttons, Cards, Dialogs |
| ConstraintLayout | 2.1.4 | Layouts de tela |
| Navigation Component | 2.7.6 | Navegação entre fragments |
| Lifecycle ViewModel + LiveData | 2.7.0 | MVVM reativo |
| RecyclerView | 1.3.2 | Listas de apps e permissões |
| CardView | 1.0.0 | Cards de apps |
| Arch Core Testing | 2.2.0 | `InstantTaskExecutorRule` para LiveData em testes |
| JUnit 4 | — | Testes unitários |

---

## Pré-requisitos

- **Android Studio** Hedgehog (2023.1.1) ou superior
- **JDK 11** (configurado no Android Studio ou via `JAVA_HOME`)
- **Android SDK** com API Level 24+ instalado
- Dispositivo físico ou emulador com **Android 7.0 (Nougat)** ou superior

> **Nota:** A permissão `QUERY_ALL_PACKAGES` usada para listar todos os apps instalados é aceita em builds de debug sem revisão do Google Play. Para distribuição na Play Store, o app conta também com o bloco `<queries>` como fallback.

---

## Como Executar

### 1. Clone o repositório

```bash
git clone https://github.com/renan1820/GetorPermition.git
cd GetorPermition
```

### 2. Abra no Android Studio

```
File → Open → selecione a pasta raiz do projeto
```

Aguarde a sincronização do Gradle.

### 3. Execute o app

- Conecte um dispositivo físico via USB com **Depuração USB ativada**, ou
- Inicie um emulador pelo **Device Manager**

Clique em **Run ▶** ou use o atalho `Shift + F10`.

> Em dispositivos físicos, o scan lista todos os apps de usuário instalados — o emulador pode ter poucos apps para demonstração. Para melhor visualização, use um dispositivo real.

### 4. Build Release (APK assinado)

```
Build → Generate Signed Bundle / APK → APK → configure keystore → Release
```

---

## Testes

### Executar todos os testes unitários

```bash
./gradlew test
```

### Executar um arquivo específico

```bash
./gradlew testDebugUnitTest --tests "com.gitproject.getorpermition.utils.RiskCalculatorTest"
```

### Cobertura de testes

| Arquivo de Teste | Testes | O que valida |
|-----------------|--------|-------------|
| `AppInfoTest` | 22+ | Contagens de risco, `getDominantRisk`, `getAppCategory`, flag `scoreSet` |
| `PermissionInfoTest` | 11 | Enum `RiskLevel` (4 valores, ordenação ordinal), construtores, `maliciousUse` |
| `AppInfoSerializationTest` | 8 | Round-trip `Serializable` (todos os campos, icon transient) |
| `RiskCalculatorTest` | 35+ | Score individual (caps, floor), score global (pesos EXTREME×4, HIGH×3), truncamento inteiro |
| `PermissionClassifierTest` | 46 | Todas as 41 permissões catalogadas + fallback para permissões desconhecidas |
| `ResultViewModelTest` | 27 | Filtros ALL/HIGH/MEDIUM/LOW, apps EXTREME no filtro HIGH, `MediatorLiveData` |
| `ScanStateTest` | 7 | Enum `ScanState`: 4 valores, ordinal IDLE=0, distinção |
| `ScoringIntegrationTest` | 12 | Pipeline completo: `PermissionClassifier → AppInfo → RiskCalculator` |

**Total: ~170 testes unitários**

---

## Catálogo de Permissões

### Alto Risco (HIGH) — Penalidade: −20 por permissão (cap −60)

| Permissão | Nome Legível | Risco em App Malicioso |
|-----------|-------------|----------------------|
| `READ_CONTACTS` | Ler Contatos | Exportar contatos para spam ou venda a terceiros |
| `WRITE_CONTACTS` | Editar Contatos | Apagar contatos ou inserir contatos falsos para phishing |
| `ACCESS_FINE_LOCATION` | Localização GPS Precisa | Monitorar localização em tempo real e revelar rotina |
| `ACCESS_BACKGROUND_LOCATION` | Localização em Segundo Plano | Rastrear movimentos 24h sem que o usuário perceba |
| `READ_CALL_LOG` | Histórico de Chamadas | Mapear relacionamentos pessoais para ataques direcionados |
| `PROCESS_OUTGOING_CALLS` | Interceptar Chamadas | Gravar ou desviar ligações para números fraudulentos |
| `READ_SMS` | Ler SMS | Interceptar códigos 2FA e realizar fraudes bancárias |
| `SEND_SMS` | Enviar SMS | Inscrever usuário em serviços premium ou enviar golpes |
| `RECEIVE_SMS` | Receber SMS | Capturar tokens de autenticação e invadir contas |
| `RECORD_AUDIO` | Gravar Áudio | Gravar conversas privadas sem consentimento |
| `BIND_ACCESSIBILITY_SERVICE` | Serviço de Acessibilidade | Capturar senhas, realizar compras e controlar o dispositivo remotamente |
| `PACKAGE_USAGE_STATS` | Estatísticas de Uso | Identificar apps bancários e direcionar ataques específicos |
| `READ_EXTERNAL_STORAGE` | Ler Armazenamento | Vazar fotos, documentos e arquivos pessoais |
| `WRITE_EXTERNAL_STORAGE` | Escrever no Armazenamento | Criptografar arquivos e exigir resgate (ransomware) |
| `WRITE_CALL_LOG` | Editar Histórico de Chamadas | Falsificar histórico de chamadas para fins fraudulentos |
| `SYSTEM_ALERT_WINDOW` | Exibir sobre outros apps | Overlay attack: telas falsas sobre apps bancários para roubar credenciais |

### Médio Risco (MEDIUM) — Penalidade: −8 por permissão (cap −24)

| Permissão | Nome Legível | Risco em App Malicioso |
|-----------|-------------|----------------------|
| `ACCESS_COARSE_LOCATION` | Localização Aproximada | Determinar bairro e rotinas de deslocamento |
| `READ_PHONE_STATE` | Estado do Telefone | Associar IMEI a perfil de rastreamento permanente |
| `GET_ACCOUNTS` | Contas do Dispositivo | Identificar contas Google para tentativas de acesso não autorizado |
| `CAMERA` | Câmera | Tirar fotos ou gravar vídeos silenciosamente |
| `USE_BIOMETRIC` | Biometria | Acionar sensor biométrico para autenticações não autorizadas |
| `USE_FINGERPRINT` | Impressão Digital | Tentar capturar dados de impressão digital |
| `BLUETOOTH` | Bluetooth | Conectar-se silenciosamente a dispositivos próximos |
| `BLUETOOTH_SCAN` | Escanear Bluetooth | Mapear dispositivos ao redor para rastreamento indireto |
| `READ_MEDIA_IMAGES` | Ler Imagens | Vazar fotos pessoais e documentos fotografados |
| `READ_MEDIA_VIDEO` | Ler Vídeos | Acessar e transmitir vídeos privados |
| `READ_MEDIA_AUDIO` | Ler Áudios | Acessar mensagens de voz e gravações locais |
| `CALL_PHONE` | Fazer Ligações | Ligar para números premium gerando cobranças |
| `MANAGE_EXTERNAL_STORAGE` | Gerenciar Armazenamento | Excluir, modificar ou exfiltrar qualquer arquivo |

### Baixo Risco (LOW) — Penalidade: −1 por permissão (sem cap)

`INTERNET` · `ACCESS_NETWORK_STATE` · `ACCESS_WIFI_STATE` · `CHANGE_WIFI_STATE` · `VIBRATE` · `RECEIVE_BOOT_COMPLETED` · `FOREGROUND_SERVICE` · `WAKE_LOCK` · `REQUEST_INSTALL_PACKAGES` · `POST_NOTIFICATIONS` · `SCHEDULE_EXACT_ALARM` · `FLASHLIGHT`

> Permissões não catalogadas são classificadas automaticamente como **LOW** com texto genérico.

---

## Autor

Desenvolvido por **Renan** — [github.com/renan1820](https://github.com/renan1820)

---

*Projeto desenvolvido com assistência de [Claude Code](https://claude.ai/code) (Anthropic).*
