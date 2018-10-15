package mapmaker;

import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;

import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;

import javafx.scene.shape.Circle;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Shape;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.FileChooser.ExtensionFilter;

public class FileNewSave {
	private PolyShape ps;
	private File currentFile;
	private MapArea map;
	public static final String MAPS_DIRECTORY = "resources/maps";
	
	public void saveMap(final PolyShape map) {
		if (currentFile != null) {
			try {
				new ObjectOutputStream(new FileOutputStream(currentFile)).writeObject(map);
			} catch (final IOException e) {
				e.printStackTrace();
			}
		} else {
//			saveMapAs();
		}
	}
	
	public void saveMapAs(Stage primary) {
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
	
	private File getFileChooser( Stage primary, boolean save){
		FileChooser fileChooser = new FileChooser();
		fileChooser.getExtensionFilters().add( new ExtensionFilter( "Maps", "*.map"));
		fileChooser.setInitialDirectory( Paths.get( MAPS_DIRECTORY).toFile());
		return save?fileChooser.showSaveDialog( primary):fileChooser.showOpenDialog( primary);
	}

	public PolyShape loadMap() {
		final JFileChooser fc = new JFileChooser();
		fc.setAcceptAllFileFilterUsed(false);
		fc.setFileFilter(new FileNameExtensionFilter("Map files (*.map)", "map"));
		if (fc.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
			try {
				currentFile = fc.getSelectedFile();
				final PolyShape map = (PolyShape) new ObjectInputStream(new FileInputStream(fc.getSelectedFile()))
						.readObject();
				return map;
			} catch (final ClassNotFoundException e) {
				e.printStackTrace();
			} catch (final IOException e) {
				e.printStackTrace();
			}
		}
		return null;
	}
}
