package git;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;

public class Tree {
	public String sha1;
	public Tree (ArrayList <String> listy) throws NoSuchAlgorithmException, IOException {
		//create string from arraylist create string with new line
		//turn string into parameter
		//create file from sha1
		String fromList = listy.get(0);
		for (int i = 1; i < listy.size(); i ++) {
			fromList += "\n"+listy.get(i);
		}
		System.out.println("\n\nLIST: \n"+fromList+"\n");
		FileWriter fOne = new FileWriter("tree.txt");//output file
		PrintWriter printW = new PrintWriter (fOne);//writing stuff onto fw
		printW.write(fromList);
		if(printW != null) {
		   printW.flush();
		   printW.close();
		}
		Blob b = new Blob ("tree.txt");
		String newFile;
		String fileSha;
		if(fromList.substring(0,4).equals("tree")) {
			fileSha = fromList.substring(7);
			newFile = b.generateSHA1Hash(fileSha);
		}else {
			fileSha = fromList.substring(7,fromList.substring(7).indexOf(" ")+7);
			newFile = b.generateSHA1Hash("./objects/"+fileSha); // fromList is not a filepath -- this is why it's throwing an error
		}
		
		System.out.println("TREE: " + "./objects/"+newFile);
		sha1 = newFile;
		FileWriter fTwo = new FileWriter(newFile);//output file
		PrintWriter printW2 = new PrintWriter (fTwo);//writing stuff onto fw
		printW2.write("fromList");
		if(printW2 != null) {
		   printW2.flush();
		   printW2.close();
		}
	}
	

	
	public String getSha1() {
		return sha1;
	}
}
//package git;
//import java.io.BufferedWriter;
//import java.io.File;
//import java.io.FileWriter;
//import java.io.IOException;
//import java.io.PrintWriter;
//import java.nio.file.Files;
//import java.nio.file.Paths;
//import java.security.MessageDigest;
//import java.security.NoSuchAlgorithmException;
//import java.util.*;
//
//public class Tree {
//	String treeSha1;
//	String treeStr;
//	ArrayList<String> arr;
//	public Tree(ArrayList<String> arr) throws NoSuchAlgorithmException, IOException {
//		this.arr = arr;
//		treeStr = arrLstToStr(arr);
//		treeSha1 = getSHA(treeStr);
//
//		
//		Files.deleteIfExists(Paths.get("./objects/" + treeSha1));
//		File treeFile = new File("./objects", treeSha1);
//		treeFile.createNewFile();
//		BufferedWriter bw = new BufferedWriter(new FileWriter(treeFile));
//		//FileWriter fw = new FileWriter(treeFile);
//		bw.write(treeStr);
//		bw.close();
//		
//	}
//	
//	private String arrLstToStr(ArrayList<String> array) {
//		String arrStr = array.get(0);
//		if(array.size()>1) {
//			for(int i = 1; i<array.size(); i++) {
//				arrStr += array.get(i)+"\n";
//			}
//		}
//		return(arrStr);
//	}
//	
//	public String getSha1() {
//		return treeSha1;
//	}
//	
//	
//	private String getSHA(String convertme) throws NoSuchAlgorithmException {
//		byte[] bytes = convertme.getBytes();
//	    MessageDigest md = MessageDigest.getInstance("SHA-1");
//	    return Base64.getEncoder().encodeToString(md.digest(bytes));
//	}
//}
