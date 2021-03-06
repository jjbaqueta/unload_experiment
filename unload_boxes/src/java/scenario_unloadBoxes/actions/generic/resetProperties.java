// Internal action code for project discharge_truck

package scenario_unloadBoxes.actions.generic;

import jason.asSemantics.DefaultInternalAction;
import jason.asSemantics.TransitionSystem;
import jason.asSemantics.Unifier;
import jason.asSyntax.Term;
import scenario_unloadBoxes.entities.model.SimpleElement;
import scenario_unloadBoxes.environments.UnloadEnv;

/**
 * This action resets some attributes of agent.
 */
public class resetProperties extends DefaultInternalAction 
{
	private static final long serialVersionUID = 1L;

	/**
	 * Arguments (come from parameter args):
	 * @param args[0]: Agent's name
	 */
	@Override
    public Object execute(TransitionSystem ts, Unifier un, Term[] args) throws Exception 
	{
		String agentName = args[0].toString();
		SimpleElement agent = UnloadEnv.model.getElement(Integer.parseInt(agentName.split("_")[1]));
		
		// Define new values for some attributes of agent
		agent.setProperties();
        return true;
    }
}
