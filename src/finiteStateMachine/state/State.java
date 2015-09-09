package finiteStateMachine.state;

import java.util.HashMap;

import finiteStateMachine.AbstractFiniteStateMachine;

public class State<I, O> {
	
	private AbstractFiniteStateMachine<I, O> machine;
	private String name;
	private HashMap<I, Transition<O>> transitions = new HashMap<>();
	private boolean accepted = false;

	/** Creates a new State Object with the given parameters */
	public State(AbstractFiniteStateMachine<I, O> machine, String name){
		this.machine = machine;
		this.name = name;
	}

	/** Set this State as accepted */
	public void setAccepted(boolean accepted){
		this.accepted = accepted;
	}
	
	/** @return True if this State is accepted, else otherwise*/
	public boolean isAccepted(){
		return accepted;
	}
	
	/** Adds a new Transition to the List of Transitions of this State.
	 * If there already is a Transition that would react to the given input, the Transition
	 * for that input will be overwritten. */
	public void addTransition(I input, String targetState, O output){
		Transition<O> trans = new Transition<O>(targetState, output);
		if(transitions.containsKey(input)){
			transitions.replace(input, trans);
		}else{
			transitions.put(input, trans);
		}
		
	}
	
	/** @return The Transition that would be performed with the given input or null if there
	 * is no Transition for that input */
	public Transition<O> getTransition(I input){
		return transitions.get(input);
	}
	
	/** Performs a Transition based on the given input. This also sets the current State of
	 * this states machine to the new value.
	 * @return The Output of the Transition
	 * @throws IllegalArgumentException If there is no Transition for the given Input*/
	public O transit(I input) throws IllegalArgumentException{
		Transition<O> trans = transitions.get(input);
		if(trans == null)
			throw new IllegalArgumentException("There is no transition for input: '" + input + "'");
		machine.setCurrentState(trans.getNextState());
		return trans.getOutput();
	}
	
	/** @return This states machine */
	public AbstractFiniteStateMachine<I, O> getMachine(){
		return machine;
	}
	
	/** @return The name of this state */
	public String getName(){
		return name;
	}
	
	/** @return The name of this state */
	public String toString(){
		return name;
	}
	
	
}
