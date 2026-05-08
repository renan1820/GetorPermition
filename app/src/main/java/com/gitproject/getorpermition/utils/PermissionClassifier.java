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

    private static void add(String permission, String readable, String explanation,
                             String maliciousUse, RiskLevel risk, String group) {
        PERMISSION_MAP.put(permission,
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
}
