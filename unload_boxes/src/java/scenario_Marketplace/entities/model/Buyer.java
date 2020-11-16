package scenario_Marketplace.entities.model;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Stack;
import jason.asSyntax.Literal;
import jason.asSyntax.NumberTermImpl;
import jason.asSyntax.Structure;
import scenario_Marketplace.enums.CriteriaType;

/*
 * This class implements a buyer.
 * Each buyer has his own list of wishes and his goal is buying theses products.
 */
public abstract class Buyer extends SimpleAgent
{
	protected Stack<Literal> productsToBuy;
	protected Map<String, Double> preferences;
	protected Integer completedPurchases;
	protected Integer abortedPurchases;
	protected Boolean buying;
	protected Double selfConfident;
	protected Double minTrustBound;
	
	// Default constructor
	public Buyer(String name, Double selfConfident, Double minTrustBound, List<String> wishList)
	{
		super(name);		
		this.completedPurchases = 0;
		this.abortedPurchases = 0;
		this.buying = true;
		
		this.preferences = new HashMap<String, Double>();
		this.productsToBuy = new Stack<Literal>();
		
		for(String product : wishList)
		{
			productsToBuy.push(Literal.parseLiteral("buy(" + product + ")"));
		}
		
		this.selfConfident = selfConfident;
		this.minTrustBound = minTrustBound;
	}
	
	// Random constructor
	public Buyer(String name, List<Product> wishList)
	{
		super(name);		
		this.completedPurchases = 0;
		this.abortedPurchases = 0;
		this.buying = true;
		
		this.preferences = new HashMap<String, Double>();
		this.productsToBuy = new Stack<Literal>();
		
		for(Product product : wishList)
		{
			productsToBuy.push(Literal.parseLiteral("buy(" + product.getName().toLowerCase() + ")"));
		}
		
		Random rand = new Random();
		
		this.selfConfident = rand.nextDouble();
		this.minTrustBound = rand.nextDouble();
		
		// Create the possibility to pick up a negative value for selfConfident variable
		if(rand.nextBoolean())
		{
			this.selfConfident *= -1;
		}
		
		// Create the possibility to pick up a negative value for minTrustBound variable
		if(rand.nextBoolean())
		{
			this.minTrustBound *= -1;
		}
	}
	
	/**
	 * Define the relevance of a given criterion.
	 * @param criterion: the criterion of preference of buyer.
	 * @param relevance: the relevance of criterion.
	 */
	public void setPreference(CriteriaType criterion, double relevance)
	{
		preferences.put(criterion.name(), relevance);
	}
	
	/**
	 * Get a value associated to a given criterion.
	 * @param criterion: the criterion of preference of buyer.
	 * @return the value associated to informed criterion.
	 */
	public Double getPreference(CriteriaType criterion) 
	{
		if(preferences.containsKey(criterion.name()))
		{
			return preferences.get(criterion.name());
		}
		return null;
	}
	
	public int getCompletedPurchases() 
	{
		return completedPurchases;
	}

	public int getAbortedPurchases() 
	{
		return abortedPurchases;
	}
	
	public void incCompletedPurchases() 
	{
		this.completedPurchases++;
	}
	
	public void incAbortedPurchases() 
	{
		this.abortedPurchases++;
	}
	
	public boolean isBuying() 
	{
		return buying;
	}

	public Stack<Literal> getProductsToBuy() 
	{
		return productsToBuy;
	}
	
	public boolean hasBuyingDesire() 
	{
		return !productsToBuy.isEmpty();
	}
	
	public void setEndOfActivities()
	{	
		this.buying = false;
	}
	
	public Double getSelfConfident() 
	{
		return selfConfident;
	}

	public void setSelfConfident(Double selfConfident) 
	{
		this.selfConfident = selfConfident;
	}

	public Double getMinTrustBound() 
	{
		return minTrustBound;
	}

	public void setMinTrustBound(Double minTrustBound) {
		this.minTrustBound = minTrustBound;
	}
	
	/**
	 * This method returns the buyer's preferences in literal format
	 * @param criteriaOrder: list of criterion that defines the order how the preferences will be shown.
	 * @return a literal.
	 */
	public Literal getPreferencesAsLiteral(List<CriteriaType> criteriaOrder)
	{
		Structure preferences = new Structure("pref");
		
		for(CriteriaType criterion : criteriaOrder)
		{
			preferences.addTerm(new NumberTermImpl(this.preferences.get(criterion.name())));
		}
		return preferences;
	}
	
	@Override
	public String toString() 
	{
		StringBuffer sb = new StringBuffer();
		sb.append("productsToBuy={");
		
		for(int i = 0; i < productsToBuy.size() - 1; i++)
		{
			sb.append(productsToBuy.get(i).toString()).append(", ");
		}
		sb.append(productsToBuy.get(productsToBuy.size() - 1).toString()).append("}");
		
		sb.append(", preferences{ ");
		
		for(Map.Entry<String, Double> pair : preferences.entrySet())
		{
			sb.append(pair).append(" ");
		}
		sb.append("}");	
		
		return "Buyer [" + super.toString() + ", " + sb.toString() + 
				", completedPurchases=" + completedPurchases + ", abortedPurchases=" + abortedPurchases +
				", selfConfident=" + selfConfident + ", minTrustBound=" + minTrustBound + "]";
	}
}