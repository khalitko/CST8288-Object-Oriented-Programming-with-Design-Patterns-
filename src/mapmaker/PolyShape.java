package mapmaker;

import java.util.Arrays;
import java.util.function.DoubleUnaryOperator;

import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Polygon;
import java.util.List;
import java.util.function.DoubleUnaryOperator;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Polygon;


/**
 * http://dimitroff.bg/generating-vertices-of-regular-n-sided-polygonspolyhedra-and-circlesspheres/
 * 
 * @author Shahriar (Shawn) Emami
 * @version Sep 27, 2018
 */
public class PolyShape extends Polygon {

	private final ObservableList<Double> POLY_POINTS;

	private int sides;
	private double angle;
	private double dx, dy;
	private double x1, y1;
	private static final String POINTS_COUNT = "sides";
	private static final String FILL = "fill";
	private static final String STROKE = "stroke";
	private static final String WIDTH = "strokeWidth";
	private static final String POINTS = "points";

	private ControlPoint[] cPoints;

	public PolyShape(int sides) {
		super();
		POLY_POINTS = getPoints();
		this.sides = sides;
		setStrokeWidth( 2);
		setStroke(Color.AQUA);
		setFill(Color.ALICEBLUE);
				
	}
	public PolyShape( List< String> list){
		this();
		convertFromString( list);
		registerControlPoints();
	}

	private PolyShape(){
		super();
		POLY_POINTS = getPoints();
	}
	
	private void calculatePoints() {
		for (int side = 0; side < sides; side++) {
			POLY_POINTS.addAll(
					point(Math::cos, dx / 2, angle, side, sides) + x1,
					point(Math::sin, dy / 2, angle, side, sides) + y1);
		}
	}

	private double radianShift(double x1, double y1, double x2, double y2) {
		return Math.atan2(y2 - y1, x2 - x1);
	}

	private double point(DoubleUnaryOperator operation, double radius, double shift, double side, final int SIDES) {
		return radius * operation.applyAsDouble(shift + side * 2.0 * Math.PI / SIDES);
	}

	public void registerControlPoints() {
		
		this.cPoints = new ControlPoint[this.POLY_POINTS.size() / 2]; // or sides or Cpoint

		for (int i = 0; i < POLY_POINTS.size(); i += 2) { // use ppoints.size to increase correctly for increment jump
															// ever 2
			final int j = i;
			cPoints[i / 2] = new ControlPoint(POLY_POINTS.get(i), POLY_POINTS.get(i + 1));
			
			this.cPoints[i / 2].addChangeListener(
					(value, oldV, newV) -> POLY_POINTS.set(j, newV.doubleValue()),
					(value, oldV, newV) -> POLY_POINTS.set(j + 1, newV.doubleValue())
					);
		}
	}

	/**
	 * measure the distance between 2 points
	 */
	private double distance(double x1, double y1, double x2, double y2) {
		return Math.sqrt((x2 - x1) * (x2 - x1) + (y2 - y1) * (y2 - y1));
	}

	/**
	 * redraw the shape without the need to remake it. till all redrawing are done
	 * registerControlPoints should not be called.
	 */
	public void reDraw(double x1, double y1, double x2, double y2, boolean symmetrical) {
		angle = radianShift(x1, y1, x2, y2);
		if (symmetrical) {
			dx = distance(x1, y1, x2, y2);
			dy = dx;
		} else {
			dx = x2-x1;
			dy = y2-y1;
		}
		this.x1 = x1 + ((x2-x1) / 2);
		this.y1 = y1 + ((y2-y1) / 2);
		POLY_POINTS.clear();
		calculatePoints();
	}

	// using radianShift to measure the drawing angle
	// if shape is symmetrical measure the distance between x1,y1 and x2,y2 and
	// assign it to dx and dy
	// if not dx is difference between x1 and x2 and dy is difference between y1 and
	// y2
	// calculate the center of your shape:
	// x1 is x1 plus half the difference between x1 and x2
	// y1 is y1 plus half the difference between y1 and y2
	// clear points
	// call calculate

	public Node[] getControlPoints() {
		return cPoints;
	}
	
	public void translate(double dx, double dy) {
		for (ControlPoint controlPoint : cPoints) {
			controlPoint.translate(dx, dy);
		}
	}
	
	public String convertToString(){
		String newLine = System.lineSeparator();
		StringBuilder builder = new StringBuilder();
		builder.append( POINTS_COUNT).append( " ").append( sides).append( newLine);
		builder.append( FILL).append( " ").append( colorToString( getFill())).append( newLine);
		builder.append( STROKE).append( " ").append( colorToString( getStroke())).append( newLine);
		builder.append( WIDTH).append( " ").append( getStrokeWidth()).append( newLine);
		builder.append( POINTS).append( " ").append( POLY_POINTS.stream().map( e -> Double.toString( e)).collect( Collectors.joining( " ")));

		return builder.toString();
	}

	private void convertFromString( List< String> list){
		list.forEach( line -> {
			String[] tokens = line.split( " ");
			switch( tokens[0]){
				case POINTS_COUNT:
					sides = Integer.valueOf( tokens[1]);
					break;
				case FILL:
					setFill( stringToColor( tokens[1], tokens[2]));
					break;
				case STROKE:
					setStroke( stringToColor( tokens[1], tokens[2]));
					break;
				case WIDTH:
					setStrokeWidth( Double.valueOf( tokens[1]));
					break;
				case POINTS:
					Stream.of( tokens).skip( 1).mapToDouble( Double::valueOf).forEach( POLY_POINTS::add);
					break;
				default:
					throw new UnsupportedOperationException( "\"" + tokens[0] + "\" is not supported");
			}
		});
	}


	private String colorToString( Paint p){
		return colorToString( Color.class.cast( p));
	}

	private String colorToString( Color c){
		return String.format( "#%02X%02X%02X %f",
				(int) (c.getRed() * 255),
				(int) (c.getGreen() * 255),
				(int) (c.getBlue() * 255),
				c.getOpacity());
	}

	private Color stringToColor( String color, String alpha){
		return Color.web( color, Double.valueOf( alpha));
	}
	
}
