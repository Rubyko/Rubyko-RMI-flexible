package com.rubyko.rmi;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.Method;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class RmiServer extends Thread {

	private final ExecutorService executorService = Executors
			.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
	
	private final Map<String, Object> serviceNameToImpl = new ConcurrentHashMap<>();

	private final int serverPort;
	private ServerSocket serverSocket = null;
	
	public RmiServer(int serverPort) {
		super();
		this.serverPort = serverPort;
	}
	
	@Override
	public void run() {
		try {
			serverSocket = new ServerSocket(RmiServer.this.serverPort);
		} catch (Exception e) {
			throw new RmiException(e);
		}

		while (!Thread.interrupted()) {
			try {
				Socket clientSocket = serverSocket.accept();
				executorService.submit(new RpcHandler(clientSocket));
			} catch (Exception e) {
				throw new RmiException(e);
			}
		}
	}

	public void registerService(String serviceName, Object serviceImpl) {
		serviceNameToImpl.put(serviceName, serviceImpl);
	}

	public boolean isServiceRegistered(String serviceName) {
		return serviceNameToImpl.containsKey(serviceName);
	}

	public void unregisterService(String serviceName) {
		serviceNameToImpl.remove(serviceName);
	}

	private class RpcHandler implements Runnable {

		private Socket clientSocket = null;

		public RpcHandler(Socket clientSocket) {
			this.clientSocket = clientSocket;
		}

		@Override
		public void run() {
			tryReadWriteObjects(clientSocket);
		}
	}

	private void tryReadWriteObjects(Socket clientSocket) {
		try {
			RmiRequest remoteRequest = readRequestObject(clientSocket);
			RmiResponse remoteResponse = handleMethodCall(remoteRequest);
			writeResponseObject(clientSocket, remoteResponse);
		} catch (Exception e) {
			throw new RmiException(e);
		}
	}

	private void writeResponseObject(Socket clientSocket, RmiResponse remoteResponse) throws IOException {
		ObjectOutputStream oos = new ObjectOutputStream(clientSocket.getOutputStream());
		oos.writeObject(remoteResponse);
		oos.flush();
	}

	private RmiRequest readRequestObject(Socket clientSocket) throws IOException, ClassNotFoundException {
		ObjectInputStream ois = new ObjectInputStream(clientSocket.getInputStream());
		RmiRequest remoteRequest = (RmiRequest) ois.readObject();
		return remoteRequest;
	}

	private RmiResponse handleMethodCall(RmiRequest remoteRequest) {

		String serviceName = checkServiceName(remoteRequest);

		Object serviceImpl = serviceNameToImpl.get(serviceName);
		Class<?> serviceImplClass = serviceImpl.getClass();

		checkServiceInterface(remoteRequest, serviceImplClass);

		String methodName = remoteRequest.getMethodName();
		Class<?>[] argTypes = remoteRequest.getArgTypes();

		Method method = null;
		try {
			method = serviceImplClass.getMethod(methodName, argTypes);
		} catch (Exception e) {
			throw new RmiException(e);
		}

		Object[] args = remoteRequest.getArgs();
		Object returnValue = null;
		Exception exception = null;
		try {
			if (!method.isAccessible()) {
				method.setAccessible(true);
			}
			returnValue = method.invoke(serviceImpl, args);
		} catch (Exception e) {
			exception = e;
		}

		return new RmiResponse(returnValue, exception);
	}

	private void checkServiceInterface(RmiRequest remoteRequest, Class<?> serviceImplClass) {
		String interfaceCName = remoteRequest.getInterfaceCName();
		Class<?> interfaceClass = null;
		try {
			interfaceClass = Class.forName(interfaceCName);
		} catch (ClassNotFoundException e) {
			throw new RmiException(e);
		}

		Class<?>[] interfaces = serviceImplClass.getInterfaces();
		int idx = Arrays.binarySearch(interfaces, interfaceClass, new Comparator<Class<?>>() {
			@Override
			public int compare(Class<?> o1, Class<?> o2) {
				return o1.equals(o2) ? 0 : -1;
			}
		});
		if (idx < 0) {
			throw new RmiException(
					interfaceCName + " not implemeted by service with name " + remoteRequest.getServiceName());
		}
	}

	private String checkServiceName(RmiRequest remoteRequest) {
		String serviceName = remoteRequest.getServiceName();
		if (!serviceNameToImpl.containsKey(serviceName)) {
			throw new RmiException("Cannot find service with name " + serviceName);
		}
		return serviceName;
	}

}
