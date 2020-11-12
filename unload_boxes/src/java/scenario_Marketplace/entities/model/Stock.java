package scenario_Marketplace.entities.model;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/*
 * This class implements a seller's stock.
 * Each seller has his own stock.
 */
public class Stock 
{
	private Map<Product, Integer> productsForSale;

	public Stock(List<Product> products) 
	{
		productsForSale = new HashMap<Product, Integer>();
		
		for(Product product : products)
		{
			if(productsForSale.containsKey(product))
			{
				int amount = productsForSale.get(product);
				productsForSale.put(product, amount + 1);
			}
			else
			{
				productsForSale.put(product, 1);
			}
		}
	}
	
	/**
	 * This method searches for a product by name.
	 * @param productName: the name a product.
	 * @return a product if it exists in stock, otherwise, returns null.
	 */
	public Product getProductByName(String productName)
	{
		for(Product product : productsForSale.keySet())
		{
			if(productName.equals(product.getName()))
			{
				return product;
			}
		}
		return null;
	}
	
	/**
	 * @return the set of product in stock. 
	 */
	public Set<Product> getProducts()
	{
		return productsForSale.keySet();
	}
	
	/**
	 * Check if there is some product in stock.
	 * @param product: a given product.
	 * @return the amount of product stored in stock
	 */
	public int getAmountOf(Product product)
	{
		return productsForSale.get(product);
	}
}
