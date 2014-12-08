package me.captaindan.taintfiletransferapp;

import java.io.Serializable;
import java.util.HashMap;

public class ControlMessage implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 8350283615463317377L;
	/**
	 * 
	 */
	public final static int error = 0;
	public final static int requestdir = 1;
	public final static int requestfile = 2;
	public final static int message = 3;
	public final static int shutdown = 4;
	public final static int finfo = 5;
	public final static int cd = 6;
	public final static int dirinfo = 7;
	
	public int controlID;
	private HashMap<String,String> parameters;
	
	public ControlMessage(int controlID){
		this.controlID = controlID;
	}
	public ControlMessage(int controlID, HashMap<String,String> parameters){
		this.controlID = controlID;
		this.parameters = parameters;
	}
	
	public String getFilename(){
		return parameters.get("filename");
	}
	
	public String getMessage(){
		return parameters.get("message");
	}
	
	public int getTaint(){
		return Integer.parseInt(parameters.get("taint"));
	}
	
	public String getDir(){
		return parameters.get("dir");
	}
	
	public static HashMap<String,String> messageParam(final String message){
		return new HashMap<String,String>(){

			/**
			 * 
			 */
			private static final long serialVersionUID = 1197778674097726383L;

			/**
			 * 
			 */

			{put("message",message);}
		};
	}
	
	public static HashMap<String,String> fileParam(final String filename){
		return new HashMap<String,String>(){/**
			 * 
			 */
			private static final long serialVersionUID = -1085120024885997530L;

		/**
			 * 
			 */
			{put("filename",filename);}
		};
	}
	
	public static HashMap<String,String> dirParam(final String newdir){
		return new HashMap<String,String>(){/**
			 * 
			 */
			private static final long serialVersionUID = -1085120024885997530L;

		/**
			 * 
			 */
			{put("dir",newdir);}
		};
	}
	
	public static HashMap<String,String> finfoParam(final String filename, final String taint){
		return new HashMap<String,String>(){/**
			 * 
			 */
			private static final long serialVersionUID = 8917473019541303949L;

		/**
			 * 
			 */
			{
				put("filename",filename);
				put("taint",taint);
			}
		};
	}
}
