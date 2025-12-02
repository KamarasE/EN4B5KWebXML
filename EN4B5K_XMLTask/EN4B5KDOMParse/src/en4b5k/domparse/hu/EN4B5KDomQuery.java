package en4b5k.domparse.hu;

import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import en4b5k.domparse.hu.EN4B5KDomRead;


public class EN4B5KDomQuery {

	public static void main(String[] args) {
		try {
			Document document = EN4B5KDomRead.parseXML("EN4B5K_XML.xml");
			
			// 1. A 1995-ben vagy utána született betegek adatai
			String betegData = getNamesBornAfter(document, 1995);
			System.out.println("1. lekérdezés:");
			System.out.println("A 1995-ben vagy utána született betegek adatai: " + betegData);
			
			// 2. Az aktív (állami) jogviszonnyal rendelkezők száma
			System.out.println("\n2. lekérdezés:");
			System.out.println("Az aktív jogviszonyok száma: " + countActiveJogviszony(document));
			
			// 3. Az orvosok átlagos fizetése
			System.out.println("\n3. lekérdezés:");
			System.out.println("Az orvosok átlagos fizetése: " + getAvgOrvosBer(document) + " Ft");
			
			// 4. Az összes terápia típusa
			List<String> therapy = getAllTherapy(document);
			System.out.println("\n4. lekérdezés:");
			System.out.println("Az összes terápia: "+ therapy);
			

		} catch (Exception e) {
			System.out.println(e.getMessage());
		}

	}
	
	
	//Adott évben/évet követően született diákok adatai strukturáltan
		private static String getNamesBornAfter(Document document, int year) {
			String output = "";
			Element root = document.getDocumentElement();
			//Ciklus a betegekre
			NodeList betegek = root.getElementsByTagName("beteg");
			for (int i = 0; i < betegek.getLength(); i++) {
				Element beteg = (Element) betegek.item(i);
				Element birthDateEl = (Element) beteg.getElementsByTagName("születési_dátum").item(0);
				//A születési év kivétele a beteg születési dátumából
				int birthYear = Integer.parseInt(birthDateEl.getTextContent().split("-")[0].trim());
				//Ha a születési évnél nem nagyobb a paraméterként megadott év, akkor a beteg adatai a kimenetbe kerülnek
				if (birthYear >= year) {
					output+=EN4B5KDomRead.formatElement(beteg, 0);
				}
			}
			return output;
		}
		
		//Aktív jogviszonnyal rendelkezők száma
		private static int countActiveJogviszony(Document document) {
		    int counter = 0;
		    Element root = document.getDocumentElement();
		    // Ciklus az összes ellátásra
		    NodeList ellatasok = root.getElementsByTagName("ellátás");
		    for (int i = 0; i < ellatasok.getLength(); i++) {
		        Element ellatas = (Element) ellatasok.item(i);
		        // Az "aktív" elem értékének lekérése
		        NodeList activeNodes = ellatas.getElementsByTagName("jogviszony");
		        if (activeNodes.getLength() > 0) { // Ellenőrzés, hogy létezik-e az "aktív" elem
		            Element activeElement = (Element) activeNodes.item(0);
		            String activeValue = activeElement.getTextContent();
		            // Ha az "aktív" elem értéke nem fizetős, a számláló növekszik
		            if (!"fizetős".equals(activeValue)) {
		                counter++;
		            }
		        } else {
		            // Ha nincs "aktív" elem, az alapértelmezett "állami"-nak tekintjük
		            counter++;
		        }
		    }
		    return counter;
		}
		
		//Az orvosok átlagfizetése
		private static int getAvgOrvosBer(Document document) {
			int orvosCount = 0;
			int wageSum = 0;
			Element root = document.getDocumentElement();
			NodeList orvosok = root.getElementsByTagName("orvos");
			// Orvosok számának meghatározása
			orvosCount = orvosok.getLength();
			// Ciklus
			for (int i = 0; i < orvosok.getLength(); i++) {
				Element orvos = (Element) orvosok.item(i);
				//Tanár órabérének hozzáadása az összeghez
				Element wage = (Element) orvos.getElementsByTagName("fizetés").item(0);
				wageSum += Integer.parseInt(wage.getTextContent().trim());
			}
			//Átlag számítás, felfelé kerekítés egészre
			return (int) Math.ceil(wageSum / (1.0 * orvosCount));
		}
		
		//Az összes terápia típus kilistázása
		private static List<String> getAllTherapy(Document document) {
			List<String> allTherapy = new ArrayList<>();
			Element root = document.getDocumentElement();
			NodeList participations = root.getElementsByTagName("részvétel");
			//Ciklus az összes részvételre
			for (int i = 0; i < participations.getLength(); i++) {
				Element participation = (Element) participations.item(i);
				//Ha létezik terápia gyerekelem, és a tartalma még nincs a listában, akkor felvesszük
				NodeList prizes = participation.getElementsByTagName("terápia");
				if (prizes.getLength()>0) {
					Element prize = (Element) prizes.item(0);
					if(!allTherapy.contains(prize.getTextContent())) {
						allTherapy.add(prize.getTextContent());
					}
				}
			}
			return allTherapy;
		}
		

}
