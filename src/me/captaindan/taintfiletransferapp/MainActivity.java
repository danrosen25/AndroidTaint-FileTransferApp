package me.captaindan.taintfiletransferapp;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Collections;
import java.util.Enumeration;

import org.apache.http.conn.util.InetAddressUtils;

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
	String myDownloadPath;
	String mySharedPath;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    	myDownloadPath = Environment.getExternalStorageDirectory().getPath()+"/Download";
    	mySharedPath = Environment.getExternalStorageDirectory().getPath()+"/Shared";
        client = new TaintClient(getServerAddress(), getServerPort(),myDownloadPath);
        UpdateMyAddress task = new UpdateMyAddress();
        task.execute();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
    
    public void startTaintFileService(View view){
    	Intent i = new Intent(this, TaintFileService.class);
    	i.putExtra("mySharedPort", Integer.toString(getMyPort()));
    	i.putExtra("mySharedPath", mySharedPath);
    	startService(i);
    	((EditText) findViewById(R.id.myPort_text)).setEnabled(false);
		Toast.makeText(this.getApplicationContext(), "Server Started\n\tPort: "+getMyPort()+"\n\tShared: "+mySharedPath, Toast.LENGTH_SHORT).show();
    }
    
    public void updateServerAddress(View view){
    	client.updateServer(getServerAddress(), getServerPort());
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
    
    private class UpdateMyAddress extends AsyncTask<Void,Void,String>{
		@Override
		protected String doInBackground(Void... params) {
    		try {
    			NetworkInterface netint = NetworkInterface.getByName("wlan0");
    			String address = "";
				Enumeration<InetAddress> inetAddresses = netint.getInetAddresses();
				for (InetAddress inetAddress : Collections.list(inetAddresses)) {
					if(!inetAddress.isLoopbackAddress() && InetAddressUtils.isIPv4Address(inetAddress.getHostAddress())) address+=inetAddress.toString();
		        }
    			return address;
			} catch (SocketException e) {
				return "Cannot Determine Address";
			}
		}

		@Override
		protected void onPostExecute(String result) {
			((TextView) findViewById(R.id.myAddress_text)).setText(result);
			super.onPostExecute(result);
		}
		
    }
    
    private int getMyPort(){
    	return Integer.parseInt(((EditText) findViewById(R.id.myPort_text)).getText().toString());
    }
    
    private int getServerPort(){
    	return Integer.parseInt(((EditText) findViewById(R.id.serverPort_text)).getText().toString());
    }
    
    private String getServerAddress(){
    	return ((EditText) findViewById(R.id.serverAddress_text)).getText().toString();
    }
}
