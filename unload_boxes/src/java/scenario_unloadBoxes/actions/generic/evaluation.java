// Internal action code for project unload_boxes

package scenario_unloadBoxes.actions.generic;

import jason.asSemantics.DefaultInternalAction;
import jason.asSemantics.TransitionSystem;
import jason.asSemantics.Unifier;
import jason.asSyntax.NumberTerm;
import jason.asSyntax.NumberTermImpl;
import jason.asSyntax.Term;

public class evaluation extends DefaultInternalAction 
{
	private static final long serialVersionUID = 1L;

	/**
	 * Arguments (come from parameter args):
	 * @param args[0]: estimated value
	 * @param args[1]: real value
	 * @return args[2]: evaluation
	 */
    @Override
    public Object execute(TransitionSystem ts, Unifier un, Term[] args) throws Exception 
    {
    	NumberTerm estimatedValue = (NumberTerm) args[0];
    	NumberTerm realValue = (NumberTerm) args[1];
    	
    	double rating = 0;
    	double estiV = estimatedValue.solve();
    	double realV = realValue.solve();
    	
    	if(estiV != 0)
    	{
    		rating = computeScore(realV / estiV);
    	}
    	
    	return un.unifies(new NumberTermImpl(rating), args[2]);
    }
    
    private double computeScore (double variation)
	{	
		if(variation <= 1)
			return 1;
		else if(variation >= 2)
			return -1;
		else
			return -2 * variation + 3;	//intermediate score (based on line equation)
	}
}
