package com.fit2cloud.sdk.model.template;

public class F2CComponent {
	private String inputType;
	private String name;
	private boolean required;
	private String group;
	private String label;
	private String description;
	private String unit;
	public String getInputType() {
		return inputType;
	}
	public void setInputType(String inputType) {
		this.inputType = inputType;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public boolean isRequired() {
		return required;
	}
	public void setRequired(boolean required) {
		this.required = required;
	}
	public String getGroup() {
		return group;
	}
	public void setGroup(String group) {
		this.group = group;
	}
	public String getLabel() {
		return label;
	}
	public void setLabel(String label) {
		this.label = label;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getUnit() {
		return unit;
	}
	public void setUnit(String unit) {
		this.unit = unit;
	}
	@Override
	public String toString() {
		return "F2CComponent [inputType=" + inputType + ", name=" + name + ", required=" + required + ", group=" + group
				+ ", label=" + label + ", description=" + description + ", unit=" + unit + "]";
	}
}
