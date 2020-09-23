package com.fit2cloud.sdk.model.template;

import java.util.List;

public class F2CCombobox extends F2CComponent {
	private String valueField;
	private String textField;
	private boolean parent;
	private String method;
	private List<String> related;
	private boolean editable;
	private String formatterValueField;
	private String formatterTextField;
	public String getValueField() {
		return valueField;
	}
	public void setValueField(String valueField) {
		this.valueField = valueField;
	}
	public String getTextField() {
		return textField;
	}
	public void setTextField(String textField) {
		this.textField = textField;
	}
	public boolean isParent() {
		return parent;
	}
	public void setParent(boolean parent) {
		this.parent = parent;
	}
	public String getMethod() {
		return method;
	}
	public void setMethod(String method) {
		this.method = method;
	}
	public List<String> getRelated() {
		return related;
	}
	public void setRelated(List<String> related) {
		this.related = related;
	}
	public boolean isEditable() {
		return editable;
	}
	public void setEditable(boolean editable) {
		this.editable = editable;
	}
	public String getFormatterValueField() {
		return formatterValueField;
	}
	public void setFormatterValueField(String formatterValueField) {
		this.formatterValueField = formatterValueField;
	}
	public String getFormatterTextField() {
		return formatterTextField;
	}
	public void setFormatterTextField(String formatterTextField) {
		this.formatterTextField = formatterTextField;
	}
	@Override
	public String toString() {
		return "F2CCombobox [valueField=" + valueField + ", textField=" + textField + ", parent=" + parent + ", method="
				+ method + ", related=" + related + ", editable=" + editable + ", formatterValueField="
				+ formatterValueField + ", formatterTextField=" + formatterTextField + "]";
	}
}
