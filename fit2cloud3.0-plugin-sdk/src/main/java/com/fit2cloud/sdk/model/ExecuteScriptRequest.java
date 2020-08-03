package com.fit2cloud.sdk.model;
/**
 * 执行脚本请求实体
 */
public class ExecuteScriptRequest extends BaseLoginRequest {
	public static final String OLD_PASSWORD_PLACEHOLER = "@[OLD_PASSWORD]";
	public static final String NEW_PASSWORD_PLACEHOLER = "@[NEW_PASSWORD]";
	public static final String LOGIN_USER_PLACEHOLER = "@[LOGIN_USER]";
	private String programPath;
	private String script;
	private String vmOs;
	private String vmOsVersion;
	private int timeoutSeconds = 300;
	public String getProgramPath() {
		return programPath;
	}
	public void setProgramPath(String programPath) {
		this.programPath = programPath;
	}
	public String getScript() {
		return script;
	}
	public void setScript(String script) {
		this.script = script;
	}
	public String getVmOs() {
		return vmOs;
	}
	public void setVmOs(String vmOs) {
		this.vmOs = vmOs;
	}
	public String getVmOsVersion() {
		return vmOsVersion;
	}
	public void setVmOsVersion(String vmOsVersion) {
		this.vmOsVersion = vmOsVersion;
	}

	public int getTimeoutSeconds() {
		return timeoutSeconds;
	}

	public void setTimeoutSeconds(int timeoutSeconds) {
		this.timeoutSeconds = timeoutSeconds;
	}
}
