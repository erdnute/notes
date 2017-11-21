package de.erdnute.notes;

import java.util.List;

import de.erdnute.notes.filerep.FileFolder;


public interface NotesRepository {
	List<Folder> findAllFolders();
	List<Note> findNotesByFolder(String tagName);
	List<Note> findNotesByText(String searchtext);
	List<Note> findNotesByText(String searchtext, String folderName);
	
	void updateNote(Note note);
	void insertNote(Note note);
	Note createEmptyNote(Folder folder);
	
	void reloadAll();
}
