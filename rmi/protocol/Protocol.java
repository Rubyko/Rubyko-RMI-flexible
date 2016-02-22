package com.rubyko.rmi.protocol;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public interface Protocol {
	
	public abstract OutputStream getOutputStream() throws IOException;
	
	public abstract void shutdownOutput() throws IOException;

	public abstract InputStream getInputStream() throws IOException;
	
	public abstract void shutdownInput() throws IOException;
	
	public abstract void close() throws IOException;

}

