// Internal action code for project unload_boxes

package scenario_Marketplace.actions.buyer;

import jason.asSemantics.DefaultInternalAction;
import jason.asSemantics.TransitionSystem;
import jason.asSemantics.Unifier;
import jason.asSyntax.Literal;
import jason.asSyntax.NumberTerm;
import jason.asSyntax.Structure;
import jason.asSyntax.Term;
import net.sourceforge.jFuzzyLogic.FIS;
import scenario_Marketplace.enums.FilePaths;

public class getFuzzyVariables extends DefaultInternalAction 
{
	private static final long serialVersionUID = 1L;

	/**
	 * Arguments (come from parameter args):
	 * @param args[0]: urgency level
	 * @param args[1]: number of own impressions
	 * @param args[2]: number of third part impressions (other's images)
	 * @param args[3]: self-confident profile
	 * @return args[4]: list of fuzzy values.
	 */
    @Override
    public Object execute(TransitionSystem ts, Unifier un, Term[] args) throws Exception 
    {	
    	FIS fis = loadFuzzyBlocks();
    	
    	NumberTerm urgencyValue = (NumberTerm) args[0];
    	NumberTerm ownImpsValue = (NumberTerm) args[1];
    	NumberTerm otherImpsValue = (NumberTerm) args[2];
    	NumberTerm selfConfidentValue = (NumberTerm) args[3];
    	
    	Structure edges = getOutputValues(fis, urgencyValue.solve(), ownImpsValue.solve(), 
    			otherImpsValue.solve(), selfConfidentValue.solve());
    	
    	return un.unifies(edges, args[4]);
    }
    
	/**
	 * Load the fuzzy blocks from a input file (fuzzy_system.fcl)
	 * @return a Fuzzy inference system (FIS)
	 */
	private FIS loadFuzzyBlocks()
	{
        FIS fis = FIS.load(FilePaths.FUZZY_VARS.getPath(), true);
        
        if( fis == null )
        	throw new Error("Can't load file: '" + FilePaths.FUZZY_VARS.getPath() + "'");
        
        return fis;
	}
	
	/**
	 * This methods compute the values for each fuzzy varible of trust model based on a flc file.
	 * @param FIS: a Fuzzy inference system (FIS) (loaded from flc file).
	 * @param urgencyValue: the urgency level of a buyer to buy a product..
	 * @param ownImpsValue: the buyer's imagem about a seller.
	 * @param otherImpsValue: the reputation of the seller.
	 * @param selfConfidentValue: buyer's self confident profile.
	 * @return the values that'll be associated the edges of the trust model.
	 */
	private Structure getOutputValues(FIS fis, double urgencyValue, double ownImpsValue, 
			double otherImpsValue, double selfConfidentValue)
	{
		fis.setVariable("urgency", urgencyValue);
		fis.setVariable("own_imps", ownImpsValue);
		fis.setVariable("other_imps", otherImpsValue);
		fis.setVariable("self_confident", selfConfidentValue);
		fis.evaluate();
		
		Structure weights = new Structure("edges");
		
		weights.addTerm(Literal.parseLiteral("ability_effect("+ fis.getVariable("ability_effect").getValue() +")"));
		weights.addTerm(Literal.parseLiteral("availability_effect("+ fis.getVariable("availability_effect").getValue() +")"));
		weights.addTerm(Literal.parseLiteral("knowhow_effect("+ fis.getVariable("knowhow_effect").getValue() +")"));
		weights.addTerm(Literal.parseLiteral("ir_effect("+ fis.getVariable("ir_effect").getValue() +")"));
		weights.addTerm(Literal.parseLiteral("rep_effect("+ fis.getVariable("rep_effect").getValue() +")"));
		weights.addTerm(Literal.parseLiteral("img_effect("+ fis.getVariable("img_effect").getValue() +")"));
		
		return weights;
	}
}