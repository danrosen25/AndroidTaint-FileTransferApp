package me.captaindan.taintfiletransferapp;

import java.io.*;
import java.net.*;
import java.util.ArrayList;

public class TaintClient
{
	private Socket mySocket;
	private ObjectOutputStream outToServer;
	private ObjectInputStream inFromServer;
	private Directory downloadDir;
	private Directory serverDirectory;
	private String serverName;
	private int serverPort;
	
	public TaintClient(String serverName, int serverPort, String downloadDir)
	{	
		this.downloadDir = new Directory(downloadDir);
		this.serverName = serverName;
		this.serverPort = serverPort;
	}
	
	private void connect() throws IOException
	{
		this.mySocket = new Socket();
		mySocket.connect(new InetSocketAddress(serverName,serverPort),3000);
		inFromServer = new ObjectInputStream(mySocket.getInputStream());
		outToServer = new ObjectOutputStream(mySocket.getOutputStream());
	}
	
	private void disconnect() {
		try{
			mySocket.close();
		}catch(IOException e){

		}
	}
	
	public String message(String message) {
		try{
			connect();
			outToServer.writeObject(new ControlMessage(ControlMessage.message,ControlMessage.messageParam(message)));
			disconnect();
			return "Message sent.";
		}catch(IOException e){
			return "Could not connect.";
		}
	}
	
	public String get(String filename) {
		try{
			connect();
			outToServer.writeObject(new ControlMessage(ControlMessage.requestfile,ControlMessage.fileParam(filename)));
			TaintFile fileout = new TaintFile(downloadDir.getPath(),filename);
			try{
				ControlMessage finfo = (ControlMessage) inFromServer.readObject();
				if(finfo.controlID==ControlMessage.error) return finfo.getMessage();
				fileout.addTaint(finfo.getTaint());
			}catch(ClassNotFoundException e){
				return "What Class is this?";
			}
			BufferedInputStream bis=new BufferedInputStream(mySocket.getInputStream());
			FileOutputStream fos = new FileOutputStream(fileout);
			int n;
			byte[] buffer = new byte[1024];
			while ((n = bis.read(buffer)) > 0){
				fos.write(buffer, 0, n);
			}
			fos.close();
			disconnect();
			return "File Downloaded: " + filename;
		}catch(IOException e){
			return "Could not connect";
		}
	}
	
	public String updateServer(String serverName, int serverPort){
		this.serverName = serverName;
		this.serverPort = serverPort;
		return "Server Address Updated";
	}
	
	public String cd(String newdir){
		try {
			connect();
			outToServer.writeObject(new ControlMessage(ControlMessage.cd,ControlMessage.dirParam(newdir)));
			disconnect();
			return "Server dir changed";
		} catch (IOException e) {
			return "Could not connect.";
		}
	}
		
	public Directory lcd(String newpath){
		downloadDir.newDirectory(newpath);
		return downloadDir;
	}
	

	
	public String lls(){
		downloadDir.refresh();
		return downloadDir.toString();
	}
	
	public ArrayList<String> getServerList(){
		return serverDirectory;
	}
	
	public String ls() {
		try{
			connect();
			outToServer.writeObject(new ControlMessage(ControlMessage.requestdir));
			ControlMessage dirPath = (ControlMessage) inFromServer.readObject();
			serverDirectory = (Directory) inFromServer.readObject();
			disconnect();
			return dirPath.getDir() +"\n"+serverDirectory.toString();
		}catch(IOException e){
			return "Could not connect.";
		}catch(ClassNotFoundException e){
			disconnect();
			return "<Unrecognized directory format>";
		}
	}
	
	public String lfinfo(String filename){
		TaintFile tfile = new TaintFile(downloadDir.getPath(),filename);
		if(!tfile.exists()) return "File does not exist";
		return "Size: "+tfile.length()+"\nTaint: "+tfile.getTaint();
	}
	
	public String finfo(String filename) {
		try{
			connect();
			outToServer.writeObject(new ControlMessage(ControlMessage.finfo,ControlMessage.fileParam(filename)));
			String finfo = (String) inFromServer.readObject();
			disconnect();
			return finfo;
		}catch(IOException e){
			return "Could not connect.";
		}catch(ClassNotFoundException e){
			disconnect();
			return "<Unrecognized file info format>";
		}
	}
	
	public String shutdown() throws IOException{
		try{
			connect();
			outToServer.writeObject(new ControlMessage(ControlMessage.shutdown));
			disconnect();
			return "Server has been shutdown";
		}catch(ConnectException e){
			return "Could not connect.";
		}
	}
	

}
