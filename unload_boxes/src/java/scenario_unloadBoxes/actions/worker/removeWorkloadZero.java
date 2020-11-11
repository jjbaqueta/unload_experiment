// Internal action code for project unload_boxes

package scenario_unloadBoxes.actions.worker;

import java.util.Map;

import jason.asSemantics.DefaultInternalAction;
import jason.asSemantics.TransitionSystem;
import jason.asSemantics.Unifier;
import jason.asSyntax.ListTerm;
import jason.asSyntax.ListTermImpl;
import jason.asSyntax.Literal;
import jason.asSyntax.NumberTerm;
import jason.asSyntax.Term;
import scenario_unloadBoxes.entities.model.Helper;
import scenario_unloadBoxes.entities.model.Worker;
import scenario_unloadBoxes.environments.UnloadEnv;

public class removeWorkloadZero extends DefaultInternalAction 
{
	private static final long serialVersionUID = 1L;
	private Map<Integer, Worker> workerMap = UnloadEnv.model.getWorld().getWorkerMap();

	/**
	 * Arguments (come from parameter args):
	 * @param args[0]: teamId
	 * @param args[1]: worker's name
	 * @return args[2]: helpers with workload zero
	 */
	@Override
    public Object execute(TransitionSystem ts, Unifier un, Term[] args) throws Exception 
	{
		// Getting the team id
    	NumberTerm id = (NumberTerm) args[0];
    	int teamId = (int) id.solve();
    	
    	// Getting worker
    	Worker worker = workerMap.get(Integer.parseInt(args[1].toString().split("_")[1]));
    	
    	ListTerm helpers = new ListTermImpl();
    	
    	for(Helper helper : worker.getTeamMembers(teamId))
    	{
    		if(worker.getEstimatedLoad(teamId, helper) <= 0)
    		{
    			helpers.add(Literal.parseLiteral(helper.getName()));
    		}
    	}
    	return un.unifies(helpers, args[2]);
    }
}
