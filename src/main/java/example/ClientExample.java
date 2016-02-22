package example;

import com.rubyko.rmi.RmiClient;

public class ClientExample {
 
	public static void main(String[] args) {
		// lookup the service with name "example", interface ServiceExample located at host localhost and port 6789
		final ServiceExample example = RmiClient.lookupService("localhost", 6789, "example", ServiceExample.class);
		// call the method concat and display the result
		for (int i = 0; i < 2000; i++) {
			final int y = i;
			new Thread(new Runnable() {
				@Override
				public void run() {
					System.out.println(y == example.add(0, y));
				}
			}).start();
		}
	}

}
