package de.erdnute.notes;

public class Folder {
	private String name;
	private int count;
	
	

	public Folder(String name) {
		super();
		this.name = name;
	}



	public String getName() {
		return name;
	}



	public boolean isAll() {
		return name.equalsIgnoreCase("all");
	}



	public void countPlus() {
		count++;		
	}



	public int getCount() {
		return count;
	}



	public void setCount(int size) {
		count = size;		
	}



	

	
}
