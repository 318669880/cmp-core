package com.fit2cloud.sdk.model.template;

public class F2CNumber extends F2CComponent {
	private Long min;
	private Long max;
	private String defaultValue;
	public Long getMin() {
		return min;
	}
	public void setMin(Long min) {
		this.min = min;
	}
	public Long getMax() {
		return max;
	}
	public void setMax(Long max) {
		this.max = max;
	}
	public String getDefaultValue() {
		return defaultValue;
	}
	public void setDefaultValue(String defaultValue) {
		this.defaultValue = defaultValue;
	}
	@Override
	public String toString() {
		return "F2CNumber [min=" + min + ", max=" + max + ", defaultValue=" + defaultValue + ", getInputType()="
				+ getInputType() + ", getName()=" + getName() + ", isRequired()=" + isRequired() + ", getGroup()="
				+ getGroup() + ", getLabel()=" + getLabel() + ", getDescription()=" + getDescription() + ", getUnit()="
				+ getUnit() + "]";
	}
}
