// Internal action code for project discharge_truck

package actions.worker;

import java.util.HashMap;
import java.util.Map;

import entities.enums.CargoType;
import entities.model.Helper;
import entities.model.Worker;
import environments.DischargeEnv;
import jason.asSemantics.DefaultInternalAction;
import jason.asSemantics.TransitionSystem;
import jason.asSemantics.Unifier;
import jason.asSyntax.Atom;
import jason.asSyntax.ListTerm;
import jason.asSyntax.Literal;
import jason.asSyntax.NumberTerm;
import jason.asSyntax.Structure;
import jason.asSyntax.Term;
import reputationAndImage.model.Skill;

/**
 * This action updates an existing team inserting or removing helpers from a team.
 */
public class updateTeam extends DefaultInternalAction {
	private static final long serialVersionUID = 1L;
	private Map<Integer, Worker> workerMap = DischargeEnv.model.getWorld().getWorkerMap();
	private Map<Integer, Helper> helperMap = DischargeEnv.model.getWorld().getHelperMap();
	
	
	/**
	 * Arguments (come from parameter args):
	 * @param args[0]: teamId
	 * @param args[1]: worker's name
	 * @param args[2]: list of offers
	 * @param args[3]: number of boxes to be unload from truck
	 * @param args[4]: time to perform the task
	 * @param args[5]: cargo type
	 * @return args[6]: a team of helpers
	 */
    @Override
    public Object execute(TransitionSystem ts, Unifier un, Term[] args) throws Exception {
    	Map<Helper, Integer> scoreMap = new HashMap<Helper, Integer>();
    	Map<Helper, Double> trustMap = new HashMap<Helper, Double>();

    	// Getting the team id
    	NumberTerm id = (NumberTerm) args[0];
    	int teamId = (int) id.solve();
    	
    	// Getting worker
    	Worker worker = workerMap.get(Integer.parseInt(args[1].toString().split("_")[1]));
    	
    	// Getting the map of offers
    	ListTerm offers = (ListTerm) args[2];
       	
    	// Getting the number of boxes and the time of task
    	NumberTerm nbBoxes = (NumberTerm) args[3];
    	NumberTerm time = (NumberTerm) args[4];
    	Atom cargoType = (Atom) args[5];
    	
    	for(Term term : offers) 
    	{
    		Structure offer = (Structure) term;
    		Helper helper = helperMap.get(Integer.parseInt(offer.getTerm(1).toString().split("_")[1]));
    		
    		// Getting estimation score
    		Structure estimation = (Structure) offer.getTerm(0);
    		NumberTerm score = (NumberTerm) estimation.getTerm(0);
    		scoreMap.put(helper, (int) score.solve());
    		
    		String belief = null;
        	
    		// Preparing search query
        	if(cargoType.toString().equals(CargoType.FRAGILE.name()))
        		belief = "trust(" + helper.getName() + ", \"" + Skill.FRAGILE_LOADER.name() + "\",_)";
        	else
        		belief = "trust(" + helper.getName() + ", \"" + Skill.COMMON_LOADER.name() + "\",_)";

        	// Getting trust value
        	Structure trust = (Structure) ts.getAg().findBel(Literal.parseLiteral(belief), un);
        	
        	if(trust != null)
        	{
        		NumberTerm value = (NumberTerm) trust.getTerm(2);
        		trustMap.put(helper, value.solve());
        	}
        	else
        	{
        		trustMap.put(helper, 0.5);
        		
        		if(cargoType.toString().equals(CargoType.FRAGILE.name()))
        			ts.getAg().addBel(Literal.parseLiteral(belief = "trust(" + helper.getName() + ", \"" + Skill.FRAGILE_LOADER.name() + "\",0.5)"));
            	else
            		ts.getAg().addBel(Literal.parseLiteral(belief = "trust(" + helper.getName() + ", \"" + Skill.COMMON_LOADER.name() + "\",0.5)"));
        	}
    	}
   
    	if(worker.containsTeam(teamId))
    	{
    		// Computing the size of team
    		if(worker.getTeam(teamId).getTeamSize() == 0)
    		{
    			worker.getTeam(teamId).setTeamSize(2);
    		}
    	
    		// Adding helper to team
	    	while(!worker.teamIsFull(teamId) && !scoreMap.keySet().isEmpty())
	    	{    	
	    		Helper helper = selectTheBest(scoreMap, trustMap, (int) nbBoxes.solve(), (long) time.solve());    		
	    		worker.addHelperToTeam(teamId, helper);
	    		scoreMap.remove(helper);
	    		trustMap.remove(helper);
	    	}
	    	
	    	return un.unifies(worker.getNotReadyMembersAsTermList(teamId), args[6]);
    	}
    	else
    		throw new IllegalAccessError("It wasn't possible to find the team. There is no a team with this id: " + teamId);
    }
    
    /**
     * This method selects the best offer and returns the helper that made this offer.
     * At this moment, the choice is done randomly.
     * @param offerMap: a map compound of all offer made by helpers.
     * @param nbBoxes: number of boxes to be discharge from truck.
     * @param time: time to perform the task.
     * @return the helper that made the best offer. 
     */
    private Helper selectTheBest(Map<Helper, Integer> scoreMap, Map<Helper, Double> trustMap, int nbBoxes, long time)
    {
    	int bestScore = Integer.MAX_VALUE;
    	double bestTrust = -2.0;
    	Helper bestHelper = null;
    	
    	for(Helper helper : scoreMap.keySet())
    	{
    		int score = scoreMap.get(helper);
    		double trust = trustMap.get(helper);
    		
    		if(bestTrust < trust)
    		{
    			bestTrust = trust;
    			bestScore = score;
    			bestHelper = helper;
    		}
    		else if(bestTrust == trust)
    		{
    			if(bestScore > score)
    			{
        			bestScore = score;
        			bestHelper = helper;
    			}
    		}
    	}
    	return bestHelper;
    }
}
