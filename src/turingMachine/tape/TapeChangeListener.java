package turingMachine.tape;

public interface TapeChangeListener<T> {

	public void onMove(Direction direction);
	
	public void onExpand(Direction direction);
	
	public void onWrite(T value);
	
	
	
	
	
}
