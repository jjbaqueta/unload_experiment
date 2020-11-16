package scenario_Marketplace.entities.model;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/*
 * This class implements a seller's stock.
 * Each seller has his own stock.
 */
public class Stock 
{
	private Map<Product, Integer> amountProduct;
	private Map<Product, Integer> originalAmount;

	public Stock()
	{
		amountProduct = new HashMap<Product, Integer>();
		originalAmount = new HashMap<Product, Integer>();
	}
	
	/**
	 * @return true if the stock is empty.
	 */
	public boolean isEmpty()
	{
		return amountProduct.isEmpty();
	}
	
	/**
	 * This method adds a product to the stock.
	 * @param product: the product to be added.
	 * @param amount: the amount of the product.
	 * @param supplyTime: time spent to replenish the stock.
	 */
	public void addProduct(Product product, int amount)
	{
		amountProduct.put(product, amount);
		originalAmount.put(product, amount);
	}
	
	/**
	 * This method withdraws a product from the stock.
	 * @param product: the product to be removed.
	 * @return true if there is product enough in stock, otherwise returns false.
	 */
	public boolean withdrawProduct(Product product)
	{
		if(amountProduct.get(product) > 0)
		{
			int amount = amountProduct.get(product);
			amountProduct.put(product, amount - 1);
			return true;
		}
		return false;
	}
	
	/**
	 * This method searches for a product by name.
	 * @param productName: the name a product.
	 * @return a product if it exists in stock, otherwise, returns null.
	 */
	public Product getProductByName(String productName)
	{
		for(Product product : amountProduct.keySet())
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
		return amountProduct.keySet();
	}
	
	/**
	 * Check if there is some product in stock.
	 * @param product: a given product.
	 * @return the amount of product stored in stock
	 */
	public int getAmountOf(Product product)
	{
		return amountProduct.get(product);
	}
	
	/**
	 * Check if there is some product in stock.
	 * @param product: a given product.
	 * @return the amount of product stored in stock (the original amount)
	 */
	public int getOriginalAmountOf(Product product)
	{
		return originalAmount.get(product);
	}
}
