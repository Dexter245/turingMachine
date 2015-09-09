package turingMachine.tape;

import java.util.ArrayList;
import java.util.List;

public class MultiTape<T> {
	
	private ArrayList<Tape<T>> tapes;
	private MultiTapeReadWriteData<T> rwData;
	private int tapeCount;

	/** Creates a new instance with tapeCount amount of empty tapes */
	public MultiTape(int tapeCount){
		this.tapeCount = tapeCount;
		tapes = new ArrayList<Tape<T>>();
		for(int i = 0; i < tapeCount; i++){
			tapes.add(new Tape<T>());
		}
		rwData = new MultiTapeReadWriteData<T>(tapeCount);
	}
	
	/** Reads the data under every head */
	public MultiTapeReadWriteData<T> read(){
		for(int i = 0; i < tapeCount; i++){
			T data = tapes.get(i).read();
			rwData.set(i, data);
		}
		
		return rwData;
	}
	
	/** Writes the given data to each tape. 
	 * @throws IllegalArgumentException If the amount of values given don't match the 
	 * amount of tapes. */
	public void write(MultiTapeReadWriteData<T> values){
		if(values.getLength() != tapeCount)
			throw new IllegalArgumentException("Amount of given values: " + values.getLength() + 
					" doesn't match amount of tapes: " + tapeCount);
		for(int i = 0; i < tapeCount; i++){
			T data = values.get(i);
			tapes.get(i).write(data);
		}
	}
	
	/** Moves each tape.
	 * @throws IllegalArgumentException if the amount of directions given don't match
	 * the amount of tapes */
	public void move(Direction[] directions){
		if(directions.length != tapeCount)
			throw new IllegalArgumentException("Amount of direction values: " + directions.length + 
					" doesn't match amount of tapes: " + tapeCount);
		for(int i = 0; i < tapeCount; i++){
			tapes.get(i).move(directions[i]);
		}
	}
	
	/** @return a list of all tapes. */
	public List<Tape<T>> getTapes(){
		return tapes;
	}
	
	/** @return the amount of tapes. */
	public int getTapeCount(){
		return tapeCount;
	}
	
	/** @return a String with the string representations of every tape seperated by newlines. */
	public String toString(){
		String str = "";
		for(int i = 0; i < tapeCount; i++){
			str += tapes.get(i).toString();
			str += "\n";
		}
		return str;
	}
	
	
	
	
}
