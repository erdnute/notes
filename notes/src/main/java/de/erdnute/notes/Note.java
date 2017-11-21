package de.erdnute.notes;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Pattern;

import de.erdnute.notes.filerep.FileFolder;

public class Note {
	private String text;
	private String title;	 
	private Date modified;
	private Folder folder;
	SimpleDateFormat sdf = new SimpleDateFormat("E, dd. MMM yyyy");

	public Note(String text, Folder folder) {
		super();
		this.text = text;
		this.folder = folder;
		this.modified = new Date();
	}

	public Note(String text, FileFolder folder, Date lastModified) {
		super();
		this.text = text;
		this.folder = folder;
		this.modified = lastModified;
		
	}

	public String getTitleShort() {
		return part1(title);
	}
	
	private String part1(String title) {
		if (title == null) return null;
		if (title.contains(".")) {
			String[] parts = title.split(Pattern.quote("."));
			return parts[0];
		} else {
			return title;
		}
	}
	
	public String getText() {
		return text;
	}

	public String getTitle() {
		if (title == null) {
			return firstLine(text);
		}
		return title;
	}

	public String getModifiedFormatted() {
		return sdf.format(modified);
	}

	private String firstLine(String text) {
		String lines[] = text.split("\\r?\\n");
		return lines[0];
	}

	public void setText(String text) {
		this.text = text;
	}

	public Folder getFolder() {
		return folder;
	}

	public void setFolder(Folder folder) {
		this.folder = folder;
	}

	public Date getModified() {
		return modified;
	}

	public void setModified(Date modified) {
		this.modified = modified;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	
	
}
