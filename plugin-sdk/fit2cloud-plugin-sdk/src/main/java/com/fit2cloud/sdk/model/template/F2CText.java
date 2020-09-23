package com.fit2cloud.sdk.model.template;

public class F2CText extends F2CComponent {
	private String defaultValue;
	private String validatePattern;
	private int maxLength;
	private int minLength;

	public String getDefaultValue() {
		return defaultValue;
	}

	public void setDefaultValue(String defaultValue) {
		this.defaultValue = defaultValue;
	}

	public String getValidatePattern() {
		return validatePattern;
	}

	public void setValidatePattern(String validatePattern) {
		this.validatePattern = validatePattern;
	}

	public int getMaxLength() {
		return maxLength;
	}

	public void setMaxLength(int maxLength) {
		this.maxLength = maxLength;
	}

	public int getMinLength() {
		return minLength;
	}

	public void setMinLength(int minLength) {
		this.minLength = minLength;
	}

	@Override
	public String toString() {
		return "F2CText [defaultValue=" + defaultValue + ", validatePattern=" + validatePattern + ", maxLength="
				+ maxLength + ", minLength=" + minLength + ", getInputType()=" + getInputType() + ", getName()="
				+ getName() + ", isRequired()=" + isRequired() + ", getGroup()=" + getGroup() + ", getLabel()="
				+ getLabel() + ", getDescription()=" + getDescription() + ", getUnit()=" + getUnit() + "]";
	}
}
