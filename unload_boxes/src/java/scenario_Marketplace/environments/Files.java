package scenario_Marketplace.environments;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import scenario_Marketplace.entities.model.Buyer;
import scenario_Marketplace.entities.model.GeneralOrientedBuyer;
import scenario_Marketplace.entities.model.GeneralSeller;
import scenario_Marketplace.entities.model.Product;
import scenario_Marketplace.entities.model.Seller;
import scenario_Marketplace.entities.services.BehaviorFactory;
import scenario_Marketplace.enums.BehaviorPattern;
import scenario_Marketplace.enums.CriteriaType;
import scenario_Marketplace.enums.FilePaths;
import scenario_Marketplace.enums.ReportType;

public abstract class Files 
{
	/**
	 * This method load the agents from a xml file.
	 * @param fileName: name of file to be loaded.
	 */
	public static void loadAgentsFromFile()
	{
		try {
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder;
			builder = factory.newDocumentBuilder();
					
			Document document = builder.parse(FilePaths.LOAD_AGENTS.getPath());
					
			// Getting sellers
			NodeList sellers = document.getElementsByTagName("seller");
			
			for(int i = 0; i < sellers.getLength(); i++)
			{
				Element seller = (Element) sellers.item(i);
				
				// Getting seller's attributes
				String sellerName = seller.getElementsByTagName("name").item(0).getTextContent();
				Seller s = new GeneralSeller(sellerName);
				
				// Getting products
				NodeList products = seller.getElementsByTagName("product");
				
				for(int j = 0; j < products.getLength(); j++)
				{
					Element product = (Element) products.item(j);
					
					String productName = product.getElementsByTagName("name").item(0).getTextContent();
					int amount = Integer.parseInt(product.getElementsByTagName("amount").item(0).getTextContent());
					int behaviorAcceleration = Integer.parseInt(product.getElementsByTagName("behaviorAcceleration").item(0).getTextContent());
					
					double price = Double.parseDouble(product.getElementsByTagName("price").item(0).getTextContent());
					String priceBehavior = product.getElementsByTagName("priceBehavior").item(0).getTextContent();
					
					double quality = Double.parseDouble(product.getElementsByTagName("quality").item(0).getTextContent());
					String qualityBehavior = product.getElementsByTagName("qualityBehavior").item(0).getTextContent();
					
					double delivery = Double.parseDouble(product.getElementsByTagName("delivery").item(0).getTextContent());
					String deliveryBehavior = product.getElementsByTagName("deliveryBehavior").item(0).getTextContent();
					
					// Setting product's attributes
					Product p = new Product(productName);
					p.setAttribute(CriteriaType.PRICE, price);
					p.setAttribute(CriteriaType.QUALITY, quality);
					p.setAttribute(CriteriaType.DELIVERY, delivery);
					
					// Setting product's behavior
					p.setBehavior(CriteriaType.PRICE, BehaviorFactory.factoryMethod(BehaviorPattern.valueOf(priceBehavior), behaviorAcceleration));
					p.setBehavior(CriteriaType.QUALITY, BehaviorFactory.factoryMethod(BehaviorPattern.valueOf(qualityBehavior), behaviorAcceleration));
					p.setBehavior(CriteriaType.DELIVERY, BehaviorFactory.factoryMethod(BehaviorPattern.valueOf(deliveryBehavior), behaviorAcceleration));
					
					s.addProductToStock(p, amount);
				}
				Market.sellers.put(s.getName(),s);
			}
			
			// Getting buyers
			NodeList buyers = document.getElementsByTagName("buyer");
			
			for(int i = 0 ; i < buyers.getLength(); i++)
			{
				Element buyer = (Element) buyers.item(i);
	
				// Getting buyer's attributes
				String buyerName = buyer.getElementsByTagName("name").item(0).getTextContent();
				double selfConfident = Double.parseDouble(buyer.getElementsByTagName("selfConfident").item(0).getTextContent());
				double urgency = Double.parseDouble(buyer.getElementsByTagName("urgency").item(0).getTextContent());
				double minTrustBound = Double.parseDouble(buyer.getElementsByTagName("minTrustBound").item(0).getTextContent());
				double pricePreference = Double.parseDouble(buyer.getElementsByTagName("pricePreference").item(0).getTextContent());
				double qualityPreference = Double.parseDouble(buyer.getElementsByTagName("qualityPreference").item(0).getTextContent());
				double deliveryPreference = Double.parseDouble(buyer.getElementsByTagName("deliveryPreference").item(0).getTextContent());
				
				// Getting the products that'll be bought by the buyer
				NodeList products = buyer.getElementsByTagName("product");
				List<String> wishList = new ArrayList<String>();
				
				for(int j = 0; j < products.getLength(); j++)
				{
					wishList.add(products.item(j).getTextContent());
				}
				Buyer b = new GeneralOrientedBuyer(buyerName, selfConfident, urgency, minTrustBound, wishList, pricePreference, qualityPreference, deliveryPreference); 
				Market.buyers.put(b.getName(), b);
			}
		} 
		catch (ParserConfigurationException e) 
		{
			System.out.println(e.getMessage());
			e.printStackTrace();
		} 
		catch (SAXException e) 
		{
			System.out.println(e.getMessage());
			e.printStackTrace();
		} 
		catch (IOException e) 
		{
			System.out.println(e.getMessage());
			e.printStackTrace();
		} 
		catch (Exception e) 
		{
			System.out.println(e.getMessage());
			e.printStackTrace();
		}
	}
	
	/**
	 * This method writes output files where the data necessary for generating the analysis charts are stored.
	 * @param agentName: the name of a buyer or seller agent.
	 * @param type: the type of report that'll be written.
	 * @param content: the content to be written.
	 */
	public static void writeReportInFile(String agentName, ReportType type, String content)
	{
		try 
		{
			String path = FilePaths.REPORTS.getPath() + "//" + agentName;
			
			switch (type) 
			{
				case AGENT:
					path += "_base.txt";
				break;
				
				case TRUST:
					path += "_trust.txt";
				break;
				
				case REPUTATION:
					path += "_reputation.txt";
				break;
				
				case IMAGE:
					path += "_image.txt";
				break;
				
				case KNOWHOW:
					path += "_knowhow.txt";
				break;
				
				case AVAILABILITY:
					path += "_availability.txt";
				break;
				
				case SALE:
					path += "_sale.txt";
				break;
				
				default:
					throw new Exception("Requested report is not valid!: " + type);
			}
			
			BufferedWriter writer = new BufferedWriter(new FileWriter(path, true));
			writer.append(System.currentTimeMillis() + ";" + content +"\n");			     
		    writer.close();
		} 
		catch (IOException e) 
		{
			System.out.println(e.getMessage());
			e.printStackTrace();
		} 
		catch (Exception e) 
		{
			System.out.println(e.getMessage());
			e.printStackTrace();
		}
	}

	/**
	 * Delete all files in the report folder.
	 * This method must be used before to write new reports.
	 */
	public static void removeOldReports(String dir)
	{
		File folder = new File(dir);
		
		if (folder.isDirectory()) 
		{
			File[] sun = folder.listFiles();
			
			for (File toDelete : sun)
				toDelete.delete();
		}
	}
}