package turingMachine.tape;

public enum Direction {
	LEFT(-1),
	RIGHT(1),
	NON(0),
	INVALID(-100);
	
	private int value;
	
	private Direction(int value){
		this.value = value;
	}
	
	/** Returns the Directions corresponding the given char value.
	 * @return NON for 'N', LEFT for 'L', RIGHT for 'R' or INVALID in every other case */
	public static Direction fromChar(char val){
		switch(val){
		case 'N':
			return Direction.NON;
		case 'L':
			return Direction.LEFT;
		case 'R':
			return Direction.RIGHT;
		}
		return Direction.INVALID;
	}
	
	/** Returns the int representation of this Enum value. This can be used to easily move the 
	 * head of a tape. 
	 * @return -1 if the value of this Direction is LEFT, 1 if RIGHT and 0 if NON*/
	int toInt(){
		return value;
	}
	
	/** Returns the String representation of this Direction
	 * @return "LEFT", "RIGHT", "NON" or "INVALID" */
	public String toString(){
		switch(value){
		case -1:
			return "LEFT";
		case 1:
			return "RIGHT";
		case 0:
			return "NON";
		}
		return "INVALID";
	}
}
