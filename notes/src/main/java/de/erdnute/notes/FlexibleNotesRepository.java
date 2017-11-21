package de.erdnute.notes;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import de.erdnute.notes.filerep.FileNotesRepository;

@Repository
public class FlexibleNotesRepository implements NotesRepository {
	
	TestNotesRepository testNotesRepository;
	
	FileNotesRepository fileNotesRepository;
	
	NotesRepository currentRepository = fileNotesRepository;
	
	public FlexibleNotesRepository(@Autowired TestNotesRepository testNotesRepository,@Autowired FileNotesRepository fileNotesRepository) {
		super();
		this.testNotesRepository = testNotesRepository;
		this.fileNotesRepository = fileNotesRepository;
		this.currentRepository = fileNotesRepository;
	}

	public void testRepository() {
		currentRepository = testNotesRepository;
	}
	
	public void fileRepository() {
		currentRepository = fileNotesRepository;
	}

	@Override
	public List<Folder> findAllFolders() {
		return currentRepository.findAllFolders();
	}

	@Override
	public List<Note> findNotesByFolder(String tagName) {
		return currentRepository.findNotesByFolder(tagName);
	}

	@Override
	public List<Note> findNotesByText(String searchtext) {
		return currentRepository.findNotesByText(searchtext);
	}

	@Override
	public List<Note> findNotesByText(String searchtext, String folderName) {
		return currentRepository.findNotesByText(searchtext,folderName);
	}

	@Override
	public void updateNote(Note note) {
		currentRepository.updateNote(note);		
	}

	@Override
	public void insertNote(Note note) {
		currentRepository.insertNote(note);		
	}

	@Override
	public Note createEmptyNote(Folder folder) {
		return currentRepository.createEmptyNote(folder);
	}

	@Override
	public void reloadAll() {
		currentRepository.reloadAll();
		
	}

	

}
