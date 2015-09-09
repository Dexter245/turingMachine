package finiteStateMachine.mealy;

import finiteStateMachine.AbstractFiniteStateMachine;

public class MealyMachine<I, O> extends AbstractFiniteStateMachine<I, O> {
	
	/** Demanded Demonstration of the functionality of Block 1 */
	public static void main(String[] args){
		
		//create the machine
		MealyMachine<Integer, String> machine = new MealyMachine<>();

		//add states
		machine.addState("state1");
		machine.addState("state2");
		machine.addState("state3");

		//add transitions
		machine.addTransition("state1", 2, "state2", "1->2");
		machine.addTransition("state1", 3, "state3", "1->3");
		
		machine.addTransition("state2", 1, "state1", "2->1");
		machine.addTransition("state2", 3, "state3", "2->3");
		
		machine.addTransition("state3", 1, "state1", "3->1");
		machine.addTransition("state3", 2, "state2", "3->2");
		
		//set current state and print it
		machine.setCurrentState("state1");
		System.out.println("currentState: " + machine.getCurrentState().toString());

		//Transit, let it print the Transitions output (via MealyMachine) and then print 
		//the current state with different inputs
		System.out.print("Output with input 2: ");
		machine.transit(2);
		System.out.println("");
		System.out.println("currentState: " + machine.getCurrentState().toString());

		System.out.print("Output with input 3: ");
		machine.transit(3);
		System.out.println("");
		System.out.println("currentState: " + machine.getCurrentState().toString());
		
		System.out.print("Output with input 1: ");
		machine.transit(1);
		System.out.println("");
		System.out.println("currentState: " + machine.getCurrentState().toString());
		
		
		
		
	}

	/** Processes the Output of Transitions by printing it onto the console */
	@Override
	protected void processOutput(O output) {
		System.out.print(output.toString());
	}

}
