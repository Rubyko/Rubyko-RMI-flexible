package com.rubyko.rmi;

public class RmiCheckedException extends Exception {

	private static final long serialVersionUID = -4803006731395557894L;

	public RmiCheckedException() {
		super();
	}

	public RmiCheckedException(String message, Throwable cause) {
		super(message, cause);
	}

	public RmiCheckedException(String message) {
		super(message);
	}

	public RmiCheckedException(Throwable cause) {
		super(cause);
	}

}
