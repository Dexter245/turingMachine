package turingMachine.fxgui;

import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.Skin;
import javafx.scene.layout.VBox;

import turingMachine.tape.Tape;

class MultiTapeViewSkin<T> implements Skin<MultiTapeControl<T>> {

	private MultiTapeControl<T> control;
	private VBox root = new VBox();

	public MultiTapeViewSkin(MultiTapeControl<T> control) {
		this.control = control;
		update();
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
	public MultiTapeControl<T> getSkinnable() {
		return control;
	}

	public void update() {
		System.out.println("Updating");
		ObservableList<Node> children = root.getChildren();
		children.clear();
		if(control.getTapes() == null) return;

		int count = 1;
		for (Tape<T> tape : control.getTapes().getTapes()) {
			System.out.println("Adding Tape view");
			children.add(new TapeControl<T>(tape, String.format("Band %d", count)));
			count++;
		}
	}
}
