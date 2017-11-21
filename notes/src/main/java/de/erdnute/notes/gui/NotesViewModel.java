package de.erdnute.notes.gui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.zkoss.bind.BindUtils;
import org.zkoss.bind.annotation.BindingParam;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.GlobalCommand;
import org.zkoss.bind.annotation.NotifyChange;
import org.zkoss.zk.ui.select.annotation.VariableResolver;
import org.zkoss.zk.ui.select.annotation.WireVariable;

import de.erdnute.notes.FlexibleNotesRepository;
import de.erdnute.notes.Folder;
import de.erdnute.notes.Note;

@VariableResolver(org.zkoss.zkplus.spring.DelegatingVariableResolver.class)
public class NotesViewModel {

	@WireVariable
	private FlexibleNotesRepository flexibleNotesRepository;

	private Folder folder = null;
	private Note selectedNote;
	private int page;
	private List<Note> notes = new ArrayList<>();
	private String search = "";

	public int getCount() {
		return notes.size();
	}
	
    // =========================================================================
    // global-commands 
    // =========================================================================

	
	@GlobalCommand
	@NotifyChange({ "notes", "page", "selectedNote", "search", "count" })
	public void clear() {
		folder = null;
		selectedNote = null;
		notes = new ArrayList<>();
		search = "";
	}
	
	@GlobalCommand
	@NotifyChange({ "folderName", "notes", "page", "count" })
	public void folderrefresh(@BindingParam("folder") Folder folder) {
		setPage(0);
		setFolder(folder);
		search(search);
	}

	@GlobalCommand
	@NotifyChange({ "selectedNote" })
	public void noteChanged(@BindingParam("note") Note note) {
		// setPage(0);// weil sortiert wird und die geaenderte notiz ganz vorne
		// steht
		selectedNote.setModified(note.getModified());
		selectedNote.setText(note.getText());
	}

    // =========================================================================
    // @commands 
    // =========================================================================

	
	@Command
	@NotifyChange({ "notes", "page", "selectedNote", "search", "count" })
	public void clearSearchBox() {
		search = "";
		search("");
	}
	
	@Command
	@NotifyChange({ "notes", "page", "selectedNote", "search", "count" })
	public void addNote() {
		
		Note newNote = flexibleNotesRepository.createEmptyNote(folder);
		flexibleNotesRepository.insertNote(newNote);
		notes.add(newNote);
		selectedNote=newNote;
		
				
		// inform others
		Map<String, Object> map = new HashMap<>();
		map.put("note", newNote);
		BindUtils.postGlobalCommand(null, null, "noterefresh",map );
	}

	@Command
	@NotifyChange({ "notes", "page", "selectedNote", "count" })
	public void search(@BindingParam("searchtext") String searchtext) {
		if (!searchtext.trim().isEmpty()) {
			notes = flexibleNotesRepository.findNotesByText(searchtext, folder.getName());
		} else {
			// search ist leer
			notes = flexibleNotesRepository.findNotesByFolder(folder.getName());
		}
		if (!notes.isEmpty()) {
			selectedNote = notes.get(0);
			Map<String, Object> map = new HashMap<>();
			map.put("note", selectedNote);
			BindUtils.postGlobalCommand(null, null, "noterefresh",map );

		}

	}

    // =========================================================================
    // getters/setters 
    // =========================================================================




	public Note getSelectedNote() {
		return selectedNote;
	}

	public Folder getFolder() {
		return folder;
	}

	public void setFolder(Folder folder) {
		this.folder = folder;
	}

	public void setSelectedNote(Note selectedNote) {
		this.selectedNote = selectedNote;
	}

	public int getPage() {
		return page;
	}

	public void setPage(int page) {
		this.page = page;
	}

	public List<Note> getNotes() {
		return notes;
	}

	public void setNotes(List<Note> notes) {
		this.notes = notes;
	}

	public String getSearch() {
		return search;
	}

	public void setSearch(String search) {
		this.search = search;
	}

}
