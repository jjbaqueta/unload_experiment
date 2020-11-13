package scenario_Marketplace.environments;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Logger;

import jason.asSyntax.Literal;
import jason.asSyntax.Structure;
import jason.asSyntax.Term;
import jason.environment.Environment;
import scenario_Marketplace.entities.model.Buyer;
import scenario_Marketplace.entities.model.GeneralOrientedBuyer;
import scenario_Marketplace.entities.model.GeneralSeller;
import scenario_Marketplace.entities.model.Product;
import scenario_Marketplace.entities.model.ProductsWarehouse;
import scenario_Marketplace.entities.model.Seller;
import scenario_Marketplace.enums.BehaviorPattern;
import scenario_Marketplace.enums.CriteriaType;
import scenario_Marketplace.enums.FilePaths;

public class Market extends Environment 
{
	/** Static attributes: */
	
	public static CriteriaType[] criteriaOrder = {CriteriaType.PRICE, CriteriaType.QUALITY, CriteriaType.DELIVERY};
    public static Map<String, Buyer> buyers = new HashMap<String, Buyer>();
    public static Map<String, Seller> sellers = new HashMap<String, Seller>();

	private Logger logger = Logger.getLogger("Log messages for Class: " + Market.class.getName());;
	private static AtomicInteger seqId = new AtomicInteger();
	
	/**
	 * @return the next CNPId.
	 */
	public static int getNextCNPId() 
	{
		return seqId.getAndIncrement();
	}
	
	/**
	 * This method checks if there is some buyer in buying process
	 * If all buyers ends their purchases, the market can close
	 * @return false there is at least one buyer buying, otherwise, return true
	 */
	public static boolean isMarketEnd()
	{	
		for(Buyer buyer : Market.buyers.values())
		{
			if(buyer.isBuying())
				return false;
		}
		
		return true;
	}
	
	/**
	 * This method load the environment from txt file.
	 * Including, the buyer and sellers are loaded from this file.
	 * @param fileName: name of file to be loaded.
	 */
	private static void loadEnvFromFile(String fileName)
	{
		try
		{	
			File file = new File(fileName);
			Scanner reader = new Scanner(file);
			ProductsWarehouse warehouse = new ProductsWarehouse();
		    
			while (reader.hasNextLine()) 
			{
				String data = reader.nextLine();
				String[] tokens = data.split(";");
				
				if(!tokens[0].equals("#comments"))
				{
					if(tokens[0].equals("SELLER"))
					{
						int numberOfProducts = Integer.parseInt(tokens[1]);
						int maxNumberInteractions = Integer.parseInt(tokens[2]);
						BehaviorPattern priceBehavior = BehaviorPattern.valueOf(tokens[3]);
						BehaviorPattern qualityBehavior = BehaviorPattern.valueOf(tokens[4]);
						BehaviorPattern deliveryBehavior = BehaviorPattern.valueOf(tokens[5]);
						List<Product> products = warehouse.generateDifferentProducts(numberOfProducts);
						
						// Remove this line to keep the products in the original order.
						warehouse.shuffling(products);
						
						Seller seller = new GeneralSeller("seller", products, maxNumberInteractions,
								priceBehavior, qualityBehavior, deliveryBehavior);
						
						sellers.put(seller.getName(), seller);
					}
					else
					{
						int numberOfProducts = Integer.parseInt(tokens[1]);
						double selfConfident = Double.parseDouble(tokens[2]);
						double minTrustBound = Double.parseDouble(tokens[3]);
						double pricePreference = Double.parseDouble(tokens[4]);
						double qualityPreference = Double.parseDouble(tokens[5]);
						double deliveryPreference = Double.parseDouble(tokens[6]);;
						List<Product> products = warehouse.generateDefaultProducts(numberOfProducts);
						
						// Remove this line to keep the products in the original order.
						warehouse.shuffling(products);
						
						Buyer buyer = new GeneralOrientedBuyer("buyer", selfConfident, minTrustBound, 
								products, pricePreference, qualityPreference, deliveryPreference);
						
						buyers.put(buyer.getName(), buyer);
					}
				}
		    }
		    reader.close();
		} 
		catch (FileNotFoundException e) 
		{
			System.out.println("Error to open file.");
		    e.printStackTrace();
		} catch (Exception e) {
			System.out.println(e.getMessage());
			e.printStackTrace();
		}
	}
	
	@Override
	public void init(String[] args) 
	{
		super.init(args);

		try 
		{	
			loadEnvFromFile(FilePaths.LOAD_AGENTS.getPath());			
		} 
		catch (Exception e) 
		{
			System.out.println(e.getMessage());
			e.printStackTrace();
		}

		System.out.println("\n--------------------- STARTING JASON APPLICATION --------------------\n");
		updatePercepts();
	}

	//	Setting the initial perceptions
	public void updatePercepts() 
	{	
		// Init manager
		clearPercepts("manager");
		
		// Creating sellers		
		for(Seller seller : sellers.values())
		{
			addPercept("manager", Literal.parseLiteral("add_seller(" + seller.getName() + ")"));
		}
		
		// Creating buyers
		for(Buyer buyer : buyers.values())
		{
			addPercept("manager", Literal.parseLiteral("add_buyer(" + buyer.getName() + ")"));
		}
		
		// Init each seller
		for (Seller seller : sellers.values()) 
		{
			clearPercepts(seller.getName());
			
			for (Term product : seller.getProductsAsLiteralList())
				addPercept(seller.getName(), Literal.parseLiteral(product.toString()));
		}
		
		// Init each buyer
		for (Buyer buyer : buyers.values()) 
		{
			clearPercepts(buyer.getName());
			addPercept(buyer.getName(), Literal.parseLiteral("task("+ getNextCNPId() + "," + buyer.getProductsToBuy().pop() + ")"));
		}
	}
	
	// Defining the actions that may be performed by agents
	@Override
	public boolean executeAction(String agName, Structure action) 
	{
		logger.info("agent: " + agName + " is executing: " + action);

		if (action.equals(Literal.parseLiteral("purchase(finished)"))) 
		{
			clearPercepts(agName);
			Buyer buyer = buyers.get(agName);
			
			if (buyer.hasBuyingDesire()) 
				addPercept(agName, buyer.getProductsToBuy().pop());
			
			else 
			{
				addPercept(agName, Literal.parseLiteral("buy(nothing)"));
				logger.info("The buyer: " + agName + " ended his purchases (-- CONCLUDED --)");
				buyer.setEndOfActivities();
			}
		}
		else if(action.equals(Literal.parseLiteral("status(end)")))
		{
			if(Market.isMarketEnd())
			{
				addPercept("manager", Literal.parseLiteral("show_report"));
			}
		}
		else if(action.equals(Literal.parseLiteral("purchase(completed)")))
			buyers.get(agName).incCompletedPurchases();
		
		else if (action.equals(Literal.parseLiteral("purchase(aborted)")))
			buyers.get(agName).incAbortedPurchases();
		
		else if(action.equals(Literal.parseLiteral("lost(sale)")))
			sellers.get(agName).increaseLostSales();

		else if(action.equals(Literal.parseLiteral("made(sale)")))
		{	
			sellers.get(agName).increaseMadeSales();
			
//			long time = System.currentTimeMillis();
//			
//			for(Seller seller : sellers)
//				Files.writeSaleStatus(seller, time);
		}
		else 
			logger.warning("executing: " + action + ", but not implemented!");

		return true;
	}

	@Override
	public void stop() 
	{
		super.stop();
	}
}