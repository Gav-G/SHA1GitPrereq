import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;

import git.Blob;
import git.Commit;
import git.Index;
import git.Tree;

public class TryingTreeOut {

	public static void main(String[] args) throws NoSuchAlgorithmException, IOException {
		// TODO Auto-generated method stub
		Index ind = new Index();
		ind.init();
		Blob blob1 = new Blob("blob1.txt");
		Blob blob2 = new Blob("blob2.txt");
		Blob blob3 = new Blob("blob3.txt");
		Blob blob4 = new Blob("blob4.txt");
		Blob blob5 = new Blob("blob5.txt");
		ind.add("blob1.txt");
		ind.add("blob2.txt");
		ind.add("blob3.txt");
		ind.add("blob4.txt");
		ind.add("blob5.txt");
		
		Commit com1 = new Commit("summary1", "Author1", null);
		com1.writesFileToObjects();
		
//		Commit com2 = new Commit("summary2", "Author2", com1);
//		com2.writesFileToObjects();
//		
//		Commit com3 = new Commit("summary3", "Author3", com2);
//		com3.writesFileToObjects();
//		
//		Commit com4 = new Commit("summary4", "Author4", com3);
//		com4.writesFileToObjects();
//		
//		Commit com5 = new Commit("summary5", "Author5", com4);
//		com5.writesFileToObjects();
		
	}

}
