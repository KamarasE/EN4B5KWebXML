package en4b5k.domparse.hu;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Comment;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class EN4B5KDomRead {

    public static void main(String[] args) {
        try {
            // XML dokumentum beolvasása
            Document document = parseXML("EN4B5K_XML.xml");

            System.out.println("A dokumentum elemei blokkformában:\n");
            printElement(document.getDocumentElement(), 0);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Adott nevű XML dokumentumból Document készítése
    public static Document parseXML(String filename)
            throws SAXException, IOException, ParserConfigurationException {

        File xmlFile = new File(filename);
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document document = builder.parse(xmlFile);
        Node root = document.getDocumentElement();
        root.normalize();
        cleanDocument(root);

        return document;
    }

    // Üres node-ok törlése a dokumentumból (sortörések miatti)
    private static void cleanDocument(Node root) {
        NodeList nodes = root.getChildNodes();
        List<Node> toDelete = new ArrayList<>();

        for (int i = 0; i < nodes.getLength(); i++) {
            if (nodes.item(i).getNodeType() == Node.TEXT_NODE &&
                    nodes.item(i).getTextContent().strip().equals("")) {

                toDelete.add(nodes.item(i));
            } else {
                cleanDocument(nodes.item(i));
            }
        }

        for (Node node : toDelete) {
            root.removeChild(node);
        }
    }

    // DOM fa visszaadása XML-ben formázva
    public static String formatXML(Document document) {

        String pi = "<?xml version=\"" + document.getXmlVersion()
                + "\" encoding=\"" + document.getXmlEncoding() + "\" ?>";

        return pi + formatElement(document.getDocumentElement(), 0);
    }

    // XML elemek strukturált Stringgé alakítása
    public static String formatElement(Node node, int indent) {

        if (node.getNodeType() != Node.ELEMENT_NODE) {
            return "";
        }

        String output = "\n";

        if (indent == 1) {
            output += "\n";
        }

        output += indent(indent) + "<" + ((Element) node).getTagName();

        // Attribútumok
        if (node.hasAttributes()) {
            for (int i = 0; i < node.getAttributes().getLength(); i++) {
                Node attribute = node.getAttributes().item(i);
                output += " " + attribute.getNodeName() + "=\"" + attribute.getNodeValue() + "\"";
            }
        }

        output += ">";

        // Gyerek elemek feldolgozása
        NodeList children = node.getChildNodes();
        for (int i = 0; i < children.getLength(); i++) {

            if (children.item(i).getNodeType() == Node.TEXT_NODE)
                return output += node.getTextContent()
                        + "</" + ((Element) node).getTagName() + ">";

            if (children.item(i).getNodeType() == Node.ELEMENT_NODE)
                output += formatElement(children.item(i), indent + 1);

            if (children.item(i).getNodeType() == Node.COMMENT_NODE)
                output += "\n" + indent(indent + 1) + "\n<!--"
                        + ((Comment) children.item(i)).getData() + "-->";
        }

        output += "\n" + indent(indent) + "</" + ((Element) node).getTagName() + ">";
        return output;
    }

    // Behúzás kezelése
    private static String indent(int indent) {
        return "  ".repeat(indent);
    }

    // DOM fa blokkos kiírása
    public static void printElement(Node node, int indent) {

        if (node.getNodeType() != Node.ELEMENT_NODE) {
            return;
        }

        Element element = (Element) node;

        StringBuilder block = new StringBuilder(indent(indent) + element.getTagName());

        if (element.hasAttributes()) {
            for (int i = 0; i < element.getAttributes().getLength(); i++) {
                Node attribute = element.getAttributes().item(i);
                block.append(" ").append(attribute.getNodeName()).append(": ").append(attribute.getNodeValue());
            }
        }

        String textContent = "";
        NodeList children = element.getChildNodes();

        for (int i = 0; i < children.getLength(); i++) {
            Node child = children.item(i);
            if (child.getNodeType() == Node.TEXT_NODE) {
                textContent += child.getTextContent().strip();
            }
        }

        if (!textContent.isEmpty()) {
            block.append(": ").append(textContent);
        }

        System.out.println(block);

        for (int i = 0; i < children.getLength(); i++) {
            Node child = children.item(i);
            if (child.getNodeType() == Node.ELEMENT_NODE) {
                printElement(child, indent + 2);
            }
        }

        if (indent == 2 || indent == 0) {
            System.out.println();
        }
    }
}
