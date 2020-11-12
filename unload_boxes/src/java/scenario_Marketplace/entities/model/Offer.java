package scenario_Marketplace.entities.model;

import java.util.Arrays;
import java.util.Map;

import jason.asSyntax.Atom;
import jason.asSyntax.Literal;
import jason.asSyntax.Structure;
import scenario_Marketplace.environments.Market;

/*
 * This class implements an offer
 * Sellers can offer products to buyers, it turn, buyers can accept the offer according to their preferences
 */
public class Offer 
{
	private String sellerName;
	private Product product;
	
	public Offer(String sellerName, Product product) 
	{		
		this.product = product;
		this.sellerName = sellerName;
	}
	
	/**
	 * This method returns the offer in literal format
	 * @param productName: product's name.
	 * @param sellerName: seller's name.
	 * @param productAttributes: list of criterion that defines the order how the products' attributes will be shown.
	 * @return a literal.
	 */
	public static Literal getOfferAsLiteral(String productName, String sellerName, Map<String, Double> productAttributes)
	{
		Structure structure = new Structure("offer");
		structure.addTerm(Product.getProductAsLiteral(Arrays.asList(Market.criteriaOrder), productName, productAttributes));
		structure.addTerm(new Atom(Literal.parseLiteral(sellerName)));
		
		return structure;
	}
	
	/**
	 * This method returns the offer in literal format
	 * @return a literal.
	 */
	public Literal getOfferAsLiteral()
	{
		Structure structure = new Structure("offer");
		structure.addTerm(product.getProductAsLiteral(Arrays.asList(Market.criteriaOrder)));
		structure.addTerm(new Atom(Literal.parseLiteral(sellerName)));
		
		return structure;
	}
	
	public Product getProduct() 
	{
		return product;
	}

	public String getSeller() 
	{
		return sellerName;
	}
	
	@Override
	public String toString() 
	{
		return "Offer [product=" + product.toString() + ", sellerName=" + sellerName + "]";
	}
}
