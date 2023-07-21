package com.alotuser.util;

import java.io.File;

import cn.hutool.core.codec.Base64Decoder;
import cn.hutool.core.codec.Base64Encoder;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.IoUtil;
import cn.hutool.core.io.resource.ClassPathResource;

public class ResUtil {

	/**
	 * writeBase64File
	 * 
	 * @param readPath  readPath
	 * @param writePath writePath
	 */
	public static void writeBase64File(String readPath, String writePath) {
		byte[] bts = FileUtil.readBytes(readPath);
		String enStr = Base64Encoder.encode(bts);
		FileUtil.writeUtf8String(enStr, writePath);
	}

	/**
	 * writeWinFile
	 * 
	 * @param resName 	resName
	 * @param dest 		dest
	 */
	public static void writeWinFile(String resName, File dest) {
		ClassPathResource cpr = new ClassPathResource(resName);
		String enStr = IoUtil.readUtf8(cpr.getStream());
		byte[] bts = Base64Decoder.decode(enStr);
		FileUtil.writeBytes(bts, dest);
	}
	
//	public static void main(String[] args) {
//		String path="D:/winsw/2.12.0/";
//		System.out.println("=================START=====================");
//		writeBase64File(path+"WinSW.NET2.exe"	, path+"out/WinSW-NET2.exe.yml");
//		writeBase64File(path+"WinSW.NET4.exe"	, path+"out/WinSW-NET4.exe.yml");
//		writeBase64File(path+"WinSW.NET461.exe"	, path+"out/WinSW-NET461.exe.yml");
//		writeBase64File(path+"WinSW-x64.exe"	, path+"out/WinSW-x64.exe.yml");
//		writeBase64File(path+"WinSW-x86.exe"	, path+"out/WinSW-x86.exe.yml");
//		System.out.println("==================END======================");
//	}
	
}
