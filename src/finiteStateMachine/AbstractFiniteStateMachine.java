package finiteStateMachine;

import java.util.HashMap;

import finiteStateMachine.state.State;
import finiteStateMachine.state.Transition;

public abstract class AbstractFiniteStateMachine<I, O> {
	
	private HashMap<String, State<I, O>> states = new HashMap<>();
	private State<I, O> currentState = null;
	
	/** Creates a new State with the given name and adds it to the list of states.
	 * @throws IllegalStateException if a state with the given name already exists */
 	public void addState(String state) throws IllegalStateException{
 		if(states.containsKey(state)){
			throw new IllegalStateException("A state with the name " + state + " already exists");
		}
		State<I, O> newState = new State<I, O>(this, state);
		states.put(state, newState);
	}

	/** Return the state that has the given name
	 * @throws IllegalStateException if there is no such state 
	 * @return The State with the given name*/
	public State<I,O> getState(String state) throws IllegalStateException{
		State<I, O> stateObj = states.get(state);
		if(stateObj == null)
			throw new IllegalStateException("There is no state with the name " + state + ".");
		else
			return stateObj;
	}
	
	/** Adds a Transition to startState
	 * @throws IllegalStateException if either startState or targetState don't exist */
	public void addTransition(String startState, I input, String targetState, O output) 
		throws IllegalStateException{

		State<I, O> state1 = getState(startState);
		getState(targetState);//check if targetState exists
		state1.addTransition(input, targetState, output);
	}
	
	/** Sets the current State
	 * @throws IllegalStateException if there is no such state */
	public void setCurrentState(String state) throws IllegalStateException{
		currentState = getState(state);
	}
	
	/** Returns the current State 
	 * @return The current state or null if no stage has been set before*/
	public State<I,O> getCurrentState(){
		return currentState;
	}

	/** @return True if currentState is accepted, false otherwise or if currentState is null */
	public boolean isInAcceptedState(){
		if(currentState == null)
			return false;
		else
			return currentState.isAccepted();
	}
	
	/** Does the Transition to the next State that is determined by the input and processes 
	 * the output from the Transition. 
	 * @throws IllegalStateException If the current State is null
	 * @throws IllegalArgumentException If there is no Transition for the given input*/
	public void transit(I input) throws IllegalStateException, IllegalArgumentException{
		if(currentState == null)
			throw new IllegalStateException("Current State is null, can't transit");
		O out = currentState.transit(input);
		processOutput(out);
	}
	
	/** Gets the transition that would occur with the given input. This method does NOT
	 * actually perform the transition
	 * @return Said transition or null if there would be no transition with the given input
	 * @throws IllegalStateException If no state has been set as the current one */
	public Transition<O> getTransition(I input) throws IllegalStateException{
		if(currentState == null)
			throw new IllegalStateException("There is no current state");
		Transition<O> trans = currentState.getTransition(input);
		return trans;
	}

	protected abstract void processOutput(O output);
	
	
}
