// Internal action code for project Trust_scenarios

package scenario_Marketplace.actions.seller;

import java.util.Arrays;

import jason.asSemantics.DefaultInternalAction;
import jason.asSemantics.TransitionSystem;
import jason.asSemantics.Unifier;
import jason.asSyntax.Structure;
import jason.asSyntax.Term;
import scenario_Marketplace.entities.model.Offer;
import scenario_Marketplace.entities.model.Seller;
import scenario_Marketplace.environments.Market;

/**
 * A seller uses this action to create the new conditions and terms of delivered product.
 */
public class getNewContract extends DefaultInternalAction 
{
	private static final long serialVersionUID = 1L;

	/**
	 * Arguments (come from parameter args):
	 * @param args[0]: seller's name.
	 * @param args[1]: the old offer.
	 * @return args[2]: the update offer with the real conditions and terms.
	 */	
    @Override
    public Object execute(TransitionSystem ts, Unifier un, Term[] args) throws Exception 
    {
    	Seller seller = Market.sellers.get(args[0].toString());
    	Offer oldOffer = Offer.parseOffer((Structure) args[1]);
        return un.unifies(seller.recalculateContractConditions(oldOffer, Arrays.asList(Market.criteriaOrder)), args[2]);
    }
}