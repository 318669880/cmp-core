package com.fit2cloud.sdk.model;

public class F2CKeyPair {
	private Object key;
	private Object value;
	public F2CKeyPair() {
	}
	public F2CKeyPair(Object key, Object value) {
		this.key = key;
		this.value = value;
	}
	public Object getKey() {
		return key;
	}
	public void setKey(Object key) {
		this.key = key;
	}
	public Object getValue() {
		return value;
	}
	public void setValue(Object value) {
		this.value = value;
	}
}
