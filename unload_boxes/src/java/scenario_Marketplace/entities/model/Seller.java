package scenario_Marketplace.entities.model;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import jason.asSyntax.ListTerm;
import jason.asSyntax.ListTermImpl;
import jason.asSyntax.Literal;
import scenario_Marketplace.enums.CriteriaType;
import scenario_Marketplace.environments.Market;

/*
 * This class implements a seller.
 * Each seller has his own list of products and his goal is selling his products.
 */
public abstract class Seller extends SimpleAgent
{
	protected int madeSales;
	protected int lostSales;
	protected Stock productsForSale;
	
	public Seller(String name) 
	{	
		super(name);
		this.madeSales = 0;
		this.lostSales = 0;
		this.productsForSale = new Stock();
	}
	
	/*
	 * This method allows the seller redefines the conditions of a contract according with his behaviour.
	 * @param oldOffer: initial contract.
	 * @param criteria: set of criteria for which the offer must be recalculated.
	 * @return a new contract in literal format, which will be replace the older offer.
	 */
	public Literal recalculateContractConditions(Offer oldOffer, Set<Map.Entry<CriteriaType, Boolean>> criteria)
	{
		Map<String, Double> newValues = new HashMap<String, Double>();
		
		// Copying all values from the old offer
		for(String attributeName : oldOffer.getProduct().getAttributesNames())
		{
			newValues.put(attributeName, oldOffer.getProduct().getAttribute(CriteriaType.valueOf(attributeName)));
		}
		
		// Updating some values from the old offer
		int interaction = madeSales + lostSales;
		
		for(Map.Entry<CriteriaType, Boolean> pair : criteria)
		{
			CriteriaType criterion = pair.getKey();
			
			double factor = getProductByName(oldOffer.getProduct().getName()).getBehavior(criterion).getBehaviorValueFor(interaction);
			
			if(pair.getValue())
				newValues.put(criterion.name(), factor * oldOffer.getProduct().getAttribute(criterion));				
			else
				newValues.put(criterion.name(), (1 / factor) * oldOffer.getProduct().getAttribute(criterion));
		}
		
		return Offer.getOfferAsLiteral(oldOffer.getProduct().getName(), name, newValues);
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
	
	/**
	 * This method adds a product to the stock.
	 * @param product: the product to be added.
	 * @param amount: the amount of the product.
	 */
	public void addProductToStock(Product product, int amount)
	{
		this.productsForSale.addProduct(product, amount);
	}
	
	/**
	 * This method withdraws a product from the stock.
	 * @param product: the product to be removed.
	 * @return true if there is product enough in stock, otherwise returns false.
	 */
	public boolean withdrawProduct(Product product)
	{
		return this.productsForSale.withdrawProduct(product);
	}
	
	/**
	 * @return true if the stock is empty.
	 */
	public boolean isEmptyStock()
	{
		return productsForSale.isEmpty();
	}
	
	/*
	 * @return a set of objects (product)
	 */
	public Set<Product> getProductsForSale() 
	{
		return this.productsForSale.getProducts();
	}
	
	/**
	 * Check if there is some product in stock.
	 * @param product: a given product.
	 * @return the amount of product stored in stock
	 */
	public int getAmountOf(Product product)
	{
		return productsForSale.getAmountOf(product);
	}
	
	/**
	 * Check if there is some product in stock.
	 * @param product: a given product.
	 * @return the amount of product stored in stock (the original amount)
	 */
	public int getOriginalAmountOf(Product product)
	{
		return productsForSale.getOriginalAmountOf(product);
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
		
		return "Seller [" + super.toString() + ", " + sb.toString() + 
				", madeSales=" + madeSales + ", lostSales=" + lostSales
				+ "]";
	}
}