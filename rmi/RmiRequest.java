package com.rubyko.rmi;

import java.io.Serializable;

public final class RmiRequest implements Serializable {

	private static final long serialVersionUID = -2152825459256424788L;

	private final String serviceName;
	private final String interfaceCName;
	private final String methodName;
	private final Class<?>[] argTypes ;
	private final Object[] args ;

	public RmiRequest(String serviceName, String interfaceCName, String methodName, Class<?>[] argTypes,
			Object[] args) {
		super();
		this.serviceName = serviceName;
		this.interfaceCName = interfaceCName;
		this.methodName = methodName;
		this.argTypes = argTypes;
		this.args = args;
	}

	public String getServiceName() {
		return serviceName;
	}

	public String getInterfaceCName() {
		return interfaceCName;
	}

	public String getMethodName() {
		return methodName;
	}

	public Class<?>[] getArgTypes() {
		return argTypes;
	}

	public Object[] getArgs() {
		return args;
	}

}
