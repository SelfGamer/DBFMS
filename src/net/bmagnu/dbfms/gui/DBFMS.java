package net.bmagnu.dbfms.gui;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

public class DBFMS extends Application {

	GUIMainWindow mainGui;
	
	public static final Image icon = new Image(DBFMS.class.getResourceAsStream("icon.png"));
	
	@Override
	public void start(Stage stage) throws Exception {
		FXMLLoader loader = new FXMLLoader(getClass().getResource("main_window.fxml"));

		Parent root = loader.load();
	    
        Scene scene = new Scene(root, 1600, 900);
    
        stage.setMinWidth(1000);
        stage.setMinHeight(1000.0f * 9.0f / 16.0f);
        stage.setTitle("DBFMS");
        stage.getIcons().add(icon);
        stage.setScene(scene);
        stage.show();
        
        mainGui = loader.getController();
        mainGui.init();
	}

}
