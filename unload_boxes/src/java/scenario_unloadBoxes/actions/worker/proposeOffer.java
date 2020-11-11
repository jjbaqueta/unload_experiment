package scenario_unloadBoxes.actions.worker;

import java.util.Map;

import jason.asSemantics.DefaultInternalAction;
import jason.asSemantics.TransitionSystem;
import jason.asSemantics.Unifier;
import jason.asSyntax.NumberTerm;
import jason.asSyntax.NumberTermImpl;
import jason.asSyntax.Structure;
import jason.asSyntax.Term;
import scenario_unloadBoxes.entities.model.Helper;
import scenario_unloadBoxes.entities.model.Truck;
import scenario_unloadBoxes.entities.model.Worker;
import scenario_unloadBoxes.environments.UnloadEnv;

/**
 * A Worker calls this action to generate an offer.
 * The Worker send his offer to a Trucker.
 */
public class proposeOffer extends DefaultInternalAction 
{
	private static final long serialVersionUID = 1L;
	private Map<Integer, Worker> workerMap = UnloadEnv.model.getWorld().getWorkerMap();
	private Map<Integer, Truck> truckMap = UnloadEnv.model.getWorld().getTruckMap();

	/**
	 * Arguments (come from parameter args):
	 * @param args[0]: teamId
	 * @param args[1]: worker's name
	 * @param args[2]: truck's name
	 * @return args[3]: an offer
	 */
	@Override
    public Object execute(TransitionSystem ts, Unifier un, Term[] args) throws Exception
    {
		// Getting the input parameters
		NumberTerm teamId = (NumberTerm) args[0]; 
		Worker worker = workerMap.get(Integer.parseInt(args[1].toString().split("_")[1]));
		Truck trucker = truckMap.get(Integer.parseInt(args[2].toString().split("_")[1]));
	
		// Returning an offer		
		return un.unifies(generateOffer(worker, (int) teamId.solve(), trucker.getCargoAmount()), args[3]);
    }
	
	/**
	 * Compute the offer based on the attributes of team members
	 * @param teamMembers: member of a team
	 * @param truck: a trucker
	 * @return an offer.
	 * @throws Exception 
	 */
	private Structure generateOffer(Worker worker, int id, int numbBoxes) throws Exception
	{
		worker.computeWorkload(id, numbBoxes);
		
		int members = 0;
		long time = 0;
		
		for(Helper helper : worker.getTeamMembers(id))
		{
			if(worker.getEstimatedLoad(id, helper) > 0)
			{
				time += worker.getEstimatedTime(id, helper);
				members++;
			}
		}
		
		Structure offer = new Structure("team");
		offer.addTerm(new NumberTermImpl(members));
		offer.addTerm(new NumberTermImpl(time));
		return offer;
	}
}