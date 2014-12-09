package me.captaindan.taintfiletransferapp;

import java.io.*;
import java.net.*;

import android.util.Log;

public class TaintServer {

	private ServerSocket listenSocket;
	private Socket clientSocket;
	private ObjectOutputStream outToClient;
	private ObjectInputStream inFromClient;
	private Directory sharedDir;
	private boolean shutdown;
	private boolean allowTaintTransfer;
	
	public TaintServer(int serverPort,String sharePath,boolean allowTaintTransfer) {
		shutdown = false;
		sharedDir = new Directory(sharePath);
		this.allowTaintTransfer = allowTaintTransfer;
		try{
			listenSocket = new ServerSocket(serverPort);
		}catch(IOException e){
			//Count not listen to socket.
		}
	}
	
	public String waitForRequest(){
		String response = null;
		try{
			clientSocket = listenSocket.accept();
			outToClient = new ObjectOutputStream(clientSocket.getOutputStream());
			inFromClient = new ObjectInputStream(clientSocket.getInputStream());
			response = "Connection esatblished";
			try{
			ControlMessage control = (ControlMessage) inFromClient.readObject();
				switch(control.controlID){
					case ControlMessage.requestdir:
						response = server_ls();
						break;
					case ControlMessage.requestfile:
						response = server_get(control.getFilename());
						break;
					case ControlMessage.message:
						response = server_message(control.getMessage());
						break;
					case ControlMessage.finfo:
						response = sever_finfo(control.getFilename());
						break;
					default:
						response = "Unknown or unimplemented request";
						break;
				}
			}catch(ClassNotFoundException e){
				Log.e("TaintFileService","ClassNotFound",e);
				response = "Unrecognized Class";
			}finally{
				clientSocket.close();
			}
		}catch(IOException e){
			response = "Connection issue.";
		}
		return response;
	}
	
	public boolean isOpen(){
		return !shutdown;
	}
	
	private String server_get(String filename) {
		TaintFile file = new TaintFile(sharedDir.getPath(),filename);	
		if(!file.exists()){
			try {
				outToClient.writeObject(new ControlMessage(ControlMessage.error,ControlMessage.messageParam("File does not exist")));
				return "File does not exist: "+filename;
			} catch (IOException e) {
				return "File does not exist: "+filename;
			}
		}
		if(!this.allowTaintTransfer && file.isTainted()){
			try {
				outToClient.writeObject(new ControlMessage(ControlMessage.error,ControlMessage.messageParam("Tainted File Transmission Blocked")));
				return "Taint File Transfer Blocked: "+filename;
			} catch (IOException e) {
				return "Taint File Transfer Blocked: "+filename;
			}
		}
		
		try{
			outToClient.writeObject(new ControlMessage(ControlMessage.finfo,ControlMessage.finfoParam(filename, Integer.toString(file.getTaintInt()))));
			BufferedInputStream bis = new BufferedInputStream(new FileInputStream(file));
			BufferedOutputStream bos= new BufferedOutputStream(clientSocket.getOutputStream());
			int n=-1;
			byte[] buffer = new byte[1024];
			while((n = bis.read(buffer))>-1){
				bos.write(buffer,0,n);
				bos.flush();
			}
			bos.close();
			bis.close();
			return "File sent: "+filename;
		}catch(IOException e){
			return "Could not send file: "+filename;
		}
	}
	
	private String sever_finfo(String filename) {
		TaintFile tfile = new TaintFile(sharedDir.getPath(),filename);
		try{
			if(!tfile.exists()){
				outToClient.writeObject("File does not exist");
				return "File does not exist: "+filename;
			}
			outToClient.writeObject(tfile.getFileInfo());
			return "File info sent: "+filename;
		} catch (IOException e) {
			return "Could not send file.";
		}
		
	}
	
	private String server_ls() {
		sharedDir.refresh();
		try {
			outToClient.writeObject(new ControlMessage(ControlMessage.dirinfo,ControlMessage.dirParam(sharedDir.getPath())));
			outToClient.writeObject(sharedDir);
			return "Directory list sent: "+sharedDir.getPath();
		} catch (IOException e) {
			return "Could not send directory.";
		}
	}
	
	private String server_message(String message) {
		try {
			outToClient.writeObject(new ControlMessage(ControlMessage.message,ControlMessage.messageParam("Server Received Message")));
			return message;
		} catch (IOException e) {
			return "Could not confirm message";
		}
		
	}
	
	public String server_shutdown() {
		shutdown = true;
		try {
			listenSocket.close();
			return "Server stopped.";
		} catch (IOException e) {
			return "Server cannot stop. not running";
		}
	}
	
	public String server_cd(String newpath){
		sharedDir.newDirectory(newpath);
		return "Directory changed to "+newpath;
	}

}
