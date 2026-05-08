package com.gitproject.getorpermition.utils;

import com.gitproject.getorpermition.data.model.PermissionInfo;
import com.gitproject.getorpermition.data.model.PermissionInfo.RiskLevel;

import java.util.HashMap;
import java.util.Map;

/**
 * Maps raw Android permission strings to human-readable metadata and risk levels.
 * Unknown permissions default to LOW risk.
 */
public class PermissionClassifier {

    private static final Map<String, PermissionInfo> PERMISSION_MAP = new HashMap<>();
    private static final Map<String, PermissionInfo> EN_MAP = new HashMap<>();

    static {
        // ── HIGH RISK ────────────────────────────────────────────────────────────
        add("android.permission.READ_CONTACTS",
                "Ler Contatos",
                "Acessa toda a sua lista de contatos.",
                "Pode exportar seus contatos para servidores de spam ou vender sua lista a terceiros.",
                RiskLevel.HIGH, "CONTACTS");

        add("android.permission.WRITE_CONTACTS",
                "Editar Contatos",
                "Pode modificar ou excluir contatos.",
                "Pode apagar todos os seus contatos ou inserir contatos falsos para golpes de phishing.",
                RiskLevel.HIGH, "CONTACTS");

        add("android.permission.ACCESS_FINE_LOCATION",
                "Localização GPS Precisa",
                "Rastreia sua posição exata via GPS.",
                "Pode monitorar sua localização em tempo real e revelar sua rotina diária a criminosos.",
                RiskLevel.HIGH, "LOCATION");

        add("android.permission.ACCESS_BACKGROUND_LOCATION",
                "Localização em Segundo Plano",
                "Rastreia localização mesmo com o app fechado.",
                "Pode monitorar seus movimentos 24h por dia sem que você perceba.",
                RiskLevel.HIGH, "LOCATION");

        add("android.permission.READ_CALL_LOG",
                "Histórico de Chamadas",
                "Lê todas as suas ligações realizadas e recebidas.",
                "Pode mapear seus relacionamentos pessoais e profissionais para ataques direcionados.",
                RiskLevel.HIGH, "PHONE");

        add("android.permission.PROCESS_OUTGOING_CALLS",
                "Interceptar Chamadas",
                "Pode interceptar e redirecionar suas ligações.",
                "Pode gravar suas ligações ou desviá-las para números fraudulentos sem você saber.",
                RiskLevel.HIGH, "PHONE");

        add("android.permission.READ_SMS",
                "Ler SMS",
                "Lê todas as suas mensagens de texto, incluindo códigos 2FA.",
                "Pode interceptar códigos de autenticação bancária e realizar fraudes financeiras em seu nome.",
                RiskLevel.HIGH, "SMS");

        add("android.permission.SEND_SMS",
                "Enviar SMS",
                "Pode enviar mensagens em seu nome gerando custos.",
                "Pode inscrever você em serviços pagos por SMS ou enviar golpes aos seus contatos.",
                RiskLevel.HIGH, "SMS");

        add("android.permission.RECEIVE_SMS",
                "Receber SMS",
                "Intercepta mensagens antes que você as veja.",
                "Pode capturar tokens de verificação de dois fatores e invadir suas contas bancárias.",
                RiskLevel.HIGH, "SMS");

        add("android.permission.RECORD_AUDIO",
                "Gravar Áudio",
                "Acessa o microfone para gravar conversas.",
                "Pode gravar conversas privadas e reuniões confidenciais sem o seu consentimento.",
                RiskLevel.HIGH, "MICROPHONE");

        add("android.permission.BIND_ACCESSIBILITY_SERVICE",
                "Serviço de Acessibilidade",
                "Pode ler e controlar qualquer coisa na tela.",
                "Pode capturar senhas digitadas, realizar compras e controlar o dispositivo remotamente.",
                RiskLevel.HIGH, "ACCESSIBILITY");

        add("android.permission.PACKAGE_USAGE_STATS",
                "Estatísticas de Uso",
                "Monitora quais apps você usa e por quanto tempo.",
                "Pode identificar apps bancários instalados e direcionar ataques específicos a eles.",
                RiskLevel.HIGH, "USAGE");

        add("android.permission.READ_EXTERNAL_STORAGE",
                "Ler Armazenamento",
                "Acessa todos os arquivos no armazenamento externo.",
                "Pode acessar e vazar fotos, documentos e arquivos pessoais armazenados no celular.",
                RiskLevel.HIGH, "STORAGE");

        add("android.permission.WRITE_EXTERNAL_STORAGE",
                "Escrever no Armazenamento",
                "Pode criar, modificar e excluir arquivos.",
                "Pode criptografar seus arquivos e exigir resgate para liberá-los (ransomware).",
                RiskLevel.HIGH, "STORAGE");

        add("android.permission.WRITE_CALL_LOG",
                "Editar Histórico de Chamadas",
                "Pode modificar o registro das suas ligações.",
                "Pode falsificar seu histórico de chamadas para encobrir atividades fraudulentas.",
                RiskLevel.HIGH, "PHONE");

        add("android.permission.SYSTEM_ALERT_WINDOW",
                "Exibir sobre outros apps",
                "Pode mostrar sobreposições em cima de outros apps.",
                "Pode exibir telas falsas sobre apps bancários para roubar suas credenciais (overlay attack).",
                RiskLevel.HIGH, "SYSTEM");

        // ── MEDIUM RISK ──────────────────────────────────────────────────────────
        add("android.permission.ACCESS_COARSE_LOCATION",
                "Localização Aproximada",
                "Obtém localização via rede Wi-Fi/celular.",
                "Pode determinar o bairro onde você mora e mapear suas rotinas de deslocamento.",
                RiskLevel.MEDIUM, "LOCATION");

        add("android.permission.READ_PHONE_STATE",
                "Estado do Telefone",
                "Lê IMEI, número e estado das chamadas.",
                "Pode associar seu IMEI a um perfil de rastreamento permanente e não apagável.",
                RiskLevel.MEDIUM, "PHONE");

        add("android.permission.GET_ACCOUNTS",
                "Contas do Dispositivo",
                "Lista suas contas Google e outros serviços.",
                "Pode identificar suas contas ativas e tentar acessos não autorizados via phishing.",
                RiskLevel.MEDIUM, "ACCOUNTS");

        add("android.permission.CAMERA",
                "Câmera",
                "Acessa câmera frontal e traseira.",
                "Pode tirar fotos ou gravar vídeos silenciosamente enquanto o app está em uso.",
                RiskLevel.MEDIUM, "CAMERA");

        add("android.permission.USE_BIOMETRIC",
                "Biometria",
                "Acessa sensores biométricos (impressão digital, rosto).",
                "Pode acionar o sensor biométrico para tentativas de autenticação não autorizadas.",
                RiskLevel.MEDIUM, "BIOMETRIC");

        add("android.permission.USE_FINGERPRINT",
                "Impressão Digital",
                "Acessa o leitor de impressão digital.",
                "Pode tentar capturar dados de impressão digital para falsificar autenticações.",
                RiskLevel.MEDIUM, "BIOMETRIC");

        add("android.permission.BLUETOOTH",
                "Bluetooth",
                "Conecta a dispositivos Bluetooth próximos.",
                "Pode conectar-se silenciosamente a fones, caixas de som e outros dispositivos ao redor.",
                RiskLevel.MEDIUM, "BLUETOOTH");

        add("android.permission.BLUETOOTH_SCAN",
                "Escanear Bluetooth",
                "Descobre dispositivos Bluetooth ao redor.",
                "Pode mapear dispositivos próximos para rastrear sua localização indiretamente.",
                RiskLevel.MEDIUM, "BLUETOOTH");

        add("android.permission.READ_MEDIA_IMAGES",
                "Ler Imagens",
                "Acessa fotos e imagens na galeria.",
                "Pode acessar e vazar fotos pessoais, documentos fotografados e imagens sensíveis.",
                RiskLevel.MEDIUM, "MEDIA");

        add("android.permission.READ_MEDIA_VIDEO",
                "Ler Vídeos",
                "Acessa vídeos armazenados no dispositivo.",
                "Pode acessar e transmitir vídeos privados para servidores externos.",
                RiskLevel.MEDIUM, "MEDIA");

        add("android.permission.READ_MEDIA_AUDIO",
                "Ler Áudios",
                "Acessa arquivos de áudio e músicas.",
                "Pode acessar mensagens de voz e gravações armazenadas localmente no dispositivo.",
                RiskLevel.MEDIUM, "MEDIA");

        add("android.permission.CALL_PHONE",
                "Fazer Ligações",
                "Pode realizar ligações telefônicas sem confirmação.",
                "Pode ligar para números premium internacionais gerando cobranças elevadas em sua conta.",
                RiskLevel.MEDIUM, "PHONE");

        add("android.permission.MANAGE_EXTERNAL_STORAGE",
                "Gerenciar Armazenamento",
                "Acesso amplo a todos os arquivos do dispositivo.",
                "Pode excluir, modificar ou exfiltrar qualquer arquivo presente no dispositivo.",
                RiskLevel.MEDIUM, "STORAGE");

        // ── LOW RISK ─────────────────────────────────────────────────────────────
        add("android.permission.INTERNET",
                "Internet",
                "Conecta à internet.",
                "Pode transmitir dados coletados para servidores externos sem que você perceba.",
                RiskLevel.LOW, "NETWORK");

        add("android.permission.ACCESS_NETWORK_STATE",
                "Estado da Rede",
                "Verifica se há conexão de internet.",
                "Pode detectar quando você se conecta a uma rede para sincronizar dados coletados.",
                RiskLevel.LOW, "NETWORK");

        add("android.permission.ACCESS_WIFI_STATE",
                "Estado do Wi-Fi",
                "Verifica informações da rede Wi-Fi.",
                "Pode identificar redes Wi-Fi visitadas para mapear sua localização histórica.",
                RiskLevel.LOW, "NETWORK");

        add("android.permission.CHANGE_WIFI_STATE",
                "Alterar Wi-Fi",
                "Pode habilitar/desabilitar o Wi-Fi.",
                "Pode desconectar você de redes seguras para forçar o uso de redes inseguras.",
                RiskLevel.LOW, "NETWORK");

        add("android.permission.VIBRATE",
                "Vibração",
                "Controla a vibração do dispositivo.",
                "Risco mínimo; em isolamento não representa ameaça significativa à privacidade.",
                RiskLevel.LOW, "HARDWARE");

        add("android.permission.RECEIVE_BOOT_COMPLETED",
                "Iniciar com o Sistema",
                "Inicia automaticamente ao ligar o dispositivo.",
                "Pode ser usado para reiniciar processos maliciosos automaticamente após cada reinicialização.",
                RiskLevel.LOW, "SYSTEM");

        add("android.permission.FOREGROUND_SERVICE",
                "Serviço em Primeiro Plano",
                "Mantém um processo rodando em segundo plano.",
                "Pode manter um processo malicioso ativo e oculto mesmo quando o app está em segundo plano.",
                RiskLevel.LOW, "SYSTEM");

        add("android.permission.WAKE_LOCK",
                "Manter Dispositivo Ativo",
                "Impede o dispositivo de entrar em modo de espera.",
                "Pode impedir o dispositivo de economizar bateria enquanto executa tarefas ocultas.",
                RiskLevel.LOW, "SYSTEM");

        add("android.permission.REQUEST_INSTALL_PACKAGES",
                "Instalar Apps",
                "Solicita instalação de outros aplicativos.",
                "Pode instalar outros aplicativos maliciosos silenciosamente sem sua confirmação.",
                RiskLevel.LOW, "SYSTEM");

        add("android.permission.POST_NOTIFICATIONS",
                "Enviar Notificações",
                "Exibe notificações na barra de status.",
                "Pode exibir notificações de phishing imitando bancos ou serviços confiáveis.",
                RiskLevel.LOW, "NOTIFICATIONS");

        add("android.permission.SCHEDULE_EXACT_ALARM",
                "Alarmes Exatos",
                "Agenda alarmes precisos (para lembretes).",
                "Risco mínimo; pode ser usado para agendar tarefas em horários específicos.",
                RiskLevel.LOW, "ALARM");

        add("android.permission.FLASHLIGHT",
                "Lanterna",
                "Controla o flash/lanterna da câmera.",
                "Risco mínimo; uso isolado não representa ameaça direta à privacidade.",
                RiskLevel.LOW, "HARDWARE");
    }

