package com.fit2cloud.sdk.model;

/**
 * Windows 加域请求实体
 */
public class AddDomainRequest extends ExecuteScriptRequest {
	public static final String DOMAIN_PLACEHOLER = "@[DOMAIN]";
	public static final String DOMAIN_USER_PLACEHOLER = "@[DOMAIN_USER]";
	public static final String DOMAIN_PASSWORD_PLACEHOLER = "@[DOMAIN_PASSWORD]";
	private String domainName;
	private String domainUserName;
	private String domainUserPasswd;

	public String getDomainName() {
		return domainName;
	}

	public void setDomainName(String domainName) {
		this.domainName = domainName;
	}

	public String getDomainUserName() {
		return domainUserName;
	}

	public void setDomainUserName(String domainUserName) {
		this.domainUserName = domainUserName;
	}

	public String getDomainUserPasswd() {
		return domainUserPasswd;
	}

	public void setDomainUserPasswd(String domainUserPasswd) {
		this.domainUserPasswd = domainUserPasswd;
	}
}
