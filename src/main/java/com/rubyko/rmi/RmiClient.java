package com.rubyko.rmi;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.rubyko.rmi.protocol.Protocol;
import com.rubyko.rmi.protocol.tcp.TcpClientProtocol;

// http://developer.android.com/reference/java/lang/reflect/InvocationHandler.html
public class RmiClient implements InvocationHandler {

	private final String serviceName;
	private final Class<?> interfaceClass;
	private final String host;
	private final int port;

	private RmiClient(String serviceName, Class<?> interfaceClass, String host, int port) {
		this.serviceName = serviceName;
		this.interfaceClass = interfaceClass;
		this.host = host;
		this.port = port;
	}

	@Override
	public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
		final String interfaceCName = interfaceClass.getCanonicalName();
		final String methodName = method.getName();
		final Class<?>[] argTypes = method.getParameterTypes();

		final RmiRequest rpcRequest = new RmiRequest(serviceName, interfaceCName, methodName, argTypes, args);

		final Protocol clientSocket = new TcpClientProtocol(host, port);
		writeRequestObject(rpcRequest, clientSocket);
		RmiResponse rpcResponse = readResponseObject(clientSocket);

		if (!rpcResponse.isSuccessfull()) {
			throw rpcResponse.getException();
		}
		return rpcResponse.getReturnValue();
	}

	private RmiResponse readResponseObject(Protocol clientSocket) throws IOException, ClassNotFoundException {
		ObjectInputStream ois = new ObjectInputStream(clientSocket.getInputStream());
		RmiResponse rpcResponse = (RmiResponse) ois.readObject();
		clientSocket.shutdownInput();
		clientSocket.close();
		return rpcResponse;
	}

	private void writeRequestObject(RmiRequest rpcRequest, Protocol clientSocket) throws IOException {
		ObjectOutputStream oos = new ObjectOutputStream(clientSocket.getOutputStream());
		oos.writeObject(rpcRequest);
		clientSocket.shutdownOutput();
	}

	public static <T> T lookupService(String rpcAddress, Class<T> interfaceClass) {
		// "rpc://192.168.1.1:8080/service"

		Pattern pattern = Pattern.compile("^rpc://(\\d{1,3}(?:\\.\\d{1,3}){3}):(\\d{1,5})/(\\w+)$");
		Matcher matcher = pattern.matcher(rpcAddress);
		if (!matcher.matches()) {
			throw new RmiException("Cannot parse rpc address " + rpcAddress + " (Format: rpc://ip:port/serviceName)");
		}

		String host = matcher.group(1);
		int port = Integer.parseInt(matcher.group(2));
		String serviceName = matcher.group(3);

		return lookupService(host, port, serviceName, interfaceClass);
	}

	// http://developer.android.com/reference/java/lang/reflect/Proxy.html
	@SuppressWarnings("unchecked")
	public static <T> T lookupService(String host, int port, String serviceName, Class<T> interfaceClass) {
		final RmiClient remoteClient = new RmiClient(serviceName, interfaceClass, host, port);
		return (T) Proxy.newProxyInstance(interfaceClass.getClassLoader(), new Class<?>[] { interfaceClass },
				remoteClient);
	}

}
