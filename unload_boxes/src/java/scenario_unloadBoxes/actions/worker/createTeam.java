// Internal action code for project discharge_truck

package scenario_unloadBoxes.actions.worker;

import java.util.Map;

import jason.asSemantics.DefaultInternalAction;
import jason.asSemantics.TransitionSystem;
import jason.asSemantics.Unifier;
import jason.asSyntax.NumberTerm;
import jason.asSyntax.Term;
import scenario_unloadBoxes.entities.model.HelperTeam;
import scenario_unloadBoxes.entities.model.Worker;
import scenario_unloadBoxes.environments.UnloadEnv;

/**
 * This actions creates a new team (the team is created empty).
 */
public class createTeam extends DefaultInternalAction 
{
	private static final long serialVersionUID = 1L;
	private Map<Integer, Worker> workerMap = UnloadEnv.model.getWorld().getWorkerMap();
	
	/**
	 * Arguments (come from parameter args):
	 * @param args[0]: teamId
	 * @param args[1]: worker's name
	 */
    @Override
    public Object execute(TransitionSystem ts, Unifier un, Term[] args) throws Exception 
    {
    	NumberTerm id = (NumberTerm) args[0];
    	int teamId = (int) id.solve();
    	Worker worker = workerMap.get(Integer.parseInt(args[1].toString().split("_")[1]));
    	
    	if(!worker.containsTeam(teamId))
    		worker.addTeam(new HelperTeam(teamId, 0));
    	
    	return true;
    }
}