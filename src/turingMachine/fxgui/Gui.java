package turingMachine.fxgui;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import finiteStateMachine.state.State;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.control.Slider;
import javafx.scene.control.TextArea;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Duration;
import turingMachine.TuringMachine;
import turingMachine.TuringTransitionOutput;
import turingMachine.Utilities;
import turingMachine.io.TuringMachineReader;
import turingMachine.tape.MultiTapeReadWriteData;

public class Gui extends Application {
	
	private static final int DEFAULT_DELAY = 500;
	
	private TuringMachine<Character> machine = null;

	private Stage stage;
	private Scene scene;
	private VBox vBoxRoot = new VBox();
	
	private Button buttonStep = new Button("Step");
	private Button buttonStart = new Button("Start");
	private Button buttonStop = new Button("Stop");
	private Label labelDelay = new Label("Delay: " + DEFAULT_DELAY);
	private Slider sliderDelay = new Slider(20, 2000, DEFAULT_DELAY);
	private Button buttonLoad = new Button("Load");
	private Button buttonOpen = new Button("Open");
	
	private Label labelCurrentState = new Label("Current State: None");
	private Label labelAcceptedState = new Label("In Accepted State: ");
	private CheckBox checkBoxAcceptedState = new CheckBox();

	private MultiTapeControl<Character> multiTapeControl = new MultiTapeControl<>();
	private TextArea statusText = new TextArea("Please load and run a machine.");
	private TextArea textAreaMachineDef = new TextArea("");
	
	private String statusTextString = "";
	private Timeline timeline = new Timeline();
	private KeyFrame keyFrame;

	public static void main(String[] args){
		launch(args);
	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		
		this.stage = primaryStage;
		scene = new Scene(vBoxRoot);
		stage.setScene(scene);

		initUI();
		setupListeners();
		
		keyFrame = new KeyFrame(Duration.millis(DEFAULT_DELAY), new EventHandler<ActionEvent>(){

			@Override
			public void handle(ActionEvent event) {
				machineStepLoop();
			}
			
		});
		timeline.getKeyFrames().add(keyFrame);
		timeline.setCycleCount(Timeline.INDEFINITE);
		
		stage.setWidth(800);
		stage.setHeight(600);
		stage.show();
		
	}
	
	/** Initializes the gui. Sets the element's properties and packs them into their 
	 * parent-containers. */
	private void initUI(){
		HBox hBoxLower = new HBox();
		HBox hBoxControlArea = new HBox();
		HBox hBoxState = new HBox();
		VBox vBoxViewArea = new VBox();

		checkBoxAcceptedState.setDisable(true);
		checkBoxAcceptedState.selectedProperty().set(false);
		labelCurrentState.setPadding(new Insets(0, 50, 0, 0));
		multiTapeControl.setPrefHeight(1000000);
		statusText.setPrefHeight(1000000);
		hBoxLower.setPrefHeight(1000000);
		
		hBoxControlArea.getChildren().add(buttonStep);
		hBoxControlArea.getChildren().add(buttonStart);
		hBoxControlArea.getChildren().add(buttonStop);
		hBoxControlArea.getChildren().add(new Separator(Orientation.VERTICAL));
		hBoxControlArea.getChildren().add(labelDelay);
		hBoxControlArea.getChildren().add(sliderDelay);
		hBoxControlArea.getChildren().add(new Separator(Orientation.VERTICAL));
		hBoxControlArea.getChildren().add(buttonLoad);
		hBoxControlArea.getChildren().add(buttonOpen);
		hBoxControlArea.setSpacing(5);
		hBoxControlArea.setAlignment(Pos.CENTER_LEFT);
		hBoxControlArea.setPadding(new Insets(5, 5, 0, 5));
		
		hBoxState.getChildren().add(labelCurrentState);
		hBoxState.getChildren().add(labelAcceptedState);
		hBoxState.getChildren().add(checkBoxAcceptedState);
		hBoxState.setSpacing(5);
		hBoxState.setPadding(new Insets(5));
		
		vBoxViewArea.getChildren().add(hBoxState);
		vBoxViewArea.getChildren().add(multiTapeControl);
		vBoxViewArea.getChildren().add(statusText);
		
		hBoxLower.getChildren().add(vBoxViewArea);
		hBoxLower.getChildren().add(textAreaMachineDef);
		hBoxLower.setSpacing(5);
		hBoxLower.setPadding(new Insets(5));
		
		vBoxRoot.getChildren().add(hBoxControlArea);
		vBoxRoot.getChildren().add(hBoxLower);
		vBoxRoot.setSpacing(5);
		vBoxRoot.setPadding(new Insets(5));
		
	}
	
	/** Creates all EventHandlers for the Gui-elements. */
	private void setupListeners(){
		
		buttonStep.setOnAction(new EventHandler<ActionEvent>(){
			@Override
			public void handle(ActionEvent event) {
				onButtonStep(event);
			}
		});
		buttonStart.setOnAction(new EventHandler<ActionEvent>(){
			@Override
			public void handle(ActionEvent event) {
				onButtonStart(event);
			}
		});

		buttonStop.setOnAction(new EventHandler<ActionEvent>(){
			@Override
			public void handle(ActionEvent event) {
				onButtonStop(event);
			}
		});

		buttonLoad.setOnAction(new EventHandler<ActionEvent>(){
			@Override
			public void handle(ActionEvent event) {
				onButtonLoad(event);
			}
		});

		buttonOpen.setOnAction(new EventHandler<ActionEvent>(){
			@Override
			public void handle(ActionEvent event) {
				onButtonOpen(event);
			}
		});

		sliderDelay.valueProperty().addListener(new ChangeListener<Number>(){
			@Override
			public void changed(ObservableValue<? extends Number> observable, Number oldValue, 
					Number newValue) {
				onSliderDelay(observable, oldValue, newValue);
			}
			
		});
		
	}

