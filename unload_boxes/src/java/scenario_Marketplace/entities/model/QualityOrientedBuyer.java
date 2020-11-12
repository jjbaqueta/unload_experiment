package scenario_Marketplace.entities.model;

import java.util.List;
import scenario_Marketplace.enums.CriteriaType;

/*
 * This class implements a QualityOrientedBuyer
 * This kind of buyer only cares about the product's quality.
 */
public class QualityOrientedBuyer extends Buyer
{
	public QualityOrientedBuyer(String name, Double selfConfident, Double minTrustBound, List<Product> wishList) 
	{
		super(name, selfConfident, minTrustBound, wishList);
		
		this.setPreference(CriteriaType.PRICE, 1.0);
		this.setPreference(CriteriaType.QUALITY, 0.0);
		this.setPreference(CriteriaType.DELIVERY, 0.0);
	}
	
	public QualityOrientedBuyer(String name, List<Product> wishList) 
	{
		super(name, wishList);
		
		this.setPreference(CriteriaType.PRICE, 1.0);
		this.setPreference(CriteriaType.QUALITY, 0.0);
		this.setPreference(CriteriaType.DELIVERY, 0.0);
	}
}
