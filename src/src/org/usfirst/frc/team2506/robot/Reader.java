package org.usfirst.frc.team2506.robot;

import java.io.*;

public class Reader {
	private FileReader fileReader;
	private BufferedReader input;
	
	public Reader(String path) {
		try {
			fileReader = new FileReader(path);
			input = new BufferedReader(fileReader);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public String readLine() {
		try {
			return input.readLine();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			return null;
		}
	}
}
