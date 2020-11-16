package scenario_Marketplace.entities.model;

import java.util.List;

/*
 * This class implements a general seller
 * This kind of seller has his behavior defined according with necessity.
 */
public class GeneralSeller extends Seller
{	
	public GeneralSeller(String name, List<Product> products) throws Exception  
	{
		super(name, products);
	}
}