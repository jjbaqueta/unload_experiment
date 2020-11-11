// Internal action code for project discharge_truck

package scenario_unloadBoxes.actions.worker;

import java.util.Map;

import jason.asSemantics.DefaultInternalAction;
import jason.asSemantics.TransitionSystem;
import jason.asSemantics.Unifier;
import jason.asSyntax.Atom;
import jason.asSyntax.ListTerm;
import jason.asSyntax.ListTermImpl;
import jason.asSyntax.Term;
import scenario_unloadBoxes.entities.model.Helper;
import scenario_unloadBoxes.entities.model.Worker;
import scenario_unloadBoxes.environments.UnloadEnv;

/**
 * This action returns a list of helpers nearby of worker.
 */
public class getHelpersNearby extends DefaultInternalAction 
{
	private static final long serialVersionUID = 1L;
	private Map<Integer, Worker> workerMap = UnloadEnv.model.getWorld().getWorkerMap();
	private Map<Integer, Helper> helperMap = UnloadEnv.model.getWorld().getHelperMap();
	
	/**
	 * Arguments (come from parameter args):
	 * @param args[0]: worker's name
	 * @return args[1]: list of helpers
	 */
    @Override
    public Object execute(TransitionSystem ts, Unifier un, Term[] args) throws Exception 
    {
    	Worker worker = workerMap.get(Integer.parseInt(args[0].toString().split("_")[1]));
    	
    	ListTerm helpers = new ListTermImpl();
    	int xRight = worker.getPos().x + worker.getSeekRange();
    	int xLeft = worker.getPos().x - worker.getSeekRange();
    	int yDown = worker.getPos().y + worker.getSeekRange();
    	int yUp = worker.getPos().y - worker.getSeekRange();
    	
    	for(Helper h : helperMap.values())
    	{
    		if((h.getPos().x <= xRight && h.getPos().y <= yDown) || (h.getPos().x >= xLeft && h.getPos().y >= yUp))
    		{
    			helpers.add(Atom.parseLiteral(h.getName()));
    		}
    	}
    	return un.unifies(helpers, args[1]);
    }
}