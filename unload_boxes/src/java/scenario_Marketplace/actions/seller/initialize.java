package scenario_Marketplace.actions.seller;

import jason.asSemantics.DefaultInternalAction;
import jason.asSemantics.TransitionSystem;
import jason.asSemantics.Unifier;
import jason.asSyntax.Literal;
import jason.asSyntax.Term;
import scenario_Marketplace.entities.model.Seller;
import scenario_Marketplace.environments.Market;

/**
 * This action initializes a seller.
 */
public class initialize extends DefaultInternalAction 
{
	private static final long serialVersionUID = 1L;
	
	/**
	 * Arguments (come from parameter args):
	 * @param args[0]: seller's name
	 */
    @Override
    public Object execute(TransitionSystem ts, Unifier un, Term[] args) throws Exception 
    {
    	Seller s = Market.sellers.get(args[0].toString());
    	
    	ts.getAg().addBel(Literal.parseLiteral("id(" + s.getId()+ ")"));

        return true;
    }
}