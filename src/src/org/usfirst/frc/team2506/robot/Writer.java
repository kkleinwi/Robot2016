package org.usfirst.frc.team2506.robot;

import java.io.*;

public class Writer {
	File file;
	FileOutputStream output = null;
	
	public Writer(String path) {
		try {
			file = new File(path);
			output = new FileOutputStream(file);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void write(String text) {
		try {
			output.write(text.getBytes());
			output.flush();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	public void close() {
		try {
			output.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
