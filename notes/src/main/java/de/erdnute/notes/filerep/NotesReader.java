package de.erdnute.notes.filerep;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.erdnute.notes.FileNote;
import de.erdnute.notes.Folder;
import de.erdnute.notes.Note;

public class NotesReader {

	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	public List<Note> readAllNotes(List<Folder> folders) {
		List<Note> all = new ArrayList<>();

		for (Folder folder : folders) {
			if (!folder.isAll()) {
				List<Note> notesInFolder = nodesInFolder((FileFolder) folder);
				folder.setCount(notesInFolder.size());
				all.addAll(notesInFolder);
			}
		}
		logger.info("notes " + all.size());
		return all;
	}
	// =========================================================================
	//
	// =========================================================================

	private List<Note> nodesInFolder(FileFolder folder) {
		List<Note> nif = new ArrayList<>();

		File sub = folder.getFile();
		File[] files = sub.listFiles();
		for (File file : files) {
			if (isNote(file)) {
				Date lastModified = new Date(file.lastModified()); 
				nif.add((Note)new FileNote(file,fileContent(file), folder, lastModified));
			}
		}
		return nif;
	}

	private boolean isNote(File file) {
		return ((file.getName().endsWith(".txt")) || ((file.getName().endsWith(".md"))));
	}

	private String fileContent(File file) {
		String content;
		try {
			byte[] encoded = Files.readAllBytes(Paths.get(file.getAbsolutePath()));
			content = new String(encoded, StandardCharsets.ISO_8859_1);
		} catch (Exception e) {
			return e.getMessage();
		}
		return content;

	}

	private String fileContent1(File file) {
		String content;
		try {
			content = FileUtils.readFileToString(file);
		} catch (IOException e) {
			return e.getMessage();
		}
		return content;
	}

}
