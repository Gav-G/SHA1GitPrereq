package git;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class Blob {
	private String fileContent;
	private String SHA1Hash;
	private String storesZippedContent;
	
	//creates Blob from file path
	public Blob (String filePath) throws IOException, NoSuchAlgorithmException {
		fileContent = this.content(filePath);
		SHA1Hash = getSHA(fileContent);
		//this.generateSHA1Hash(filePath);
		String hashFile = this.createsNewFile();
	//	String zipFilePath = this.zipFile(hashFile);
		//storesZippedContent = this.content(zipFilePath);
		System.out.println ("Reading contents of " + filePath + ": " + fileContent);

		//only displays the first 10 letters of the hash
		//System.out.println ("Creating new blob " + SHA1Hash.substring(0,10) + " from content: " + storesZippedContent);
		

	}
		

	private String getSHA(String value) throws NoSuchAlgorithmException, UnsupportedEncodingException {
		MessageDigest digest = MessageDigest.getInstance("SHA-1");
		digest.reset();
		digest.update(value.getBytes("utf8"));
		String sha1 = String.format("%040x", new BigInteger(1, digest.digest()));
		return sha1;
	}
	
	//generates a Hash String for given filepath
	public String generateSHA1Hash (String filePath) throws IOException, NoSuchAlgorithmException {
		//https://gist.github.com/zeroleaf/6809843
		FileInputStream fileInputStream = new FileInputStream(filePath);
		MessageDigest digest = MessageDigest.getInstance("SHA-1");
		DigestInputStream digestInputStream = new DigestInputStream(fileInputStream, digest);
		byte[] bytes = new byte[1024];
		// read all file content
		while (digestInputStream.read(bytes) > 0);

		// digest = digestInputStream.getMessageDigest();
		byte[] resultByteArry = digest.digest();
		SHA1Hash = bytesToHexString(resultByteArry);
		return SHA1Hash;
	}
	
	public String getHash () {
		return (SHA1Hash);
	}
	
	public String getZipContent () {
		return storesZippedContent;
	}
	
	
	// reads contents of the file
	public String content (String filepath) throws IOException {
		File file = new File (filepath);
		BufferedReader br = new BufferedReader(new FileReader(file)); 
		StringBuilder sb = new StringBuilder(); 
		String line = br.readLine(); 
		while (line != null) { 
			sb.append(line).append("\n"); 
			line = br.readLine(); 
		} 
		String fileAsString = sb.toString();
		return fileAsString;
	} 

	// creates new file in objects folder labeled by hash with same content as original
	public String createsNewFile () throws IOException {
		File f = new File ("objects/" + SHA1Hash);
		String path = f.getAbsolutePath();
		FileWriter writer = new FileWriter(path);
		
		writer.write (fileContent);
		writer.close();
		return path;
	}
	
	
	// converts file to zipped format
	// opens unzipped file in the objects folder
	public String zipFile (String sourceFile) throws IOException {
		File f = new File (sourceFile);
		String zipFile = sourceFile + ".zip";
		FileOutputStream fos = new FileOutputStream(zipFile);
		ZipOutputStream zipOut = new ZipOutputStream(fos);
		File fileToZip = new File(sourceFile);
		FileInputStream fis = new FileInputStream(fileToZip);
		ZipEntry zipEntry = new ZipEntry(fileToZip.getName());
		zipOut.putNextEntry(zipEntry);
		byte[] bytes = new byte[1024];
		int length;
		while((length = fis.read(bytes)) >= 0) {
			zipOut.write(bytes, 0, length);
		}
		zipOut.close();
		fis.close();
		fos.close();
		f.delete();
		return zipFile;
	}
	
	// some method that helps with generating hash
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

