package fileManager;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Attr;
import org.w3c.dom.Comment;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import fileManager.getFiles.fieldData;

public class makeXMLfile {
	ArrayList<ArrayList<fieldData>> mInBox, mOutBox;
	String TRName = "";
	String commentData = "";
	boolean isArray = false;
	boolean isAttr = false;
	String trans = "<transaction>";
	StringBuffer totalBuffer = new StringBuffer();
	String setAttrr = "\t\t\t<set length=\'1\' type=\"hex\"></set>\n";
	String getAttrr = "\t\t\t\t<get length=\'1\' type=\"hex\"></get>\n";
	ArrayList<String> arrayBlocks = new ArrayList<String>();
	
	public makeXMLfile(ArrayList<ArrayList<fieldData>> inBox, ArrayList<ArrayList<fieldData>> outBox) {
		mInBox = inBox;
		mOutBox = outBox;
	}
	
	public void initFile(){
		totalBuffer.append("<!-- " + commentData + " - " + TRName + "-->\n");
		totalBuffer.append(trans + "\n");
		totalBuffer.append("\t<tr name=\""+TRName+"\">\n");
		
		int inSize = mInBox.size();
		int outSize = mOutBox.size();
		
		for(int i=0;i<inSize;i++){
			totalBuffer.append(makeInFile(mInBox.get(i)));
		}
		totalBuffer.append("\t\t<receive>\n");
		int length = 0;
		for(int i=0;i<outSize;i++){
			try {
				length = arrayBlocks.size();	
			} catch (Exception e) {}
			
			boolean arraybool = false;
			for(int a=0;a<length;a++){
				if(i==Integer.parseInt(arrayBlocks.get(a)))
					arraybool = true;
					
			}
			totalBuffer.append(makeOutFile(mOutBox.get(i),arraybool));
		}
		totalBuffer.append("\t\t</receive>\n");
		totalBuffer.append("\t</tr>\n");
		totalBuffer.append("</transaction>");
		
		fileCreate(totalBuffer);
	}
	
	  public void fileCreate(StringBuffer InFile){
	      String path = TRName + ".xml";	
		  OutputStreamWriter out = null;
		try {
			out = new OutputStreamWriter(new FileOutputStream(path), "UTF-8");
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}  
//	        File CreateFile = new File(CreateFilePath + CreateFileName);
	        String StringInFile = new String(InFile);
            try {
            	out.write(StringInFile);
            	out.close();
            	System.out.println(path + " created!!!!");
            } catch (IOException ex) {}
	    }
	
	public StringBuffer makeInFile(ArrayList<fieldData> box){
		StringBuffer buf = new StringBuffer();
		buf.append("\t\t<send>\n");
		int size = box.size();
		
		for(int i=0;i<size;i++){
			fieldData temp;
			temp = box.get(i);
			buf.append(setvalue(temp.fileSize, temp.fileName));
			if(isAttr)
				buf.append(setAttrr);
		}
		buf.append("\t\t</send>\n");

		return buf;
	}

	public StringBuffer makeOutFile(ArrayList<fieldData> box, boolean isArr){
		StringBuffer buf = new StringBuffer();
		int size = box.size();
		if(isArr)
			buf.append("\t\t\t<get length=\"4\" name=\"COUNT\"></get>\n\n");
		buf.append("\t\t\t<record>\n");
		for(int i=0;i<size;i++){
			fieldData temp;
			temp = box.get(i);
			buf.append(getvalue(temp.fileSize, temp.fileName));
			if(isAttr)
				buf.append(getAttrr);
		}
		buf.append("\t\t\t</record>\n");
		
		return buf;
	}
	
	public String setvalue(String num, String name){
		String result = "\t\t\t<set length=\"" + num + "\" name=" + "\"" + name + "\">" + "</set>\n";
		return result;
	}
	
	public String getvalue(String num, String name){
		String result = "\t\t\t\t<get length=\"" + num + "\" name=" + "\"" + name + "\">" + "</get>\n";
		return result;
	}
	
