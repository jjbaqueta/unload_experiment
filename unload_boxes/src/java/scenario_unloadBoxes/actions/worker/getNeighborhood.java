// Internal action code for project unload_boxes

package scenario_unloadBoxes.actions.worker;

import java.util.Map;

import jason.asSemantics.DefaultInternalAction;
import jason.asSemantics.TransitionSystem;
import jason.asSemantics.Unifier;
import jason.asSyntax.Atom;
import jason.asSyntax.ListTerm;
import jason.asSyntax.ListTermImpl;
import jason.asSyntax.Term;
import scenario_unloadBoxes.entities.model.Worker;
import scenario_unloadBoxes.environments.UnloadEnv;

/**
 * This action gets all neighbors of a worker.
 */
public class getNeighborhood extends DefaultInternalAction 
{
	private static final long serialVersionUID = 1L;
	private Map<Integer, Worker> workerMap = UnloadEnv.model.getWorld().getWorkerMap();
	
	/**
	 * Arguments (come from parameter args):
	 * @param args[0]: worker's name
	 * @return list of neighbors
	 */
    @Override
    public Object execute(TransitionSystem ts, Unifier un, Term[] args) throws Exception 
    {
    	Worker worker = workerMap.get(Integer.parseInt(args[0].toString().split("_")[1]));
    	ListTerm workers = new ListTermImpl();
    	
    	int xRight = worker.getPos().x + worker.getProximity();
    	int xLeft = worker.getPos().x - worker.getProximity();
    	int yDown = worker.getPos().y + worker.getProximity();
    	int yUp = worker.getPos().y - worker.getProximity();
    	
    	for(Worker w : workerMap.values())
    	{
    		if(w != worker)
    		{
	    		if((w.getPos().x <= xRight && w.getPos().y <= yDown) || (w.getPos().x >= xLeft && w.getPos().y >= yUp))
	    		{
	    			workers.add(Atom.parseLiteral(w.getName()));
	    		}
    		}
    	}
    	return un.unifies(workers, args[1]);
    }
}