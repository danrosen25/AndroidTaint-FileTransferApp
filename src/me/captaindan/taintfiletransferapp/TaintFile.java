package me.captaindan.taintfiletransferapp;

import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URI;

import android.util.Log;
import dalvik.system.Taint;

public class TaintFile extends File {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6136428861051802031L;
	/**
	 * 
	 */
    public static final int TAINT_CLEAR         = 0x00000000; // 0		AAAAAA==	0x00000000
    public static final int TAINT_LOCATION      = 0x00000001; // 1		AAAAAQ==	0x01000000
    public static final int TAINT_CONTACTS      = 0x00000002; // 2		AAAAAg==	0x02000000
    public static final int TAINT_MIC           = 0x00000004; // 4		AAAABA==	0x04000000
    public static final int TAINT_PHONE_NUMBER  = 0x00000008; // 8		AAAACA==	0x08000000
    public static final int TAINT_LOCATION_GPS  = 0x00000010; // 16		AAAAEA==	0x10000000
    public static final int TAINT_LOCATION_NET  = 0x00000020; // 32		AAAAIA==	0x20000000
    public static final int TAINT_LOCATION_LAST = 0x00000040; // 64		AAAAQA==	0x40000000
    public static final int TAINT_CAMERA        = 0x00000080; // 128	AAAAgA==	0x80000000
    public static final int TAINT_ACCELEROMETER = 0x00000100; // 256	AAABAA==	0x00010000
    public static final int TAINT_SMS           = 0x00000200; // 512	AAACAA==	0x00020000
    public static final int TAINT_IMEI          = 0x00000400; // 1024	AAAEAA==	0x00040000
    public static final int TAINT_IMSI          = 0x00000800; // 2048	AAAIAA==	0x00080000
    public static final int TAINT_ICCID         = 0x00001000; // 4096	AAAQAA==	0x00100000
    public static final int TAINT_DEVICE_SN     = 0x00002000; // 8192	AAAgAA==	0x00200000
    public static final int TAINT_ACCOUNT       = 0x00004000; // 16384 	AABAAA==	0x00400000
    public static final int TAINT_HISTORY       = 0x00008000; // 32768	AACAAA==	0x00800000
	public static final int TAINT_TFTACUSTOM	= 0x10000000; //					0x00000010
    public static final int TAINT_FAILURE		= 0x20000000; //					0x00000020
    
	public TaintFile(File parent, String child) {
		super(parent, child);
		// TODO Auto-generated constructor stub
	}

	public TaintFile(String parent, String child) {
		super(parent, child);
		// TODO Auto-generated constructor stub
	}

	public TaintFile(String pathname) {
		super(pathname);
		// TODO Auto-generated constructor stub
	}

	public TaintFile(URI uri) {
		super(uri);
		// TODO Auto-generated constructor stub
	}
	
	public String getTaint(){
	    int taintInt = getTaintInt();
	    if(taintInt == TAINT_CLEAR) return "<No Taint>; ";
	    String taintString = "";
	    if((taintInt & TAINT_LOCATION) == TAINT_LOCATION) taintString += "Location; ";
	    if((taintInt & TAINT_CONTACTS) == TAINT_CONTACTS) taintString += "Contact; ";
	    if((taintInt & TAINT_MIC) == TAINT_MIC) taintString += "Mic; ";
	    if((taintInt & TAINT_PHONE_NUMBER) == TAINT_PHONE_NUMBER) taintString += "Phone_Number; ";
	    if((taintInt & TAINT_LOCATION_GPS) == TAINT_LOCATION_GPS) taintString += "Location_GPS; ";
	    if((taintInt & TAINT_LOCATION_NET) == TAINT_LOCATION_NET) taintString += "Location_Net; ";
	    if((taintInt & TAINT_LOCATION_LAST) == TAINT_LOCATION_LAST) taintString += "Location_Last; ";
	    if((taintInt & TAINT_CAMERA) == TAINT_CAMERA) taintString += "Camera; ";
	    if((taintInt & TAINT_ACCELEROMETER) == TAINT_ACCELEROMETER) taintString += "Accelerometer; ";
	    if((taintInt & TAINT_SMS) == TAINT_SMS) taintString += "SMS; ";
	    if((taintInt & TAINT_IMEI) == TAINT_IMEI) taintString += "IMEI; ";
	    if((taintInt & TAINT_IMSI) == TAINT_IMSI) taintString += "IMSI; ";
	    if((taintInt & TAINT_ICCID) == TAINT_ICCID) taintString += "ICCID; ";
	    if((taintInt & TAINT_DEVICE_SN) == TAINT_DEVICE_SN) taintString += "Device_SN; ";
	    if((taintInt & TAINT_ACCOUNT) == TAINT_ACCOUNT) taintString += "Account; ";
	    if((taintInt & TAINT_HISTORY) == TAINT_HISTORY) taintString += "History; ";
	    if((taintInt & TAINT_TFTACUSTOM) == TAINT_TFTACUSTOM) taintString += "File Trans App; ";
	    if((taintInt & TAINT_FAILURE) == TAINT_FAILURE) taintString += "Failed Taint Retrieval; ";

	    return taintString;
	}
	
	public int getTaintInt() {
		FileInputStream fin;
		int tint;
		try {
			fin = new FileInputStream(this);
			FileDescriptor fd = fin.getFD();
			try{
				Method method = fd.getClass().getMethod("getDescriptor");
				method.setAccessible(true);
				int fdInt =(Integer) method.invoke(fd); 
				tint = Taint.getTaintFile(fdInt);
			}catch (NoSuchMethodException e){
				tint = TAINT_FAILURE;
			} catch (IllegalArgumentException e){
				tint =  TAINT_FAILURE;
			} catch (IllegalAccessException e){
				tint =  TAINT_FAILURE;
			} catch (InvocationTargetException e){
				tint =  TAINT_FAILURE;
			}
			fin.close();
			return tint;
		} catch (IOException e) {
			return TAINT_FAILURE;
		} 
	}
	
	public String getFileInfo(){
		return "Size: "+this.length()+"\nTaint: "+this.getTaint();
	}
	
	public void addTaint(int tvalue){
		FileOutputStream fout;
		try {
			fout = new FileOutputStream(this, true);
			FileDescriptor fd = fout.getFD();
			try{
				Method method = fd.getClass().getMethod("getDescriptor");
				method.setAccessible(true);
				int fdInt =(Integer) method.invoke(fd); 
				Taint.addTaintFile(fdInt, tvalue);
			}catch (NoSuchMethodException e){
				Log.e("addTaintExisting","NoSuchMethodException");
			} catch (IllegalArgumentException e){
				Log.e("addTaintExisting","IllegalArgumentException");
			} catch (IllegalAccessException e){
				Log.e("addTaintExisting","IllegalAccessException");
			} catch (InvocationTargetException e){
				Log.e("addTaintExisting","InvocationTargetException");
			}
			fout.close();
		} catch (IOException e) {
			Log.e("addTaintExisting","Cannot access file.");
		}
	}
	
	public boolean isTainted(){
	    if(this.getTaintInt() == TAINT_CLEAR) return false;
	    return true;
	}
}
