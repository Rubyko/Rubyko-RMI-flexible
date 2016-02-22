package com.rubyko.rmi;

public class RmiException extends RuntimeException {
	
	private static final long serialVersionUID = -9024341845349105984L;

	public RmiException(Exception e) {
		super(e);
	}

	public RmiException(String string) {
		super(string);
	}
	
}
