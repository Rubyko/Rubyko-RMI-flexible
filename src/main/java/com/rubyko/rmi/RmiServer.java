package com.rubyko.rmi;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.rubyko.rmi.protocol.Protocol;
import com.rubyko.rmi.protocol.tcp.TcpServerProtocol;

public class RmiServer extends Thread {

	private final ExecutorService executorService = Executors.newCachedThreadPool();

	private final Map<String, Object> serviceNameToImpl = new ConcurrentHashMap<>();

	private final int serverPort;
	private Protocol protocol = null;

	public RmiServer(int serverPort) {
		this.serverPort = serverPort;
	}

	@Override
	public void run() {
		try {
			protocol = new TcpServerProtocol(serverPort);
		} catch (Exception e) {
			throw new RmiException(e);
		}

		while (!Thread.interrupted()) {
			try {
				final Protocol clientProtocol = protocol.accept();
				executorService.execute(new RpcHandler(clientProtocol));
			} catch (IOException e) {
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

		private Protocol clientProtocol = null;

		public RpcHandler(Protocol clientProtocol) {
			this.clientProtocol = clientProtocol;
		}

		@Override
		public void run() {
			tryReadWriteObjects(clientProtocol);
		}

		private void tryReadWriteObjects(Protocol clientProtocol) {
			try {
				RmiRequest remoteRequest = readRequestObject(clientProtocol);
				RmiResponse remoteResponse = handleMethodCall(remoteRequest);
				writeResponseObject(clientProtocol, remoteResponse);
			} catch (Exception e) {
				try {
					clientProtocol.close();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
				throw new RmiException(e);
			}
		}
	}

	private void writeResponseObject(Protocol clientProtocol, RmiResponse remoteResponse) throws IOException {
		final ObjectOutputStream oos = new ObjectOutputStream(clientProtocol.getOutputStream());
		oos.writeObject(remoteResponse);
		oos.flush();
	}

	private RmiRequest readRequestObject(Protocol clientSocket) throws IOException, ClassNotFoundException {
		final ObjectInputStream ois = new ObjectInputStream(clientSocket.getInputStream());
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
		Throwable exception = null;
		try {
			if (!method.isAccessible()) {
				method.setAccessible(true);
			}
			returnValue = method.invoke(serviceImpl, args);
		} catch (Throwable e) {
			// Throw the cause !
			exception = e.getCause();
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
