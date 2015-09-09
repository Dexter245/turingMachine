package turingMachine.fxgui;

import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.geometry.Orientation;
import javafx.scene.Node;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.Skin;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.util.Callback;

class TapeSkin<T> implements Skin<TapeControl<T>> {

	private TapeControl<T> control;
	private VBox root = new VBox();
	private ListView<T> tapeCells;

	public TapeSkin(TapeControl<T> control) {
		this.control = control;
		draw();
	}

	@Override
	public void dispose() {
		control = null;
		root = null;
	}

	@Override
	public Node getNode() {
		return root;
	}

	@Override
	public TapeControl<T> getSkinnable() {
		return control;
	}

	public ObservableList<T> getItems() {
		return tapeCells.getItems();
	}

	private void draw() {
		tapeCells = new ListView<>();

		tapeCells.setOrientation(Orientation.HORIZONTAL);
		tapeCells.setMaxHeight(50);

		tapeCells.addEventFilter(MouseEvent.MOUSE_PRESSED, changeByMouseEvent);
		tapeCells.addEventFilter(KeyEvent.KEY_PRESSED, changeByKeyboardEvent);

		tapeCells.setCellFactory(new Callback<ListView<T>, ListCell<T>>() {
			@Override
			public ListCell<T> call(ListView<T> list) {
				return new TapeCell<T>();
			}
		});

		root.getChildren().setAll(new Text(control.getTitle()), tapeCells);
	}

	public void updatePosition(int position) {
		tapeCells.getSelectionModel().clearAndSelect(position);
	}

	private final EventHandler<KeyEvent> changeByKeyboardEvent = new EventHandler<KeyEvent>() {
		@Override
		public void handle(final KeyEvent event) {
			if (event.getCode().isArrowKey()) {
				// do something if you want
				event.consume();
			}
		}
	};

	private final EventHandler<MouseEvent> changeByMouseEvent = new EventHandler<MouseEvent>() {
		@Override
		public void handle(final MouseEvent event) {
			event.consume();
		}
	};
}
