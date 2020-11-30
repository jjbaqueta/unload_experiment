// Internal action code for project Trust_scenarios

package scenario_Marketplace.actions.buyer;

import jason.asSemantics.DefaultInternalAction;
import jason.asSemantics.TransitionSystem;
import jason.asSemantics.Unifier;
import jason.asSyntax.NumberTerm;
import jason.asSyntax.NumberTermImpl;
import jason.asSyntax.Structure;
import jason.asSyntax.Term;
import scenario_Marketplace.entities.model.Offer;
import scenario_Marketplace.enums.CriteriaType;

/**
 * A buyer uses this action to rate a seller according to contract terms.
 */
public class getRating extends DefaultInternalAction 
{
	private static final long serialVersionUID = 1L;
	
	/**
	 * Arguments (come from parameter args):
	 * @param args[0]: old offer
	 * @param args[1]: new offer
	 * @param args[2]: average Price
	 * @return args[3]: a rating.
	 */	
    @Override
    public Object execute(TransitionSystem ts, Unifier un, Term[] args) throws Exception 
    {	
    	Offer proposal = Offer.parseOffer((Structure) args[0]);
		Offer contract = Offer.parseOffer((Structure) args[1]);
		NumberTerm priceAverage = (NumberTerm) args[2];

        return un.unifies(evaluateSeller(proposal, contract, priceAverage.solve()), args[3]);
    }
    
    /*
	 * This method computes the discrepancy between the terms of the proposal and the contract terms. 
	 * @param proposal: initial seller's offer.
	 * @param contract: real seller's offer, considering respective penalties (like delivery delay and quality reductions).
	 * @param priceAverage: the average of price based on the all received proposals.
	 * @return a structure that represents a rating.
	 */
	private Structure evaluateSeller(Offer proposal, Offer contract, double priceAverage)
	{
		double priceDiscrepancy = contract.getProduct().getAttribute(CriteriaType.PRICE) / proposal.getProduct().getAttribute(CriteriaType.PRICE);
		double qualityDiscrepancy = contract.getProduct().getAttribute(CriteriaType.QUALITY) / proposal.getProduct().getAttribute(CriteriaType.QUALITY);
		double deliveryDiscrepancy = contract.getProduct().getAttribute(CriteriaType.DELIVERY) / proposal.getProduct().getAttribute(CriteriaType.DELIVERY);
		
		Structure rating = new Structure("rating");
		rating.addTerm(new NumberTermImpl(getScoreUp(priceDiscrepancy)));
		rating.addTerm(new NumberTermImpl(getScoreDown(qualityDiscrepancy)));
		rating.addTerm(new NumberTermImpl(getScoreUp(deliveryDiscrepancy)));
		
		return rating;
	}
	
	/**
	 * This method defines the score for an increasing criterion
	 * The minimum score is -1 and the maximum score is 1.
	 * @param variation: a normalized value
	 * @return the score value.
	 */
	private double getScoreUp (double variation)
	{	
		if(variation <= 1)
			return 1;
		else if(variation >= 2)
			return -1;
		else
			return -2 * variation + 3;
	}
	
	/**
	 * This method defines the score for an decreasing criterion
	 * The minimum score is -1 and the maximum score is 1.
	 * @param variation: a normalized value
	 * @return the score value.
	 */
	private double getScoreDown (double variation)
	{	
		if(variation >= 1)
			return 1;
		else if(variation <= 0)
			return -1;
		else
			return 2 * variation - 1;
	}
}