// Internal action code for project unload_boxes

package scenario_unloadBoxes.actions.worker;

import java.util.Map;

import jason.asSemantics.DefaultInternalAction;
import jason.asSemantics.TransitionSystem;
import jason.asSemantics.Unifier;
import jason.asSyntax.NumberTerm;
import jason.asSyntax.NumberTermImpl;
import jason.asSyntax.Term;
import scenario_unloadBoxes.entities.model.Helper;
import scenario_unloadBoxes.entities.model.Worker;
import scenario_unloadBoxes.environments.UnloadEnv;

public class getHelperEstimatedLoad extends DefaultInternalAction 
{
	private static final long serialVersionUID = 1L;
	private Map<Integer, Worker> workerMap = UnloadEnv.model.getWorld().getWorkerMap();
	private Map<Integer, Helper> helperMap = UnloadEnv.model.getWorld().getHelperMap();

	/**
	 * Arguments (come from parameter args):
	 * @param args[0]: teamId
	 * @param args[1]: worker's name
	 * @param args[2]: helper's name
	 * @return args[3]: estimated load
	 */
    @Override
    public Object execute(TransitionSystem ts, Unifier un, Term[] args) throws Exception 
    {
    	NumberTerm teamId = (NumberTerm) args[0];
    	Worker worker = workerMap.get(Integer.parseInt(args[1].toString().split("_")[1]));
    	Helper helper = helperMap.get(Integer.parseInt(args[2].toString().split("_")[1]));
    	
    	return un.unifies(new NumberTermImpl(worker.getEstimatedLoad((int) teamId.solve(), helper)), args[3]);
    }
}
