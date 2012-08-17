package fileManager;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;

public class getFiles {
	File mFile;
	String fileName = "";
	String comment = "";
	ArrayList<String> arrayBlock = new ArrayList<String>();
	public getFiles(File file) {
		// TODO Auto-generated constructor stub
		mFile = file;
		fileName = mFile.getName().substring(0, mFile.getName().indexOf(".qry"));
	}
	
	public void mainFile() throws IOException{
		ArrayList<String> qryTxt;
		boolean attr = false;
		ArrayList<fieldData> dataBucks = null;
		ArrayList<ArrayList<fieldData>> inDataBucks;
		ArrayList<ArrayList<fieldData>> outDataBucks;
		boolean input = false;
		boolean output = false;
		boolean getStart = false;
		
		inDataBucks = new ArrayList<ArrayList<fieldData>>();
		outDataBucks = new ArrayList<ArrayList<fieldData>>();
		
		try {
			
			BufferedReader br1 = new BufferedReader(new InputStreamReader(new FileInputStream(mFile),"euc-kr"));
			String line;
			
			qryTxt = new ArrayList<String>();
			
			while((line = br1.readLine())!=null){
				qryTxt.add(line);
				String[] readline = line.split(",");
				if(readline.length>=3){
					if(readline[2].matches(".*input.*")){
						input = true;
					}else if(readline[2].matches(".*output.*")){
						if(readline[1].matches(".*OCCUR.*"))
							arrayBlock.add(Integer.toString(outDataBucks.size()));
						output = true;
					}
				}
				
				if(line.matches(".*begin.*")){
					getStart = true;
					dataBucks = new ArrayList<getFiles.fieldData>();
					continue;
				}else if(line.matches(".*end.*")){
					getStart = false;
					if(input){
						inDataBucks.add(dataBucks);
					}else if(output){
						outDataBucks.add(dataBucks);
					}
					input = false;
					output = false;
					
				}
				if(getStart){
						String[] templine = line.split(","); 
						if(templine.length>1){
							fieldData tempLine = new fieldData(templine);
							dataBucks.add(tempLine);	
						}
					
				}
					
			}
			
			String[] attrBoolean = qryTxt.get(2).split(",");
			
			// 속성데이터가 발견되면 해당값은 속성이 있는 함수로 지정
			if(attrBoolean[3].matches(".*ATT."))
				attr = true;
			
			comment = attrBoolean[1];
			br1.close();
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		
		makeXMLfile makeFile = new makeXMLfile(inDataBucks, outDataBucks);
		makeFile.isAttr = attr;
		makeFile.commentData = comment;
		makeFile.TRName = fileName;
		makeFile.arrayBlocks = arrayBlock;
		makeFile.initFile();
				
	}
	
	static class fieldData{
		String[] cRecordData;
		String fileName = "";
		String fileType = "";
		String fileSize = "0";
		
		public fieldData(String[] recordData) {
			cRecordData = recordData;
			init();
		}
		public void init(){
			fileName = cRecordData[0].trim();
			fileType = cRecordData[3].trim();
			fileSize = cRecordData[4].trim();
		}
	}
}
