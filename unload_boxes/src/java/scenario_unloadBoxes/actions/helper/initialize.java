package scenario_unloadBoxes.actions.helper;

import java.util.Map;

import jason.asSemantics.DefaultInternalAction;
import jason.asSemantics.TransitionSystem;
import jason.asSemantics.Unifier;
import jason.asSyntax.Literal;
import jason.asSyntax.Term;
import scenario_unloadBoxes.entities.model.Artifact;
import scenario_unloadBoxes.entities.model.Helper;
import scenario_unloadBoxes.environments.UnloadEnv;

/**
 * This action initializes a helper.
 */
public class initialize extends DefaultInternalAction 
{
	private static final long serialVersionUID = 1L;
	private Map<Integer, Helper> helperMap = UnloadEnv.model.getWorld().getHelperMap();
	private Map<Integer, Artifact> garageMap = UnloadEnv.model.getWorld().getGarageMap();
	private Map<Integer, Artifact> rechargeMap = UnloadEnv.model.getWorld().getRechargeMap();
	
	/**
	 * Arguments (come from parameter args):
	 * @param args[0]: Helper's name
	 */
    @Override
    public Object execute(TransitionSystem ts, Unifier un, Term[] args) throws Exception 
    {
    	Helper h = helperMap.get(Integer.parseInt(args[0].toString().split("_")[1]));
    	
    	ts.getAg().addBel(Literal.parseLiteral("pos(" + h.getPos().x + "," + h.getPos().y +")"));
    	ts.getAg().addBel(Literal.parseLiteral("visible(" + h.isVisible() + ")"));
    	ts.getAg().addBel(Literal.parseLiteral("capacity(" + h.getCapacity() + ")"));
    	ts.getAg().addBel(Literal.parseLiteral("velocity(" + h.getVelocity() + ")"));
    	ts.getAg().addBel(Literal.parseLiteral("battery(" + h.getBattery() + ")"));    	
    	ts.getAg().addBel(Literal.parseLiteral("energy_cost(" + h.getEnergyCost() +")"));
    	ts.getAg().addBel(Literal.parseLiteral("failure_prob(" + h.getFailureProb() +")"));
    	ts.getAg().addBel(Literal.parseLiteral("safety(" + h.getSafety() +")"));
    	ts.getAg().addBel(Literal.parseLiteral("dexterity(" + h.getDexterity() +")"));
    	
    	for(Artifact g: garageMap.values())
    		ts.getAg().addBel(Literal.parseLiteral("garage(" + g.getName() +")"));
    	
    	for(Artifact r: rechargeMap.values())
    		ts.getAg().addBel(Literal.parseLiteral("recharge(" + r.getName() +")"));
    		
        return true;
    }
}