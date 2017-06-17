package net.serveron.sato.pic2pdfv2.util;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.zip.ZipInputStream;

import org.apache.tools.zip.ZipEntry;
import org.apache.tools.zip.ZipFile;



public class FileUtil {

	/***
	 * ファイル拡張子取得
	 * 
	 * @param fileName
	 * @return
	 */
	public static String GetExtension(String fileName) {
		int idx = fileName.indexOf(".");
		if (idx < 0) {
			return "";
		} else {
			return fileName.substring(idx).substring(1);
		}
	}

	/**
	 * ファイルリスト取得
	 * 
	 * @param filePath
	 * @return
	 */
	public static File[] getFileList(String filePath) {

		File file = new File(filePath);

		return file.listFiles(new FileFilter() {

			@Override
			public boolean accept(File pathname) {

				if (FileUtil.GetExtension(pathname.getName()).toUpperCase().equals("JPEG")) {
					return true;
				}
				
				if (FileUtil.GetExtension(pathname.getName()).toUpperCase().equals("PNG")) {
					return true;
				}
				
				if (FileUtil.GetExtension(pathname.getName()).toUpperCase().equals("JPG")) {
					return true;
				}
				
				if (FileUtil.GetExtension(pathname.getName()).equals("GIF")) {
					return true;
				}
				
				if (FileUtil.GetExtension(pathname.getName()).equals("TIFF")) {
					return true;
				}
				
				return false;
			}
		});
	}
	
	/**
	 * ディレクトリリスト取得
	 * 
	 * @param filePath
	 * @return
	 */
	public static File[] getDirList(String filePath) {

		File file = new File(filePath);

		return file.listFiles(new FileFilter() {
			@Override
			public boolean accept(File pathname) {

				if (pathname.isDirectory()) {
					return true;
				}
				return false;
			}
		});
	}

	public static byte[] readAll(InputStream inputStream) throws IOException {
		ByteArrayOutputStream bout = new ByteArrayOutputStream();
		byte[] buffer = new byte[1024];
		while (true) {
			int len = inputStream.read(buffer);
			if (len < 0) {
				break;
			}
			bout.write(buffer, 0, len);
		}
		return bout.toByteArray();
	}
}
