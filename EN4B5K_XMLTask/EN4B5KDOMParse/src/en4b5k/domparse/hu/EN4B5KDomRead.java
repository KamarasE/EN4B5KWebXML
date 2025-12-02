package en4b5k.domparse.hu;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Comment;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;


public class EN4B5KDomRead {

	public static void main(String[] args){

		try {
			
			//output file megadása
			File newXmlFile = new File("ReadEN4B5K_XML.xml");
			
			//XML dokumentum beolvasása
			Document document = parseXML("EN4B5K_XML.xml");
			
			// Format the document as a string
            String formattedXML = formatXML(document);
			
            //Write formatted XML to output file
            writeStringToFile(formattedXML, newXmlFile);
            
			//Kiírás az output fájlba
			//writeDocument(document, newXmlStream);
			
            System.out.println("A dokumentum elemei blokkformában:\n");
            printElement(document.getDocumentElement(), 0); 
			
		} catch (Exception e) {
			e.printStackTrace();
		}
}


// Formázott String írása fájlba 
private static void writeStringToFile(String content, File file) throws IOException {
    try (FileWriter writer = new FileWriter(file)) {
        writer.write(content);
    }
}

//DOM fa tartalmának kiírása megadott streambe transformerrel (Később használt)
public static void writeDocument(Document document, StreamResult output) throws TransformerException {
  TransformerFactory transformerFactory = TransformerFactory.newInstance();
  Transformer transformer;
  transformer = transformerFactory.newTransformer();
  transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
  transformer.setOutputProperty(OutputKeys.INDENT, "yes");
  
  DOMSource source = new DOMSource(document);
  transformer.transform(source, output);
}

//Adott nevű XML dokumentumból Document készítése
public static Document parseXML(String filename) throws SAXException, IOException, ParserConfigurationException {
	File xmlFile = new File(filename);
	DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
	DocumentBuilder builder = factory.newDocumentBuilder();
	Document document = builder.parse(xmlFile);
	Node root = document.getDocumentElement();
	root.normalize();
	cleanDocument(root);
	
	return document;
}

//Üres node-ok törlése a dokumentumból (sortörések miatti)
private static void cleanDocument(Node root) {
	NodeList nodes = root.getChildNodes(); 
	List<Node> toDelete = new ArrayList<>(); 
	for(int i=0; i<nodes.getLength(); i++) { 
		if(nodes.item(i).getNodeType()==Node.TEXT_NODE && 
				nodes.item(i).getTextContent().strip().equals("")) {
			
			toDelete.add(nodes.item(i)); 
		}else { 
			cleanDocument(nodes.item(i)); 
		} 
	} for(Node node: toDelete) { 
		root.removeChild(node); 
	}
	
}

//DOM fa visszaadása XML-ben formázva (általános XML beolvasó)
public static String formatXML(Document document) {
	
	//Prolog kiírva 
	String pi = "<?xml version=\""+document.getXmlVersion() 
	+"\" encoding=\""+document.getXmlEncoding()+"\" ?>"; 
	return pi+formatElement(document.getDocumentElement(), 0); 
	//return formatElement(document.getDocumentElement(), 0); 
}

//Adott XML csomópontnak és tartalmának strukturált Stringgé konvertálása formázott kiíráshoz (általános XML beolvasó)
public static String formatElement(Node node, int indent) {
	
	//Ha node nem elem, üres Stringgel térünk vissza 
	if (node.getNodeType() != Node.ELEMENT_NODE) {
		return ""; 
	} 
	//Egyébként felépítjük az xml elemet 
	String output = "\n"; 
	
	  // Üres sor hozzáadása az első szintű gyermek elemek előtt
    if (indent == 1) {
        output += "\n"; // Extra üres sor minden fő elem előtt és után
    }
	
	output += indent(indent)+"<" + ((Element) node).getTagName(); 
	
	//Attribútumok formázott felvétele, ha vannak 
	if (node.hasAttributes()) { 
		for (int i = 0; i < node.getAttributes().getLength(); i++) { 
			Node attribute = node.getAttributes().item(i); 
			output += " " + attribute.getNodeName() + "=\"" + attribute.getNodeValue() + "\""; 
			} 
		} 
	output += ">";
	
	//Gyerekelemek feldolgozása 
	NodeList children = node.getChildNodes(); 
	for (int i = 0; i < children.getLength(); i++) { 
		//Szöveges tartalom
		if(children.item(i).getNodeType()==Node.TEXT_NODE) 
			return output+=node.getTextContent()
			+"</" + ((Element) node).getTagName() + ">";
		//Gyerekelem 
		if(children.item(i).getNodeType()==Node.ELEMENT_NODE) 
			output+=formatElement(children.item(i), indent+1); 
		//Komment
		if(children.item(i).getNodeType()==Node.COMMENT_NODE)
			output+="\n"+indent(indent+1)+"\n<!--" 
			+((Comment)children.item(i)).getData()+"-->"; 
		}
	output+="\n"+indent(indent)+"</" + ((Element) node).getTagName() + ">";

	return output;
	}
	

//Tabulálás
private static String indent(int indent) { 
	return "  ".repeat(indent); 
}


public static void printElement(Node node, int indent) {
    if (node.getNodeType() != Node.ELEMENT_NODE) {
        return; // Csak elemeket dolgozunk fel
    }

    Element element = (Element) node;

    // Kiírjuk az elem nevét és attribútumait
    StringBuilder block = new StringBuilder(indent(indent) + element.getTagName());
    if (element.hasAttributes()) {
        for (int i = 0; i < element.getAttributes().getLength(); i++) {
            Node attribute = element.getAttributes().item(i);
            block.append(" ").append(attribute.getNodeName()).append(": ").append(attribute.getNodeValue());
        }
    }

    // Szöveges tartalom összegyűjtése az aktuális elemhez
    String textContent = "";
    NodeList children = element.getChildNodes();
    for (int i = 0; i < children.getLength(); i++) {
        Node child = children.item(i);
        if (child.getNodeType() == Node.TEXT_NODE) {
            textContent += child.getTextContent().strip(); // Összegyűjtjük a szöveges tartalmat
        }
    }

    // Ha van szöveges tartalom, hozzáadjuk
    if (!textContent.isEmpty()) {
        block.append(": ").append(textContent);
    }

    System.out.println(block.toString()); // Az aktuális elem kiírása

    // Gyermek elemek feldolgozása
    for (int i = 0; i < children.getLength(); i++) {
        Node child = children.item(i);
        if (child.getNodeType() == Node.ELEMENT_NODE) {
            printElement(child, indent + 2); // Rekurzívan feldolgozzuk a gyermek elemeket
        }
    }

    // Üres sor hozzáadása csak a szülő elem után (amikor a gyerekek már kiíródtak)
    if (indent == 2 || indent == 0) {
        System.out.println(); // Üres sor kiírása a szülő elem után
    }
}


}
