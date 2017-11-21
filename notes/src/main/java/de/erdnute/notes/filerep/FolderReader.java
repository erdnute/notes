package de.erdnute.notes.filerep;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.erdnute.notes.Folder;

public class FolderReader {

	private static final String META_FILE_NAME = "meta.meta";
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	File rootFolder;

	public FolderReader(File rootFolder) {
		this.rootFolder = rootFolder;
	}

	public List<Folder> readAllFolders() {
		List<Folder> all = new ArrayList<>();

		List<File> subfolders = subfolders(rootFolder);
		for (File subfolder : subfolders) {
			if (isNotesFolder(subfolder)) {
				all.add(new FileFolder(subfolder));
			}
		}
		
		logger.info("folders " + all.size());		
		return all;
	}
	// =========================================================================
	//
	// =========================================================================

	private boolean isNotesFolder(File subfolder) {
		File metaFile = new File(subfolder, META_FILE_NAME);
		return metaFile.exists();
	}

	private List<File> subfolders(File folder) {
		List<File> folders = new ArrayList<>();
		for (File file : folder.listFiles()) {
			if (file.isDirectory()) {
				folders.add(file);
				folders.addAll(subfolders(file));
			}
		}
		return folders;
	}

}
