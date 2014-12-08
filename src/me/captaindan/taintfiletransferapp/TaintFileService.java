package me.captaindan.taintfiletransferapp;

import android.app.IntentService;
import android.content.Intent;
import android.os.Environment;
import android.util.Log;

public class TaintFileService extends IntentService {
    public TaintFileService() {
		super("me.captaindan.taintfiletransferapp.TaintFileService");
		// TODO Auto-generated constructor stub
	}

	public static final String PARAM_IN_MSG = "imsg";
    public static final String PARAM_OUT_MSG = "omsg";


	TaintServer server;
	
	@Override
	protected void onHandleIntent(Intent intent) {
		int portNum;
		String sharedPath = intent.getStringExtra("mySharedPath");
		try{
			portNum = Integer.parseInt(intent.getStringExtra("mySharedPort"));
			if (portNum<5000) throw new NumberFormatException();
		}catch(NumberFormatException e){
			portNum = 9999;
		}
		server = new TaintServer(portNum,sharedPath);
		Log.d("TaintFileService","Server Started on port"+portNum);
		while(server.isOpen()){
			Log.d("TaintFileService",server.waitForRequest());
		}
		this.stopSelf();
	}

	@Override
	public void onDestroy() {
		Log.d("TaintFileService","Server Stopped.");
		super.onDestroy();
	}

	
    
} 
