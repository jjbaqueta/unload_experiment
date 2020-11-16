// Internal action code for project Trust_scenarios

package scenario_Marketplace.actions.buyer;

import jason.asSemantics.DefaultInternalAction;
import jason.asSemantics.TransitionSystem;
import jason.asSemantics.Unifier;
import jason.asSyntax.ListTerm;
import jason.asSyntax.NumberTermImpl;
import jason.asSyntax.Structure;
import jason.asSyntax.Term;
import scenario_Marketplace.entities.model.Offer;
import scenario_Marketplace.enums.CriteriaType;

/**
 * A buyer uses this action to compute the average price of a list of products.
 */
public class getAveragePrice extends DefaultInternalAction 
{
	private static final long serialVersionUID = 1L;

	/**
	 * Arguments (come from parameter args):
	 * @param args[0]: list of offers
	 * @return args[1]: the average price.
	 */	
	@Override
    public Object execute(TransitionSystem ts, Unifier un, Term[] args) throws Exception 
	{
		ListTerm offers = (ListTerm) args[0];
		double averagePrice = 0.0;
		
		for(Term term : offers)
		{
			Offer offer = Offer.parseOffer((Structure) term);
			averagePrice += offer.getProduct().getAttribute(CriteriaType.PRICE);
		}
		return un.unifies(new NumberTermImpl(averagePrice/offers.size()), args[1]);
    }
}
