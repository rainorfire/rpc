package com.soa.ssf.core;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.Arrays;

public class SsfRpcRequest implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String requestId;
	
	private Class<?> interfaceClazz;
	
	private Method invokeMethod;
	
	private Class<?>[] argType;
	
	private String[] argName;
	
	private Object[] argValue;

	public String getRequestId() {
		return requestId;
	}

	public void setRequestId(String requestId) {
		this.requestId = requestId;
	}

	public Class<?> getInterfaceClazz() {
		return interfaceClazz;
	}

	public void setInterfaceClazz(Class<?> interfaceClazz) {
		this.interfaceClazz = interfaceClazz;
	}

	public Method getInvokeMethod() {
		return invokeMethod;
	}

	public void setInvokeMethod(Method invokeMethod) {
		this.invokeMethod = invokeMethod;
	}

	public Class<?>[] getArgType() {
		return argType;
	}

	public void setArgType(Class<?>[] argType) {
		this.argType = argType;
	}

	public String[] getArgName() {
		return argName;
	}

	public void setArgName(String[] argName) {
		this.argName = argName;
	}

	public Object[] getArgValue() {
		return argValue;
	}

	public void setArgValue(Object[] argValue) {
		this.argValue = argValue;
	}

	@Override
	public String toString() {
		return "SsfRpcRequest [requestId=" + requestId + ", interfaceClazz=" + interfaceClazz + ", invokeMethod="
				+ invokeMethod + ", argType=" + Arrays.toString(argType) + ", argName=" + Arrays.toString(argName)
				+ ", argValue=" + Arrays.toString(argValue) + "]";
	}

}