    static {
        // ── HIGH RISK (EN) ───────────────────────────────────────────────────────
        addEn("android.permission.READ_CONTACTS",
                "Read Contacts",
                "Accesses your full contact list.",
                "Can export all your contacts to spam servers or sell your list to third parties.",
                RiskLevel.HIGH, "CONTACTS");

        addEn("android.permission.WRITE_CONTACTS",
                "Edit Contacts",
                "Can modify or delete contacts.",
                "Can erase all your contacts or inject fake ones for phishing attacks.",
                RiskLevel.HIGH, "CONTACTS");

        addEn("android.permission.ACCESS_FINE_LOCATION",
                "Precise GPS Location",
                "Tracks your exact position via GPS.",
                "Can monitor your real-time location and reveal your home, work, and daily routines.",
                RiskLevel.HIGH, "LOCATION");

        addEn("android.permission.ACCESS_BACKGROUND_LOCATION",
                "Background Location",
                "Tracks location even when the app is closed.",
                "Can send your movements to remote servers 24/7 without your knowledge.",
                RiskLevel.HIGH, "LOCATION");

        addEn("android.permission.READ_CALL_LOG",
                "Call History",
                "Reads all your call records.",
                "Can map your personal and professional relationships for targeted attacks.",
                RiskLevel.HIGH, "PHONE");

        addEn("android.permission.PROCESS_OUTGOING_CALLS",
                "Intercept Calls",
                "Can intercept and redirect your outgoing calls.",
                "Can record your calls or redirect them to fraudulent numbers without your knowledge.",
                RiskLevel.HIGH, "PHONE");

        addEn("android.permission.READ_SMS",
                "Read SMS",
                "Reads all your text messages, including 2FA codes.",
                "Can steal banking authentication codes and commit financial fraud in your name.",
                RiskLevel.HIGH, "SMS");

        addEn("android.permission.SEND_SMS",
                "Send SMS",
                "Can send messages on your behalf, incurring costs.",
                "Can silently subscribe you to paid SMS services or send scams to your contacts.",
                RiskLevel.HIGH, "SMS");

        addEn("android.permission.RECEIVE_SMS",
                "Receive SMS",
                "Intercepts messages before you see them.",
                "Can silently capture two-factor verification tokens to hijack your accounts.",
                RiskLevel.HIGH, "SMS");

        addEn("android.permission.RECORD_AUDIO",
                "Record Audio",
                "Accesses the microphone to record conversations.",
                "Can eavesdrop on private conversations and confidential meetings without consent.",
                RiskLevel.HIGH, "MICROPHONE");

        addEn("android.permission.BIND_ACCESSIBILITY_SERVICE",
                "Accessibility Service",
                "Can read and control anything on the screen.",
                "Can log your keystrokes, make purchases, and control the device remotely.",
                RiskLevel.HIGH, "ACCESSIBILITY");

        addEn("android.permission.PACKAGE_USAGE_STATS",
                "App Usage Stats",
                "Monitors which apps you use and for how long.",
                "Can identify installed banking apps and direct targeted attacks against them.",
                RiskLevel.HIGH, "USAGE");

        addEn("android.permission.READ_EXTERNAL_STORAGE",
                "Read Storage",
                "Accesses all files on external storage.",
                "Can access and leak photos, documents, and personal files stored on the device.",
                RiskLevel.HIGH, "STORAGE");

        addEn("android.permission.WRITE_EXTERNAL_STORAGE",
                "Write to Storage",
                "Can create, modify, and delete files.",
                "Can encrypt your files and demand a ransom to release them (ransomware).",
                RiskLevel.HIGH, "STORAGE");

        addEn("android.permission.WRITE_CALL_LOG",
                "Edit Call History",
                "Can modify your call records.",
                "Can falsify your call history to cover up fraudulent activity.",
                RiskLevel.HIGH, "PHONE");

        addEn("android.permission.SYSTEM_ALERT_WINDOW",
                "Draw Over Other Apps",
                "Can show overlays on top of other apps.",
                "Can display fake screens over banking apps to steal your credentials (overlay attack).",
                RiskLevel.HIGH, "SYSTEM");

        // ── MEDIUM RISK (EN) ─────────────────────────────────────────────────────
        addEn("android.permission.ACCESS_COARSE_LOCATION",
                "Approximate Location",
                "Gets location via Wi-Fi/cell network.",
                "Can determine your neighborhood and map your daily movement patterns.",
                RiskLevel.MEDIUM, "LOCATION");

        addEn("android.permission.READ_PHONE_STATE",
                "Phone State",
                "Reads IMEI, phone number, and call status.",
                "Can link your IMEI to a permanent, unerasable tracking profile.",
                RiskLevel.MEDIUM, "PHONE");

        addEn("android.permission.GET_ACCOUNTS",
                "Device Accounts",
                "Lists your Google and other service accounts.",
                "Can identify your active accounts and attempt unauthorized access via phishing.",
                RiskLevel.MEDIUM, "ACCOUNTS");

        addEn("android.permission.CAMERA",
                "Camera",
                "Accesses front and rear cameras.",
                "Can silently take photos or record video while you use the app.",
                RiskLevel.MEDIUM, "CAMERA");

        addEn("android.permission.USE_BIOMETRIC",
                "Biometrics",
                "Accesses biometric sensors (fingerprint, face).",
                "Can trigger biometric authentication prompts for unauthorized sensitive actions.",
                RiskLevel.MEDIUM, "BIOMETRIC");

        addEn("android.permission.USE_FINGERPRINT",
                "Fingerprint",
                "Accesses the fingerprint reader.",
                "Can attempt to capture fingerprint data to spoof authentication.",
                RiskLevel.MEDIUM, "BIOMETRIC");

        addEn("android.permission.BLUETOOTH",
                "Bluetooth",
                "Connects to nearby Bluetooth devices.",
                "Can silently connect to headphones, speakers, and devices nearby.",
                RiskLevel.MEDIUM, "BLUETOOTH");

        addEn("android.permission.BLUETOOTH_SCAN",
                "Scan Bluetooth",
                "Discovers nearby Bluetooth devices.",
                "Can map nearby devices to covertly track your location.",
                RiskLevel.MEDIUM, "BLUETOOTH");

        addEn("android.permission.READ_MEDIA_IMAGES",
                "Read Images",
                "Accesses photos and images in the gallery.",
                "Can access and leak personal photos, photographed documents, and sensitive images.",
                RiskLevel.MEDIUM, "MEDIA");

        addEn("android.permission.READ_MEDIA_VIDEO",
                "Read Videos",
                "Accesses videos stored on the device.",
                "Can access and stream private videos to external servers.",
                RiskLevel.MEDIUM, "MEDIA");

        addEn("android.permission.READ_MEDIA_AUDIO",
                "Read Audio Files",
                "Accesses audio files and music.",
                "Can access voice memos and local recordings stored on the device.",
                RiskLevel.MEDIUM, "MEDIA");

        addEn("android.permission.CALL_PHONE",
                "Make Calls",
                "Can place phone calls without confirmation.",
                "Can call international premium-rate numbers, generating high charges on your bill.",
                RiskLevel.MEDIUM, "PHONE");

        addEn("android.permission.MANAGE_EXTERNAL_STORAGE",
                "Manage Storage",
                "Broad access to all files on the device.",
                "Can delete, modify, or exfiltrate any file present on the device.",
                RiskLevel.MEDIUM, "STORAGE");

        // ── LOW RISK (EN) ────────────────────────────────────────────────────────
        addEn("android.permission.INTERNET",
                "Internet",
                "Connects to the internet.",
                "Can transmit collected data to external servers without your knowledge.",
                RiskLevel.LOW, "NETWORK");

        addEn("android.permission.ACCESS_NETWORK_STATE",
                "Network State",
                "Checks whether there is an internet connection.",
                "Can detect when you connect to a network to time data transmissions.",
                RiskLevel.LOW, "NETWORK");

        addEn("android.permission.ACCESS_WIFI_STATE",
                "Wi-Fi State",
                "Checks Wi-Fi network information.",
                "Can use visited Wi-Fi networks as a coarse location beacon.",
                RiskLevel.LOW, "NETWORK");

        addEn("android.permission.CHANGE_WIFI_STATE",
                "Change Wi-Fi",
                "Can enable/disable Wi-Fi.",
                "Can disconnect you from secure networks to force use of insecure ones.",
                RiskLevel.LOW, "NETWORK");

        addEn("android.permission.VIBRATE",
                "Vibration",
                "Controls device vibration.",
                "Minimal risk; in isolation it poses no significant threat to privacy.",
                RiskLevel.LOW, "HARDWARE");

        addEn("android.permission.RECEIVE_BOOT_COMPLETED",
                "Start on Boot",
                "Launches automatically when the device starts.",
                "Can restart malicious processes automatically after every reboot.",
                RiskLevel.LOW, "SYSTEM");

        addEn("android.permission.FOREGROUND_SERVICE",
                "Foreground Service",
                "Keeps a process running in the background.",
                "Can keep a malicious process active and hidden while the app runs in the background.",
                RiskLevel.LOW, "SYSTEM");

        addEn("android.permission.WAKE_LOCK",
                "Keep Device Awake",
                "Prevents the device from entering sleep mode.",
                "Can keep the processor active to execute hidden tasks while draining your battery.",
                RiskLevel.LOW, "SYSTEM");

        addEn("android.permission.REQUEST_INSTALL_PACKAGES",
                "Install Apps",
                "Can request installation of other applications.",
                "Can silently install additional malicious apps on your device without confirmation.",
                RiskLevel.LOW, "SYSTEM");

        addEn("android.permission.POST_NOTIFICATIONS",
                "Send Notifications",
                "Displays notifications in the status bar.",
                "Can display phishing notifications imitating banks or trusted services.",
                RiskLevel.LOW, "NOTIFICATIONS");

        addEn("android.permission.SCHEDULE_EXACT_ALARM",
                "Exact Alarms",
                "Schedules precise alarms (for reminders).",
                "Minimal risk; can be used to schedule background operations at specific times.",
                RiskLevel.LOW, "ALARM");

        addEn("android.permission.FLASHLIGHT",
                "Flashlight",
                "Controls the camera flash/flashlight.",
                "Minimal risk; isolated use poses no direct threat to privacy.",
                RiskLevel.LOW, "HARDWARE");
    }

