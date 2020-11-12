package scenario_Marketplace.entities.model;

import java.util.List;
import java.util.Random;
import scenario_Marketplace.enums.CriteriaType;

/*
 * This class implements a GeneralOrientedBuyer.
 * This kind of buyer cares about different preferences.
 */
public class GeneralOrientedBuyer extends Buyer
{	
	public GeneralOrientedBuyer(String name, Double selfConfident, Double minTrustBound, List<Product> wishList, 
			double pricePreference, double qualityPreference, double deliveryPreference) 
	{
		super(name, selfConfident, minTrustBound, wishList);
		
		this.setPreference(CriteriaType.PRICE, pricePreference);
		this.setPreference(CriteriaType.QUALITY, qualityPreference);
		this.setPreference(CriteriaType.DELIVERY, deliveryPreference);
	}
	
	public GeneralOrientedBuyer(String name, List<Product> wishList) 
	{
		super(name, wishList);
		
		Random rand = new Random();
		
		this.setPreference(CriteriaType.PRICE, rand.nextDouble());
		this.setPreference(CriteriaType.QUALITY, rand.nextDouble());
		this.setPreference(CriteriaType.DELIVERY, rand.nextDouble());
	}
}