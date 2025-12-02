package en4b5k.domparse.hu;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public class EN4B5KDomModify {

    public static void main(String[] args) {
        try {
            Document document = EN4B5KDomRead.parseXML("EN4B5K_XML.xml");

            // 1. fájdalom -> fejfájdalomra
            System.out.println("1. módosítás:");
            modifyPanaszToFejfajdalom(document);

            // 2. Nagy Anna jogviszonyának fizetős-re állítása
            System.out.println("\n2. módosítás:");
            setToPassive(document, "Nagy Anna");

            // 3. Kiss Ádám neve Kiss József-re
            System.out.println("\n3. módosítás:");
            modifyRelativeName(document, "Kiss Ádám", "Kiss József");

            // 4. A 2. orvos 1. email címének törlése
            System.out.println("\n4. módosítás:");
            deleteOrvosEmail(document, 2, 1);

            System.out.println("\nA módosított dokumentum:");
            System.out.println(EN4B5KDomRead.formatXML(document));

        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }


    // Kötelezett: orvos email törlése
    private static void deleteOrvosEmail(Document document, int orvosNum, int emailNum) {

        Element root = document.getDocumentElement();
        NodeList orvosok = root.getElementsByTagName("orvos");

        if (orvosok.getLength() >= orvosNum) {
            Element orvos = (Element) orvosok.item(orvosNum - 1);

            System.out.println("\nELŐTTE:" + EN4B5KDomRead.formatElement(orvos, 0));

            Element contact = (Element) orvos.getElementsByTagName("elérhetőség").item(0);
            NodeList emails = contact.getElementsByTagName("email");

            if (emails.getLength() >= emailNum) {
                Element email = (Element) emails.item(emailNum - 1);

                contact.insertBefore(document.createComment("Törölt email helye"), email);
                contact.removeChild(email);
            }

            System.out.println("\nUTÁNA:" + EN4B5KDomRead.formatElement(orvos, 0));
        }
    }

    
    
 //Az 1201-es szakrendelés "fájdalom", "fejfájdalom"-ra cserélése
    private static void modifyPanaszToFejfajdalom(Document document) {

        Element root = document.getDocumentElement();
        NodeList szakrendelesek = root.getElementsByTagName("szakrendelés");

        for (int i = 0; i < szakrendelesek.getLength(); i++) {
            Element szak = (Element) szakrendelesek.item(i);

            // Csak a Szid="1201" szakrendelést módosítjuk
            if (szak.getAttribute("Szid").equals("1201")) {

                Element panasz = (Element) szak.getElementsByTagName("panasz").item(0);

                if (panasz != null && panasz.getTextContent().equals("fájdalom")) {

                    // ELŐTTE
                    System.out.println("\nELŐTTE: ");
                    System.out.println(EN4B5KDomRead.formatElement(szak, 0));

                    // Módosítás és komment beszúrása
                    szak.insertBefore(
                        document.createComment("Panasz átírva fejfájdalom-ra"),
                        panasz
                    );
                    panasz.setTextContent("fejfájdalom");

                    // UTÁNA
                    System.out.println("\nUTÁNA: ");
                    System.out.println(EN4B5KDomRead.formatElement(szak, 0));
                }
            }
        }
    }


    // Hozzátartozó neve módosítása
    private static void modifyRelativeName(Document document, String oldName, String newName) {

        Element root = document.getDocumentElement();
        NodeList hozzatartozok = root.getElementsByTagName("hozzátartozó");

        for (int i = 0; i < hozzatartozok.getLength(); i++) {
            Element hozzatartozo = (Element) hozzatartozok.item(i);
            Element name = (Element) hozzatartozo.getElementsByTagName("név").item(0);

            if (name.getTextContent().equals(oldName)) {

                System.out.println("\nELŐTTE:" + EN4B5KDomRead.formatElement(hozzatartozo, 0));

                hozzatartozo.insertBefore(
                        document.createComment("Átírt név és születési év"),
                        name
                );

                name.setTextContent(newName);

                System.out.println("\nUTÁNA:" + EN4B5KDomRead.formatElement(hozzatartozo, 0));
            }
        }
    }


    // Jogviszony módosítása (beteg -> ellátás alapján)
    private static void setToPassive(Document document, String patientName) {

        String ellatasId = "";
        Element root = document.getDocumentElement();
        NodeList betegek = root.getElementsByTagName("beteg");

        // A beteghez tartozó ellátás ID kikeresése
        for (int i = 0; i < betegek.getLength(); i++) {
            Element beteg = (Element) betegek.item(i);
            Element name = (Element) beteg.getElementsByTagName("név").item(0);

            if (name.getTextContent().equals(patientName)) {
                ellatasId = beteg.getAttribute("ellátás");
            }
        }

        if (ellatasId.isEmpty()) {
            System.out.println("A beteg nem található!");
            return;
        }

        NodeList ellatasok = root.getElementsByTagName("ellátás");

        for (int i = 0; i < ellatasok.getLength(); i++) {
            Element ellatas = (Element) ellatasok.item(i);

            if (ellatas.getAttribute("Eid").equals(ellatasId)) {
                System.out.println("\nELŐTTE: " + EN4B5KDomRead.formatElement(ellatas, 0));

                NodeList jogNodes = ellatas.getElementsByTagName("jogviszony");

                if (jogNodes.getLength() > 0) {
                    Element jog = (Element) jogNodes.item(0);
                    jog.setTextContent("fizetős");
                } else {
                    Element newJog = document.createElement("jogviszony");
                    newJog.setTextContent("fizetős");
                    ellatas.appendChild(newJog);
                }

                ellatas.appendChild(document.createComment("Nem támogatott jogviszony"));

                System.out.println("\nUTÁNA: " + EN4B5KDomRead.formatElement(ellatas, 0));
            }
        }
    }
}
