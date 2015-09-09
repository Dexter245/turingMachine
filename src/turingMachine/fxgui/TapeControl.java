package turingMachine.fxgui;

import java.util.List;

import javafx.scene.control.Control;

import turingMachine.tape.Direction;
import turingMachine.tape.Tape;
import turingMachine.tape.TapeChangeListener;

class TapeControl<T> extends Control implements TapeChangeListener<T> {

	private Tape<T> tape;
	private String title;

	public TapeControl(Tape<T> tape, String title) {
		this.tape = tape;
		this.title = title;

		setSkin(new TapeSkin<T>(this));

		List<T> contents = tape.getContents();
		getSkinT().getItems().setAll(contents);
		getSkinT().updatePosition(tape.getPosition());
		tape.addListener(this);
	}

	@SuppressWarnings("unchecked")
	private TapeSkin<T> getSkinT() {
		return (TapeSkin<T>) getSkin();
	}


	public Tape<T> getTape() {
		return tape;
	}

	public String getTitle() {
		return title;
	}

	@Override
	public void onMove(Direction direction) {
		getSkinT().updatePosition(tape.getPosition());
	}

	@Override
	public void onWrite(T value) {
		getSkinT().getItems().set(tape.getPosition(), value);
	}

	@Override
	public void onExpand(Direction direction) {
		if(direction == Direction.RIGHT) {
			getSkinT().getItems().add(null);
		} else if (direction == Direction.LEFT) {
			getSkinT().getItems().add(0, null);
		}
	}
}
