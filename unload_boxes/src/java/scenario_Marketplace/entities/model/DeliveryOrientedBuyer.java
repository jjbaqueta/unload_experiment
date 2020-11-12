package scenario_Marketplace.entities.model;

import java.util.List;
import scenario_Marketplace.enums.CriteriaType;

/*
 * This class implements a DeliveryOrientedBuyer.
 * This kind of buyer only cares about the product's delivery time.
 */
public class DeliveryOrientedBuyer extends Buyer
{
	public DeliveryOrientedBuyer(String name, Double selfConfident, Double minTrustBound, List<Product> wishList) 
	{
		super(name, selfConfident, minTrustBound, wishList);
		
		this.setPreference(CriteriaType.PRICE, 0.0);
		this.setPreference(CriteriaType.QUALITY, 0.0);
		this.setPreference(CriteriaType.DELIVERY, 1.0);
	}
	
	public DeliveryOrientedBuyer(String name, List<Product> wishList) 
	{
		super(name, wishList);
		
		this.setPreference(CriteriaType.PRICE, 0.0);
		this.setPreference(CriteriaType.QUALITY, 0.0);
		this.setPreference(CriteriaType.DELIVERY, 1.0);
	}
}