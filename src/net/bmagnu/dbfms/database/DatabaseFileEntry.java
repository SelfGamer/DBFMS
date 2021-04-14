package net.bmagnu.dbfms.database;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileTime;

import net.bmagnu.dbfms.util.Thumbnail;

public class DatabaseFileEntry {
	
	public String filename;
	public Thumbnail thumbnail;
	public float rating;
	
	private FileTime lastModified = null;
	private FileTime created = null;
	
	private void populateAttrs() {
		try {
			Path file = Paths.get(filename);
			BasicFileAttributes attr = Files.readAttributes(file, BasicFileAttributes.class);
		
			created = attr.creationTime();
			lastModified = attr.lastModifiedTime();
		} catch (IOException e) {
			created = FileTime.fromMillis(0);
			lastModified = FileTime.fromMillis(0);
		}
	}
	
	public FileTime getLastModified() {
		if(lastModified == null) 
			populateAttrs();
		
		return lastModified;
	}
	
	public FileTime getCreated() {
		if(created == null) 
			populateAttrs();
		
		return created;
	}
	
	public DatabaseFileEntry(String filename, Thumbnail thumbnail, float rating) {
		this.filename = filename;
		this.thumbnail = thumbnail;
		this.rating = rating;
	}

}
