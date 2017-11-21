package de.erdnute.notes.filerep;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.RuntimeBeanNameReference;
import org.springframework.stereotype.Repository;

import de.erdnute.notes.FileNote;
import de.erdnute.notes.Folder;
import de.erdnute.notes.Note;
import de.erdnute.notes.NotesRepository;

@Repository
public class FileNotesRepository implements NotesRepository {

	private static final String ENCODING = "ISO_8859_1";

	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	String root;
	File rootFolder;
	List<Folder> folders = new ArrayList<>();
	List<Note> notes = new ArrayList<>();

	public FileNotesRepository(@Value("${root}") String root) {
		super();
		this.root = root;
		init(root);
	}

	private void init(String root) {
		rootFolder = new File(root);
		checkFilesystem(root);
		logger.info("root folder " + root);
		folders = new FolderReader(rootFolder).readAllFolders();
		notes = new NotesReader().readAllNotes(folders);
		addSpecialFolderAll();
	}

	public void reloadAll() {
		init(root);
	}
	
	private void addSpecialFolderAll() {
		Folder allFolder = new Folder("All");
		allFolder.setCount(notes.size());
		folders.add(allFolder);
		sortFoldersByName();
	}

	private void checkFilesystem(String root) {
		if (!rootFolder.exists())
			throw new RuntimeException("folder does not exist " + root);
		if (!rootFolder.isDirectory())
			throw new RuntimeException("root is not a folder " + root);
		if (!rootFolder.canWrite())
			logger.warn("no write access on root folder " + root);
	}

	// =========================================================================
	// Search Folder
	// =========================================================================

	@Override
	public List<Folder> findAllFolders() {
		return folders;
	}

	// =========================================================================
	// Search Notes
	// =========================================================================

	@Override
	public List<Note> findNotesByFolder(String folderName) {
		if (folderName.equalsIgnoreCase("all"))
			return sortByDate(notes);
		List<Note> result = new ArrayList<>();
		for (Note note : notes) {
			if (note.getFolder().getName().equalsIgnoreCase(folderName)) {
				result.add(note);
			}
		}
		return sortByDate(result);
	}

	@Override
	public List<Note> findNotesByText(String searchtext) {
		List<Note> result = new ArrayList<>();
		for (Note note : notes) {
			if (note.getText().toLowerCase().contains(searchtext.toLowerCase())) {
				result.add(note);
			}
		}
		return result;
	}

	@Override
	public List<Note> findNotesByText(String searchtext, String folderName) {
		if (folderName.equalsIgnoreCase("all"))
			return findNotesByText(searchtext);
		List<Note> result = new ArrayList<>();
		for (Note note : notes) {
			if ((note.getFolder().getName().equalsIgnoreCase(folderName))
					& (note.getText().toLowerCase().contains(searchtext.toLowerCase()))) {
				result.add(note);
			}
		}
		return result;
	}

	// =========================================================================
	// Updates
	// =========================================================================

	@Override
	public void updateNote(Note note) {
		FileNote n = (FileNote) note;
		if (!n.getTitle().equalsIgnoreCase(n.getFile().getName())) {
			// rename
			String newName = n.getTitle();
			File newFile = new File(((FileFolder) n.getFolder()).getFile(), newName);
			if (n.getFile().exists()) {
				try {
					FileUtils.moveFile(n.getFile(), newFile);
					n.setFile(newFile);
				} catch (IOException e) {
					throw new RuntimeException("error renaming file " + n.getFile().getName(), e);
				}
			}
		}
		saveFile(n.getFile(), n.getText());

	}

	@Override
	public void insertNote(Note note) {
		updateNote(note);
		notes.add(note);
		note.getFolder().countPlus();
	}

	// =========================================================================
	//
	// =========================================================================
	private List<Note> sortByDate(List<Note> result) {
		Collections.sort(result, new Comparator<Note>() {
			@Override
			public int compare(Note n1, Note n2) {
				return n2.getModified().compareTo(n1.getModified());
			}
		});
		return result;
	}

	private void sortFoldersByName() {
		Collections.sort(folders, new Comparator<Folder>() {
			@Override
			public int compare(Folder f1, Folder f2) {
				return f1.getName().compareTo(f2.getName());
			}
		});
	}

	private void saveFile1(File file, String content) {
		try {
			Files.write(Paths.get(file.getAbsolutePath()), content.getBytes(), StandardOpenOption.CREATE);
		} catch (IOException e) {
			throw new RuntimeException("error saving file " + file.getAbsolutePath(), e);
		}
	}

	private void saveFile(File file, String content) {
		try {

			FileUtils.writeStringToFile(file, content, ENCODING);

		} catch (IOException e) {
			throw new RuntimeException("error saving file " + file.getAbsolutePath(), e);
		}
	}

	@Override
	public Note createEmptyNote(Folder folder) {

		FileFolder f = (FileFolder) folder;
		File noteFile = new File(f.getFile(), defaultFileName());
		FileNote note = new FileNote(noteFile, "", f, new Date());

		return note;
	}

	private String defaultFileName() {
		String dateString = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss").format(new Date());
		return "note_" + dateString + ".txt";
	}

}
