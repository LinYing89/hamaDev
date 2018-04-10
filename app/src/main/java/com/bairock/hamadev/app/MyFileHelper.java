package com.bairock.hamadev.app;

import android.content.Context;
import android.os.Environment;

import java.io.File;

/**
 * Created by Administrator on 2016/3/4.
 */
public class MyFileHelper {
	/**
	 * get the file path of root
	 * @param context
	 * @return
	 */
	public static String getRootFilePath(Context context){
		return context.getFilesDir().getPath();
	}

	public static String getSDPath(){
		File sdDir = null;
		boolean sdCardExist = Environment.getExternalStorageState()
				.equals(Environment.MEDIA_MOUNTED);
		if (sdCardExist) {
			sdDir = Environment.getExternalStorageDirectory();
			return sdDir.toString() + File.separator;
		}
		return null;
	}

	/**
	 * get zhibo file path in sdcard
	 * @return
	 */
	public static String getZhiBoFile(){
		String sdDir = getSDPath();
		String zhiboFilePath = null;
		if(null != sdDir){
			zhiboFilePath = sdDir+"zhibo" + File.separator;
		}
		return zhiboFilePath;
	}

	/**
	 * get remote file path in zhibo directory
	 * @return
	 */
	public static String getRemoteFilePath(){
		String sdDir = getZhiBoFile();
		String remotePath = null;
		if(null != sdDir){
			remotePath = sdDir + "遥控器" + File.separator;
		}
		return remotePath;
	}

	public static String getLogPath(){
		String sdDir = getZhiBoFile();
		String logPath = null;
		if(null != sdDir){
			logPath = sdDir + "logTxt.txt";
		}
		return  logPath;
	}

	/**
	 * get backup directory in zhibo directory
	 * @return
	 */
	public String getSDBackupsPath(){
		String sdDir = getZhiBoFile();
		String backupsPath = null;
		if(null != sdDir){
			backupsPath = sdDir + "backups" + File.separator;
		}
		return backupsPath;
	}

	/**
	 * get the backup file directory for the file name
	 * @param name
	 * @return
	 */
	public String getSDBackupsName(String name){
		String sdDir = getSDBackupsPath();
		String backupsNamePath = null;
		if(null != sdDir){
			backupsNamePath = sdDir + name + File.separator;
		}
		return backupsNamePath;
	}

	/**
	 * get projector directory
	 * @param context
	 * @return
	 */
	public static String getProjectFilePath(Context context){
		String dataPath = context.getFilesDir().getPath();
		return dataPath;
	}

	/**
	 * get shared_pref director
	 * @param context
	 * @return
	 */
	public static String getSharedPath(Context context){
		String dataPath = getProjectFilePath(context) + File.separator + "shared_prefs" + File.separator;
		return dataPath;
	}

	public boolean DeleteFolder(String sPath) {
		boolean flag = false;
		File file = new File(sPath);

		if (!file.exists()) {
			return flag;
		} else {
			if (file.isFile()) {
				return deleteFile(sPath);
			} else {
				return deleteDirectory(sPath);
			}
		}
	}

	public static boolean deleteFile(String sPath) {
		boolean flag = false;
		File file = new File(sPath);
		if (file.isFile() && file.exists()) {
			file.delete();
			flag = true;
		}
		return flag;
	}

	public static boolean deleteDirectory(String sPath) {
		if (!sPath.endsWith(File.separator)) {
			sPath = sPath + File.separator;
		}
		File dirFile = new File(sPath);
		if (!dirFile.exists() || !dirFile.isDirectory()) {
			return false;
		}
		boolean flag = true;

		File[] files = dirFile.listFiles();
		for (int i = 0; i < files.length; i++) {
			if (files[i].isFile()) {
				flag = deleteFile(files[i].getAbsolutePath());
				if (!flag) break;
			}
			else {
				flag = deleteDirectory(files[i].getAbsolutePath());
				if (!flag) break;
			}
		}
		if (!flag) return false;
        return dirFile.delete();
	}
}
