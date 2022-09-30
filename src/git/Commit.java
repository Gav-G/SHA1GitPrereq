package git;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Scanner;

public class Commit {
	private Commit nextCommit;
	private Commit parent;
	private String pTree;
	private Tree tree;
	ArrayList<String> treeContents;
	private String summary;
	private String author;
	private String date;
	private String hash;
	private String content;
	
	public Commit (String treeSHAPath, String summary1, String author1, Commit parent1) throws NoSuchAlgorithmException, IOException {
		nextCommit = null;
		parent = parent1;
		summary = summary1;
		author = author1;
		treeContents = getTreeContents();
		tree = new Tree(treeContents);
		date = this.getDate();
		this.connectParent();
		//SHA1 hash is produced by new line deliminated info
		String pSHA = "";
		if (parent == null)
			pSHA = "";
		else
			pSHA = "./objects/" + parent.returnSha();
		String info = summary + "\n" + date + "\n" + author + "\n" + pSHA;
		this.writeFile("./hashFile", info);
		this.generateSHA1Hash("./hashFile");
		File hashFile = new File ("./hashFile");
		hashFile.delete();
	}
	
	private ArrayList<String> getTreeContents() {
		ArrayList<String> arr = new ArrayList<String>();
		String fileName;
		String sha;
		Scanner sc = new Scanner("./index");
		while(sc.hasNextLine()) {
			fileName = sc.next();
			sha = (sc.next() + sc.next()).substring(1);
			arr.add("blob : "+sha + " "+fileName);
		}
		sc.close();
		return arr;
		
	}
	
	
	
	//creates the SHA-named file in objects
		public void writesFileToObjects () throws IOException, NoSuchAlgorithmException {
			
			this.contentOfFile();
			File obj = new File ("./objects");
			obj.mkdir();
			this.createsNewFile();
			File commitText = new File ("commit.txt");
			commitText.delete();
		}
	
	//creates a file with these contents
	private File contentOfFile () throws NoSuchAlgorithmException, IOException {
		String pSHA = "";
		String c = "";
		if (parent != null)
			pSHA = "./objects/" + parent.returnSha();
		if (nextCommit != null)
			c = "./objects/" + nextCommit.returnSha();
		
		content = pTree + "\n" + pSHA + "\n" + c + "\n" + author + "\n" + date + "\n" + summary;
		this.writeFile("commit.txt", content);
		File contentFile = new File ("./commit.txt");
		return contentFile;
	}
	
	private String returnSha () throws NoSuchAlgorithmException, IOException {
		return hash;
	}

	//sets the parent's nextCommit to child
	private void setNextCommit (Commit child) throws NoSuchAlgorithmException, IOException {
		nextCommit = child;
	}
	
	//sets the parent's nextCommit to this Commit
	private boolean connectParent () throws NoSuchAlgorithmException, IOException {
		if (parent != null) {
			parent.setNextCommit(this);
			return true;
		}
		return false;
	}

	//generates SHA1Hash
	private String generateSHA1Hash (String filePath) throws IOException, NoSuchAlgorithmException {
		//https://gist.github.com/zeroleaf/6809843
		FileInputStream fileInputStream = new FileInputStream(filePath);
		MessageDigest digest = MessageDigest.getInstance("SHA-1");
		DigestInputStream digestInputStream = new DigestInputStream(fileInputStream, digest);
		byte[] bytes = new byte[1024];
		// read all file content
		while (digestInputStream.read(bytes) > 0);

		// digest = digestInputStream.getMessageDigest();
		byte[] resultByteArry = digest.digest();
		hash = bytesToHexString(resultByteArry);
		return hash;
		
	}
	
	//returns the exact date
	public String getDate () {
		Calendar cal = Calendar.getInstance();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String d = sdf.format(cal.getTime());
		return d.substring(0, 10);
	}
	
	//writes String into file
	private void writeFile (String fileName, String content) throws IOException {
		
		 Path p = Paths.get(fileName);
	        try {
	            Files.writeString(p, content, StandardCharsets.ISO_8859_1);
	        } catch (IOException e) {
	            // TODO Auto-generated catch block
	            e.printStackTrace();
	        }
	}
	
	//names the file 
	private String createsNewFile () throws IOException {
		File f = new File ("objects/" + hash);
		String path = f.getAbsolutePath();
		FileWriter writer = new FileWriter(path);
		
		writer.write (content);
		writer.close();
		return path;
	}
	
	//helper method
	public static String bytesToHexString(byte[] bytes) {
		StringBuilder sb = new StringBuilder();
		for (byte b : bytes) {
			int value = b & 0xFF;
			if (value < 16) {
				// if value less than 16, then it's hex String will be only
				// one character, so we need to append a character of '0'
				sb.append("0");
			}
			sb.append(Integer.toHexString(value).toUpperCase());
		}
		return sb.toString();
	}
}
