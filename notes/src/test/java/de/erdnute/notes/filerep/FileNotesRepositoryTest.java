package de.erdnute.notes.filerep;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.awt.Desktop;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;
import java.util.List;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import de.erdnute.notes.FileNote;
import de.erdnute.notes.Folder;
import de.erdnute.notes.Note;

public class FileNotesRepositoryTest {

	// private static final String ROOT = "C:/dev/_dev/workspace/docs";

	private static final String META = "meta.meta";
	@Rule
	public TemporaryFolder folder = new TemporaryFolder();
	File rootFolder;
	FileNotesRepository rep;
	List<Folder> folders;
	List<Note> notes;

	@Before
	public void init() throws IOException {
		rootFolder = folder.newFolder("subfolder");
		System.out.println("ROOT=" + rootFolder);
	}

	// =========================================================================
	// findAllFolders
	// =========================================================================

	@Test
	public void findAllFolders_empty() {
		initRepository();
		folders = rep.findAllFolders();
		assertNotNull(folders);
		assertEquals(1, folders.size());// All-Folder is always there
	}

	private void initRepository() {
		rep = new FileNotesRepository(rootFolder.getAbsolutePath());
	}

	@Test
	public void findAllFolders_flat_one() throws IOException {

		createFolderWithFile(rootFolder, "java", META, "files=txt");
		initRepository();

		// openFile(rootFolder);

		folders = rep.findAllFolders();
		assertNotNull(folders);
		assertFalse(folders.isEmpty());
	}

	@Test
	public void findAllFolders_flat_2TRUE_1FALSE() throws IOException {

		createFolderWithFile(rootFolder, "java", META, "files=txt");
		createFolderWithFile(rootFolder, "eclipse", META, "files=txt");
		createFolderWithFile(rootFolder, "maven", "otherFile", "nothing");
		initRepository();

		// openFile(rootFolder);

		folders = rep.findAllFolders();
		assertNotNull(folders);
		assertEquals(3, folders.size());
	}

	@Test
	public void findAllFolders_deepStructure() throws IOException {

		File java = createFolder(rootFolder, "java");
		File javaEE = createFolderWithFile(java, "javaEE", META, "files=txt");
		File javaMessaging = createFolderWithFile(javaEE, "javaMessaging", META, "files=txt");
		initRepository();

		// openFile(rootFolder);

		folders = rep.findAllFolders();
		assertNotNull(folders);
		assertEquals(3, folders.size());
	}

	// =========================================================================
	// findNotes
	// =========================================================================

	@Test
	public void findNotesByFolder_empty() throws IOException {

		File java = createFolderWithFile(rootFolder, "java", META, "files=txt");
		initRepository();
		notes = rep.findNotesByFolder("java");
		assertNotNull(notes);
		assertTrue(notes.isEmpty());
	}

	@Test
	public void findNotesByFolder_oneNote() throws IOException {
		File java = createFolderWithFile(rootFolder, "java", META, "files=txt");
		createFileWithContent(java, "eins.txt", "Hallo Welt!");
		initRepository();

		// openFile(rootFolder);

		notes = rep.findNotesByFolder("java");
		assertNotNull(notes);
		assertEquals(1, notes.size());
	}

	// =========================================================================
	// update notes
	// =========================================================================

	@Test
	public void updateNote() throws IOException {

		File folderFile = createFolderWithFile(rootFolder, "folder", META, "files=txt");
		File noteFile = createFileWithContent(folderFile, "note1.txt", "hallo world!");

		initRepository();

		List<Note> notes = rep.findNotesByFolder("folder");
		Note note = notes.get(0);

		note.setText("new text öäü");
		rep.updateNote(note);

		initRepository();
		notes = rep.findNotesByFolder("folder");
		note = notes.get(0);
		assertEquals("new text öäü", note.getText());

	}

	@Test
	public void createEmptyNote() throws IOException {

		File folderFile = createFolderWithFile(rootFolder, "folder", META, "files=txt");
		initRepository();

		Note note = rep.createEmptyNote(new FileFolder(folderFile));

		assertEquals("folder", note.getFolder().getName());
		assertEquals("", note.getText());
		assertNotNull(note.getTitle());
		assertTrue(note.getTitle().startsWith("note_"));
		System.out.println(note.getTitle());

	}

	@Test
	public void insertNote() throws IOException {

		createFolderWithFile(rootFolder, "folder", META, "files=txt");
		initRepository();

		List<Note> notes = rep.findNotesByFolder("folder");
		assertEquals(0, notes.size());
		List<Folder> folders = rep.findAllFolders();
		assertEquals(2, folders.size());

		FileFolder folder = (FileFolder) folders.get(1);

		Note note = rep.createEmptyNote(folder);
		rep.insertNote(note);

		initRepository();
		// openFile(folderFile);
		notes = rep.findNotesByFolder("folder");
		Note note1 = notes.get(0);
		assertEquals("", note1.getText());
		assertEquals("folder", note1.getFolder().getName());
		assertTrue(note1.getTitle().startsWith("note_"));

	}

	@Test
	// whenever the title fo the note does not match the filename: rename the
	// file to title. Check on every update.
	public void renameIfTitleChanged() throws IOException {

		// create a note with default-name
		File f = createFolderWithFile(rootFolder, "folder", META, "files=txt");
		initRepository();
		Note note = rep.createEmptyNote(new FileFolder(f));
		rep.insertNote(note);

		// change the name and save the note
		note.setText("my new text");
		note.setTitle("myName.txt");
		rep.updateNote(note);

		// Reload all an check if file is renamend
		initRepository();
		//openFile(f);
		FileNote changedNote = (FileNote) rep.findNotesByFolder("folder").get(0);
		assertEquals("myName.txt", changedNote.getTitle());
		assertEquals("myName.txt", changedNote.getFile().getName());
		assertEquals("my new text", changedNote.getText());

	}

	// =========================================================================
	//
	// =========================================================================

	private File createFolder(File rootFolder, String folderName) throws IOException {
		File newFolder = new File(rootFolder + "/" + folderName);
		newFolder.mkdirs();
		return newFolder;
	}

	private File createFolderWithFile(File rootFolder, String folderName, String fileName, String fileContent)
			throws IOException {
		File newFolder = new File(rootFolder + "/" + folderName);
		newFolder.mkdirs();
		createFileWithContent(newFolder, fileName, fileContent);
		return newFolder;
	}

	private File createFileWithContent(File folder, String fileName, String content) throws IOException {
		File file = new File(folder, fileName);
		writeToFile(file, content.getBytes());
		return file;
	}

	// Hilfsmethoden, um eine Datei zu schreiben
	private File createFileWithContent(String content) throws IOException {
		File file = folder.newFile();
		writeToFile(file, content.getBytes());
		return file;
	}

	private void writeToFile(File file, byte[] bytes) throws IOException {
		FileOutputStream stream = null;
		stream = new FileOutputStream(file);
		stream.write(bytes);
		stream.close();
	}

	public void openFile(File targetFile) {
		// Open File with Default-Application (awt.Desktop)
		try {
			Desktop.getDesktop().open(targetFile);
			System.out.println("opened " + targetFile.getAbsolutePath());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
