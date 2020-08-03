package com.fit2cloud.sdk.model.template;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class F2CTemplate {
	@SuppressWarnings("rawtypes")
	private List data;
	
	@SuppressWarnings("rawtypes")
	private Map params = new HashMap();
	
	@SuppressWarnings("rawtypes")
	private Map price = new HashMap();

	@SuppressWarnings("rawtypes")
	public List getData() {
		return data;
	}

	@SuppressWarnings("rawtypes")
	public void setData(List data) {
		this.data = data;
	}
	
	@SuppressWarnings("rawtypes")
	public Map getParams() {
		return params;
	}

	@SuppressWarnings("rawtypes")
	public void setParams(Map params) {
		this.params = params;
	}

	@SuppressWarnings("rawtypes")
	public Map getPrice() {
		return price;
	}

	@SuppressWarnings("rawtypes")
	public void setPrice(Map price) {
		this.price = price;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void addComponent(F2CComponent component) {
		if(component == null) {
			return;
		}
		if(data == null) {
			data = new ArrayList();
		}
		data.add(component);
	}

	@Override
	public String toString() {
		return "F2CTemplate [data=" + data + ", params=" + params + ", price=" + price + "]";
	}
}
