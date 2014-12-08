package me.captaindan.taintfiletransferapp;

import java.net.InetAddress;
import java.net.UnknownHostException;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


public class MainActivity extends Activity {
	TaintClient client;
	int mySharedPort;
	String myAddress;
	String myDownloadPath;
	String mySharedPath;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    	mySharedPort = getMyPort();
    	myDownloadPath = Environment.getExternalStorageDirectory().getPath()+"/Download";
    	mySharedPath = Environment.getExternalStorageDirectory().getPath()+"/Shared";
        client = new TaintClient("localhost", mySharedPort,myDownloadPath);
    }

    private int getMyPort(){
    	//return Integer.parseInt(((EditText) findViewById(R.id.myPort_text)).getText().toString());
    	return 9999;
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
    
    public void startTaintFileService(View view){
    	Intent i = new Intent(this, TaintFileService.class);
    	i.putExtra("mySharedPort", mySharedPort);
    	i.putExtra("mySharedPath", mySharedPath);
    	startService(i);
		Toast.makeText(this.getApplicationContext(), "Server Started\n\tPort: "+mySharedPort+"\n\tShared: "+mySharedPath, Toast.LENGTH_SHORT).show();
    }
    
    public void stopTaintFileService(View view){
    	stopService(new Intent(this,TaintFileService.class));
		Toast.makeText(this.getApplicationContext(), "Server Stopped.", Toast.LENGTH_SHORT).show();
    }
    
    public void toastUnimplemented(View view){
		Toast.makeText(this.getApplicationContext(), "Function not implemented", Toast.LENGTH_SHORT).show();
    }
    
    public void updateServerAddress(View view){
    	client.updateServer(((EditText) findViewById(R.id.serverAddress_text)).getText().toString(), ((EditText) findViewById(R.id.serverPort_text)).getText().toString());
		Toast.makeText(this.getApplicationContext(), "Server Address Updated.", Toast.LENGTH_SHORT).show();
    }
    
    public void downloadPress(View view){
    	GetFile task = new GetFile();
    	task.execute(new String[]{((EditText) findViewById(R.id.downloadFile_text)).getText().toString()});
    }
    
    public void refreshPress(View view){
    	RefreshLocal task1 = new RefreshLocal();
    	task1.execute();
    	RefreshRemote task2 = new RefreshRemote();
    	task2.execute();
    }
    
    public void localInfoPress(View view){
    	GetLocalFileInfo task = new GetLocalFileInfo();
    	task.execute(new String[]{((EditText) findViewById(R.id.downloadFile_text)).getText().toString()});
    }
    
    public void remoteInfoPress(View view){
    	GetRemoteFileInfo task = new GetRemoteFileInfo();
    	task.execute(new String[]{((EditText) findViewById(R.id.downloadFile_text)).getText().toString()});
    }
    
    private class GetFile extends AsyncTask<String, Void, String>{
		@Override
		protected String doInBackground(String... params) {
			return client.get(params[0]);
		}

		@Override
		protected void onPostExecute(String result) {
			Toast.makeText(getApplicationContext(), result, Toast.LENGTH_SHORT).show();
			super.onPostExecute(result);
		}
    }
    
    private class RefreshLocal extends AsyncTask<Void, Void, String>{
		
    	@Override
		protected String doInBackground(Void... params) {
    		return client.lls();
		}
		
		@Override
		protected void onPostExecute(String result) {
			((TextView) findViewById(R.id.clientFolder_text)).setText(result);
			super.onPostExecute(result);
		}
    }
    
    private class RefreshRemote extends AsyncTask<Void, Void, String>{
		
    	@Override
		protected String doInBackground(Void... params) {
    		return client.ls();
		}
		
		@Override
		protected void onPostExecute(String result) {
			((TextView) findViewById(R.id.serverFolder_text)).setText(result);
			super.onPostExecute(result);
		}
    }
    
    private class GetRemoteFileInfo extends AsyncTask<String, Void, String>{
		@Override
		protected String doInBackground(String... params) {
			return client.finfo(params[0]);
		}

		@Override
		protected void onPostExecute(String result) {
			Toast.makeText(getApplicationContext(), result, Toast.LENGTH_LONG).show();
			super.onPostExecute(result);
		}
    }
    
    private class GetLocalFileInfo extends AsyncTask<String, Void, String>{
		@Override
		protected String doInBackground(String... params) {
			return client.lfinfo(params[0]);
		}

		@Override
		protected void onPostExecute(String result) {
			Toast.makeText(getApplicationContext(), result, Toast.LENGTH_LONG).show();
			super.onPostExecute(result);
		}
    }
    
    private class GetMyAddress extends AsyncTask<Void,Void,String>{
		@Override
		protected String doInBackground(Void... params) {
	    	try {
				return InetAddress.getLocalHost().getHostAddress();
			} catch (UnknownHostException e) {
				return "Cannot Determine Address";
			}
		}

		@Override
		protected void onPostExecute(String result) {
			myAddress = result;
			super.onPostExecute(result);
		}
		
    }
}