	/** This method is called whenever the Button "Step" is clicked. It performs a single
	 * transition in the machine. */
	private void onButtonStep(ActionEvent event){
		if(machine == null){
			addStatusText("ERROR: Can't Step since no machine is loaded.");
		}else{
			step();
		}
	}

	/** This method is called whenever the Button "Start" is clicked. It starts the ongoing
	 * execution of steps until an accepted state has been reached in the machine.*/
	private void onButtonStart(ActionEvent event){
		if(machine == null){
			addStatusText("ERROR: Can't Start since no machine is loaded.");
			return;
		}
		if(machine.isInAcceptedState()){
			addStatusText("ERROR: Can't Start since the machine already is in an accepted state");
		}
			
		timeline.play();
		
	}

	/** this method is called whenever the Button "Stop" is clicked. It stops the running execution
	 * of the machine if possible. */
	private void onButtonStop(ActionEvent event){
		if(machine == null){
			addStatusText("ERROR: Can't Stop since no machine is loaded.");
		}else{
			timeline.stop();
		}

	}

	/** This method is called whenever the Button "Load" is clicked. It uses the machine-definition
	 * from the appropriate TextArea to try to create a machine based on that definition. */
	private void onButtonLoad(ActionEvent event){
		timeline.stop();
		try{
			machine = TuringMachineReader.createMachine(textAreaMachineDef.getText());
		}catch(Exception e){
			machine = null;
			addStatusText("ERROR: Invalid machine configuration: " + e.getMessage());
			return;
		}

		multiTapeControl.setTapes(machine.getTapes());
		addStatusText("Machine loaded...");
		labelCurrentState.setText("Current State: " + machine.getCurrentState());
		if(machine.isInAcceptedState()){
			checkBoxAcceptedState.selectedProperty().set(true);
			addStatusText("Accepted state reached.");
		}
		else
			checkBoxAcceptedState.selectedProperty().set(false);
		
	}

	/** This method is called whenever the Button "Open" is clicked. It shows a file selection
	 * dialog and loads the file's content into the appropriate TextArea */
	private void onButtonOpen(ActionEvent event){
		timeline.stop();
		FileChooser fc = new FileChooser();
		File file = fc.showOpenDialog(stage);
		if(file == null)
			return;
		FileInputStream fis;
		try{
			fis = new FileInputStream(file);
		}catch(FileNotFoundException e){
			addStatusText("ERROR: File not found.");
			return;
		}
		String data = "";
		try {
			data = Utilities.getFileContentAsString(fis);
		} catch (IOException e) {
			addStatusText("ERROR: Error reading file: " + e.getMessage());
		}
		
		textAreaMachineDef.textProperty().set(data);
		
	}
	
	/** This method is called every time the slider has been moved/changed. */
	private void onSliderDelay(ObservableValue<? extends Number> observable, Number oldValue, 
			Number newValue){
		labelDelay.setText("Delay: " + newValue.intValue());
		timeline.setRate(DEFAULT_DELAY / newValue.doubleValue());

	}
	
	/** This method is called periodically after the Button "Start" has been clicked. */
	private void machineStepLoop(){
		if(!machine.isInAcceptedState())
			step();
		else
			timeline.stop();
	}
	
	/** Performs a single transition on the machine. Handles all possible errors. */
	private void step(){
		State<MultiTapeReadWriteData<Character>, TuringTransitionOutput<Character>> 
			stateBeforeTransition = machine.getCurrentState();
		//no start-state set
		try{
			machine.getTransition(machine.getTapes().read());
		}catch(IllegalStateException e){
			addStatusText("ERROR: Can't transit: No start-state has been set");
			return;
		}
		//in accepted state
		try{
			machine.transit();
		}catch(IllegalStateException e){
			addStatusText("ERROR: Can't transit: The machine already is in an accepted state.");
			return;
		}catch(IllegalArgumentException e){
			addStatusText("ERROR: Can't transit: There is no transition from state '" + 
					machine.getCurrentState() + "' with input '" + machine.getTapes().read() + 
					"'.");
			return;
		}
		
		afterTransition(stateBeforeTransition.toString());
	
	}
	
	/** This method should be called after every transition as it does all the necessary things
	 * post-transition. */
	private void afterTransition(String stateBeforeTransition){
		addStatusText(stateBeforeTransition + " -> " + machine.getCurrentState());
		if(machine.isInAcceptedState()){
			checkBoxAcceptedState.selectedProperty().set(true);
			addStatusText("Accepted state reached.");
		}
		
		labelCurrentState.setText("Current State: " + machine.getCurrentState());
		
	}
	
	/** Adds a new status message to the statusText-TextArea */
	private void addStatusText(String data){
		statusTextString = data + "\n" + statusTextString;
		statusText.setText(statusTextString);
	}
	
	
	
	
	
	
	
}
