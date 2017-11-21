package de.erdnute.notes.gui;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.zkoss.bind.BindUtils;
import org.zkoss.bind.annotation.GlobalCommand;
import org.zkoss.bind.annotation.NotifyChange;
import org.zkoss.zk.ui.select.annotation.VariableResolver;
import org.zkoss.zk.ui.select.annotation.WireVariable;

import de.erdnute.notes.FlexibleNotesRepository;
import de.erdnute.notes.Folder;

@VariableResolver(org.zkoss.zkplus.spring.DelegatingVariableResolver.class)
public class FolderViewModel {


	@WireVariable
	private FlexibleNotesRepository flexibleNotesRepository;
	
	@WireVariable
	private Options options;


	private Folder selectedFolder;
	
	
    // =========================================================================
    // @global-command 
    // =========================================================================

	
	@GlobalCommand
	@NotifyChange({"folders"})
	public void reload() {
	}

	public List<Folder> getFolders() {
		List<Folder> all = flexibleNotesRepository.findAllFolders();
		if (!all.isEmpty()) {
			selectedFolder = all.get(0);
			Map<String, Object> map = new HashMap<>();
			map.put("folder", selectedFolder);
			BindUtils.postGlobalCommand(null, null, "folderrefresh",map );
		}
		return all;
	}

	
    // =========================================================================
    // getter/setter 
    // =========================================================================

	
	public Folder getSelectedFolder() {
		return selectedFolder;
	}

	public void setSelectedFolder(Folder selectedFolder) {
		this.selectedFolder = selectedFolder;
	}

	public String getColor() {
		return options.getColor();
	}



}
