// Internal action code for project discharge_truck

package scenario_unloadBoxes.actions.trucker;

import jason.asSemantics.DefaultInternalAction;
import jason.asSemantics.TransitionSystem;
import jason.asSemantics.Unifier;
import jason.asSyntax.Atom;
import jason.asSyntax.Literal;
import jason.asSyntax.Term;
import scenario_unloadBoxes.entities.model.Truck;
import scenario_unloadBoxes.environments.UnloadEnv;

/**
 * This actions gets the next trucker to be inserted in the system.
 */
public class getNext extends DefaultInternalAction 
{
	private static final long serialVersionUID = 1L;
	
	/**
	 * Arguments (come from parameter args):
	 * @return args[0]: trucker's name.
	 */
	@Override
    public Object execute(TransitionSystem ts, Unifier un, Term[] args) throws Exception 
    {
		Truck t = UnloadEnv.model.getWorld().getNextTruck();
		
		if(t != null)
			return un.unifies(new Atom(t.getName()), args[0]);
		else
			return un.unifies(Literal.parseLiteral("none"), args[0]);
    }
}
