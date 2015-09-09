package turingMachine.fxgui;

import javafx.scene.control.ListCell;
import javafx.scene.paint.Color;

class TapeCell<T> extends ListCell<T> {

	public TapeCell() { }

	@Override
	protected void updateItem(T item, boolean empty) {
		super.updateItem(item, empty);
		setText(item == null ? "_" : item.toString());

		// change the text fill based on whether it is positive (green)
		// or negative (red). If the cell is selected, the text will
		// always be white (so that it can be read against the blue
		// background), and if the value is zero, we'll make it black.
		if (item == null) {
			setTextFill(Color.GRAY);
		} else {
			setTextFill(Color.BLACK);
		}
	}
}
