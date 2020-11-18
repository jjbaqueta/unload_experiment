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
 * This action get the number of made sales of a seller.
 */
public class getMadeSales extends DefaultInternalAction 
{
	private static final long serialVersionUID = 1L;

	/**
	 * Arguments (come from parameter args):
	 * @param args[0]: seller's name.
	 * @return the number of made sales.
	 */
    @Override
    public Object execute(TransitionSystem ts, Unifier un, Term[] args) throws Exception 
    {
    	Seller seller = Market.sellers.get(args[0].toString());
        return un.unifies(new NumberTermImpl(seller.getMadeSales()), args[1]);
    }
}
