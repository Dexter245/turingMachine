package finiteStateMachine.state;

public class Transition<O> {
	
	private String targetState;
	private O output;

	/** Creates a new Transition based on the given parameters
	 * @param targetState may be null
	 * @param output may be null */
	public Transition(String targetState, O output){
		this.targetState = targetState;
		this.output = output;
	}

	/** @return The name of the next/target state */
	public String getNextState(){
		return targetState;
	}
	
	/** @return The Output of this Transition */
	public O getOutput(){
		return output;
	}
	
	/** Creates a String representation of this object based on this Transitions targetState
	 * and its output. If either of those are null, then their String representations will be
	 * "null"*/
	@Override
	public String toString(){
		String state = targetState;
		if(state == null)
			state = "null";
		String out = null;
		if(output == null)
			out = "null";
		else
			out = output.toString();
		return state + "," + out;
	}
	
	
	
	
	
}
