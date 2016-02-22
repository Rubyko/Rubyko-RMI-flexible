package com.rubyko.rmi.protocol.tcp;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import com.rubyko.rmi.protocol.Protocol;

public class TcpServerProtocol implements Protocol {

	
	public TcpServerProtocol(){
		
	}
	
	@Override
	public OutputStream getOutputStream() throws IOException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void shutdownOutput() throws IOException {
		// TODO Auto-generated method stub

	}

	@Override
	public InputStream getInputStream() throws IOException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void shutdownInput() throws IOException {
		// TODO Auto-generated method stub

	}

	@Override
	public void close() throws IOException {
		// TODO Auto-generated method stub

	}

}
