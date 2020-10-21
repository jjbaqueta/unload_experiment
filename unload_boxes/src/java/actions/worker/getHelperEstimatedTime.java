// Internal action code for project unload_boxes

package actions.worker;

import java.util.Map;

import entities.model.Helper;
import entities.model.Worker;
import environments.DischargeEnv;
import jason.asSemantics.DefaultInternalAction;
import jason.asSemantics.TransitionSystem;
import jason.asSemantics.Unifier;
import jason.asSyntax.NumberTerm;
import jason.asSyntax.NumberTermImpl;
import jason.asSyntax.Term;

public class getHelperEstimatedTime extends DefaultInternalAction 
{
	private static final long serialVersionUID = 1L;
	private Map<Integer, Worker> workerMap = DischargeEnv.model.getWorld().getWorkerMap();
	private Map<Integer, Helper> helperMap = DischargeEnv.model.getWorld().getHelperMap();

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
    	
    	return un.unifies(new NumberTermImpl(worker.getEstimatedTime((int) teamId.solve(), helper)), args[3]);
    }
}
