// Internal action code for project unload_boxes

package scenario_Marketplace.actions.buyer;

import jason.asSemantics.DefaultInternalAction;
import jason.asSemantics.TransitionSystem;
import jason.asSemantics.Unifier;
import jason.asSyntax.Atom;
import jason.asSyntax.ListTerm;
import jason.asSyntax.ListTermImpl;
import jason.asSyntax.Term;
import scenario_Marketplace.entities.model.Buyer;
import scenario_Marketplace.environments.Market;

/**
 * This action returns all buyer on the system. 
 */
public class getOtherBuyers extends DefaultInternalAction 
{
	private static final long serialVersionUID = 1L;

	/**
	 * Arguments (come from parameter args):
	 * @param args[0]: buyer's name
	 * @return args[1]: other buyers on the system.
	 */	
    @Override
    public Object execute(TransitionSystem ts, Unifier un, Term[] args) throws Exception 
    {
    	ListTerm others = new ListTermImpl();
    	Buyer buyer = Market.buyers.get(args[0].toString());
    	
    	for(String b : Market.buyers.keySet())
    	{
    		if(!b.equals(buyer.getName()))
    		{
    			others.add(Atom.parseLiteral(b));
    		}
    	}
    	return un.unifies(others, args[1]);
    }
}