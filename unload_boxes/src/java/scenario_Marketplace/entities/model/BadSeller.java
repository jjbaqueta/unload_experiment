package scenario_Marketplace.entities.model;

import java.util.List;

import scenario_Marketplace.entities.services.BehaviorFactory;
import scenario_Marketplace.enums.BehaviorPattern;
import scenario_Marketplace.enums.CriteriaType;

/*
 * This class implements a bad seller
 * This kind of seller may change all conditions from an initial contract. 
 */
public class BadSeller extends Seller
{	
	public BadSeller(String name, List<Product> products, int maxNumberInteractions) throws Exception 
	{
		super(name, products);
		
		setBehavior(CriteriaType.PRICE, BehaviorFactory.factoryMethod(BehaviorPattern.EXPONENTIAL_INCREASING, maxNumberInteractions));
		setBehavior(CriteriaType.QUALITY, BehaviorFactory.factoryMethod(BehaviorPattern.EXPONENTIAL_INCREASING, maxNumberInteractions));
		setBehavior(CriteriaType.DELIVERY, BehaviorFactory.factoryMethod(BehaviorPattern.EXPONENTIAL_INCREASING, maxNumberInteractions));
	}
}