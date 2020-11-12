package scenario_Marketplace.entities.model;

import java.util.List;
import scenario_Marketplace.enums.CriteriaType;

/*
 * This class implements a PriceOrientedBuyer.
 * This kind of buyer only cares about the product's price.
 */
public class PriceOrientedBuyer extends Buyer
{
	public PriceOrientedBuyer(String name, Double selfConfident, Double minTrustBound, List<Product> wishList) 
	{
		super(name, selfConfident, minTrustBound, wishList);
		
		this.setPreference(CriteriaType.PRICE, 1.0);
		this.setPreference(CriteriaType.QUALITY, 0.0);
		this.setPreference(CriteriaType.DELIVERY, 0.0);
	}
	
	public PriceOrientedBuyer(String name, List<Product> wishList) 
	{
		super(name, wishList);
		
		this.setPreference(CriteriaType.PRICE, 1.0);
		this.setPreference(CriteriaType.QUALITY, 0.0);
		this.setPreference(CriteriaType.DELIVERY, 0.0);
	}
}
