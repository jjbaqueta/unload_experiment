package scenario_Marketplace.actions.buyer;

import jason.asSemantics.DefaultInternalAction;
import jason.asSemantics.TransitionSystem;
import jason.asSemantics.Unifier;
import jason.asSyntax.Literal;
import jason.asSyntax.Term;
import scenario_Marketplace.entities.model.Buyer;
import scenario_Marketplace.environments.Market;

/**
 * This action initializes a buyer.
 */
public class initialize extends DefaultInternalAction 
{
	private static final long serialVersionUID = 1L;
	
	/**
	 * Arguments (come from parameter args):
	 * @param args[0]: buyer's name
	 */
    @Override
    public Object execute(TransitionSystem ts, Unifier un, Term[] args) throws Exception 
    {
    	Buyer b = Market.buyers.get(args[0].toString());
    	
    	ts.getAg().addBel(Literal.parseLiteral("id(" + b.getId() + ")"));
    	ts.getAg().addBel(Literal.parseLiteral("self_confident(" + b.getSelfConfident() + ")"));
    	ts.getAg().addBel(Literal.parseLiteral("task_urgency(-0.5)"));

        return true;
    }
}