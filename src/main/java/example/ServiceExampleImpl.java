package example;


public class ServiceExampleImpl implements ServiceExample {

	@Override
	public int add(int p1, int p2) throws DatabaseException {
		if(true)
			throw new DatabaseException();
		return p1 + p2;
	}

}
