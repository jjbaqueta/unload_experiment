// Internal action code for project discharge_truck

package scenario_unloadBoxes.actions.trucker;

import java.util.Map;

import jason.asSemantics.DefaultInternalAction;
import jason.asSemantics.TransitionSystem;
import jason.asSemantics.Unifier;
import jason.asSyntax.Literal;
import jason.asSyntax.Term;
import scenario_unloadBoxes.entities.model.Truck;
import scenario_unloadBoxes.environments.UnloadEnv;

/**
 * This action initializes a trucker.
 */
public class initialize extends DefaultInternalAction 
{
	private static final long serialVersionUID = 1L;
	private Map<Integer, Truck> truckMap = UnloadEnv.model.getWorld().getTruckMap();
	
	/**
	 * Arguments (come from parameter args):
	 * @param args[0]: Truck's name
	 */
    @Override
    public Object execute(TransitionSystem ts, Unifier un, Term[] args) throws Exception 
    {
    	Truck t = truckMap.get(Integer.parseInt(args[0].toString().split("_")[1]));
    	
    	ts.getAg().delBel(Literal.parseLiteral("id(_)"));
    	ts.getAg().delBel(Literal.parseLiteral("pos(_,_)"));
    	ts.getAg().delBel(Literal.parseLiteral("visible(_)"));    	
    	ts.getAg().delBel(Literal.parseLiteral("cargo_amount(_)"));
    	ts.getAg().delBel(Literal.parseLiteral("cargo_type(_)"));
    	ts.getAg().delBel(Literal.parseLiteral("task_urgency(_)"));
    	ts.getAg().delBel(Literal.parseLiteral("self_confident(_)"));
    	
    	if(t.isVisible())
    	{
    		ts.getAg().addBel(Literal.parseLiteral("visible(true)"));
    		
	    	ts.getAg().addBel(Literal.parseLiteral("id(" + t.getId() + ")"));
	    	ts.getAg().addBel(Literal.parseLiteral("pos(" + t.getPos().x + ", " + t.getPos().y + ")"));
	    	ts.getAg().addBel(Literal.parseLiteral("cargo_amount(" + t.getCargoAmount() + ")"));
	    	ts.getAg().addBel(Literal.parseLiteral("cargo_type(" + t.getCargoType().name().toLowerCase() + ")"));
	    	ts.getAg().addBel(Literal.parseLiteral("task_urgency(" + t.getUrgency() + ")"));
	    	ts.getAg().addBel(Literal.parseLiteral("self_confident(" + t.getSelfConfident() + ")"));
    	}
    	else
    	{
    		ts.getAg().addBel(Literal.parseLiteral("visible(false)"));
    	}
    	
        return true;
    }
}