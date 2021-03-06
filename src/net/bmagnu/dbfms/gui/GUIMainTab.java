package net.bmagnu.dbfms.gui;

import java.awt.Desktop;
import java.awt.Toolkit;
import java.awt.datatransfer.StringSelection;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.LinkedList;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.TilePane;
import javafx.scene.layout.VBox;
import javafx.util.Pair;
import net.bmagnu.dbfms.database.Collection;
import net.bmagnu.dbfms.database.DatabaseFileEntry;
import net.bmagnu.dbfms.database.DatabaseFileEntryComparator;
import net.bmagnu.dbfms.util.Logger;

public class GUIMainTab {

	@FXML 
	private TextField searchQueryField;
	
	@FXML
	private TilePane filePane;
	
	@FXML
	private ScrollPane fileScrollPane;
	
	@FXML
	private ComboBox<DatabaseFileEntryComparator.SortMode> sortModeBox;
	
	@FXML
	private Label labelPerformance;
	
	@FXML
	private VBox relevantTagBox;
	
	public Collection collection;
	
	private LinkedList<DatabaseFileEntry> files;
	private DatabaseFileEntryComparator sorter = new DatabaseFileEntryComparator();

	public void init(Collection collection) {
		this.collection = collection;
		
		filePane.setOnScroll((event) -> {
	            double deltaY = event.getDeltaY() * 2;
	            double width = fileScrollPane.getContent().getBoundsInLocal().getWidth();
	            double vvalue = fileScrollPane.getVvalue();
	            fileScrollPane.setVvalue(vvalue + -deltaY / width);
	    });
		
		sortModeBox.getItems().addAll(DatabaseFileEntryComparator.SortMode.values());
		sortModeBox.setValue(DatabaseFileEntryComparator.SortMode.SORT_ARBITRARY);
		sortModeBox.valueProperty().addListener((obs, oldV, newV) -> {
			sorter.mode = newV;
			displayFiles();
		});
		
		CTXMenuTagSuggestion.register(searchQueryField, this.collection);
		
		searchFiles("");
		displayFiles();
	}
	
	@FXML
	public void search_onSearch(ActionEvent event) {
		doQuery();
    }
	
	public void doQuery() {
		doQuery(searchQueryField.getText());
	}
	
	public void doQuery(String text) {
		long time1 = System.nanoTime(), time2, time3;
		searchFiles(text);
		
		time2 = System.nanoTime();
       
		displayFiles();
		
        time3 = System.nanoTime();
        
        String query = "Query Time: " + ((time2 - time1) / 1000000) + "ms, Display Time: " + ((time3 - time2) / 1000000) + "ms";
        labelPerformance.setText(query);
        Logger.logInfo(query);
	}
	
	public void searchFiles(String queryString) {
        files = new LinkedList<>(collection.queryFiles(queryString).values());
	}
	
	public void displayFiles() {
		files.sort(sorter);
		
        filePane.getChildren().clear();
        
        for(DatabaseFileEntry file : files) {
        	StackPane filePaneLocal = new StackPane();
        	ImageView fileThumb = new ImageView();
   
        	fileThumb.setImage(file.thumbnail.getImage());
        	fileThumb.setPreserveRatio(true);
        	fileThumb.setFitWidth(300);
        	fileThumb.setFitHeight(300);
        	
        	filePaneLocal.setAlignment(Pos.CENTER);
        	filePaneLocal.getChildren().add(fileThumb);  	
        	
        	filePaneLocal.setMinHeight(300);
        	filePaneLocal.setMinWidth(300);
        	
        	FXMLLoader loaderCtx = new FXMLLoader(getClass().getResource("ctxmenu_file.fxml"));
        	ContextMenu ctxMenu;
    		try {
    			ctxMenu = loaderCtx.load();
    			((CTXMenuFile) loaderCtx.getController()).init(this, file);
    		} catch (IOException e) {
    			Logger.logError(e);
    			continue;
    		}
        	
        	filePaneLocal.setOnMouseClicked((mouseEvent) -> {
        		if(mouseEvent.getButton() == MouseButton.PRIMARY) {
        			try {
        				if (file.filename.startsWith("http://") || file.filename.startsWith("https://"))
        					Desktop.getDesktop().browse(new URI(file.filename));
        				else
        					Desktop.getDesktop().open(new File(file.filename));
					} catch (IOException | URISyntaxException e) {
						Logger.logError(e);
					}
        		}
        		else if (mouseEvent.getButton() == MouseButton.SECONDARY) {
        			ctxMenu.show(filePaneLocal, mouseEvent.getScreenX(), mouseEvent.getScreenY());
        		}
        	});
        	
        	filePaneLocal.setStyle("-fx-border-color: black");

        	filePane.getChildren().add(filePaneLocal);
        }
        
        relevantTagBox.getChildren().clear();
        for (Pair<String, Integer> tag : collection.relevantTags(files)) {
        	StackPane pane = new StackPane();
        	VBox.setMargin(pane, new Insets(1.5, 7, 1.5, 7));
			Label tagL = new Label(tag.getKey());
			Label count = new Label(tag.getValue().toString());
			StackPane.setAlignment(tagL, Pos.CENTER_LEFT);
			StackPane.setAlignment(count, Pos.CENTER_RIGHT);
			pane.getChildren().addAll(tagL, count);
			pane.setOnMouseEntered(e -> {
				tagL.setStyle("-fx-underline: true;");
			});
			pane.setOnMouseExited(e -> {
				tagL.setStyle("-fx-underline: false;");
			});
			pane.setOnMouseClicked(mouseEvent -> {
				if(mouseEvent.getButton() == MouseButton.PRIMARY) 
					doQuery(tag.getKey());
				else
					Toolkit.getDefaultToolkit().getSystemClipboard().setContents(new StringSelection(tag.getKey()), null);
			});
			relevantTagBox.getChildren().add(pane);
        }
	}	
}

