package example;

import com.rubyko.rmi.RmiServer;

public class ServerExample {
 
	public static void main(String[] args) {
		// create the RMI server
		RmiServer rpcServer = new RmiServer(6789);
		// register a service under the name example
		// the service has to implement an interface for the magic to work
		rpcServer.registerService("example", new ServiceExampleImpl());
		rpcServer.registerService("example2", new ServiceExampleImpl2());
		// start the server at port 6789
		rpcServer.start();

	}

}
