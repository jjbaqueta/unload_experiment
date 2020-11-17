package scenario_Marketplace.entities.model;

import java.util.Arrays;
import java.util.Map;

import jason.NoValueException;
import jason.asSyntax.Atom;
import jason.asSyntax.Literal;
import jason.asSyntax.NumberTerm;
import jason.asSyntax.Structure;
import scenario_Marketplace.enums.CriteriaType;
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
	 * This method parses a offer in literal format to a object.
	 * @param offer: a structure that represents an offer.
	 * @return an offer.
	 */
	public static Offer parseOffer(Structure offer) throws NoValueException
	{
		// Parsing the offer - format{offer(product(Product,_,_,_), Seller)}
		Structure product  = (Structure) offer.getTerm(0);
		Seller seller =  Market.sellers.get(offer.getTerm(1).toString());
		
		// Parsing the product
		Atom productName = (Atom) product.getTerm(0);
		NumberTerm price = (NumberTerm) product.getTerm(1);
		NumberTerm quality = (NumberTerm) product.getTerm(2);
		NumberTerm delivery = (NumberTerm) product.getTerm(3);

		Product p = new Product(null, productName.toString());
		p.setAttribute(CriteriaType.PRICE, price.solve());
		p.setAttribute(CriteriaType.QUALITY, quality.solve());
		p.setAttribute(CriteriaType.DELIVERY, delivery.solve());
		
		return new Offer(seller.getName(), p);
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
