package scenario_Marketplace.entities.model;

import java.util.List;

import scenario_Marketplace.entities.services.BehaviorFactory;
import scenario_Marketplace.enums.BehaviorPattern;
import scenario_Marketplace.enums.CriteriaType;

/*
 * This class implements a general seller
 * This kind of seller has his behavior defined according with necessity.
 */
public class GeneralSeller extends Seller
{	
	public GeneralSeller(String name, List<Product> products, int maxNumberInteractions,
			BehaviorPattern priceBehavior, BehaviorPattern qualityBehavior, BehaviorPattern deliveryBehavior) throws Exception  
	{
		super(name, products);
		
		setBehavior(CriteriaType.PRICE, BehaviorFactory.factoryMethod(priceBehavior, maxNumberInteractions));
		setBehavior(CriteriaType.QUALITY, BehaviorFactory.factoryMethod(qualityBehavior, maxNumberInteractions));
		setBehavior(CriteriaType.DELIVERY, BehaviorFactory.factoryMethod(deliveryBehavior, maxNumberInteractions));
	}
}