package com.rubyko.rmi.protocol.tcp;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;

import com.rubyko.rmi.protocol.Protocol;

public class TcpServerProtocol implements Protocol {

	private ServerSocket serverSocket;
	
	public TcpServerProtocol(int port) throws IOException{
		this.serverSocket = new ServerSocket(port);
	}
	
	@Override
	public OutputStream getOutputStream() throws IOException {
		throw new UnsupportedOperationException();
	}

	@Override
	public void shutdownOutput() throws IOException {
		throw new UnsupportedOperationException();
	}

	@Override
	public InputStream getInputStream() throws IOException {
		throw new UnsupportedOperationException();
	}

	@Override
	public void shutdownInput() throws IOException {
		throw new UnsupportedOperationException();
	}

	@Override
	public void close() throws IOException {
		serverSocket.close();
	}

	@Override
	public Protocol accept() throws IOException {
		return new TcpClientProtocol(serverSocket.accept());
	}

}
