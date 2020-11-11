package scenario_unloadBoxes.actions.generic;

import jason.asSemantics.DefaultInternalAction;
import jason.asSemantics.TransitionSystem;
import jason.asSemantics.Unifier;
import jason.asSyntax.Literal;
import jason.asSyntax.Term;
import scenario_unloadBoxes.entities.model.SimpleElement;
import scenario_unloadBoxes.environments.UnloadEnv;

/**
 * This action get the position of a given target.
 */

public class getTargetPosition extends DefaultInternalAction 
{
	private static final long serialVersionUID = 1L;

	/**
	 * Arguments (come from parameter args):
	 * @param args[0]: target' name
	 * @return args[1]: the target' position
	 */
	@Override
    public Object execute(TransitionSystem ts, Unifier un, Term[] args) throws Exception 
    {
    	int id = Integer.parseInt(args[0].toString().split("_")[1]);
    	SimpleElement element = UnloadEnv.model.getElement(id);
    	return un.unifies(Literal.parseLiteral("pos("+ element.getPos().x + "," + element.getPos().y + ")"), args[1]);
    }
}