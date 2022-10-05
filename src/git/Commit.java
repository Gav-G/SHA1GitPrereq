package git;

import java.io.BufferedWriter;
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
import java.util.Base64;
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
	private File head;
	
	public Commit (String summary1, String author1, Commit parent1) throws NoSuchAlgorithmException, IOException {
		nextCommit = null;
		parent = parent1;
		summary = summary1;
		author = author1;
		treeContents = getTreeContents();
		tree = new Tree(treeContents);
		date = this.getDate();
		this.connectParent();
		
		//SHA1 hash is produced by new line deliminated info
//		String pSHA = "";
//		if (parent == null)
//			pSHA = "";
//		else
//			pSHA = "./objects/" + parent.returnSha();
		
		String info = summary + "\n" + date + "\n" + author + "\n" + getTreeSha1();
		this.writeFile("./hashFile", info);
		this.generateSHA1Hash("./hashFile");
		File hashFile = new File ("./hashFile");
		hashFile.delete();
		if(head != null)
			head.delete();
		head = new File("./HEAD");
		head.createNewFile();
		FileWriter fw = new FileWriter("./HEAD");
		fw.write(hash);
		fw.close();
		Files.deleteIfExists(Paths.get("./index"));
		File ind = new File("./index");
		ind.createNewFile();
		
	}
	
	public boolean editFile(String fileName) throws NoSuchAlgorithmException, IOException {
		FileWriter fw = new FileWriter("./index");
		BufferedWriter bw = new BufferedWriter(fw);
		bw.append("*edited* "+fileName);
		bw.close();
		fw.close();
		return true;
	}
	
	public boolean deleteFile(String fileName) throws NoSuchAlgorithmException, IOException {
		FileWriter fw = new FileWriter("./index");
		BufferedWriter bw = new BufferedWriter(fw);
		bw.append("*deleted* "+fileName);
		bw.close();
		fw.close();
		return true;
	}
	
	
	private ArrayList<String> getTreeContents() throws NoSuchAlgorithmException, IOException {
		ArrayList<String> arr = new ArrayList<String>();
		String fileName;
		String sha;
		File ind = new File("./index");
		Scanner sc = new Scanner(ind);
		while(sc.hasNextLine()) {
			fileName = sc.next();
			if(fileName.charAt(0) == '*') {
				String nFileName = sc.next();
				for(int i = 0; i<arr.size(); i++) {
					if(arr.get(i).contains(nFileName)) {
						arr.remove(i);
						break;
					}
				}
			}else {
				sha = (sc.next() + sc.next()).substring(1);
				arr.add("blob : "+sha + " "+fileName);
			}
		}
		sc.close();
		if (parent != null) {
			arr.add("tree : "+ parent.getTreeSha1());
		}
		return arr;
		
	}
	
	public String getTreeSha1() {
		return (tree.getSha1());
	}

	private String toSHA1(String str) throws NoSuchAlgorithmException {
		byte[] convertme = str.getBytes();
		MessageDigest md = MessageDigest.getInstance("SHA-1");
		return Base64.getEncoder().encodeToString(md.digest(convertme));
	}
//	
	//creates the SHA-named file in objects
		public void writesFileToObjects () throws IOException, NoSuchAlgorithmException {
			
			this.contentOfFile();
//			File obj = new File ("./objects");
//			obj.mkdir();
			this.createsNewFile();
			File commitText = new File ("commit.txt");
			commitText.delete();
		}
	
	//creates a file with these contents
	private File contentOfFile () throws NoSuchAlgorithmException, IOException {
		String pSHA = "";
		String treeSha = this.getTreeSha1();
		String c = "";
		if (nextCommit != null) {
			c = "./objects/" + nextCommit.returnSha();
		}
		if (parent != null)
			pSHA = "./objects/" + parent.returnSha();
		if (nextCommit != null)
			//c = "./objects/" + nextCommit.returnSha();
		if(treeSha == null) {
			treeSha = "";
		}
		
		
		content = "./objects/"+treeSha + "\n" + pSHA + "\n" + c + "\n" + author + "\n" + date + "\n" + summary;
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
		//Files.deleteIfExists(Paths.get("./objects/"+hash));
		File updated = new File("./objects/"+hash);
		this.contentOfFile();
		BufferedWriter bw = new BufferedWriter(new FileWriter(updated));
		bw.append(content);
		bw.close();
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
//		//https://gist.github.com/zeroleaf/6809843
//		FileInputStream fileInputStream = new FileInputStream(filePath);
//		MessageDigest digest = MessageDigest.getInstance("SHA-1");
//		DigestInputStream digestInputStream = new DigestInputStream(fileInputStream, digest);
//		byte[] bytes = new byte[1024];
//		// read all file content
//		while (digestInputStream.read(bytes) > 0);
//
//		// digest = digestInputStream.getMessageDigest();
//		byte[] resultByteArry = digest.digest();
//		hash = bytesToHexString(resultByteArry);
//		return hash;
		hash = toSHA1(filePath);
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
		System.out.println("\nFile name:" + fileName + "\n\ncontent:\n" + content);
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
