// Internal action code for project discharge_truck

package actions.trucker;

import java.util.HashMap;
import java.util.Map;

import entities.enums.CargoType;
import entities.model.Truck;
import entities.model.Worker;
import environments.DischargeEnv;
import jason.NoValueException;
import jason.asSemantics.DefaultInternalAction;
import jason.asSemantics.TransitionSystem;
import jason.asSemantics.Unifier;
import jason.asSyntax.Atom;
import jason.asSyntax.ListTerm;
import jason.asSyntax.ListTermImpl;
import jason.asSyntax.Literal;
import jason.asSyntax.NumberTerm;
import jason.asSyntax.Structure;
import jason.asSyntax.Term;
import reputationAndImage.model.Mnemonic;
import reputationAndImage.model.Skill;

/**
 * A trucker uses this action to choose the best offer among the received offers.
 */
public class evaluateOffers extends DefaultInternalAction 
{
	private static final long serialVersionUID = 1L;
	private Map<Integer, Worker> workerMap = DischargeEnv.model.getWorld().getWorkerMap();
	private Map<Integer, Truck> truckMap = DischargeEnv.model.getWorld().getTruckMap();

	/**
	 * Arguments (come from parameter args):
	 * @param args[0]: trucker's name
	 * @param args[1]: list of offers
	 * @param args[2]: Task_type
	 * @return args[3]: list of winner and losers {workers(Winners, Losers)}.
	 */	
	@Override
    public Object execute(TransitionSystem ts, Unifier un, Term[] args) throws Exception 
	{	
		Map<String, Double> trustMap = new HashMap<String, Double>();
		Map<String, Long> timeMap = new HashMap<String, Long>();
		Map<String, Integer> teamSizeMap = new HashMap<String, Integer>();
		
		Truck t = truckMap.get(Integer.parseInt(args[0].toString().split("_")[1]));
		ListTerm offers = (ListTerm) args[1];
		Atom taskType = (Atom) args[2];
		
		for(Term offer : offers)
		{	
			Structure s = (Structure) offer;
			Structure attributes  = (Structure) s.getTerm(0);			
			Worker worker = workerMap.get(Integer.parseInt(s.getTerm(1).toString().split("_")[1]));
			NumberTerm teamSize = (NumberTerm) attributes.getTerm(0);
			NumberTerm time = (NumberTerm) attributes.getTerm(1);
			
			timeMap.put(worker.getName(), (long) time.solve());
			teamSizeMap.put(worker.getName(), (int) teamSize.solve());
			
			String belief = null;
			
			// Preparing search query
	    	if(taskType.toString().equals(CargoType.FRAGILE.name().toLowerCase()))
	    		belief = Mnemonic.TRUST.getMnemonic() + "(" + worker.getName() + ", \"" + Skill.FRAGILE_SPECIALIZATION.name() + "\",_)";
	    	else
	    		belief = Mnemonic.TRUST.getMnemonic() + "(" + worker.getName() + ", \"" + Skill.COMMON_SPECIALIZATION.name() + "\",_)";
			
	    	Structure trust = (Structure) ts.getAg().findBel(Literal.parseLiteral(belief), un);
        	NumberTerm value = (NumberTerm) trust.getTerm(2);
        	trustMap.put(worker.getName(), value.solve());
		}	
		return un.unifies(classify(trustMap, timeMap, teamSizeMap, t.getMinTrustBound()), args[3]);
    }
	
	/**
	 * This method classifies the agents into good and bad agents according with their trustworthiness
	 * @param trustMap: trust value of each worker.
	 * @param timeMap: time estimated for each worker. 
	 * @param teamSizeMap: size of teams stored by each worker.
	 * @param minTrustBound: minimum trust value.
	 * @return a structure that defines who are the winners and losers workers. 
	 */
	private Structure classify(Map<String, Double> trustMap, Map<String, Long> timeMap, 
			Map<String, Integer> teamSizeMap, double minTrustBound) throws NoValueException
	{
		Structure workers = new Structure(Literal.parseLiteral("workers"));
		ListTerm winners = new ListTermImpl();
		ListTerm losers = new ListTermImpl();
		
		Atom bestWorker = getBestOffer(trustMap, timeMap, teamSizeMap, minTrustBound); 
		
		// Finding the best workers 
		while(bestWorker != null)
		{
			winners.add(bestWorker);
			trustMap.remove(bestWorker.toString());
			teamSizeMap.remove(bestWorker.toString());
			timeMap.remove(bestWorker.toString());
			
			bestWorker = getBestOffer(trustMap, timeMap, teamSizeMap, minTrustBound);
		}
		
		// The rest of workers are added in the loser list
		for(String worker : teamSizeMap.keySet())
			losers.add(new Atom(Literal.parseLiteral(worker)));
		
		workers.addTerm(winners);
		workers.addTerm(losers);		
		return workers;
	}
	
	/**
	 * Find the best worker according with the trust value, team size and task time;
	 * If there is no workers with the trust value above the minTrustBound, this method returns null..
	 * @param trustMap: trust value of each worker.
	 * @param timeMap: time estimated for each worker. 
	 * @param teamSizeMap: size of teams stored by each worker.
	 * @param minTrustBound: minimum trust value.
	 * @return the best worker's name. 
	 */
	private Atom getBestOffer(Map<String, Double> trustMap, Map<String, Long> timeMap, 
			Map<String, Integer> teamSizeMap, double minTrustBound) throws NoValueException
	{	
		long bestTime = Long.MAX_VALUE;
		int bestSize = Integer.MAX_VALUE;
		double bestTrust = -2.0;
		String bestWorker = null;

		// Finding the best worker
    	for(String worker : trustMap.keySet())
    	{
    		long time = timeMap.get(worker);
    		double trust = trustMap.get(worker);
    		int size = teamSizeMap.get(worker);
    		
    		if(bestTrust < trust)
    		{
    			bestTrust = trust;
    			bestTime = time;
    			bestSize = size;
    			bestWorker = worker;
    		}
    		else if(bestTrust == trust)
    		{
    			if(bestTime > time)
    			{
        			bestTime = time;
        			bestSize = size;
        			bestWorker = worker;
    			}
    			else if(bestTime == time)
    			{
    				if(bestSize > size)
        			{
    					bestSize = size;
    					bestWorker = worker;
        			}
    			}
    		}
    	}
    	
    	// There is no a good worker
    	if(bestTrust < minTrustBound) 
    	{
    		return null;
    	}    	
    	return new Atom(Literal.parseLiteral(bestWorker));
	}
}