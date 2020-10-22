// Internal action code for project unload_boxes

package actions.generic;

import fuzzyClassifier.FuzzySystem;
import jason.asSemantics.DefaultInternalAction;
import jason.asSemantics.TransitionSystem;
import jason.asSemantics.Unifier;
import jason.asSyntax.ListTerm;
import jason.asSyntax.ListTermImpl;
import jason.asSyntax.NumberTerm;
import jason.asSyntax.NumberTermImpl;
import jason.asSyntax.Term;

public class getFuzzyVariables extends DefaultInternalAction 
{
	private static final long serialVersionUID = 1L;

	/**
	 * Arguments (come from parameter args):
	 * @param args[0]: urgency level
	 * @param args[1]: number of boxes
	 * @param args[2]: number of own impressions
	 * @param args[3]: number of third part impressions (other's images)
	 * @param args[4]: self-confident profile
	 * @return args[5]: list of fuzzy values.
	 */
    @Override
    public Object execute(TransitionSystem ts, Unifier un, Term[] args) throws Exception 
    {
    	ListTerm fuzzyOutputs = new ListTermImpl();
    	
    	NumberTerm urgencyValue = (NumberTerm) args[0];
    	NumberTerm numBoxesValue = (NumberTerm) args[1];
    	NumberTerm ownImpsValue = (NumberTerm) args[2];
    	NumberTerm otherImpsValue = (NumberTerm) args[3];
    	NumberTerm selfConfidentValue = (NumberTerm) args[4];
    	
    	double[] outputs = FuzzySystem.getOutputValues(urgencyValue.solve(), numBoxesValue.solve(), ownImpsValue.solve(), otherImpsValue.solve(), selfConfidentValue.solve());
    	
    	for(Double output : outputs)
    	{
    		fuzzyOutputs.add( new NumberTermImpl(output));
    	}
    	return un.unifies(fuzzyOutputs, args[5]);
    }
}