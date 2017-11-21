package de.erdnute.notes.gui;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class Options {

	@Value("${color:'red'}")
	private String color;

	public String getColor() {
		return this.color;
	}

}
