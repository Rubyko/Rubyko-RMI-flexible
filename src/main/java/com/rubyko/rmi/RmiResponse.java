package com.rubyko.rmi;

import java.io.Serializable;

public class RmiResponse implements Serializable {

	private static final long serialVersionUID = 8369611010358173111L;

	private final Object returnValue;

	private final Throwable exception;

	public RmiResponse(Object returnValue, Throwable exception) {
		this.returnValue = returnValue;
		this.exception = exception;
	}

	public Object getReturnValue() {
		return returnValue;
	}

	public boolean isSuccessfull() {
		return exception == null;
	}

	public Throwable getException() {
		return exception;
	}

}
