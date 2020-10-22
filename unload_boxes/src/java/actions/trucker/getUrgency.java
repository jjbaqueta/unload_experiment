// Internal action code for project unload_boxes

package actions.trucker;

import java.util.Map;

import entities.model.Truck;
import environments.DischargeEnv;
import jason.asSemantics.DefaultInternalAction;
import jason.asSemantics.TransitionSystem;
import jason.asSemantics.Unifier;
import jason.asSyntax.NumberTerm;
import jason.asSyntax.NumberTermImpl;
import jason.asSyntax.Term;

public class getUrgency extends DefaultInternalAction 
{
	private static final long serialVersionUID = 1L;
	private Map<Integer, Truck> truckMap = DischargeEnv.model.getWorld().getTruckMap();

	/**
	 * Arguments (come from parameter args):
	 * @param args[0]: Agent's name
	 * @param args[1]: urgency
	 */
	@Override
    public Object execute(TransitionSystem ts, Unifier un, Term[] args) throws Exception 
    {
		Truck t = truckMap.get(Integer.parseInt(args[0].toString().split("_")[1]));
		NumberTerm urgency = new NumberTermImpl(t.getUrgency());
        return un.unifies(urgency, args[1]);
    }
}
