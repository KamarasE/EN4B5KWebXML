public static void main(String argv[]) {

    try {
        File inputFile = new File("hallgatok.xml");

        DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dcoBuilder = docFactory.newDocumentBuilder();

        Document doc = dcoBuilder.parse(inputFile);

        Node hallgatok = doc.getFirstChild();

        Node hallgat = doc.getElementsByTagName("hallgato").item(0);


        NamedNodeMap attr = hallgat.getAttributes();
        Node nodeAttr = attr.getNamedItem("id");
        nodeAttr.setTextContent("01");

        NodeList list = hallgat.getChildNodes();

        for (int temp = 0; temp < list.getLength(); temp++){
            Node node = list.item(temp);

            if (node.getNodeType() == Node.ELEMENT_NODE) {
                if ("keresztnev".equals(eElement.getNodeName())) {
                    if("Pál".equals(eElement.getTextContent())) {
                        eElement.setTextContent("Erik");
                    }
                }

                if ("vezeteknev".equals(eElement.getNodeName())) {
                    if("Kiss".equals(eElement.getTextContent())) {
                        eElement.setTextContent("Kamarás");
                    }
                }

                //Tartalom

                TransformerFactory transfromerFactory = TransformerFactory.newInstance();
                Transformer transformer = transformerFactory.newTransformer();

                //ide kerül a dom fa
                DOMSource source = new DOMSource(doc);

                System.out.println("---Módosított fájl---");
                StreamResult consoleResult = new StreamResult(System.out);
                transformer.transform(source, consoleResult);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}