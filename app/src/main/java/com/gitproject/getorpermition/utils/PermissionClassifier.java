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
        // --- HIGH RISK ---
        add("android.permission.READ_CONTACTS",
                "Ler Contatos", "Acessa toda a sua lista de contatos.",
                RiskLevel.HIGH, "CONTACTS");
        add("android.permission.WRITE_CONTACTS",
                "Editar Contatos", "Pode modificar ou excluir contatos.",
                RiskLevel.HIGH, "CONTACTS");
        add("android.permission.ACCESS_FINE_LOCATION",
                "Localização GPS Precisa", "Rastreia sua posição exata via GPS.",
                RiskLevel.HIGH, "LOCATION");
        add("android.permission.ACCESS_BACKGROUND_LOCATION",
                "Localização em Segundo Plano", "Rastreia localização mesmo com o app fechado.",
                RiskLevel.HIGH, "LOCATION");
        add("android.permission.READ_CALL_LOG",
                "Histórico de Chamadas", "Lê todas as suas ligações realizadas e recebidas.",
                RiskLevel.HIGH, "PHONE");
        add("android.permission.PROCESS_OUTGOING_CALLS",
                "Interceptar Chamadas", "Pode interceptar e redirecionar suas ligações.",
                RiskLevel.HIGH, "PHONE");
        add("android.permission.READ_SMS",
                "Ler SMS", "Lê todas as suas mensagens de texto, incluindo códigos 2FA.",
                RiskLevel.HIGH, "SMS");
        add("android.permission.SEND_SMS",
                "Enviar SMS", "Pode enviar mensagens em seu nome gerando custos.",
                RiskLevel.HIGH, "SMS");
        add("android.permission.RECEIVE_SMS",
                "Receber SMS", "Intercepta mensagens antes que você as veja.",
                RiskLevel.HIGH, "SMS");
        add("android.permission.RECORD_AUDIO",
                "Gravar Áudio", "Acessa o microfone para gravar conversas.",
                RiskLevel.HIGH, "MICROPHONE");
        add("android.permission.BIND_ACCESSIBILITY_SERVICE",
                "Serviço de Acessibilidade", "Pode ler e controlar qualquer coisa na tela.",
                RiskLevel.HIGH, "ACCESSIBILITY");
        add("android.permission.PACKAGE_USAGE_STATS",
                "Estatísticas de Uso", "Monitora quais apps você usa e por quanto tempo.",
                RiskLevel.HIGH, "USAGE");
        add("android.permission.READ_EXTERNAL_STORAGE",
                "Ler Armazenamento", "Acessa todos os arquivos no armazenamento externo.",
                RiskLevel.HIGH, "STORAGE");
        add("android.permission.WRITE_EXTERNAL_STORAGE",
                "Escrever no Armazenamento", "Pode criar, modificar e excluir arquivos.",
                RiskLevel.HIGH, "STORAGE");
        add("android.permission.WRITE_CALL_LOG",
                "Editar Histórico de Chamadas", "Pode modificar o registro das suas ligações.",
                RiskLevel.HIGH, "PHONE");
        add("android.permission.SYSTEM_ALERT_WINDOW",
                "Exibir sobre outros apps", "Pode mostrar sobreposições em cima de outros apps.",
                RiskLevel.HIGH, "SYSTEM");

        // --- MEDIUM RISK ---
        add("android.permission.ACCESS_COARSE_LOCATION",
                "Localização Aproximada", "Obtém localização via rede Wi-Fi/celular.",
                RiskLevel.MEDIUM, "LOCATION");
        add("android.permission.READ_PHONE_STATE",
                "Estado do Telefone", "Lê IMEI, número e estado das chamadas.",
                RiskLevel.MEDIUM, "PHONE");
        add("android.permission.GET_ACCOUNTS",
                "Contas do Dispositivo", "Lista suas contas Google e outros serviços.",
                RiskLevel.MEDIUM, "ACCOUNTS");
        add("android.permission.CAMERA",
                "Câmera", "Acessa câmera frontal e traseira.",
                RiskLevel.MEDIUM, "CAMERA");
        add("android.permission.USE_BIOMETRIC",
                "Biometria", "Acessa sensores biométricos (impressão digital, rosto).",
                RiskLevel.MEDIUM, "BIOMETRIC");
        add("android.permission.USE_FINGERPRINT",
                "Impressão Digital", "Acessa o leitor de impressão digital.",
                RiskLevel.MEDIUM, "BIOMETRIC");
        add("android.permission.BLUETOOTH",
                "Bluetooth", "Conecta a dispositivos Bluetooth próximos.",
                RiskLevel.MEDIUM, "BLUETOOTH");
        add("android.permission.BLUETOOTH_SCAN",
                "Escanear Bluetooth", "Descobre dispositivos Bluetooth ao redor.",
                RiskLevel.MEDIUM, "BLUETOOTH");
        add("android.permission.READ_MEDIA_IMAGES",
                "Ler Imagens", "Acessa fotos e imagens na galeria.",
                RiskLevel.MEDIUM, "MEDIA");
        add("android.permission.READ_MEDIA_VIDEO",
                "Ler Vídeos", "Acessa vídeos armazenados no dispositivo.",
                RiskLevel.MEDIUM, "MEDIA");
        add("android.permission.READ_MEDIA_AUDIO",
                "Ler Áudios", "Acessa arquivos de áudio e músicas.",
                RiskLevel.MEDIUM, "MEDIA");
        add("android.permission.CALL_PHONE",
                "Fazer Ligações", "Pode realizar ligações telefônicas sem confirmação.",
                RiskLevel.MEDIUM, "PHONE");
        add("android.permission.MANAGE_EXTERNAL_STORAGE",
                "Gerenciar Armazenamento", "Acesso amplo a todos os arquivos do dispositivo.",
                RiskLevel.MEDIUM, "STORAGE");

        // --- LOW RISK ---
        add("android.permission.INTERNET",
                "Internet", "Conecta à internet.",
                RiskLevel.LOW, "NETWORK");
        add("android.permission.ACCESS_NETWORK_STATE",
                "Estado da Rede", "Verifica se há conexão de internet.",
                RiskLevel.LOW, "NETWORK");
        add("android.permission.ACCESS_WIFI_STATE",
                "Estado do Wi-Fi", "Verifica informações da rede Wi-Fi.",
                RiskLevel.LOW, "NETWORK");
        add("android.permission.CHANGE_WIFI_STATE",
                "Alterar Wi-Fi", "Pode habilitar/desabilitar o Wi-Fi.",
                RiskLevel.LOW, "NETWORK");
        add("android.permission.VIBRATE",
                "Vibração", "Controla a vibração do dispositivo.",
                RiskLevel.LOW, "HARDWARE");
        add("android.permission.RECEIVE_BOOT_COMPLETED",
                "Iniciar com o Sistema", "Inicia automaticamente ao ligar o dispositivo.",
                RiskLevel.LOW, "SYSTEM");
        add("android.permission.FOREGROUND_SERVICE",
                "Serviço em Primeiro Plano", "Mantém um processo rodando em segundo plano.",
                RiskLevel.LOW, "SYSTEM");
        add("android.permission.WAKE_LOCK",
                "Manter Dispositivo Ativo", "Impede o dispositivo de entrar em modo de espera.",
                RiskLevel.LOW, "SYSTEM");
        add("android.permission.REQUEST_INSTALL_PACKAGES",
                "Instalar Apps", "Solicita instalação de outros aplicativos.",
                RiskLevel.LOW, "SYSTEM");
        add("android.permission.POST_NOTIFICATIONS",
                "Enviar Notificações", "Exibe notificações na barra de status.",
                RiskLevel.LOW, "NOTIFICATIONS");
        add("android.permission.SCHEDULE_EXACT_ALARM",
                "Alarmes Exatos", "Agenda alarmes precisos (para lembretes).",
                RiskLevel.LOW, "ALARM");
        add("android.permission.FLASHLIGHT",
                "Lanterna", "Controla o flash/lanterna da câmera.",
                RiskLevel.LOW, "HARDWARE");
    }

    private static void add(String permission, String readable, String explanation,
                             RiskLevel risk, String group) {
        PERMISSION_MAP.put(permission, new PermissionInfo(permission, readable, explanation, risk, group));
    }

    public static PermissionInfo classify(String permissionName) {
        PermissionInfo known = PERMISSION_MAP.get(permissionName);
        if (known != null) return known;

        // Unknown permission: derive a short readable name from the last segment
        String short_name = permissionName.contains(".")
                ? permissionName.substring(permissionName.lastIndexOf('.') + 1)
                        .replace("_", " ").toLowerCase()
                : permissionName;
        return new PermissionInfo(permissionName, short_name,
                "Permissão não catalogada.", RiskLevel.LOW, "OTHER");
    }
}
