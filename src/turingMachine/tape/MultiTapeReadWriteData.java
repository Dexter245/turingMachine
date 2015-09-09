package turingMachine.tape;

import java.util.ArrayList;

public class MultiTapeReadWriteData<T> {
	
	private ArrayList<T> allData = new ArrayList<>();
	private int length;

	/** Creates a new instance with length amount of tapedata all of which are initialized 
	 * with null */
	public MultiTapeReadWriteData(int length){
		this.length = length;
		for(int i = 0; i < length; i++){
			allData.add(null);
		}
	}
	
	/** @return The data the i-th head is currently pointed at.
	 * @throws IndexOutOfBoundsException if i is out of range */
	public T get(int i) throws IndexOutOfBoundsException{
		if(i < 0 || i >= length)
			throw new IndexOutOfBoundsException("i may not be " + i + " with length: " + length);
		return allData.get(i);
	}
	
	/** Sets the data for the i-th head.
	 * @throw IndexOutOfBoundsException if i is out of range */
	public void set(int i, T value) throws IndexOutOfBoundsException{
		if(i < 0 || i >= length)
			throw new IndexOutOfBoundsException("i may not be " + i + " with length: " + length);
		allData.remove(i);
		allData.add(i, value);
	}
	
	/** @return the amount of heads/tapes */
	public int getLength(){
		return length;
	}
	
	/** @return a String containing the String representations of the data of all heads */
	public String toString(){
		String str = "";
		T t;
		for(int i = 0; i < length; i++){
			t = allData.get(i);
			if(t == null)
				str += "_";
			else
				str += t.toString();
		}
		return str;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((allData == null) ? 0 : allData.hashCode());
		result = prime * result + length;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		MultiTapeReadWriteData<?> other = (MultiTapeReadWriteData<?>) obj;
		if (allData == null) {
			if (other.allData != null)
				return false;
		} else if (!allData.equals(other.allData))
			return false;
		if (length != other.length)
			return false;
		return true;
	}

	
	
	
	
	
	
	
	
	
	
	
}
