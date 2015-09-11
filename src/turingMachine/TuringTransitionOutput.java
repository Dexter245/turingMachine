package turingMachine;

import turingMachine.tape.Direction;
import turingMachine.tape.MultiTapeReadWriteData;

public class TuringTransitionOutput<T> {
	
	private MultiTapeReadWriteData<T> rwData;
	private Direction[] directions;

	/** Creates a new instance.
	 * @throws IllegalArgumentException if the amount of directions given doesn't match
	 * the amount of ReadWriteData */
	public TuringTransitionOutput(MultiTapeReadWriteData<T> toWrite, Direction... directions){
		if(toWrite.getLength() != directions.length)
			throw new IllegalArgumentException("Amount of data: " + toWrite.getLength() + 
					" doesn't match amount of directions: " + directions.length);
		this.rwData = toWrite;
		this.directions = directions;
	}
	
	/** @return The read/write data */
	public MultiTapeReadWriteData<T> getToWrite(){
		return rwData;
	}
	
	/** @return The directions */
	public Direction[] getDirections(){
		return directions;
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
}
