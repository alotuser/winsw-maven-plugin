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
	 * @param resName resName
	 * @param writePath writePath
	 */
	public static void writeBase64File(String resName, String writePath) {
		ClassPathResource cpr = new ClassPathResource(resName);
		byte[] bts = FileUtil.readBytes(cpr.getFile());
		String enStr = Base64Encoder.encode(bts);
		FileUtil.writeUtf8String(enStr, writePath + cpr.getName());
	}

	/**
	 * writeWinFile
	 * 
	 * @param resName resName
	 * @param dest dest
	 */
	public static void writeWinFile(String resName, File dest) {
		ClassPathResource cpr = new ClassPathResource(resName);
		String enStr = IoUtil.readUtf8(cpr.getStream());
		byte[] bts = Base64Decoder.decode(enStr);
		FileUtil.writeBytes(bts, dest);
	}
	
}
