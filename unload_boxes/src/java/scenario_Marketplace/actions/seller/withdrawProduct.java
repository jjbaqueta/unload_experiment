// Internal action code for project Trust_scenarios

package scenario_Marketplace.actions.seller;

import jason.asSemantics.DefaultInternalAction;
import jason.asSemantics.TransitionSystem;
import jason.asSemantics.Unifier;
import jason.asSyntax.Atom;
import jason.asSyntax.Literal;
import jason.asSyntax.Term;
import scenario_Marketplace.entities.model.Product;
import scenario_Marketplace.entities.model.Seller;
import scenario_Marketplace.environments.Market;

/**
 * A seller uses this action remove a product from his stock.
 */
public class withdrawProduct extends DefaultInternalAction 
{
	private static final long serialVersionUID = 1L;

	/**
	 * Arguments (come from parameter args):
	 * @param args[0]: seller's name.
	 * @param args[1]: product's name.
	 * @return args[2]: a product status according to the product's condition.
	 */	
    @Override
    public Object execute(TransitionSystem ts, Unifier un, Term[] args) throws Exception 
    {
    	Seller seller = Market.sellers.get(args[0].toString());
    	Atom productName = (Atom) args[1];
    	
    	Product product = seller.getProductByName(productName.toString());
    	
    	if(product == null)
    	{
    		throw new Error("It is impossible to finde de product in the stock: " + productName.toString());
    	}
    	else
    	{
    		if(seller.withdrawProduct(product))
    		{
    			return un.unifies(Literal.parseLiteral("done"), args[2]);
    		}
    		else
    		{
    			int originalAmount = seller.getOriginalAmountOf(product);
    			seller.addProductToStock(product, originalAmount);
    			
    			return un.unifies(Literal.parseLiteral("empty"), args[2]);
    		}
    	}
    }
}