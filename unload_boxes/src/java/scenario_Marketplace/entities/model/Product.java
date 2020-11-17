package scenario_Marketplace.entities.model;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

import jason.asSyntax.Atom;
import jason.asSyntax.Literal;
import jason.asSyntax.NumberTermImpl;
import jason.asSyntax.Structure;
import scenario_Marketplace.entities.behaviors.Behavior;
import scenario_Marketplace.enums.CriteriaType;

/*
 * This class implements a product
 * The products are the base elements for negotiation between buyers and sellers. 
 */
public class Product implements Cloneable
{	
	private static AtomicInteger seqId = new AtomicInteger();
	
	private Integer id;
	private String name;
	private Map<String, Double> attributes;
	private Map<String, Behavior> behaviors;

	public Product(String name) 
	{
		this.id = seqId.getAndIncrement();
		this.name = name;
		this.attributes = new HashMap<String, Double>();
		this.behaviors = new HashMap<String, Behavior>();
	}
	
	public Product(Integer id, String name) 
	{
		this.id = id;
		this.name = name;
		this.attributes = new HashMap<String, Double>();
		this.behaviors = new HashMap<String, Behavior>();
	}

	public String getName() 
	{
		return name;
	}

	public Integer getId() 
	{
		return id;
	}

	/**
	 * Set a value for a product's attribute.
	 * @param criterion: the criterion associated to the attribute.
	 * @param value: attribute's value.
	 */
	public void setAttribute(CriteriaType criterion, Double value)
	{
		attributes.put(criterion.name(), value);
	}
	
	/**
	 * Get a value associated to a given attribute.
	 * @param criterion: the criterion associated to the attribute.
	 * @return value of the attribute.
	 */
	public Double getAttribute(CriteriaType criterion) 
	{
		if(attributes.containsKey(criterion.name()))
		{
			return attributes.get(criterion.name());
		}
		return null;
	}
	
	/**
	 * @return the name of all attributes.
	 */
	public Set<String> getAttributesNames()
	{
		return attributes.keySet();
	}
	
	/**
	 * Associate a sale behavior to the product.
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

	/**
	 * This method returns the product's attributes in literal format
	 * @param criteriaOrder: list of criterion that defines the order how the attributes will be shown.
	 * @return a literal.
	 */
	public Literal getProductAsLiteral(List<CriteriaType> criteriaOrder)
	{
		Structure structure = new Structure("product");
		structure.addTerm(new Atom(Literal.parseLiteral(name)));
		
		for(CriteriaType criterion : criteriaOrder)
		{
			structure.addTerm(new NumberTermImpl(attributes.get(criterion.name())));
		}
		return structure;
	}
	
	/**
	 * This method returns the product's attributes in literal format
	 * @param criteriaOrder: list of criterion that defines the order how the attributes will be shown.
	 * @param productName: a name for the product.
	 * @param productAttributes: the attributes for the product.
	 * @return a literal.
	 */
	public static Literal getProductAsLiteral(List<CriteriaType> criteriaOrder, String productName , Map<String, Double> productAttributes)
	{
		Structure structure = new Structure("product");
		structure.addTerm(new Atom(Literal.parseLiteral(productName)));
		
		for(CriteriaType criterion : criteriaOrder)
		{
			structure.addTerm(new NumberTermImpl(productAttributes.get(criterion.name())));
		}
		return structure;
	}

	@Override
	public int hashCode() 
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) 
	{
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Product other = (Product) obj;
		if (id == null) 
		{
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}

	public Object clone() 
	{
        try 
        {
            return super.clone();
        } catch (CloneNotSupportedException e) {
            System.out.println("Cloning not allowed.");
            return this;
        }
    }
	
	@Override
	public String toString() 
	{
		StringBuffer sb = new StringBuffer();
		sb.append(", attributes{ ");
		
		for(Map.Entry<String, Double> pair : attributes.entrySet())
		{
			sb.append(pair).append(" ");
		}
		sb.append(" }");
		
		sb.append(", behaviors{ ");
		
		for(Map.Entry<String, Behavior> pair : behaviors.entrySet())
		{
			sb.append(pair).append(" ");
		}
		sb.append(" }");
		
		return "Product [id=" + id + ", name=" + name + sb.toString() + "]";
	}
}