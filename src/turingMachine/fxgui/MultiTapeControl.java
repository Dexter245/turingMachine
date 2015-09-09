package turingMachine.fxgui;

import javafx.scene.control.Control;

import turingMachine.tape.MultiTape;

class MultiTapeControl<T> extends Control {
	private MultiTape<T> tapes;

	public MultiTapeControl() {
		this(null);
	}

	public MultiTapeControl(MultiTape<T> tapes) {
		if (tapes != null)
			setTapes(tapes);

		setSkin(new MultiTapeViewSkin<T>(this));
	}

	public MultiTape<T> getTapes() {
		return tapes;
	}

	@SuppressWarnings("unchecked")
	public void setTapes(MultiTape<T> tapes) {
		this.tapes = tapes;
		((MultiTapeViewSkin<T>) getSkin()).update();
	}
}
