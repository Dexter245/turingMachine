package turingMachine;

import finiteStateMachine.AbstractFiniteStateMachine;
import turingMachine.tape.Direction;
import turingMachine.tape.MultiTape;
import turingMachine.tape.MultiTapeReadWriteData;

public class TuringMachine<T> extends AbstractFiniteStateMachine<MultiTapeReadWriteData<T>, 
	TuringTransitionOutput<T>> {
	
	private MultiTape<T> tapes;
	private MultiTapeReadWriteData<T> rwData;
	private int tapeCount;
	
	/** Demanded demonstration of functionality for block 2 by using the given countingSort
	 * example */
	public static void main(String[] args){
		TuringMachine<Character> machine = new TuringMachine<>(2);

		//write initial tape data
		machine.getTapes().getTapes().get(0).initializeTapeWithData(
				new Character[]{'a', 'b', 'a', 'c', 'c', 'b', 'c', 'a', 'b', 'a'}, 0);
		machine.getTapes().getTapes().get(1).initializeTapeWithData(new Character[]{null}, 0);

		//add states
		machine.addState("S");
		machine.addState("A");
		machine.addState("B");
		machine.addState("C");
		machine.addState("F");
		
		//set current and accepted states
		machine.setCurrentState("S");
		machine.getState("F").setAccepted(true);
		
		//add transitions
		machine.addTransitionFromLine(machine, "S,__ -> F,__,NN");
		machine.addTransitionFromLine(machine, "S,a_ -> A,a_,NN");
		machine.addTransitionFromLine(machine, "S,b_ -> A,b_,NN");
		machine.addTransitionFromLine(machine, "S,c_ -> A,c_,NN");
		
		machine.addTransitionFromLine(machine, "A,a_ -> A,aa,RR");
		machine.addTransitionFromLine(machine, "A,b_ -> A,b_,RN");
		machine.addTransitionFromLine(machine, "A,c_ -> A,c_,RN");
		machine.addTransitionFromLine(machine, "A,__ -> B,__,LN");
		
		machine.addTransitionFromLine(machine, "B,a_ -> B,a_,LN");
		machine.addTransitionFromLine(machine, "B,b_ -> B,bb,LR");
		machine.addTransitionFromLine(machine, "B,c_ -> B,c_,LN");
		machine.addTransitionFromLine(machine, "B,__ -> C,__,RN");
		
		machine.addTransitionFromLine(machine, "C,a_ -> C,a_,RN");
		machine.addTransitionFromLine(machine, "C,b_ -> C,b_,RN");
		machine.addTransitionFromLine(machine, "C,c_ -> C,cc,RR");
		machine.addTransitionFromLine(machine, "C,__ -> F,__,NN");

		//run the machine
		System.out.println("before run:");
		System.out.println(machine.getTapes().toString());
		
		machine.run();
		
		System.out.println("after run:");
		System.out.println(machine.getTapes().toString());
		
	}
	
	/** Adds a transition to the given machine based on the given line. The line must be in
	 * a very specific format. This method is only used for the block2-demonstration/example */
	void addTransitionFromLine(TuringMachine<Character> machine, String line){
		//format is:
		//<startState>,<input> -> <targetState>,<output>,<dirs>
		
		//split into parts
		String[] parts = new String[5];
		int index = -1;
		index = line.indexOf(",");
		parts[0] = line.substring(0, index);
		line = line.substring(index+1);
		
		index = line.indexOf(" ");
		parts[1] = line.substring(0, index);
		line = line.substring(index+1);
		
		index = line.indexOf(" ");
		line = line.substring(index+1);
		
		index = line.indexOf(",");
		parts[2] = line.substring(0, index);
		line = line.substring(index+1);

		index = line.indexOf(",");
		parts[3] = line.substring(0, index);
		line = line.substring(index+1);
		
		parts[4] = new String(line);
		
		//split in and out into its parts and replace with null if necessary
		Character[] in = new Character[2];
		in[0] = parts[1].charAt(0);
		in[1] = parts[1].charAt(1);
		if(in[0] == '_')
			in[0] = null;
		if(in[1] == '_')
			in[1] = null;
		Character[] out = new Character[2];
		out[0] = parts[3].charAt(0);
		out[1] = parts[3].charAt(1);
		if(out[0] == '_')
			out[0] = null;
		if(out[1] == '_')
			out[1] = null;
		
		Direction[] dirs = new Direction[2];
		dirs[0] = Direction.fromChar(parts[4].charAt(0));
		dirs[1] = Direction.fromChar(parts[4].charAt(1));


		MultiTapeReadWriteData<Character> inputData = new MultiTapeReadWriteData<>(2);
		MultiTapeReadWriteData<Character> outputData = new MultiTapeReadWriteData<>(2);
		TuringTransitionOutput<Character> output;
		
		inputData.set(0, in[0]);
		inputData.set(1, in[1]);
		outputData.set(0, out[0]);
		outputData.set(1, out[1]);
		output = new TuringTransitionOutput<>(outputData, dirs);
		
		machine.addTransition(parts[0], inputData, parts[2], output);
		
	}
	
	/** Creates a new instance with tapeCount amount of empty tapes */
	public TuringMachine(int tapeCount){
		this.tapeCount = tapeCount;
		tapes = new MultiTape<>(tapeCount);
	}
	
	/** Performs transitions until the machine is in an accepted state or until no transition 
	 * is available. */
	public void run(){
		
		while(!isInAcceptedState()){
			try{
				transit();
			}catch(IllegalArgumentException e){
				break;
			}
		}
		
	}
	
	/** Performs a single transition based on the input that is read from the tapes with the
	 * current tape-head positions. 
	 * @throws IllegalStateException if the machine already is in an accepted state 
	 * @throws IllegalArgumentException if there is no transition for the given input.*/
	public void transit() throws IllegalStateException, IllegalArgumentException{
		if(isInAcceptedState())
			throw new IllegalStateException("Cannot transit because the machine is in an "
					+ "accepted state");
		
		rwData = tapes.read();
		super.transit(rwData);
		
	}

	/** @return the amount of tapes in this machine */
	public int getTapeCount(){
		return tapeCount;
	}
	
	/** @return a MultiTape object that represents all the tapes of this machine */
	public MultiTape<T> getTapes(){
		return tapes;
	}
	
	/** Processes the output of a transition by writing the new data to the tapes and moving
	 * the tape-heads. */
	@Override
	public void processOutput(TuringTransitionOutput<T> output){
		tapes.write(output.getToWrite());
		tapes.move(output.getDirections());
	}
	
}
