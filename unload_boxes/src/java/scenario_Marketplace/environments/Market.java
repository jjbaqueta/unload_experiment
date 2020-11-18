package scenario_Marketplace.environments;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Logger;

import jason.asSyntax.Literal;
import jason.asSyntax.Structure;
import jason.asSyntax.Term;
import jason.environment.Environment;
import scenario_Marketplace.entities.model.Buyer;
import scenario_Marketplace.entities.model.Product;
import scenario_Marketplace.entities.model.Seller;
import scenario_Marketplace.enums.CriteriaType;

public class Market extends Environment 
{
	/** Static attributes: */
	
	public static CriteriaType[] criteriaOrder = {CriteriaType.PRICE, CriteriaType.QUALITY, CriteriaType.DELIVERY};
    public static Map<String, Buyer> buyers = new HashMap<String, Buyer>();
    public static Map<String, Seller> sellers = new HashMap<String, Seller>();

	private Logger logger = Logger.getLogger("Log messages for Class: " + Market.class.getName());;
	private static AtomicInteger seqId = new AtomicInteger();
	
	@Override
	public void init(String[] args) 
	{
		super.init(args);
		
		Files.removeOldReports();
		Files.loadAgentsFromFile();
		MarketFuzzyConfig.createFuzzyFile(sellers.size(), buyers.size());
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
			Buyer buyer = buyers.get(agName);
			clearPercepts(buyer.getName());
			
			if (buyer.hasBuyingDesire())
				addPercept(buyer.getName(), Literal.parseLiteral("task("+ getNextCNPId() + "," + buyer.getProductsToBuy().pop() + ")"));
			
			else 
			{
				addPercept(buyer.getName(), Literal.parseLiteral("buy(nothing)"));
				logger.info("The buyer: " + agName + " ended his purchases (-- CONCLUDED --)");
				buyer.setEndOfActivities();
			}
		}
		else if(action.equals(Literal.parseLiteral("status(end)")))
		{
			if(Market.isMarketEnd())
				addPercept("manager", Literal.parseLiteral("show_report"));
		}
		else if(action.equals(Literal.parseLiteral("lost(sale)")))
			sellers.get(agName).increaseLostSales();

		else if(action.equals(Literal.parseLiteral("made(sale)")))	
			sellers.get(agName).increaseMadeSales();
		else 
			logger.warning("executing: " + action + ", but not implemented!");

		return true;
	}

	@Override
	public void stop() 
	{
		super.stop();
	}
	
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
	 * This method shows all buyers.
	 */
	public static void showBuyers()
	{
		for(Buyer buyer : buyers.values())
		{
			System.out.println(buyer);
		}
	}
	
	/**
	 * This method shows all sellers.
	 */
	public static void showSellers()
	{
		for(Seller seller : sellers.values())
		{
			System.out.println(seller);
		}
	}
	
	/**
     * This method shows a list of products
     * @param products: a list of products
     */
    public static void showProducts(List<Product> products)
    {	
    	for(Product product : products)
    		System.out.println(product);
    }
}