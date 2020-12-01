// Internal action code for project discharge_truck

package scenario_Marketplace.actions.buyer;

import java.util.HashMap;
import java.util.Map;

import jason.NoValueException;
import jason.asSemantics.DefaultInternalAction;
import jason.asSemantics.TransitionSystem;
import jason.asSemantics.Unifier;
import jason.asSyntax.Atom;
import jason.asSyntax.ListTerm;
import jason.asSyntax.Literal;
import jason.asSyntax.NumberTerm;
import jason.asSyntax.Structure;
import jason.asSyntax.Term;
import scenario_Marketplace.entities.model.Buyer;
import scenario_Marketplace.entities.model.Offer;
import scenario_Marketplace.enums.CriteriaType;
import scenario_Marketplace.environments.Market;

/**
 * A buyer uses this action to choose the best offer among the received offers.
 */
public class evaluateOffers extends DefaultInternalAction 
{
	private static final long serialVersionUID = 1L;

	/**
	 * Arguments (come from parameter args):
	 * @param args[0]: buyer's name
	 * @param args[1]: list of offers
	 * @return args[2]: the best seller.
	 */	
	@Override
    public Object execute(TransitionSystem ts, Unifier un, Term[] args) throws Exception 
	{	
		Map<String, Double> trustMap = new HashMap<String, Double>();
		Map<String, Double> priceMap = new HashMap<String, Double>();
		Map<String, Double> qualityMap = new HashMap<String, Double>();
		Map<String, Double> deliveryMap = new HashMap<String, Double>();
		
		Buyer buyer = Market.buyers.get(args[0].toString());
		ListTerm offers = (ListTerm) args[1];
	
		for(Term term : offers)
		{	
			// Parsing the offer - format{offer(product(Product,_,_,_), Seller)}
			Offer offer = Offer.parseOffer((Structure) term);
			
			// Getting trust value
			String query = "trust("+ offer.getSeller() +"," + offer.getProduct().getName() + ",_)";
			Structure trust = (Structure) ts.getAg().findBel(Literal.parseLiteral(query), un);
			NumberTerm trustValue = (NumberTerm) trust.getTerm(2);
			
			// Updating maps
			trustMap.put(offer.getSeller(), trustValue.solve());
			priceMap.put(offer.getSeller(), offer.getProduct().getAttribute(CriteriaType.PRICE));
			qualityMap.put(offer.getSeller(), offer.getProduct().getAttribute(CriteriaType.QUALITY));
			deliveryMap.put(offer.getSeller(), offer.getProduct().getAttribute(CriteriaType.DELIVERY));
		}		
		return un.unifies(classify(buyer, trustMap, priceMap, qualityMap, deliveryMap), args[2]);
    }
	
	/**
	 * This method find the best seller among all candidate seller.
	 * This choose is done according with sellers' trustworthiness.
	 * @param buyer: the buyer that is evaluating the sellers.
	 * @param trustMap: trust value of each seller.
	 * @param priceMap: price of evaluated product. 
	 * @param qualityMap: quality of evaluated product.
	 * @param deliveryMap: delivery time of evaluated product.
	 * @return the name of the best seller, or none, case the all of sellers aren't trustworthiness. 
	 */
	private Atom classify(Buyer buyer, Map<String, Double> trustMap, Map<String, Double> priceMap, 
			Map<String, Double> qualityMap, Map<String, Double> deliveryMap) throws NoValueException
	{
		// Getting optimal values for each criteria
		Double maxTrust = getMaxValue(trustMap).getValue();
		
		if(maxTrust >= buyer.getMinTrustBound())
		{
			Map.Entry<String, Double> minPrice = getMinValue(priceMap);
			Map.Entry<String, Double> maxQuality = getMaxValue(qualityMap); 
			Map.Entry<String, Double> minDelivery = getMinValue(deliveryMap);
			
			// Rating map
			Map<String, Double> ratings = new HashMap<String, Double>(); 
			
			// Normalization and computation of the ratings (based on weighted average)
			for(String sellerName : trustMap.keySet())
			{
				if(trustMap.get(sellerName) >= buyer.getMinTrustBound())
				{
					double score = 0.0;
					
					score += (minPrice.getValue() == 0.0 ? 0.0 : (priceMap.get(sellerName) / minPrice.getValue()) * (buyer.getPreference(CriteriaType.PRICE)));
					score -= (maxQuality.getValue() == 0.0 ? 0.0 : (qualityMap.get(sellerName) / maxQuality.getValue()) * (buyer.getPreference(CriteriaType.QUALITY)));
					score += (minDelivery.getValue() == 0.0 ? 0.0 : (deliveryMap.get(sellerName) / minDelivery.getValue()) * (buyer.getPreference(CriteriaType.DELIVERY)));
					score /= 3;
					
					ratings.put(sellerName, (score == 0.0 ? 0.0 : trustMap.get(sellerName) / score));
				}
			}
			
			// Getting best offer according with buyer preferences
			Map.Entry<String, Double> bestScore = getMaxValue(ratings);
			return new Atom(Literal.parseLiteral(bestScore.getKey()));
		}
		else
		{
			return new Atom(Literal.parseLiteral("none"));
		}
	}
	
	/**
	 * This methods searches for a tuple with minimum value in a hash map .
	 * @param map: a hash map.
	 * @return a tuple (entry)
	 */
	private Map.Entry<String, Double> getMinValue(Map<String, Double> map)
	{
		double minValue = Integer.MAX_VALUE;
		Map.Entry<String, Double> bestTuple = null;
		
		for(Map.Entry<String, Double> pair : map.entrySet())
		{
			if(minValue > pair.getValue())
			{
				minValue = pair.getValue();
				bestTuple = pair;
			}
		}
		return bestTuple;
	}
	
	/**
	 * This methods searches for a tuple with maximum value in a hash map .
	 * @param map: a hash map.
	 * @return a tuple (entry)
	 */
	private Map.Entry<String, Double> getMaxValue(Map<String, Double> map)
	{
		double maxValue = Integer.MIN_VALUE;
		Map.Entry<String, Double> bestTuple = null;
		
		for(Map.Entry<String, Double> pair : map.entrySet())
		{
			if(maxValue < pair.getValue())
			{
				maxValue = pair.getValue();
				bestTuple = pair;
			}
		}		
		return bestTuple;
	}
}