	public void makeFile2(){
		
		
		try {
			DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
			
			// root elements
			Document doc = docBuilder.newDocument();
			String data = TRName + "-" + commentData;
			Comment comment = doc.createComment(data);
			doc.appendChild(comment);
			
			Element rootElement = doc.createElement("transaction");
			doc.appendChild(rootElement);

			
//			Attr attrTemp5 = doc.createAttribute("length");
//			recoedd.setAttributeNode(attrTemp5);
			
			
			// staff elements
			Element staff = doc.createElement("tr");
			rootElement.appendChild(staff);
	 
			// set attribute to staff element
			Attr attr = doc.createAttribute("name");
			attr.setValue(TRName);
			staff.setAttributeNode(attr);
	
			// input block
			Element send = doc.createElement("send");
			staff.appendChild(send);
	 		//여기서부터 인풋블락을 완성해야 함

			// set attribute to staff element
			int inBoxSize = mInBox.size();
			Element setInput;
			Attr attrTemp;
			for(int i=0;i<inBoxSize;i++){
				ArrayList<fieldData> tempInput = mInBox.get(i);
				int totalInputLength = tempInput.size();
				for(int k=0;k<totalInputLength;k++){
					fieldData tempData = tempInput.get(k);
					
					setInput = doc.createElement("set");
					send.appendChild(setInput);
					attrTemp = doc.createAttribute("length");
					attrTemp.setValue(tempData.fileSize);
					setInput.setAttributeNode(attrTemp);
					attrTemp = doc.createAttribute("name");
					attrTemp.setValue(tempData.fileName);
					setInput.setAttributeNode(attrTemp);
					
					if(isAttr){
						setInput = doc.createElement("set");
						send.appendChild(setInput);
						Attr attrTemp3;
						attrTemp3 = doc.createAttribute("length");
						attrTemp3.setValue("1");
						setInput.setAttributeNode(attrTemp3);
						attrTemp3 = doc.createAttribute("type");
						attrTemp3.setValue("hex");
						setInput.setAttributeNode(attrTemp3);
					}
				}	
			}
			
			// output block
			Element receive = doc.createElement("receive");
			staff.appendChild(receive);
			//여기서부터 아웃풋을 완성해야 함
			//어레이라면 추가해줘야 할 부분
			if(isArray){
				Element getArray = doc.createElement("get");
				receive.appendChild(getArray);
				// set attribute to staff element
				Attr attrTemp1;
				attrTemp1 = doc.createAttribute("length");
				attrTemp1.setValue("4");
				
				getArray.setAttributeNode(attrTemp1);
				attrTemp1 = doc.createAttribute("name");
				attrTemp1.setValue("COUNT");
				getArray.setAttributeNode(attrTemp1);
			}
			
			int outBoxSize = mOutBox.size();
			for(int i=0;i<outBoxSize;i++){
				ArrayList<fieldData> tempOutput = mOutBox.get(i);
				Element record = doc.createElement("record");
				receive.appendChild(record);
				int totalInputLength = tempOutput.size();
				for(int k=0;k<totalInputLength;k++){
					fieldData tempData = tempOutput.get(k);
					
					//아웃풋 블락
					Element getInput = doc.createElement("get");
					record.appendChild(getInput);
					// set attribute to staff element
					Attr attrTemp2;
					attrTemp2 = doc.createAttribute("length");
					attrTemp2.setValue(tempData.fileSize);
					getInput.setAttributeNode(attrTemp2);
					attrTemp2 = doc.createAttribute("name");
					attrTemp2.setValue(tempData.fileName);
					getInput.setAttributeNode(attrTemp2);
					
					
					if(isAttr){
						//아웃풋 블락
						getInput = doc.createElement("get");
						record.appendChild(getInput);
						// set attribute to staff element
						attrTemp2 = doc.createAttribute("length");
						attrTemp2.setValue("1");
						getInput.setAttributeNode(attrTemp2);
						
						attrTemp2 = doc.createAttribute("type");
						attrTemp2.setValue("hex");
						getInput.setAttributeNode(attrTemp2);
					}
				}	
			}
			

			
			
			
			// write the content into xml file
			TransformerFactory transformerFactory = TransformerFactory.newInstance();
			Transformer transformer = transformerFactory.newTransformer();
			
			transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes"); 
			transformer.setOutputProperty(OutputKeys.INDENT, "yes");
			transformer.setOutputProperty(OutputKeys.ENCODING, "utf-8");
			transformer.setOutputProperty(OutputKeys.INDENT, "yes");
			transformer.setOutputProperty(OutputKeys.METHOD, "xml");
			transformer.setOutputProperty(OutputKeys.STANDALONE,"yes");
			transformer.setOutputProperty
		       ("{http://xml.apache.org/xslt}indent-amount", "4");
			DOMSource source = new DOMSource(doc);
			StreamResult result = new StreamResult(new File(TRName + ".xml"));
	 
			// Output to console for testing
			// StreamResult result = new StreamResult(System.out);
	 
			transformer.transform(source, result);
	 
			System.out.println("File saved!");
			
			
		} catch (Exception e) {
			// TODO: handle exception
		}
	}
	
}
