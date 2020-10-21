package actions.helper;

import java.util.Map;

import entities.model.Artifact;
import entities.model.Helper;
import entities.model.Truck;
import environments.DischargeEnv;
import jason.asSemantics.DefaultInternalAction;
import jason.asSemantics.TransitionSystem;
import jason.asSemantics.Unifier;
import jason.asSyntax.NumberTermImpl;
import jason.asSyntax.Structure;
import jason.asSyntax.Term;

/**
 * A Helper calls this action to generate an offer.
 * The Helper send his offer to a worker.
 */
public class proposeOffer extends DefaultInternalAction 
{
	private static final long serialVersionUID = 1L;
	private Map<Integer, Helper> helperMap = DischargeEnv.model.getWorld().getHelperMap();
	private Map<Integer, Truck> truckMap = DischargeEnv.model.getWorld().getTruckMap();
	private Map<Integer, Artifact> depotMap = DischargeEnv.model.getWorld().getDepotsMap();
	
	/**
	 * Arguments (come from parameter args):
	 * @param args[0]: Helper's name
	 * @param args[1]: Truck's name (origin)
	 * @param args[2]: Depot's name (target)
	 * @return args[3]: an offer
	 */
	@Override
    public Object execute(TransitionSystem ts, Unifier un, Term[] args) throws Exception
    {
		Helper helper = helperMap.get(Integer.parseInt(args[0].toString().split("_")[1]));
		Truck truck = truckMap.get(Integer.parseInt(args[1].toString().split("_")[1]));
		Artifact depot = depotMap.get(Integer.parseInt(args[2].toString().split("_")[1]));
		
		int meToTruck = (int) (1.8 * Math.abs(helper.getPos().x - truck.getPos().x) + Math.abs(helper.getPos().y - truck.getPos().y));
		int truckToDepot = (int) (1.8 * Math.abs(depot.getPos().x - truck.getPos().x) + Math.abs(depot.getPos().y - truck.getPos().y));		
		double trips = 2 * ((double) truck.getQtdThings() / helper.getCapacity());
		int stopTime = (int) ((trips / (helper.getBattery() / helper.getEnergyCost())) * (2000 / helper.getVelocity()));
		long estimation = (long) ((meToTruck + (truckToDepot * trips)) * helper.getVelocity()) + stopTime;
		
		Structure offer = new Structure("estimation");
		offer.addTerm(new NumberTermImpl(estimation));
		
		return un.unifies(offer, args[3]);
    }
}