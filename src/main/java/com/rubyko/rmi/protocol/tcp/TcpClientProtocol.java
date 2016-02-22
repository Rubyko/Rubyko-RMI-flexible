package com.rubyko.rmi.protocol.tcp;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import com.rubyko.rmi.protocol.Protocol;

public class TcpClientProtocol implements Protocol {

	private final Socket socket;
	
	public TcpClientProtocol(String host, int port) throws UnknownHostException, IOException {
		this.socket = new Socket(host, port);
	}
	
	public TcpClientProtocol(Socket socket){
		this.socket = socket;
	}

	@Override
	public OutputStream getOutputStream() throws IOException {
		return socket.getOutputStream();
	}

	@Override
	public void shutdownOutput() throws IOException {
		socket.shutdownOutput();
	}

	@Override
	public InputStream getInputStream() throws IOException {
		return socket.getInputStream();
	}

	@Override
	public void shutdownInput() throws IOException {
		socket.shutdownInput();
	}

	@Override
	public void close() throws IOException {
		socket.close();
	}

	@Override
	public Protocol accept() {
		System.out.println("DO NOTHING");
		return null;
	}

}
