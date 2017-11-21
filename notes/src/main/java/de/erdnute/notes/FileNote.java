package de.erdnute.notes;

import java.io.File;
import java.util.Date;

import de.erdnute.notes.filerep.FileFolder;

public class FileNote extends Note {
	
	private File file;

	public FileNote(String text, FileFolder folder, Date lastModified) {
		super(text, folder, lastModified);
	}

	public FileNote(File file, String fileContent, FileFolder folder, Date lastModified) {
		super(fileContent,folder,lastModified);
		this.file=file;		
		setTitle(file.getName());
	}

	public File getFile() {
		return file;
	}

	public void setFile(File newFile) {
		this.file=newFile;		
	}

	

}
