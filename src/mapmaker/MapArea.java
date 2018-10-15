package mapmaker;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.stream.Collectors;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventTarget;
import javafx.scene.Node;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Shape;
import java.util.List;
import java.util.Map;
import mapmaker.PolyShape;

public class MapArea extends Pane {

	ArrayList<Polygon> list = new ArrayList<>();
	private PolyShape activeShape;
	private double sx, sy;// start y coordinate
	private ObservableList<Node> children;
	private ToolState tool;
	private SelectionArea sa;
	Pane pane = new Pane();
	public ObservableList<ControlPoint> selectionList = FXCollections.observableList(new LinkedList<ControlPoint>());
	public ObservableList<PolyShape> shapeList = FXCollections.observableList(new LinkedList<PolyShape>());
	private Line currentLine;
	
	
	public MapArea() {
		super();
		tool = ToolState.getState();
		children = this.getChildren();
		registerMouseEvents();
		sa = new SelectionArea();

	}

	public void registerMouseEvents() {
		addEventHandler(MouseEvent.MOUSE_PRESSED, (e) -> pressed(e));
		addEventHandler(MouseEvent.MOUSE_DRAGGED, (e) -> dragged(e));
		addEventHandler(MouseEvent.MOUSE_RELEASED, (e) -> released(e));

	}

	public void pressed(MouseEvent e) {
		e.consume();
		sx = e.getX();
		sy = e.getY();

		switch (tool.getTool()) {
		case DOOR:
			break;
		case MOVE:
			break;
		case PATH:
			break;
		case SELECT:
			sa.start(sx, sy);
			children.add(sa);
			break;
		case ERASE:
			break;
		case ROOM:
				activeShape = new PolyShape(tool.getOption());
				System.out.println(tool.getOption());
				children.add(activeShape);
			break;
		default:
			
			throw new UnsupportedOperationException(
					"Cursor for Tool \"" + activeTool().name() + "\" is not implemneted");
		}
	}

	public void dragged(MouseEvent e) {
		e.consume();
		switch (tool.getTool()) {
		case DOOR:
			break;
		case MOVE:
				EventTarget t = e.getTarget();
			if (selectionList.isEmpty()) {
				if (t instanceof PolyShape) {
					((PolyShape) t).translate(e.getX() - sx, e.getY() - sy);
				} else if (ControlPoint.class.isInstance(t)) {
					((ControlPoint) t).translate(e.getX() - sx, e.getY() - sy);
					ControlPoint.class.cast(t).translate(e.getX() - sx, e.getY() - sy);
				}
			} else {
				for (ControlPoint controlPoint : selectionList) {
					controlPoint.translate(e.getX() - sx, e.getY() - sy);
				}
			}
			sx = e.getX();
			sy = e.getY();
			
			break;
		case PATH:
			break;
		case SELECT:
			sa.end(e.getX(), e.getY());
			break;
		case ERASE:
			break;
		case ROOM:

			if (tool.getOption() == 12) {
				((PolyShape) activeShape).getPoints().setAll(sx, sy, // top left
						e.getX(), sy, // top right
						(e.getX()), (e.getY()), // bottom right
						sx, e.getY() // bottom left
				);
			} else {
				activeShape.reDraw(sx, sy, e.getX(), e.getY(), true);
			}
			break;
		default:
			throw new UnsupportedOperationException("Drag for Tool \"" + activeTool().name() + "\" is not implemneted");
		}
	}

	public void released(MouseEvent e) {
		e.consume();
		switch (tool.getTool()) {
		case DOOR:
			break;
		case MOVE:
		case PATH:
			break;
		case SELECT:
			children.remove(sa);

			shapeList.forEach((PolyShape) -> {
				PolyShape.setFill(Color.ALICEBLUE);
			});
			selectionList.forEach((CounterPoint) -> {
				CounterPoint.setFill(Color.PALEVIOLETRED);
			});
			selectionList.clear();

			sa.containsAny(children, (t) -> {
				if (ControlPoint.class.isInstance(t)) {

					selectionList.add((ControlPoint) t);
					((Shape) t).setFill(Color.BLACK);
				}

				if (PolyShape.class.isInstance(t)) {

					shapeList.add((PolyShape) t);
					((Shape) t).setFill(Color.BLACK);
				}
			}); 

			sa.clear();
			System.out.println(selectionList);
			break;
		case ERASE:
			EventTarget t = e.getTarget();
//			if (t instanceof ControlPoint) {
//				ControlPoint s = (PolyShape) t;
//				super.getChildren().remove(selectionList.contains(selectionList));
//				selectionList.remove((ControlPoint) t);
//				children.remove(t);
//			}
			if (t instanceof PolyShape) {
				PolyShape s = (PolyShape) t;
				super.getChildren().removeAll(s.getControlPoints());
				children.remove(t);
//			}else if (t instanceof ControlPoint){
//				ControlPoint s = (ControlPoint) t;
//				super.getChildren().removeAll(activeShape.getControlPoints());
//				super.getChildren().removeAll(children);
//				children.remove(t);
			}
			break;
		case ROOM:
			if (tool.getOption() == 12) {
				((PolyShape) activeShape).registerControlPoints(); // **
				children.addAll(((PolyShape) activeShape).getControlPoints());
				break;
			}

			activeShape.registerControlPoints();
			children.addAll((activeShape).getControlPoints());
			break;
		default:
			throw new UnsupportedOperationException("Drag for Tool \"" + activeTool().name() + "\" is not implemneted");
		}
		System.out.println("X:" + e.getX());
		System.out.println("Y:" + e.getY());

	}

	public void reset() {
		getChildren().clear();
		selectionList.clear();
	}

	private Tool activeTool() {
		return tool.getTool();
	}

	public String convertToString() {
		return children.stream().filter(PolyShape.class::isInstance).map(PolyShape.class::cast)
				.map(PolyShape::convertToString).collect(Collectors.joining(System.lineSeparator()));
	}

	public void convertFromString(Map<Object, List<String>> map) {
		map.keySet().stream().map(k -> new PolyShape(map.get(k))).forEach(s -> {
			children.add(s);
			children.addAll(s.getControlPoints());
		});

	}

}
