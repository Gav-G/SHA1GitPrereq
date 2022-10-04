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
		
		String newFile = b.generateSHA1Hash("./objects/"+fromList.substring(7,fromList.substring(7).indexOf(" ")+7)); // fromList is not a filepath -- this is why it's throwing an error
		System.out.println("TREE: " + "./objects/"+fromList.substring(7,fromList.substring(7).indexOf(" ")+7));
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
