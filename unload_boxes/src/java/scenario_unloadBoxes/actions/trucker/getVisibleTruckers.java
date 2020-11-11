// Internal action code for project unload_boxes

package scenario_unloadBoxes.actions.trucker;

import java.util.Map;

import jason.asSemantics.DefaultInternalAction;
import jason.asSemantics.TransitionSystem;
import jason.asSemantics.Unifier;
import jason.asSyntax.Atom;
import jason.asSyntax.ListTerm;
import jason.asSyntax.ListTermImpl;
import jason.asSyntax.Term;
import scenario_unloadBoxes.entities.model.Truck;
import scenario_unloadBoxes.environments.UnloadEnv;

/**
 * This action returns all visible truckers 
 */
public class getVisibleTruckers extends DefaultInternalAction 
{
	private static final long serialVersionUID = 1L;
	private Map<Integer, Truck> truckMap = UnloadEnv.model.getWorld().getTruckMap();

    @Override
    public Object execute(TransitionSystem ts, Unifier un, Term[] args) throws Exception 
    {
    	ListTerm truckers = new ListTermImpl();
    	
    	for(Truck t : truckMap.values())
    	{
    		if(t.isVisible())
    		{
    			truckers.add(Atom.parseLiteral(t.getName()));
    		}
    	}
    	return un.unifies(truckers, args[0]);
    }
}