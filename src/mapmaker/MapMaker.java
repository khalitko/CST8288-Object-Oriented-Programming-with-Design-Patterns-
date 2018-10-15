package mapmaker;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;

import javafx.geometry.Orientation;

import javafx.geometry.Side;

import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Separator;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.control.TextInputDialog;
import javafx.scene.control.ToolBar;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.BorderPane;


import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.FileChooser.ExtensionFilter;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class MapMaker<T> extends Application {
	
	private final String INFO_PATH = "resources/icons/info.txt";
	private final String HELP_PATH = "resources/icons/help.txt";
	private final String CREDITS_PATH = "resources/icons/credits.txt";
	private final Label selectedTool = new Label("Tool: ");
	
	public static final String REGEX_DECIMAL = "-?(([1-9][0-9]*)|0)?(\\.[0-9]*)?";
	public static final String REGEX_POSITIVE_INTEGER = "([1-9][0-9]*)";
	public static final Pattern P = Pattern.compile( REGEX_POSITIVE_INTEGER);
	public static final String MAPS_DIRECTORY = "resources/maps";
		
	private MapArea map;

		
	@Override
    public void init() throws Exception {
        super.init();
        System.out.println("Inside init() method! Perform necessary initializations here.");
    }
	
	@Override
	public void start(Stage primaryStage) throws Exception {
		
		BorderPane rootPane = new BorderPane();
		
		map = new MapArea();

		MenuBar menuBar = new MenuBar(
				new Menu("File", null, 
						createMenuItem("New",    (e)-> map.reset()), 
						createMenuItem("Open",   (e)-> loadMap(primaryStage)), 
						createMenuItem("Save",   (e)-> saveMap(primaryStage)), 						
						new SeparatorMenuItem(), 
						createMenuItem("Exit",   (e)-> Platform.exit())),
				new Menu("Help", null, 
						createMenuItem("Credit", (e)-> displayCredit()), 
						createMenuItem("Info",   (e)-> displayInfo()), 
						new SeparatorMenuItem(), 
						createMenuItem("Help",   (e)-> displayHelp()))
				);
		
			Label mousePositionToolTip = new Label("");
		    rootPane.setOnMouseMoved(new EventHandler<MouseEvent>() {
		        @Override
		        public void handle(MouseEvent e) {
		            String msg = "(x: " + e.getX() + ", y: " + e.getY() + ")";
		            mousePositionToolTip.setText(msg);
		        }
		    });
		    
		menuBar.setId("MenuBar");

        ToolBar statusBar = new ToolBar(
						selectedTool, 
						new Separator(),
						new Label("Options: {}"),
						new Label("Mouse Coord"),
						mousePositionToolTip
         				);
        
        
        statusBar.setPrefWidth(rootPane.getWidth());
                 		
		ToolBar toolsBar = new ToolBar(
        				createToolButton("Select", (e)-> setTool(Tool.SELECT, "Select")),
        				createToolButton("Move",   (e)-> setTool(Tool.MOVE, "Move")),
        				roomMenuItems(		   (e)-> setTool(Tool.ROOM, "Room")),
        				createToolButton("Path",   (e)-> setTool(Tool.PATH, "Path")),
        				createToolButton("Erase",  (e)-> setTool(Tool.ERASE, "Erase")),
        				createToolButton("Door",   (e)-> setTool(Tool.DOOR, "Door"))
        				);
        toolsBar.setOrientation(Orientation.VERTICAL);
        toolsBar.setId("ToolBar");
        

        rootPane.setCenter(map);
        rootPane.setTop(menuBar);
        rootPane.setBottom(statusBar);
        rootPane.setLeft(toolsBar);
        
		Scene rootScene = new Scene(rootPane, 1000, 800);
		rootScene.getStylesheets().add(new File ("resources/css/style.css").toURI().toString());
		primaryStage.addEventHandler(KeyEvent.KEY_RELEASED, e -> {
			if (e.getCode() == KeyCode.ESCAPE)
				primaryStage.hide();
		});
		primaryStage.setScene(rootScene);
		primaryStage.setTitle("Map Maker");
		
		primaryStage.show();

	}
		
	private MenuItem createMenuItem(String name, EventHandler<ActionEvent> handler) {
		Label icon = new Label();
		icon.setId(name + "-icon");
		MenuItem item = new MenuItem(name, icon);
		item.setId(name);
		item.addEventHandler(ActionEvent.ACTION,handler);
		return item;
	}
	
	private void setTool(Tool t, String name) {
		ToolState.getState().setTool(t);
		selectedTool.setText("Tool: " + name);
	}
	
	private void setItemTool(String name, int option) {
		ToolState.getState().setTool(Tool.ROOM);
		selectedTool.setText("Tool: Room: " + name);
		ToolState.getState().setOption(option);
	}
	  
	private MenuButton roomMenuItems(EventHandler<ActionEvent> handler){

		Label roomIcon = new Label();
		roomIcon.setId("Room-icon");
        MenuButton roomItems = new MenuButton("", roomIcon,
        	createMenuButton("Line",      (e)->setItemTool("Line",       2)),
        	createMenuButton("Triangle",  (e)->setItemTool("Triangle",   3)),
        	createMenuButton("Square",    (e)->setItemTool("Square",     4)),
          	createMenuButton("Rectangle", (e)->setItemTool("Rectangle", 12)),
        	createMenuButton("Pentagon",  (e)->setItemTool("Pentagon",   5)),
        	createMenuButton("Hexagon",   (e)->setItemTool("Hexagon",    6)),
        	createMenuButton("Heptagon",  (e)->setItemTool("Heptagon",   7)),
        	createMenuButton("Octagon",   (e)->setItemTool("Octagon",    8)),
        	createMenuButton("Nonagon",   (e)->setItemTool("Nonagon",    9)),
        	createMenuButton("Decagon",   (e)->setItemTool("Decagon",   10)));
        roomItems.setId("Room");
        roomItems.setPopupSide(Side.RIGHT);
//        roomItems.
        roomItems.setOnAction(handler);
        return roomItems;
}
	
    private MenuItem createMenuButton(String name, EventHandler<ActionEvent> handler) {
    	MenuItem menuItem = new MenuItem(name);
    	menuItem.setId(name);
    	menuItem.addEventHandler(ActionEvent.ACTION,handler);
        return menuItem;
    }
		
	
	private Button createToolButton(String name, EventHandler<ActionEvent> handler) {
		Label toolLabel = new Label();
		toolLabel.setId(name + "-icon");
		Button item = new Button("", toolLabel);
		item.setId(name);
		item.addEventHandler(ActionEvent.ACTION,handler);
		return item;
	}
	

	
	private void displayAlert(String title, String context) {
		Alert alert = new Alert(AlertType.INFORMATION);
		alert.setTitle(title);
		alert.setContentText(context);
		alert.show();
	}
	
	private void displayCredit() {
		displayAlert("Credit", loadFile(CREDITS_PATH));
	}
	
	private void displayHelp() {
		displayAlert("Help", loadFile(HELP_PATH));
	}
	private void displayInfo() {
		displayAlert("Info", loadFile(INFO_PATH));
	}
	
	@Override
	public void stop() throws Exception {
		super.stop();
	}
	
	private String loadFile( String path) {
		String message = "";
		try {
			message = Files.lines( Paths.get(path)).reduce("", (a,b)->a+System.lineSeparator()+b+System.lineSeparator());
		} catch (IOException e) {
			e.printStackTrace();
		}
		return message;
	}
		
	private void showInputDialog( String title, String content, String match, Consumer<String> action){
		TextInputDialog input = new TextInputDialog();
		input.setTitle( title);
		input.setHeaderText( null);
		input.setContentText( content);
		input.getEditor().textProperty().addListener( (value, oldV, newV)->{
			if(!newV.isEmpty() && !Pattern.matches( match, newV)){
				input.getEditor().setText( oldV);
			}
		});
		input.showAndWait().ifPresent(e->{if(e.matches( match))action.accept( e);});
	}

	private String loadFile( String path, String seprator){
		try{
			return Files.lines( Paths.get( path)).reduce( "", ( a, b) -> a + seprator + b);
		}catch( IOException e){
			e.printStackTrace();
			return "\"" + path + "\" was probably not found" + "\nmessage: " + e.getMessage();
		}
	}

	private void saveMap( Stage primary){
		File file = getFileChooser( primary, true);
		if (file==null)
			return;
		try{
			if( !file.exists())
				file.createNewFile();
			Files.write( file.toPath(), map.convertToString().getBytes());
		}catch( IOException e){
			e.printStackTrace();
		}
	}

	private void loadMap( Stage primary){
		File file = getFileChooser( primary, false);
		if (file==null)
			return;
		try{
			if( !file.exists())
				file.createNewFile();
			//no parallel here but this is safer
			AtomicInteger index = new AtomicInteger(0);  
			//index.getAndIncrement()/5 every 5 elements the value division increases by 1
			//allowing for every 5 element placed in the same key
			map.convertFromString( Files.lines( file.toPath()).collect( Collectors.groupingBy( l->index.getAndIncrement()/5)));
		}catch( IOException e){
			e.printStackTrace();
		}
	}
	
	private File getFileChooser( Stage primary, boolean save){
		FileChooser fileChooser = new FileChooser();
		fileChooser.getExtensionFilters().add( new ExtensionFilter( "Maps", "*.map"));
		fileChooser.setInitialDirectory( Paths.get( MAPS_DIRECTORY).toFile());
		return save?fileChooser.showSaveDialog( primary):fileChooser.showOpenDialog( primary);
	}

	public static void main(String[] args) {
		launch(args);
	}

}
