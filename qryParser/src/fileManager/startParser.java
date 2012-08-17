package fileManager;

import java.io.File;
import java.io.IOException;

public class startParser {
	
	public static void main(String args[]) throws IOException{
		String ST_dir = "qryFile";
		File myDir = new File(ST_dir);
		File[] myDirList = myDir.listFiles();
		
		int fileCnt = myDirList.length;
		
		for(int i=0;i<fileCnt;i++){
			File file1 = myDirList[i];
			if(file1.getName().matches(".*qry.*")){
				getFiles startMakeFiles = new getFiles(file1);
				startMakeFiles.mainFile();
			}
				
		}
	}
}
