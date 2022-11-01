package git;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
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
	private String pCSha;
	private Tree tree;
	ArrayList<String> treeContents;
	private String summary;
	private String author;
	private String date;
	private String hash;
	private String content;
	public static File head;
	
//	Layout: 
//		- Tree SHA1 Pointer
//		- Parent Commit SHA1 Pointer
//		- Child Commit SHA1 Pointer
	
	public Commit (String summary1, String author1, Commit parent1) throws NoSuchAlgorithmException, IOException {
		nextCommit = null;
		parent = parent1;
		if(parent1 != null) {
			pCSha = parent1.returnSha();
		}else {
			pCSha = "";
		}
		summary = summary1;
		author = author1;
		treeContents = getTreeContents();
		tree = new Tree(treeContents);
		date = this.getDate();
		
		
		//SHA1 hash is produced by new line deliminated info
//		String pSHA = "";
//		if (parent == null)
//			pSHA = "";
//		else
//			pSHA = "./objects/" + parent.returnSha();
		
		String info = summary + "\n" + date + "\n" + author + "\n" + getTreeSha1();
		hash = toSHA1(contentOfFile());
		this.connectParent();
		makeComFile();
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
	//Writes or updates CommitFile into objects Folder
	private void makeComFile() throws IOException, NoSuchAlgorithmException{
		Files.deleteIfExists(Paths.get("./objects/"+hash));
		File comt = new File("./objects/"+hash);
		comt.createNewFile();
		FileWriter fw = new FileWriter(comt);
		BufferedWriter bw = new BufferedWriter(fw);
		bw.write(contentOfFile());
		bw.close(); fw.close();
	}
	
	public boolean editFile(String fileName) throws NoSuchAlgorithmException, IOException {
//		FileWriter fw = new FileWriter("./index");
//		BufferedWriter bw = new BufferedWriter(fw);
//		bw.append("*edited* "+fileName);
//		bw.close();
//		fw.close();
		return true;
	}
	
	public boolean deleteFile(String fileName) throws NoSuchAlgorithmException, IOException {
//		FileWriter fw = new FileWriter("./index");
//		BufferedWriter bw = new BufferedWriter(fw);
//		bw.append("*deleted* "+fileName);
//		bw.close();
//		fw.close();
		return true;
	}
	
	
	private ArrayList<String> getTreeContents() throws NoSuchAlgorithmException, IOException {
		ArrayList<String> toAdd = new ArrayList<String>();
		ArrayList<String> edtDel = new ArrayList<String>();
		String fileName;
		String sha;
		File ind = new File("./index");
		Scanner sc = new Scanner(ind);
		while(sc.hasNextLine()) {
			
			fileName = sc.next();
			System.out.println("READING INDEX--> filename:"+fileName);
			if(fileName.contains("*")) {
				fileName = sc.next();
				System.out.println("filename: "+fileName+"  TreeSha1: "+ parent.getTreeSha1());
				edtDel.add(findFileSha(fileName, parent.getTreeSha1()));
				System.out.println("ADDED EDDL: "+findFileSha(fileName, parent.getTreeSha1()));
			}else {
				
				sha = (sc.next() + sc.next()).substring(1);
				System.out.println("ADDING: blob : "+sha + " "+fileName);
				toAdd.add("blob : "+sha + " "+fileName);
			}
			
		}
		
		File hd = new File("HEAD");
		Scanner headSc = new Scanner(hd);
		if(sc.hasNext()) {
			String headSha = sc.next();
		}
		if(edtDel.isEmpty()) {
			if (parent != null) {
				toAdd.add("tree : "+ parent.getTreeSha1());
			}
		}else {
			toAdd = makeAddArray(toAdd, edtDel, parent.getTreeSha1(), parent.getTreeSha1());
		}
//		if (parent != null) {
//			toAdd.add("tree : "+ parent.getTreeSha1());
//		}
		sc.close();
		return toAdd;
		
	}
	
	private String findFileSha(String fileName, String trSha) throws FileNotFoundException {
		File trFl = new File("./objects/"+trSha);
		Scanner trScanner = new Scanner(trFl);
		String sha;
		String tSha="";
		String fileN; 
		while(trScanner.hasNextLine() && trScanner.hasNext()) {
			sha = trScanner.next() + trScanner.next();
			if(sha.contains("tree:")) {
				tSha = trScanner.next();
			}else {
				System.out.println("SHA: " + sha);
				sha = trScanner.next();
				fileN = trScanner.next();
				if(fileName.equals(fileN)) {
					return sha;
				}
				//sha = trScanner.next();
			}
		}
		if(tSha.equals(""))
			return "";
		return findFileSha(fileName, tSha);
	}
	
	private ArrayList<String> makeAddArray(ArrayList<String> add, ArrayList<String> edDl, String trSha, String conTr) throws FileNotFoundException{
	
		String addStr;
//		File cmFl = new File("./objects/"+cmSha);
//		Scanner trSc = new Scanner(cmFl);
//		String trSha = trSc.nextLine().substring(10);
		if(trSha.equals("")) {
			if(!conTr.equals("")) {
				File conTrFl = new File("./objects/"+conTr);
				Scanner cTFSc = new Scanner(conTrFl);
				ArrayList<String> conTrArr = new ArrayList<String>();
				String cTrAdd;
				boolean clearContent = true;
				if(cTFSc.hasNextLine()) {
					cTrAdd = cTFSc.nextLine();
					System.out.println("CTRADD: "+cTrAdd);
					conTrArr.add(cTrAdd);
					clearContent = clearContent == true && add.contains(cTrAdd);
					while(cTFSc.hasNextLine() && cTFSc.hasNext()) {
						cTrAdd = cTFSc.nextLine();
						System.out.println("CTRADD: "+cTrAdd);
						conTrArr.add(cTrAdd);
						clearContent = clearContent == true && add.contains(cTrAdd);
					}
					System.out.println("CLRCONT: " + clearContent +", SIZE: "+add.size());
				}
				if(clearContent) {
					for(int i = add.size()-1; i>=0; i--) {
						if(conTrArr.contains(add.get(i))) {
							System.out.println("CLEARING: "+add.get(i) + " AT INDEX "+i +" size "+add.size());
							add.remove(i);
						}
					}
				}
			}
			
			if(!conTr.equals("")) {
				add.add("tree : "+conTr);
			}
			return add;
		}
		File trFl = new File("./objects/"+trSha);
		String parTrSha = "";
		Scanner addSc = new Scanner(trFl);
		boolean setNewTr = false;
		while(addSc.hasNextLine()) {
			addStr = addSc.nextLine();
			System.out.println("ADDSTR: " + addStr);
			String addSha = addStr.substring(7, addStr.length()-10);
			System.out.println("ADDSHA: " + addSha);
			if(addStr.contains("tree :")) {
				parTrSha = addStr.substring(7);
			}else {
				System.out.println("EDDL AT 0: "+edDl.get(0) + ", "+addSha+", "+edDl.contains(addSha));
				if(edDl.contains(addSha)) {
					System.out.println("EDDL FOUND FOR " + addStr);
					setNewTr = true;
				}else {
					System.out.println("ADDING: "+addStr);
					add.add(addStr);
				}
			}
		}
		
		addSc.close();
		
		if(setNewTr) { 
				conTr = parTrSha;
		}
		
		return makeAddArray(add, edDl, parTrSha, conTr);
	}
	
	public String getTreeSha1() {
		System.out.println("Tree Contents: \n"+treeContents);
		return (tree.getSha1());
	}

	private String toSHA1(String value) throws NoSuchAlgorithmException, UnsupportedEncodingException {
		MessageDigest digest = MessageDigest.getInstance("SHA-1");
		digest.reset();
		digest.update(value.getBytes("utf8"));
		String sha1 = String.format("%040x", new BigInteger(1, digest.digest()));
		return sha1;
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
	private String contentOfFile () throws NoSuchAlgorithmException, IOException {
		String pSHA = "";
		String treeSha = this.getTreeSha1();
		String c = "";
		if (nextCommit != null) {
			c = "./objects/" + nextCommit.returnSha();
		}
		if (parent != null)
			pSHA = "./objects/" + parent.returnSha();
		if (nextCommit != null)
			c = "./objects/" + nextCommit.returnSha();
		if(treeSha == null) {
			treeSha = "";
		}
		
		
		content = "./objects/"+treeSha + "\n" + pSHA + "\n" + c + "\n" + author + "\n" + date + "\n" + summary;
		return content;
	}
	
	
	
	public String returnSha () throws NoSuchAlgorithmException, IOException {
		return hash;
	}

	//sets the parent's nextCommit to child
	private void setNextCommit (Commit child) throws NoSuchAlgorithmException, IOException {
		System.out.println("Commit "+hash+" setting child " + child.returnSha()+"\n");
		nextCommit = child;
		makeComFile();
	}
	
	//sets the parent's nextCommit to this Commit
	private boolean connectParent () throws NoSuchAlgorithmException, IOException {
		if (parent != null) {
			System.out.println("\nCommit "+this.returnSha()+" as child");
			parent.setNextCommit(this);
			return true;
		}
		return false;
	}

	//generates SHA1Hash
	private String generateFileSHA1 (String filePath) throws IOException, NoSuchAlgorithmException {
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
		String readStr = "";
		File read = new File(filePath);
		Scanner sc = new Scanner(read);
		for(readStr = sc.nextLine(); sc.hasNextLine(); readStr += sc.nextLine()){}
		System.out.println("ReadStr: "+ readStr);
		hash = toSHA1(readStr);
		System.out.println("ReadStr Hash: "+hash);
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
		System.out.println("\nFile name:" + fileName + "\ncontent:\n" + content);
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
