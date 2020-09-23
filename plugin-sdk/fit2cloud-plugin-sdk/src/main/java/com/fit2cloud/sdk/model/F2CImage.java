package com.fit2cloud.sdk.model;
/**
 * 镜像实体
 */
public class F2CImage extends F2CResource {
	private String id;
	private String name;
	private String description;
	private String os;
	private String region;
	private Long created;
	
	public F2CImage() {
	}
	public F2CImage(String id, String name, String description, String os, String region,
			Long created) {
		this.id = id;
		this.name = name;
		this.description = description;
		this.os = os;
		this.region = region;
		this.created = created;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getOs() {
		return os;
	}
	public void setOs(String os) {
		this.os = os;
	}
	public Long getCreated() {
		return created;
	}
	public void setCreated(Long created) {
		this.created = created;
	}
	public String getRegion() {
		return region;
	}
	public void setRegion(String region) {
		this.region = region;
	}
}
