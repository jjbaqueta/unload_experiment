package scenario_Marketplace.actions.buyer;

import jason.asSemantics.DefaultInternalAction;
import jason.asSemantics.TransitionSystem;
import jason.asSemantics.Unifier;
import jason.asSyntax.Literal;
import jason.asSyntax.Term;
import scenario_Marketplace.entities.model.Buyer;
import scenario_Marketplace.environments.Market;
import scenario_Marketplace.environments.MarketFuzzyConfig;

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
    	
    	// Buyer's attributes
    	ts.getAg().addBel(Literal.parseLiteral("id(" + b.getId() + ")"));
    	ts.getAg().addBel(Literal.parseLiteral("self_confident(" + b.getSelfConfident() + ")"));
    	ts.getAg().addBel(Literal.parseLiteral("urgency(" + b.getUrgency() + ")"));
    	
    	// Attributes for social module
//    	ts.getAg().addBel(Literal.parseLiteral("itm(knowhow," + MarketFuzzyConfig.max_references + ")"));
    	ts.getAg().addBel(Literal.parseLiteral("itm(knowhow," + MarketFuzzyConfig.max_own_impressions / 2 + ")"));
    	ts.getAg().addBel(Literal.parseLiteral("itm(reputation," + MarketFuzzyConfig.max_other_impressions + ")"));
    	ts.getAg().addBel(Literal.parseLiteral("itm(image," + MarketFuzzyConfig.max_own_impressions + ")"));
    	
        return true;
    }
}