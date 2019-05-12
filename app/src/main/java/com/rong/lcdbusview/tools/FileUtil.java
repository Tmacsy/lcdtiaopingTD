package com.rong.lcdbusview.tools;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.channels.FileChannel;

/**
 * 文件读写工具类
 * 
 * @author bear
 * 
 */
public class FileUtil {

	/**
	 * 如果文件不存在，就创建文件
	 * 
	 * @param path
	 *            文件路径
	 * @return
	 */
	public static String createIfNotExist(String path) {
		File file = new File(path);
		if (!file.exists()) {
			try {
				file.createNewFile();
			} catch (Exception e) {
				System.out.println(e.getMessage());
			}
		}
		return path;
	}

	/**
	 * 向文件中写入数据
	 * 
	 * @param filePath
	 *            目标文件全路径
	 * @param data
	 *            要写入的数据
	 * @return true表示写入成功 false表示写入失败
	 */
	public synchronized static boolean writeBytes(String filePath, byte[] data) {
		boolean isResult = false;
		FileOutputStream fos = null;
		try {
			File file = new File(filePath);
			if (!file.exists()) {
				file.createNewFile();
			}
			fos = new FileOutputStream(filePath);
			fos.write(data);
			fos.flush();
			isResult = true;
		} catch (Exception e) {
			isResult = false;
			System.out.println(e.getMessage());
		} finally {
			try {
				if (fos != null)
					fos.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return isResult;
	}

	/**
	 * 从文件中读取数据
	 * 
	 * @param file
	 * @return
	 */
	public static byte[] readBytes(String file) {
		try {
			FileInputStream fis = new FileInputStream(file);
			int len = fis.available();
			byte[] buffer = new byte[len];
			fis.read(buffer);
			fis.close();
			return buffer;
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}

		return null;

	}

	/**
	 * 向文件中写入字符串String类型的内容
	 * 
	 * @param file
	 *            文件路径
	 * @param content
	 *            文件内容
	 * @param charset
	 *            写入时候所使用的字符集
	 */
	public  static void writeString(String file, String content, String charset) {
		try {
			byte[] data = content.getBytes(charset);
			writeBytes(file, data);
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}

	}

	/**
	 * 从文件中读取数据，返回类型是字符串String类型
	 * 
	 * @param file
	 *            文件路径
	 * @param charset
	 *            读取文件时使用的字符集，如utf-8、GBK等
	 * @return
	 */
	public static String readString(String file, String charset) {
		byte[] data = readBytes(file);
		String ret = null;

		try {
			ret = new String(data, charset);
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
		return ret;
	}

	/**
	 * 复制单个文件
	 * 
	 * @param oldPath
	 *            String 原文件路径 如：c:/fqf.txt
	 * @param newPath
	 *            String 复制后路径 如：f:/fqf.txt
	 * @return boolean
	 */
	public static void copyFile(String oldPath, String newPath) {
		try {
			int bytesum = 0;
			int byteread = 0;
			File oldfile = new File(oldPath);
			if (oldfile.exists()) { // 文件存在时
				InputStream inStream = new FileInputStream(oldPath); // 读入原文件
				FileOutputStream fs = new FileOutputStream(newPath);
				byte[] buffer = new byte[1024 * 1024];
				int length;
				while ((byteread = inStream.read(buffer)) != -1) {
					bytesum += byteread; // 字节数 文件大小
					System.out.println(bytesum);
					fs.write(buffer, 0, byteread);
				}
				inStream.close();
			}
		} catch (Exception e) {
			System.out.println("复制单个文件操作出错");
			e.printStackTrace();

		}

	}

	/**
	 * 复制整个文件夹内容
	 * 
	 * @param oldPath
	 *            String 原文件路径 如：c:/fqf
	 * @param newPath
	 *            String 复制后路径 如：f:/fqf/ff
	 * @return boolean
	 */
	public static void copyFolder(String oldPath, String newPath) {

		try {
			(new File(newPath)).mkdirs(); // 如果文件夹不存在 则建立新文件夹
			File a = new File(oldPath);
			String[] file = a.list();
			File temp = null;
			for (int i = 0; i < file.length; i++) {
				if (oldPath.endsWith(File.separator)) {
					temp = new File(oldPath + file[i]);
				} else {
					temp = new File(oldPath + File.separator + file[i]);
				}

				if (temp.isFile()) {
					File file2 = new File(newPath + "/" + (temp.getName()).toString());
					nioTransferCopy(temp, file2);
				}
				if (temp.isDirectory()) {// 如果是子文件夹
					copyFolder(oldPath + "/" + file[i], newPath + "/" + file[i]);
				}
			}
		} catch (Exception e) {
			System.out.println("复制整个文件夹内容操作出错");
			e.printStackTrace();

		}

	}

	private synchronized static void nioTransferCopy(File source, File target) {
		FileChannel in = null;
		FileChannel out = null;
		FileInputStream inStream = null;
		FileOutputStream outStream = null;
		try {
			inStream = new FileInputStream(source);
			outStream = new FileOutputStream(target);
			in = inStream.getChannel();
			out = outStream.getChannel();
			in.transferTo(0, in.size(), out);
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (inStream != null)
					inStream.close();
				if (in != null)
					in.close();
				if (outStream != null)
					outStream.close();
				if (out != null)
					out.close();
			} catch (IOException e) {
				e.printStackTrace();
			}

		}
	}

}