package de.erdnute.notes;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.springframework.stereotype.Service;

@Service
public class TestNotesRepository implements NotesRepository {

	List<Folder> allFolders = new ArrayList<>();
	List<Note> allNotes = new ArrayList<>();

	public TestNotesRepository() {
		super();
		init();
	}

	private void init() {		
		createAllFolders();
		createAllNotes();

	}

	private void createAllNotes() {

		for (Folder folder : allFolders) {
			if (!folder.isAll()) {
				List<Note> notes = new ArrayList<>();
				for (int i = 0; i < 200; i++) {
					notes.add(new Note("note-" + folder.getName() + i, folder));
					folder.countPlus();
					allFolders.get(0).countPlus();
				}
				allNotes.addAll(notes);
			}
		}
	}

	public void createAllFolders() {
		allFolders.add(new Folder("All"));
		allFolders.add(new Folder("java"));
		allFolders.add(new Folder("python"));
		allFolders.add(new Folder("maven"));
		allFolders.add(new Folder("spring"));
		allFolders.add(new Folder("eclipse"));
	}

	@Override
	public List<Folder> findAllFolders() {		
		return allFolders;
	}

	@Override
	public List<Note> findNotesByFolder(String folderName) {
		if (folderName.equalsIgnoreCase("all"))
			return sortByDate(allNotes);
		List<Note> result = new ArrayList<>();
		for (Note note : allNotes) {
			if (note.getFolder().getName().equalsIgnoreCase(folderName)) {
				result.add(note);
			}
		}
		return sortByDate(result);
	}

	private List<Note> sortByDate(List<Note> result) {
		Collections.sort(result, new Comparator<Note>() {
		    @Override
		    public int compare(Note n1, Note n2) {
		        return n2.getModified().compareTo(n1.getModified());
		    }
		});
		return result;
	}

	@Override
	public List<Note> findNotesByText(String searchtext) {
		List<Note> result = new ArrayList<>();
		for (Note note : allNotes) {
			if (note.getText().toLowerCase().contains(searchtext.toLowerCase())) {
				result.add(note);
			}
		}
		return result;
	}

	@Override
	public List<Note> findNotesByText(String searchtext, String folderName) {
		if (folderName.equalsIgnoreCase("all")) return findNotesByText(searchtext);
		List<Note> result = new ArrayList<>();
		for (Note note : allNotes) {
			if ((note.getFolder().getName().equalsIgnoreCase(folderName)) &(note.getText().toLowerCase().contains(searchtext.toLowerCase()))) {
				result.add(note);
			}
		}
		return result;
	}

	@Override
	public void updateNote(Note note) {
		// do nothing	
	}

	@Override
	public void insertNote(Note note) {
		// do nothing
	}

	@Override
	public Note createEmptyNote(Folder folder) {
		return new Note("",folder);
	}

	@Override
	public void reloadAll() {			
	}

}
