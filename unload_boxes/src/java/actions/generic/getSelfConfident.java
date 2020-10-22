// Internal action code for project unload_boxes

package actions.generic;

import entities.model.SimpleElement;
import environments.DischargeEnv;
import jason.asSemantics.DefaultInternalAction;
import jason.asSemantics.TransitionSystem;
import jason.asSemantics.Unifier;
import jason.asSyntax.NumberTermImpl;
import jason.asSyntax.Term;

public class getSelfConfident extends DefaultInternalAction 
{
	private static final long serialVersionUID = 1L;

	/**
	 * Arguments (come from parameter args):
	 * @param args[0]: Agent's name
	 * @param args[1]: urgency
	 */
	@Override
    public Object execute(TransitionSystem ts, Unifier un, Term[] args) throws Exception 
    {
		String agentName = args[0].toString();
		SimpleElement agent = DischargeEnv.model.getElement(Integer.parseInt(agentName.split("_")[1]));
		Boolean value = agent.getSelfConfident();

		if(!value)
			return un.unifies(new NumberTermImpl(0.0), args[1]);
		else
			return un.unifies(new NumberTermImpl(1.0), args[1]);
    }
}
