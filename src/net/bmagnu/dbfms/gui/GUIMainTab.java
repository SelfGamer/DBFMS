package net.bmagnu.dbfms.gui;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.Map.Entry;

import javafx.beans.binding.Bindings;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Bounds;
import javafx.geometry.Pos;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.TilePane;
import javafx.util.Pair;

import net.bmagnu.dbfms.database.Collection;
import net.bmagnu.dbfms.util.Logger;
import net.bmagnu.dbfms.util.Thumbnail;

public class GUIMainTab {

	@FXML 
	private TextField searchQueryField;
	
	@FXML
	private TilePane filePane;
	
	@FXML
	private ScrollPane fileScrollPane;
	
	public Collection collection;
	
	private Map<Integer, Pair<String, Thumbnail>> files;
	
	public void init(Collection collection) {
		this.collection = collection;
		
		filePane.setOnScroll((event) -> {
	            double deltaY = event.getDeltaY() * 6;
	            double width = fileScrollPane.getContent().getBoundsInLocal().getWidth();
	            double vvalue = fileScrollPane.getVvalue();
	            fileScrollPane.setVvalue(vvalue + -deltaY / width);
	        
	    });
	}
	
	@FXML
	public void search_onSearch(ActionEvent event) {
        files = collection.queryFiles(searchQueryField.getText());
        
        filePane.getChildren().clear();
        
        for(Entry<Integer, Pair<String, Thumbnail>> file : files.entrySet()) {
        	StackPane filePaneLocal = new StackPane();
        	ImageView fileThumb = new ImageView();
   
        	fileThumb.setImage(file.getValue().getValue().loadImage());
        	fileThumb.setPreserveRatio(true);
        	fileThumb.setFitWidth(300);
        	fileThumb.setFitHeight(300);
        	
        	filePaneLocal.setAlignment(Pos.CENTER);
        	filePaneLocal.getChildren().add(fileThumb);
        	
        	//TODO Fix Unloading Images
        	/*fileThumb.imageProperty().bind(Bindings.createObjectBinding(() -> {
        		Bounds scrollBounds = fileScrollPane.localToScene(fileScrollPane.getBoundsInParent());
        		Bounds thumbBounds = fileThumb.localToScene(fileThumb.getBoundsInLocal());
        		if(scrollBounds.intersects(thumbBounds)) 
        			return file.getValue().getValue().loadImage();
        		else
        			return null;
        	}, filePane.boundsInLocalProperty(), fileScrollPane.boundsInParentProperty(), fileScrollPane.vvalueProperty()));
        	*/
        	
        	filePaneLocal.setMinHeight(300);
        	filePaneLocal.setMinWidth(300);
        	
        	fileThumb.setOnMouseClicked((mouseEvent) -> {
        		if(mouseEvent.getButton() == MouseButton.PRIMARY) {
        			try {
        				//TODO Correct Opening
						Desktop.getDesktop().edit(new File(file.getValue().getKey()));
					} catch (IOException e) {
						Logger.logError(e);
					}
        		}
        		else if (mouseEvent.getButton() == MouseButton.SECONDARY) {
        			//TODO Edit
        		}
        	});
        	
        	filePane.getChildren().add(filePaneLocal);
        }
    }
	
}
