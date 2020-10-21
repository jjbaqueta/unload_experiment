// Internal action code for project discharge_truck

package actions.trucker;

import java.util.HashMap;
import java.util.Map;

import entities.enums.CargoType;
import entities.model.Worker;
import environments.DischargeEnv;
import jason.NoValueException;
import jason.asSemantics.DefaultInternalAction;
import jason.asSemantics.TransitionSystem;
import jason.asSemantics.Unifier;
import jason.asSyntax.Atom;
import jason.asSyntax.ListTerm;
import jason.asSyntax.Literal;
import jason.asSyntax.NumberTerm;
import jason.asSyntax.Structure;
import jason.asSyntax.Term;
import reputationAndImage.model.Mnemonic;
import reputationAndImage.model.Skill;

/**
 * A trucker uses this action to choose the best offer among the received offers.
 */
public class chooseBestOffer extends DefaultInternalAction 
{
	private static final long serialVersionUID = 1L;
	private Map<Integer, Worker> workerMap = DischargeEnv.model.getWorld().getWorkerMap();

	/**
	 * Arguments (come from parameter args):
	 * @param args[0]: list of offers
	 * @param args[1]: Task_type
	 * @return args[2]: name of worker who sent the best offer.
	 */	
	@Override
    public Object execute(TransitionSystem ts, Unifier un, Term[] args) throws Exception 
	{	
		Map<String, Double> trustMap = new HashMap<String, Double>();
		Map<String, Long> timeMap = new HashMap<String, Long>();
		Map<String, Integer> teamSizeMap = new HashMap<String, Integer>();
		
		ListTerm offers = (ListTerm) args[0];
		Atom taskType = (Atom) args[1];
		
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
		return un.unifies(getBestOffer(teamSizeMap, timeMap, trustMap), args[2]);
    }
	
	/**
	 * Compute the best offer;
	 * @param offers: list of received offers.
	 * @param truck: a truck 
	 */
	private Atom getBestOffer(Map<String, Integer> teamSizeMap, Map<String, Long> timeMap, Map<String, Double> trustMap) throws NoValueException
	{
		long bestTime = Long.MAX_VALUE;
		int bestSize = Integer.MAX_VALUE;
    	double bestTrust = -2.0;
    	String bestWorker = null;
    	
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
    	return new Atom(Literal.parseLiteral(bestWorker));
	}
}