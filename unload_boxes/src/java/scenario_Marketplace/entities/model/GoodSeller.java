package scenario_Marketplace.entities.model;

import java.util.List;

import scenario_Marketplace.entities.services.BehaviorFactory;
import scenario_Marketplace.enums.BehaviorPattern;
import scenario_Marketplace.enums.CriteriaType;

/*
 * This class implements a god seller
 * This kind of seller doesn't change the original contract conditions. 
 */
public class GoodSeller extends Seller
{	
	public GoodSeller(String name, List<Product> products, int maxNumberInteractions) throws Exception 
	{
		super(name, products);
		
		setBehavior(CriteriaType.PRICE, BehaviorFactory.factoryMethod(BehaviorPattern.CONSTANT, maxNumberInteractions));
		setBehavior(CriteriaType.QUALITY, BehaviorFactory.factoryMethod(BehaviorPattern.CONSTANT, maxNumberInteractions));
		setBehavior(CriteriaType.DELIVERY, BehaviorFactory.factoryMethod(BehaviorPattern.CONSTANT, maxNumberInteractions));
	}
}