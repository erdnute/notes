package de.erdnute.notes.filerep;

import java.io.File;

import de.erdnute.notes.Folder;

public class FileFolder extends Folder {

	private File file;
	
	public FileFolder(String name) {
		super(name);
	}

	public FileFolder(File file) {
		super(file.getName());
		this.file = file;
	}

	public File getFile() {
		return file;
	}
	
	
	

}
