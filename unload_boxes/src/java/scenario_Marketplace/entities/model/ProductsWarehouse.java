package scenario_Marketplace.entities.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Random;

import scenario_Marketplace.enums.CriteriaType;

/*
 * This class implements a set of operations regarding to products
 */
public class ProductsWarehouse 
{	
	private Queue<Product> baseProducts;
	
	public ProductsWarehouse() 
	{
		baseProducts = new LinkedList<Product>();
		
		Product tv = new Product("tv");
		tv.setAttribute(CriteriaType.PRICE, 1500.0);
		tv.setAttribute(CriteriaType.QUALITY, 1.0);
		tv.setAttribute(CriteriaType.DELIVERY, 10.0);
		baseProducts.add(tv);
		
		Product desktop = new Product("desktop");
		desktop.setAttribute(CriteriaType.PRICE, 2500.0);
		desktop.setAttribute(CriteriaType.QUALITY, 1.0);
		desktop.setAttribute(CriteriaType.DELIVERY, 10.0);
		baseProducts.add(desktop);
		
		Product notebook = new Product("notebook");
		notebook.setAttribute(CriteriaType.PRICE, 3000.0);
		notebook.setAttribute(CriteriaType.QUALITY, 1.0);
		notebook.setAttribute(CriteriaType.DELIVERY, 10.0);
		baseProducts.add(notebook);
		
		Product smartphone = new Product("smartphone");
		smartphone.setAttribute(CriteriaType.PRICE, 4500.0);
		smartphone.setAttribute(CriteriaType.QUALITY, 1.0);
		smartphone.setAttribute(CriteriaType.DELIVERY, 10.0);
		baseProducts.add(smartphone);
		
		Product tablet = new Product("tablet");
		tablet.setAttribute(CriteriaType.PRICE, 3500.0);
		tablet.setAttribute(CriteriaType.QUALITY, 1.0);
		tablet.setAttribute(CriteriaType.DELIVERY, 10.0);
		baseProducts.add(tablet);
	}
	
	/**
	 * Generate a list of products, where each type of product has a same attribute
	 * @param NumberOfProducts: amount of products that will be added to the list of products.
	 * @return a list of products.
	 */
	public List<Product> generateDefaultProducts(int NumberOfProducts) throws Exception
	{
		List<Product> products = new ArrayList<Product>();
		
		for(int i = 0; i < NumberOfProducts; i++)
		{
			Product baseProduct = baseProducts.poll();
			
			Product product = new Product(baseProduct.getName());
			product.setAttribute(CriteriaType.PRICE, baseProduct.getAttribute(CriteriaType.PRICE));
			product.setAttribute(CriteriaType.QUALITY, baseProduct.getAttribute(CriteriaType.QUALITY));
			product.setAttribute(CriteriaType.DELIVERY, baseProduct.getAttribute(CriteriaType.DELIVERY));
			
			products.add(product);
			baseProducts.add(baseProduct);
		}
		return products;
	}
	
	/**
	 * Generate a list of products, where each type of product has a different attribute
	 * @param NumberOfProducts: amount of products that will be added to the list of products.
	 * @return a list of products.
	 */
	public List<Product> generateDifferentProducts(int NumberOfProducts) throws Exception
	{
		Random rand = new Random();
		List<Product> products = new ArrayList<Product>();
		
		products = generateDefaultProducts(NumberOfProducts);
		Iterator<Product> i = baseProducts.iterator();
		
		while(i.hasNext())
		{
			Product baseProduct = i.next();
			double changed = rand.nextDouble() / 2;
			
			for(Product product : products)
			{
				if(product.getName().equals(baseProduct.getName()))
				{
					product.setAttribute(CriteriaType.PRICE, product.getAttribute(CriteriaType.PRICE) * (1 + changed));
					product.setAttribute(CriteriaType.QUALITY, product.getAttribute(CriteriaType.QUALITY) * changed);
					product.setAttribute(CriteriaType.DELIVERY, product.getAttribute(CriteriaType.DELIVERY) * (1 + changed));
				}
			}
		}
		return products;
	}
	
	/**
	 * This method shuffles a list of products.
	 * @param products: a list of products.
	 */
	public void shuffling(List<Product> products)
	{
		Collections.shuffle(products);
	}
    
	/**
     * This method shows the original products.
     */
	public void showProducts()
	{
		Iterator<Product> i = baseProducts.iterator();
		
		while(i.hasNext())
		{
			System.out.println(i.next());
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
