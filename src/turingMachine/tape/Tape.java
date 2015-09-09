package turingMachine.tape;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class Tape<T> {
	
	private ArrayList<TapeChangeListener<T>> listeners = new ArrayList<>();
	private LinkedList<T> allData = new LinkedList<>();
	private int pos = 0;
	
	public Tape(){
		allData.add(null);
	}
	
	/** Initializes the Tape with the given data. For each entry in parameter data a cell is written
	 * and the tape is moved to the right. After writing the data, the head is put into the given
	 * position. */
	public void initializeTapeWithData(ArrayList<T> data, int position){
		for(int i = 0; i < data.size(); i++){
			write(data.get(i));
			if(i != data.size()-1)
				move(Direction.RIGHT);
		}
		
		pos = position;
	}
	
	/** Initializes the Tape with the given data. For each entry in parameter data a cell is written
	 * and the tape is moved to the right. After writing the data, the head is put into the given
	 * position. */
	public void initializeTapeWithData(T[] data, int position){
		for(int i = 0; i < data.length; i++){
			write(data[i]);
			if(i != data.length-1)
				move(Direction.RIGHT);
		}

		pos = position;
	}

	/** Moves the Head of the Tape in the given direction by 1 cell.
	 * Also creates empty cells with content null if the Head moves to a new position it's 
	 * never been before. 
	 * Notifies the listeners that the tape has been moved and, if it happened, that the
	 * tape has been expanded in size.*/
	public void move(Direction direction){
		pos += direction.toInt();
		//create empty cells if necessary
		boolean expanded = false;
		if(pos < 0){
			allData.addFirst(null);
			pos = 0;
			expanded = true;
		}
		else if(pos >= allData.size()){
			allData.addLast(null);
			pos = allData.size()-1;
			expanded = true;
		}

		//notify listeners
		if(expanded){
			for(int i = 0; i < listeners.size(); i++){
				listeners.get(i).onExpand(direction);
			}
		}
		for(int i = 0; i < listeners.size(); i++){
			listeners.get(i).onMove(direction);
		}

	}
	
	/** Reads the data of the position where the head is currently located.
	 * @return The data in that position, or null if the header is at an invalid position */
	public T read(){
		T data = null;
		try{
			data = allData.get(pos);
		}catch(IndexOutOfBoundsException e){
			//nothing to do here
		}
		return data;
	}
	
	/** @return All the data from this Tape including empty cells. This List is unmodifiable.*/
	public List<T> getContents(){
		List<T> unmodifiableAllData = Collections.unmodifiableList(allData);
		return unmodifiableAllData;
	}
	
	/** @return The current position of the head */
	public int getPosition(){
		return pos;
	}
	
	/** Writes the given data onto the tape at the current position of the head. Any existing 
	 * data will be overwritten.
	 * Notifies the listeners that data has been written. */
	public void write(T content){
		allData.remove(pos);
		allData.add(pos, content);
		//notify listeners
		for(int i = 0; i < listeners.size(); i++){
			listeners.get(i).onWrite(content);
		}
	}
	
	/** Adds the given listener to the list of listeners that will be notified according to the
	 * TapeChangeListener<T> Interface. If the listener already is in the list, it will not be
	 * added again. */
	public void addListener(TapeChangeListener<T> listener){
		if(!listeners.contains(listener))
			listeners.add(listener);
	}
	
	/** @return The String representation of the data in the cell the head is currently at.
	 * If a cell is empty(null) an underscore will be returned ("_") */
	public String getCurrent(){
		return getDataAtPos(pos, "_");
	}
	
	/** @return The data of the Tape to the left of the head, exluding the cell the head is
	 * currently at. Also removes any empty cells from the leftmost cell up to and excluding the
	 * first non-empty cell or the current head position.*/
	public String getLeft(){
		stripEmptyLeft(pos);
		return getDataFromTo(0, pos-1, "_");
	}
	
	/** @return The data of the Tape to the right of the head, excluding the cell the head is
	 * currently at. Also removes any empty cells from the rightmost cell down to and excluding the
	 * last non-empty cell or the current head position. */
	public String getRight(){
		stripEmptyRight(pos);
		return getDataFromTo(pos+1, allData.size()-1, "_");
	}
	
	/** @return The String representation of all the data in the tape. Empty cells are 
	 * represented by a "_". Also removes any empty cells from the edges. */
	public String toString(){
		stripEmptyLeft(pos);
		stripEmptyRight(pos);
		String str = getDataFromTo(0, allData.size()-1, "_");
		str += System.lineSeparator();
		for(int i = 0; i < allData.size(); i++){
			if(i == pos)
				str += "^";
			else
				str += " ";
		}
		
		return str;
	}
	
	/** Removes every empty cell starting at the leftmost cell up to and excluding the
	 * first non-empty cell or the given position. */
	int stripEmptyLeft(int position){
		int numRemoved = 0;
		for(int i = 0; i < position; i++){
			if(allData.getFirst() == null){
				allData.removeFirst();
				numRemoved++;
				position--;
				pos--;
				i--;
			}else{
				break;
			}
		}
		return numRemoved;
	}
	
	/** Removes every empty cell starting at the rightmost cell up to and excluding the
	 * first non-empty cell or the given position */
	int stripEmptyRight(int position){
		int numRemoved = 0;
		for(int i = allData.size()-1; i > position; i--){
			if(allData.getLast() == null){
				allData.removeLast();
				numRemoved++;
			}else{
				break;
			}
		}
		return numRemoved;
	}
	
	/** @return A String containing the string representations of all the cells between and 
	 * including startPos to endPos. For any empty cell the parameter empty is used as the
	 * String representation. 
	 * @param startPos The first position to get
	 * @param endPos The last position to get
	 * @param empty is used as the string representation for empty cells*/
	private String getDataFromTo(int startPos, int endPos, String empty){
		String str = "";
		for(int i = startPos; i <= endPos; i++){
			str += getDataAtPos(i, empty);
		}
		return str;
	}
	
	/** Gets the string representation of a single cell. Empty cells are represented with the
	 * parameter empty 
	 * @param position The position of the cell to get
	 * @param empty is used as the string representation for empty cells*/
	private String getDataAtPos(int position, String empty){
		T data = null;
		try{
			data = allData.get(position);
		}catch(IndexOutOfBoundsException e){
			//nothing to do here
		}
		String dataString;
		if(data == null)
			dataString = new String(empty);
		else
			dataString = data.toString();
		return dataString;
		
	}
	
	
	
	
	
	
	
	
	
	
	
	
}
