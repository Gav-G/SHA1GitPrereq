package git;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class Index {
	private File index;
	private File obj;
//	private HashMap <String, String> txtMap;
	private String hash;
	private File head;

	public Index () {
		index = new File ("./index");
		head = new File("./HEAD");
		obj = new File ("./objects");
		obj.mkdir();
	}

	// initializes index file and objects folder in test
	public void init () throws IOException {
		if(head != null)
			head.delete();
		index.delete();
		head.delete();
		if (obj.exists()) {
			File[] contents = obj.listFiles();
			for (File f : contents) {
	            f.delete();
	        }
		}
		index = new File ("./index");
		head = new File("./HEAD");
		obj = new File ("./objects");
		index.createNewFile();
		head.createNewFile();
		obj.createNewFile();
		obj.mkdir(); 
//		txtMap = new HashMap <String, String> ();
	}

	//creates blob from file, stores hash in hashmap, and updates index file
	// file MUST be created in the same project
	public void add (String fileName) throws NoSuchAlgorithmException, IOException {
		Blob blob = new Blob (fileName);
		hash = blob.getHash();
		String indCont = "";
		Scanner sc = new Scanner(index);
		if(sc.hasNextLine()) {
			indCont= sc.nextLine();
		while(sc.hasNextLine()) {
			indCont += "\n"+sc.nextLine();
		}
		}
		BufferedWriter bw = new BufferedWriter(new FileWriter(index));
		bw.append(indCont + "\n"+fileName + " : " + hash);
		bw.close();
		//txtMap.put (fileName, hash);
//		this.updateIndex();
	}
	
	public boolean editFile(String fileName) throws NoSuchAlgorithmException, IOException {
		writeIndex("*edited* "+fileName);
		return true;
	}
	
	public boolean deleteFile(String fileName) throws NoSuchAlgorithmException, IOException {
		writeIndex("*deleted* "+fileName);
		return true;
	}
	//writes a string in Index
	private void writeIndex(String toAdd) throws IOException {
		String indCont = "";
		Scanner sc = new Scanner(index);
		if(sc.hasNextLine()) {
			indCont= sc.nextLine();
			while(sc.hasNextLine()) {
				indCont += "\n"+sc.nextLine();
			}
		}
		BufferedWriter bw = new BufferedWriter(new FileWriter(index));
		bw.append(indCont+"\n"+toAdd);
		bw.close();
	}
	
	

//	public void updateIndex () throws IOException {
//		BufferedWriter bf = new BufferedWriter(new FileWriter(index));
//
//		// iterate map entries
//		for (Map.Entry<String, String> entry :
//			txtMap.entrySet()) {
//
//			// put key and value separated by a colon
//			bf.write(entry.getKey() + " : "
//					+ entry.getValue());
//
//			// new line
//			bf.newLine();
//		}
//		bf.close();
//	}
	
	// removes file from index and objects folder
//	public void remove (String fileName) throws IOException, NoSuchAlgorithmException {
//		File f = new File ("objects/"+txtMap.get(fileName)+".zip");
//		f.delete();
//		txtMap.remove(fileName);
//		this.updateIndex();
//	}
}


