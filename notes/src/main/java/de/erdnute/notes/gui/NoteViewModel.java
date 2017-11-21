package de.erdnute.notes.gui;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

import org.zkoss.bind.BindUtils;
import org.zkoss.bind.annotation.BindingParam;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.GlobalCommand;
import org.zkoss.bind.annotation.NotifyChange;
import org.zkoss.zk.ui.select.annotation.VariableResolver;
import org.zkoss.zk.ui.select.annotation.WireVariable;

import de.erdnute.notes.FileNote;
import de.erdnute.notes.FlexibleNotesRepository;
import de.erdnute.notes.Note;

@VariableResolver(org.zkoss.zkplus.spring.DelegatingVariableResolver.class)
public class NoteViewModel {

	@WireVariable
	private FlexibleNotesRepository flexibleNotesRepository;
	private Note note;
	private boolean titleReadonly = false;
	private boolean textfocus = true;
	private String title;
	private String titlePostfix;

	// =========================================================================
	// @global-command
	// =========================================================================

	@GlobalCommand
	@NotifyChange({ "note","title","titlePostfix" })
	public void clear() {
		note = null;
	}

	@GlobalCommand
	@NotifyChange({ "note","title","titlePostfix" })
	public void noterefresh(@BindingParam("note") Note note) {

		this.note = note;
		this.title = part1(note.getTitle());
		this.titlePostfix = part2(note.getTitle());

	}

	// =========================================================================
	// @command
	// =========================================================================

	private String part1(String title) {
		if (title.contains(".")) {
			String[] parts = title.split(Pattern.quote("."));
			return parts[0];
		} else {
			return title;
		}
	}

	private String part2(String title) {
		if (title.contains(".")) {
			String[] parts = title.split(Pattern.quote("."));
			if (parts.length > 1) {
				return parts[1];
			} else {
				return "";
			}
		} else {
			return "";
		}
	}

	@Command
	@NotifyChange({ "textfocus" })
	public void titleChanged() {
		changed(note.getText());
		textfocus = true;
	}

	@Command
	@NotifyChange({"title","titlePostfix"})
	public void changed(@BindingParam("text") String text) {

		logging();

		note.setText(text);
		note.setModified(new Date());
		note.setTitle(title+"."+titlePostfix);
		// TODO save
		flexibleNotesRepository.updateNote(note);

		Map<String, Object> map = new HashMap<>();
		map.put("note", note);
		BindUtils.postGlobalCommand(null, null, "noteChanged", map);
	}

	private void logging() {
		System.out.println("SAVE----------------------------------");
		System.out.println(note.getTitle());
		FileNote fn = (FileNote) note;
		System.out.println(fn.getFile().getName());
	}

	// =========================================================================
	// getter/setter
	// =========================================================================

	public Note getNote() {
		return note;
	}

	public boolean isTitleReadonly() {
		return titleReadonly;
	}

	public void setTitleReadonly(boolean titleReadonly) {
		this.titleReadonly = titleReadonly;
	}

	public boolean isTextfocus() {
		return textfocus;
	}

	public void setTextfocus(boolean textfocus) {
		this.textfocus = textfocus;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getTitlePostfix() {
		return titlePostfix;
	}

	public void setTitlePostfix(String titlePostfix) {
		this.titlePostfix = titlePostfix;
	}

}
