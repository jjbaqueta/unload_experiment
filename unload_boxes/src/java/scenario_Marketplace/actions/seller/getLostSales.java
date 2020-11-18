// Internal action code for project Trust_scenarios

package scenario_Marketplace.actions.seller;

import jason.asSemantics.DefaultInternalAction;
import jason.asSemantics.TransitionSystem;
import jason.asSemantics.Unifier;
import jason.asSyntax.NumberTermImpl;
import jason.asSyntax.Term;
import scenario_Marketplace.entities.model.Seller;
import scenario_Marketplace.environments.Market;

/**
 * This action get the number of lost sales of a seller.
 */
public class getLostSales extends DefaultInternalAction 
{
	private static final long serialVersionUID = 1L;

	/**
	 * Arguments (come from parameter args):
	 * @param args[0]: seller's name.
	 * @return the number of lost sales.
	 */
    @Override
    public Object execute(TransitionSystem ts, Unifier un, Term[] args) throws Exception 
    {
    	Seller seller = Market.sellers.get(args[0].toString());
        return un.unifies(new NumberTermImpl(seller.getLostSales()), args[1]);
    }
}
