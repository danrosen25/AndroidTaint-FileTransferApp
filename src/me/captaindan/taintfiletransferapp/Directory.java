package me.captaindan.taintfiletransferapp;

import java.io.File;
import java.util.ArrayList;


public class Directory extends ArrayList<String> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8769318573749047852L;
	/**
	 * 
	 */
	private String path; 
	
	public Directory(){
		this(System.getProperty("user.home"));
	}
	
	public Directory(String path){
		super();
		newDirectory(path);
	}

	public void newDirectory(String newdir){
		path = newdir;
		refresh();
	}
	
	public void refresh(){
		this.clear();
		File directory = new File(path);
		if(directory.isDirectory()){
			for(File fileEntry : directory.listFiles()){
				if (fileEntry.isFile() && !fileEntry.isHidden()){
					this.add(fileEntry.getName());
				}
			}
		}
	}

	public String getPath(){
		return path;
	}
	
	@Override
	public String toString() {
		// TODO Auto-generated method stub
		String filelist = "";
		if(this.size()<=0) return "";
		for(int i=0; i<this.size()-1;i++){
			filelist += this.get(i)+"\n";
		}
		filelist += this.get(this.size()-1);
		return filelist;
	}
}
