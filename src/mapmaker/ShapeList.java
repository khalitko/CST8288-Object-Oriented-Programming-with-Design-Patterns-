package mapmaker;

import javafx.scene.Node;
import javafx.scene.control.ListView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;

public class ShapeList extends VBox {
	
	private ListView<PolyShape> shapesList;
	private GridPane detailEditor;

	public ShapeList() {
		this.shapesList = new ListView<>();
		this.detailEditor = new GridPane();
		super.getChildren().addAll(shapesList, detailEditor);
	}

	public ListView<PolyShape> getShapesList() {
		return shapesList;
	}

	public void setShapesList(ListView<PolyShape> shapesList) {
		this.shapesList = shapesList;
	}

	public GridPane getDetailEditor() {
		return detailEditor;
	}

	public void setDetailEditor(GridPane detailEditor) {
		this.detailEditor = detailEditor;
	}

}
