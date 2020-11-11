// Internal action code for project unload_boxes

package scenario_unloadBoxes.actions.worker;

import java.util.Map;

import jason.asSemantics.DefaultInternalAction;
import jason.asSemantics.TransitionSystem;
import jason.asSemantics.Unifier;
import jason.asSyntax.NumberTerm;
import jason.asSyntax.Term;
import scenario_unloadBoxes.entities.model.Truck;
import scenario_unloadBoxes.entities.model.Worker;
import scenario_unloadBoxes.environments.UnloadEnv;

public class getTeamsByTruckId extends DefaultInternalAction 
{
	private static final long serialVersionUID = 1L;
	private Map<Integer, Truck> truckMap = UnloadEnv.model.getWorld().getTruckMap();
	private Map<Integer, Worker> workerMap = UnloadEnv.model.getWorld().getWorkerMap();
	
	/**
	 * Arguments (come from parameter args):
	 * @param args[0]: work
	 * @param args[1]: truck
	 * @param args[2]: worker's name
	 * @return members of team
	 */
	@Override
    public Object execute(TransitionSystem ts, Unifier un, Term[] args) throws Exception 
    {
		// Getting the input parameters
		NumberTerm teamId = (NumberTerm) args[0]; 
		Worker worker = workerMap.get(Integer.parseInt(args[0].toString().split("_")[1]));
		Truck truck = truckMap.get(Integer.parseInt(args[1].toString().split("_")[1]));
		
		// Updating the helper status, the helper becomes a member of team.		
        return un.unifies(worker.getReadyMembersAsTermList((int) teamId.solve()), args[2]);
    }
}