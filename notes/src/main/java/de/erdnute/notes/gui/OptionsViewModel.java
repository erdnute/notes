package de.erdnute.notes.gui;

import org.zkoss.bind.BindUtils;
import org.zkoss.bind.annotation.BindingParam;
import org.zkoss.bind.annotation.Command;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.select.annotation.VariableResolver;
import org.zkoss.zk.ui.select.annotation.WireVariable;

import de.erdnute.notes.FlexibleNotesRepository;

@VariableResolver(org.zkoss.zkplus.spring.DelegatingVariableResolver.class)
public class OptionsViewModel {
	
	@WireVariable
	private FlexibleNotesRepository flexibleNotesRepository;
	
	@Command
	public void testdaten(@BindingParam("v") boolean v) {
		if (v) {
			flexibleNotesRepository.testRepository();
		} else {
			flexibleNotesRepository.fileRepository();
		}
		BindUtils.postGlobalCommand(null, null, "clear", null);
		BindUtils.postGlobalCommand(null, null, "reload", null);
	}
	
	@Command
	public void reload() {
		flexibleNotesRepository.reloadAll();
		Executions.sendRedirect("/");
	}
}
