// Internal action code for project Trust_scenarios

package scenario_Marketplace.actions.seller;

import java.util.HashMap;
import java.util.Map;

import jason.asSemantics.DefaultInternalAction;
import jason.asSemantics.TransitionSystem;
import jason.asSemantics.Unifier;
import jason.asSyntax.Structure;
import jason.asSyntax.Term;
import scenario_Marketplace.entities.model.Offer;
import scenario_Marketplace.entities.model.Seller;
import scenario_Marketplace.enums.CriteriaType;
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
    	
    	/*
    	 *  Setting the criteria that'll be changed.
    	 *  A criterion defined as false has its values reduced as it is changed.
    	 *  A criterion defined as true has its values increased as it is changed.
    	 */
    	Map<CriteriaType, Boolean> criteria = new HashMap<CriteriaType, Boolean>();
    	criteria.put(CriteriaType.QUALITY, false);
    	criteria.put(CriteriaType.DELIVERY, true);
    	
    	// Creating a new contract
        return un.unifies(seller.recalculateContractConditions(oldOffer, criteria.entrySet()), args[2]);
    }
}