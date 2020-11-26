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
import scenario_Marketplace.environments.Market;
import trustModel.repAndImg.enums.Mnemonic;

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
    	NumberTerm urgencyValue = (NumberTerm) args[0];
    	NumberTerm ownImpsValue = (NumberTerm) args[1];
    	NumberTerm otherImpsValue = (NumberTerm) args[2];
    	NumberTerm selfConfidentValue = (NumberTerm) args[3];
    	NumberTerm references = (NumberTerm) args[4];
    	
    	Structure edges = getOutputValues(Market.fis, urgencyValue.solve(), ownImpsValue.solve(), 
    			otherImpsValue.solve(), selfConfidentValue.solve(), references.solve());
    	
    	return un.unifies(edges, args[5]);
    }
	
	/**
	 * This methods compute the values for each fuzzy varible of trust model based on a flc file.
	 * @param FIS: a Fuzzy inference system (FIS) (loaded from flc file).
	 * @param urgencyValue: the urgency level of a buyer to buy a product..
	 * @param ownImpsValue: the buyer's imagem about a seller.
	 * @param otherImpsValue: the reputation of the seller.
	 * @param selfConfidentValue: buyer's self confident profile.
	 * @param references: if the seller has references
	 * @return the values that'll be associated the edges of the trust model.
	 */
	private Structure getOutputValues(FIS fis, double urgencyValue, double ownImpsValue, 
			double otherImpsValue, double selfConfidentValue, double references)
	{
		fis.setVariable(Mnemonic.URGENCY.getMnemonic(), urgencyValue);
		fis.setVariable(Mnemonic.OWN_IMPS.getMnemonic(), ownImpsValue);
		fis.setVariable(Mnemonic.OTHER_IMPS.getMnemonic(), otherImpsValue);
		fis.setVariable(Mnemonic.SELFCONFIDENT.getMnemonic(), selfConfidentValue);
		fis.setVariable(Mnemonic.REFERENCES.getMnemonic(), references);
		fis.evaluate();
		
//		printVars(fis);
		
		Structure weights = new Structure("edges");
		
		weights.addTerm(Literal.parseLiteral(Mnemonic.ABILITY_EFFECT.getMnemonic() + "("+ fis.getVariable(Mnemonic.ABILITY_EFFECT.getMnemonic()).getValue() +")"));
		weights.addTerm(Literal.parseLiteral(Mnemonic.AVAILABILITY_EFFECT.getMnemonic() + "("+ fis.getVariable(Mnemonic.AVAILABILITY_EFFECT.getMnemonic()).getValue() +")"));
		weights.addTerm(Literal.parseLiteral(Mnemonic.KNOWHOW_EFFECT.getMnemonic() + "("+ fis.getVariable(Mnemonic.KNOWHOW_EFFECT.getMnemonic()).getValue() +")"));
		weights.addTerm(Literal.parseLiteral(Mnemonic.REASONING_EFFECT.getMnemonic() + "("+ fis.getVariable(Mnemonic.REASONING_EFFECT.getMnemonic()).getValue() +")"));
		weights.addTerm(Literal.parseLiteral(Mnemonic.REP_EFFECT.getMnemonic() + "("+ fis.getVariable(Mnemonic.REP_EFFECT.getMnemonic()).getValue() +")"));
		weights.addTerm(Literal.parseLiteral(Mnemonic.IMG_EFFECT.getMnemonic() + "("+ fis.getVariable(Mnemonic.IMG_EFFECT.getMnemonic()).getValue() +")"));
		
		return weights;
	}
	
	/**
	 * Show the fuzzy variable on screen.
	 * @param FIS: a Fuzzy inference system (FIS) (loaded from flc file).
	 */
	@SuppressWarnings("unused")
	private void printVars(FIS fis)
	{
		System.out.println(fis.getVariable(Mnemonic.URGENCY.getMnemonic()));
		System.out.println(fis.getVariable(Mnemonic.OWN_IMPS.getMnemonic()));
		System.out.println(fis.getVariable(Mnemonic.OTHER_IMPS.getMnemonic()));
		System.out.println(fis.getVariable(Mnemonic.SELFCONFIDENT.getMnemonic()));
		System.out.println(fis.getVariable(Mnemonic.REFERENCES.getMnemonic()));
	}
}