    private static void add(String permission, String readable, String explanation,
                             String maliciousUse, RiskLevel risk, String group) {
        PERMISSION_MAP.put(permission,
                new PermissionInfo(permission, readable, explanation, maliciousUse, risk, group));
    }

    private static void addEn(String permission, String readable, String explanation,
                               String maliciousUse, RiskLevel risk, String group) {
        EN_MAP.put(permission,
                new PermissionInfo(permission, readable, explanation, maliciousUse, risk, group));
    }

    public static PermissionInfo classify(String permissionName) {
        PermissionInfo known = PERMISSION_MAP.get(permissionName);
        if (known != null) return known;

        String shortName = permissionName.contains(".")
                ? permissionName.substring(permissionName.lastIndexOf('.') + 1)
                        .replace("_", " ").toLowerCase()
                : permissionName;
        return new PermissionInfo(permissionName, shortName,
                "Permissão não catalogada.",
                "Comportamento desconhecido; trate com cautela.",
                RiskLevel.LOW, "OTHER");
    }

    public static PermissionInfo classify(String permissionName, String languageCode) {
        Map<String, PermissionInfo> map = "en".equals(languageCode) ? EN_MAP : PERMISSION_MAP;
        PermissionInfo known = map.get(permissionName);
        if (known != null) return known;

        String shortName = permissionName.contains(".")
                ? permissionName.substring(permissionName.lastIndexOf('.') + 1)
                        .replace("_", " ").toLowerCase()
                : permissionName;
        boolean isEn = "en".equals(languageCode);
        return new PermissionInfo(permissionName, shortName,
                isEn ? "Uncatalogued permission." : "Permissão não catalogada.",
                isEn ? "Unknown behavior; treat with caution." : "Comportamento desconhecido; trate com cautela.",
                RiskLevel.LOW, "OTHER");
    }
}
