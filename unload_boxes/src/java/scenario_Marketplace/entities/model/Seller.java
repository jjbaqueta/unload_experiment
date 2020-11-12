package scenario_Marketplace.entities.model;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import jason.asSyntax.ListTerm;
import jason.asSyntax.ListTermImpl;
import jason.asSyntax.Literal;
import scenario_Marketplace.entities.behaviors.Behavior;
import scenario_Marketplace.enums.CriteriaType;
import scenario_Marketplace.environments.Market;

/*
 * This class implements a seller.
 * Each seller has his own list of products and his goal is selling his products.
 */
public abstract class Seller extends SimpleAgent
{
	protected Stock productsForSale;
	protected Map<String, Behavior> behaviors;
	protected int madeSales;
	protected int lostSales;
	
	public Seller(String name, List<Product> products) 
	{	
		super(name);
		this.madeSales = 0;
		this.lostSales = 0;
		this.productsForSale = new Stock(products);
		this.behaviors = new HashMap<String, Behavior>();
	}
	
	/*
	 * This method allows the seller redefines the conditions of a contract according with his behaviour.
	 * @param oldOffer: initial contract.
	 * @param criteria: list of criteria for which the offer must be recalculated.
	 * @return a new contract in literal format, which will be replace the older offer.
	 */
	public Literal recalculateContractConditions(Offer oldOffer, List<CriteriaType> criteria)
	{
		Map<String, Double> newValues = new HashMap<String, Double>();
		
		// Copying all values from the old offer
		for(String attributeName : oldOffer.getProduct().getAttributesNames())
		{
			newValues.put(attributeName, oldOffer.getProduct().getAttribute(CriteriaType.valueOf(attributeName)));
		}
		
		// Updating some values from the old offer
		for(CriteriaType criterion : criteria)
		{			
			newValues.put(criterion.name(), getBehavior(criterion).getBehaviorValueFor(madeSales + lostSales) 
					* oldOffer.getProduct().getAttribute(criterion));
		}
		
		return Offer.getOfferAsLiteral(oldOffer.getProduct().getName(), name, newValues);
	}
	
	/**
	 * Associate a behavior for a given sale criterion.
	 * @param criterion: the criterion of preference of buyer.
	 * @param behavior: a sale behavior.
	 */
	public void setBehavior(CriteriaType criterion, Behavior behavior)
	{
		behaviors.put(criterion.name(), behavior);
	}
	
	/**
	 * Get a value associated to a given criterion.
	 * @param criterion: the associated to a behavior.
	 * @return a sale behavior.
	 */
	public Behavior getBehavior(CriteriaType criterion) 
	{
		if(behaviors.containsKey(criterion.name()))
		{
			return behaviors.get(criterion.name());
		}
		return null;
	}
	
	public int getMadeSales() 
	{
		return this.madeSales;
	}

	public int getLostSales() 
	{
		return this.lostSales;
	}

	public void increaseMadeSales() 
	{
		this.madeSales++;
	}
	
	public void increaseLostSales() 
	{
		this.lostSales++;
	}
	
	public Product getProductByName(String productName)
	{
		return this.productsForSale.getProductByName(productName);
	}
	
	/*
	 * @return a set of objects (product)
	 */
	public Set<Product> getProductsForSale() 
	{
		return this.productsForSale.getProducts();
	}
	
	/*
	 * @return a sale list with all items in literal format
	 */
	public ListTerm getProductsAsLiteralList()
	{
		ListTerm saleList = new ListTermImpl();
		
		for(Product product : productsForSale.getProducts())
		{
			saleList.add(product.getProductAsLiteral(Arrays.asList(Market.criteriaOrder)));
		}
		return saleList;
	}

	@Override
	public String toString() 
	{
		StringBuffer sb = new StringBuffer();
		sb.append("productsForSale={");
		
		List<Object> products = Arrays.asList(productsForSale.getProducts().toArray());
		
		for(int i = 0; i < products.size() - 1; i++)
		{
			Product p = (Product) products.get(i);
			sb.append(p.getName()).append(", ");
		}
		
		Product p = (Product) products.get(products.size() - 1);
		sb.append(p.getName()).append("}");
		
		sb.append(", behaviors{ ");
		
		for(Map.Entry<String, Behavior> pair : behaviors.entrySet())
		{
			sb.append(pair).append(" ");
		}
		sb.append(" }");
		
		return "Seller [" + super.toString() + ", " + sb.toString() + 
				", madeSales=" + madeSales + ", lostSales=" + lostSales
				+ "]";
	}
}