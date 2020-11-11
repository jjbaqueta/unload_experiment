package scenario_unloadBoxes.actions.helper;

import java.util.Map;

import jason.asSemantics.DefaultInternalAction;
import jason.asSemantics.TransitionSystem;
import jason.asSemantics.Unifier;
import jason.asSyntax.Literal;
import jason.asSyntax.Term;
import scenario_unloadBoxes.entities.model.Helper;
import scenario_unloadBoxes.entities.model.Truck;
import scenario_unloadBoxes.environments.UnloadEnv;

/**
 * This action updates the number of boxes took by a helper from a truck.
 */
public class takeBoxes extends DefaultInternalAction 
{
	private static final long serialVersionUID = 1L;
	private Map<Integer, Helper> helperMap = UnloadEnv.model.getWorld().getHelperMap();
	private Map<Integer, Truck> truckMap = UnloadEnv.model.getWorld().getTruckMap();
	
	/**
	 * Arguments (come from parameter args):
	 * @param args[0]: Helper's name
	 * @param args[1]: Trucker's name
	 */
    @Override
    public Object execute(TransitionSystem ts, Unifier un, Term[] args) throws Exception 
    {    
    	Helper h = helperMap.get(Integer.parseInt(args[0].toString().split("_")[1]));
    	Truck t = truckMap.get(Integer.parseInt(args[1].toString().split("_")[1]));
    	
    	ts.getAg().delBel(Literal.parseLiteral("carrying(_)"));
    	
    	if(!t.isUnloaded())
    	{
	    	int rest = t.getCargoAmount() - h.getCapacity();
	    	
	    	if(rest <= 0)	// The helper takes the rest of boxes from truck
	    	{
	    		ts.getAg().addBel(Literal.parseLiteral("carrying(" + t.getCargoAmount() + ")"));	  
	    		t.setCargoAmount(0);
	    	}
	    	else	// The helper takes all possible boxes according with its capacity
	    	{
	    		ts.getAg().addBel(Literal.parseLiteral("carrying(" + h.getCapacity() + ")"));
	    		t.setCargoAmount(rest);
	    	}
    	}
    	else
    	{
    		// Helper isn't carrying boxes
    		ts.getAg().addBel(Literal.parseLiteral("carrying(0)"));
    		
    		// Update the status of truck in the helper's mind
    		ts.getAg().delBel(Literal.parseLiteral("empty_truck(_)"));
    		ts.getAg().addBel(Literal.parseLiteral("empty_truck(true)"));
    	}
        return true;
    }
}