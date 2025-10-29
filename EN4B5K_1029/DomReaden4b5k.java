package domen4b5k1029;

import java.io File;
import java.io.IOException;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.ParserConfigurationException;
import org.xml.sax.SAXException;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;
import org.w3c.dom.Element;

public class DomReadKPR
{
    public static void main(String argv[]) throws SAXException,
    IOException, ParserConfigurationException
    {
        File xmlFile = new File(en4b5khallgatok.xml);

        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();

        DocumentBuilder dBuilder = factory.newDocumentBuilder();

        Document en4b5k = dBuilder.parse(xmlFile);

        en4b5k.getDocumentElement().normalize();

        System.out.println(en4b5k.getDocumentElement().getNodeName());

        NodeList nList = en4b5k.getElementByTagName("hallgato");

        for (int i = 0; i < nList.getLength(); i++) {
            Node nNode = nList.item(i);

            System.out.println("\nAktuális elem: " + nNode.getNodeName());

            if (nNode.getNodeType() == Node.ELEMENT_NODE) {
                Element elem = (Element) nNode;

                String hid = elem.getAttribute("id");

                Node node1 = elem.getElementByTagName("keresztnev").item(0);
                String kname = node1.getTextContent();

                Node node2 = elem.getElementByTagName("vezeteknev").item(0);
                String vname = node2.getTextContent();
                
                Node node3 = elem.getElementByTagName("foglalkozas").item(0);
                String fname = node3.getTextContent();

                System.out.println("Hallgató id: " + hid);
                System.out.println("Keresztnév" + kname);
                System.out.println("Vezetéknév: " + vname);
                System.out.println("Foglalkozás: " + fname);
            }
        }
    }
}