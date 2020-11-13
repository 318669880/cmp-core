package com.fit2cloud.commons.server.kobe;

import com.fit2cloud.commons.utils.UUIDUtil;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

public class OsUtils {

    public static void mixWinParams(AdhocRequest adhocRequest) {
        adhocRequest.withModule("win_shell");
        Map<String, String> winVars = new HashMap<>();
        winVars.put("ansible_connection", "ssh");
        winVars.put("ansible_shell_type", "cmd");
        adhocRequest.addVars(winVars);
    }

    public static Map<String, String> osParams(Boolean b) {
        Map<String, String> winVars = null;
        if (b) {
            winVars = new HashMap<>();
            winVars.put("ansible_connection", "ssh");
            winVars.put("ansible_shell_type", "cmd");
        }
        return winVars;
    }


    public static String winPython(String pythonScriptContent) throws UnsupportedEncodingException {
        String fileName = "C:/Windows/Temp/" + "tmp-" + UUIDUtil.newUUID();
        String cmd = "echo \"" +
                pythonScriptContent.replace("\n", "`n") +
                "\" " +
                "| Out-File -Encoding utf8 " + fileName +
                "\n" +
                "python " + fileName;
        return new String(cmd.getBytes(), "utf-8");

    }

    public static void mixLinuxParams(AdhocRequest adhocRequest, Integer timeout,
                                      String runas, String username, String password, CloudServerCredentialType cloudServerCredentialType) {

        StringBuffer buffer = new StringBuffer();
        if (timeout != null) {
            buffer.append("timeout ").append(timeout).append(" ");
        }
        if (CloudServerCredentialType.PASSWORD.equals(cloudServerCredentialType) && password != null && runas != null && !runas.equals(username)) {
            buffer.append("echo ").append(password).append(" | ");
        }
        if (runas != null && !runas.equals(username)) {
            buffer.append("sudo -S -u ").append(runas).append(" ");
        }

        buffer.append(adhocRequest.getContent());
        adhocRequest.withContent(buffer.toString());

    }


}
