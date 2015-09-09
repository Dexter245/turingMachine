package turingMachine.io;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.util.ArrayList;

import turingMachine.TuringMachine;
import turingMachine.TuringTransitionOutput;
import turingMachine.tape.Direction;
import turingMachine.tape.MultiTapeReadWriteData;

public class TuringMachineReader {
	
	/** Demanded demonstration of functionality for block 3 by using the given countingSort
	 * example using both a Reader and an InputStream */
	public static void main(String[] args){

		FileReader fileReader = null;
		try {
			fileReader = new FileReader("countingsort.txt");
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		
		FileInputStream fis = null;
		try{
			fis = new FileInputStream("countingsort.txt");
		}catch(FileNotFoundException e){
			e.printStackTrace();
		}

		//machine1, using Reader
		TuringMachine<Character> machine1 = TuringMachineReader.read(fileReader);

		System.out.println("machine1 tapedata before run:");
		System.out.println(machine1.getTapes().toString());
		
		machine1.run();
		
		System.out.println("machine1 tapedata after run:");
		System.out.println(machine1.getTapes().toString());//as expected
		
		//machine2, using InputStream
		TuringMachine<Character> machine2 = TuringMachineReader.read(fis);
		
		System.out.println("machine2 tapedata before run:");
		System.out.println(machine2.getTapes().toString());
		
		machine2.run();
		
		System.out.println("machine2 tapedata after run:");
		System.out.println(machine2.getTapes().toString());//as expected
		
		try{
			fileReader.close();
			fis.close();
		}catch(IOException e){
			e.printStackTrace();
		}
		
	}
	
	/** Given an ArrayList of Strings where each string is a line of text, this method removes
	 * all lines from the list that are either empty("") or contain a '#' character(which means
	 * that line is a comment) */
	private static void removeCommentsAndBlankLines(ArrayList<String> lines){
		
		String line;
		int index = -1;
		for(int i = 0; i < lines.size(); i++){
			line = lines.get(i);
			index = line.indexOf('#');
			if(line.equals("") || index != -1){
				lines.remove(i);
				i--;
			}
		}
		
	}
	
	/** Creates a TuringMachine based on the configuration given as a String.
	 * @param data The configuration read in from a file as a string
	 * @return a fully configured TuringMachine. Plug'n'Play!
	 * @throws IllegalArgumentException in case the provided configuration data has errors in it
	 * like formatting errors for example. */
	public static TuringMachine<Character> createMachine(String data) 
			throws IllegalArgumentException{

		//split into lines
		ArrayList<String> lines = new ArrayList<>();
		String line;
		String[] linesArray = data.split(System.lineSeparator());
		for(int i = 0; i < linesArray.length; i++){
			lines.add(linesArray[i]);
		}
		
		removeCommentsAndBlankLines(lines);
		
		//numTapes
		int numTapes = getInt(getLine(lines, 0, true));
		if(numTapes < 1)
			throw new IllegalArgumentException("Invalid amount of tapes: " + numTapes);
		
		//create machine
		TuringMachine<Character> machine = new TuringMachine<>(numTapes);
		
		//initial tapeData
		ArrayList<ArrayList<Character>> allInitTapeData = new ArrayList<>();
		ArrayList<Character> initTapeData;
		
		for(int i = 0; i < numTapes; i++){
			line = getLine(lines, 0, false);
			initTapeData = new ArrayList<>();
			//head pos
			int index = line.indexOf(':');
			if(index == -1)
				throw new IllegalArgumentException("No ':' found in data for tape " + (i+1));
			String posS = line.substring(0, index);
			int pos = getInt(posS);
			pos--;//index in file starts with 1
			line = line.substring(index+1);
			if(pos < 0 || pos > line.length()-1)
				throw new IllegalArgumentException("Invalid head-position for tape.");
			//data
			char[] tapeData = line.toCharArray();
			for(int j = 0; j < tapeData.length; j++){
				if(tapeData[j] != '_')
					initTapeData.add(tapeData[j]);
				else
					initTapeData.add(null);
			}
			
			machine.getTapes().getTapes().get(i).initializeTapeWithData(initTapeData, pos);
			

			allInitTapeData.add(initTapeData);
			lines.remove(0);
		}
		
		//states
		line = getLine(lines, 0, true);
		String[] states = line.split(",");
		for(int i = 0; i < states.length; i++){
			try{
				machine.addState(states[i]);
			}catch(IllegalStateException e){
				throw new IllegalArgumentException("State with the name " + states[i] + 
						" already exists.");
			}
		}
		
		//start state
		line = getLine(lines, 0, true);
		try{
			machine.setCurrentState(line);
		}catch(IllegalStateException e){
			throw new IllegalArgumentException("Can't set the state " + line + " as the startState " + 
					"since it doesn't exist in the list of states.");
		}
		
		//accepted states
		line = getLine(lines, 0, true);
		String[] acceptedStates = line.split(",");
		for(int i = 0; i < acceptedStates.length; i++){
			try{
				machine.getState(acceptedStates[i]).setAccepted(true);
			}catch(IllegalStateException e){
				throw new IllegalArgumentException("Can't set the state " + acceptedStates[i] + 
						" as an accepted state, since it doesn't exist in the list of states.");
			}
		}
		
		//transitions
		for(int i = 0; i < lines.size(); i++){
			line = getLine(lines, i, false);
			
			addTransition(machine, line);
			
			lines.remove(0);
			i--;
		}
		
		
		return machine;
	}
	
	/** Returns the element with the given index of the arrayList if it exists. If not an exception 
	 * is thrown.
	 * @param lines The arrayList from which an element should be extracted and returned
	 * @param index the index of the element in the arrayList to get
	 * @param remove whether to remove the element from the arrayList
	 * @return the element of the arrayList with the given index, if it exists
	 * @throws IllegalArgumentException if no element with the given index exists in the arrayList*/
	private static String getLine(ArrayList<String> lines, int index, boolean remove) 
			throws IllegalArgumentException{

		String line = null;
		try{
			line = lines.get(index);
		}catch(IndexOutOfBoundsException e){
			throw new IllegalArgumentException("Not enough data/lines in configuration data");
		}

		if(remove)
			lines.remove(index);
		return line;
	}
	
	/** Adds a transition to the given machine. The data for the Transition is provided as a 
	 * string that contains one line of a configuration-file that contains transition data. 
	 * @throws IllegalArgumentException in case the provided configuration data has errors in it
	 * like formatting errors for example. */
	private static void addTransition(TuringMachine<Character> machine, String line)
			throws IllegalArgumentException{
		
		//startState
		int index = line.indexOf(',');
		if(index == -1)
			throw new IllegalArgumentException("invalid transition");
		String startState = line.substring(0, index);
		line = line.substring(index+1);
		
		//input
		MultiTapeReadWriteData<Character> inputRwData = 
				new MultiTapeReadWriteData<>(machine.getTapeCount());
		index = line.indexOf(' ');
		if(index == -1)
			throw new IllegalArgumentException("invalid transition");
		String input = line.substring(0, index);
		char[] inputCh = input.toCharArray();
		if(inputCh.length != machine.getTapeCount())
			throw new IllegalArgumentException("invalid amount of inputData in transition");
		for(int i = 0; i < inputCh.length; i++){
			if(inputCh[i] != '_')
				inputRwData.set(i, inputCh[i]);
			else
				inputRwData.set(i, null);
		}
		line = line.substring(index+1);
		
		//remove "-> " part
		index = line.indexOf("-> ");
		if(index == -1)
			throw new IllegalArgumentException("invalid Transition: no ' -> ' detected");
		line = line.substring(index+3);
		
		//targetState
		index = line.indexOf(',');
		String targetState = line.substring(0, index);
		line = line.substring(index+1);
		
		//output
		MultiTapeReadWriteData<Character> outputRwData = 
				new MultiTapeReadWriteData<>(machine.getTapeCount());
		index = line.indexOf(',');
		if(index == -1)
			throw new IllegalArgumentException("invalid transition");
		String output = line.substring(0, index);
		char[] outputCh = output.toCharArray();
		if(outputCh.length != machine.getTapeCount())
			throw new IllegalArgumentException("invalid amount of outputData in transition");
		for(int i = 0; i < outputCh.length; i++){
			if(outputCh[i] != '_')
				outputRwData.set(i, outputCh[i]);
			else
				outputRwData.set(i, null);
		}
		line = line.substring(index+1);
		
		//directions
		Direction[] directions = new Direction[machine.getTapeCount()];
		char[] dirCh = line.toCharArray();
		if(dirCh.length != machine.getTapeCount())
			throw new IllegalArgumentException("invalid amount of directions in transition");
		for(int i = 0; i < dirCh.length; i++){
			directions[i] = Direction.fromChar(dirCh[i]);
			if(directions[i] == Direction.INVALID)
				throw new IllegalArgumentException("Invalid direction in transition");
		}
		
		TuringTransitionOutput<Character> transitionOutput = 
				new TuringTransitionOutput<>(outputRwData, directions);

		machine.addTransition(startState, inputRwData, targetState, transitionOutput);
		
	}
	
	/** Parses a string to an int.
	 * @return the integer value of the string, if parsing was successful
	 * @throws IllegalArgumentException if parsing was not successful */
	private static int getInt(String string) throws IllegalArgumentException{
		int value;
		try{
			value = Integer.parseInt(string);
		}catch(NumberFormatException e){
			throw new IllegalArgumentException(string + " is not an Integer.");
		}
		
		return value;
	}

	/** Creates a TuringMachine based on the configuration given as an InputStream.
	 * @param input InputStream pointing to the Configuration file
	 * @return a fully configured TuringMachine. Plug'n'Play!
	 * @throws IllegalArgumentException in case the provided configuration data has errors in it
	 * like formatting errors for example. */
	public static TuringMachine<Character> read(InputStream input) throws IllegalArgumentException{

		String data = "";
		byte[] buffer = new byte[100];
		int numRead = -1;
		
		while(true){
			try{
				numRead = input.read(buffer);
			}catch(IOException e){
				throw new IllegalArgumentException("Can't read file. Error: " + e.getMessage());
			}
			if(numRead == 100){
				data += new String(buffer);
			}
			else{
				if(numRead != -1)
					data += new String(buffer, 0, numRead);
				break;
			}
		}
		
		return createMachine(data);
	}
	
	/** Creates a TuringMachine based on the configuration given as a Reader.
	 * @param input Reader pointing to the Configuration file
	 * @return a fully configured TuringMachine. Plug'n'Play!
	 * @throws IllegalArgumentException in case the provided configuration data has errors in it
	 * like formatting errors for example. */
	public static TuringMachine<Character> read(Reader input) throws IllegalArgumentException{
		
		String data = "";
		char[] buffer = new char[100];
		int numRead = -1;

		while(true){
			try{
				numRead = input.read(buffer);
			}catch(IOException e){
				throw new IllegalArgumentException("Can't read file. Error: " + e.getMessage());
			}
			if(numRead == 100)
				data += new String(buffer);
			else{
				if(numRead != -1)
					data += new String(buffer, 0, numRead);
				break;
			}
		}
		
		return createMachine(data);
	}
	
	
}
