package reputationAndImage.model;

import jason.asSemantics.Agent;
import jason.bb.DefaultBeliefBase;

/**
 * This class starts the execution time for addition of believes.
 * Whenever a belief is added in the belief base of an agent,
 * this belief receives a time stamp based on the initial time stored in such a belief base  
 */
public class TimeBB extends DefaultBeliefBase 
{
	private static final long serialVersionUID = 1L;
	public static long start;
	
	@Override
	public void init(Agent ag, String[] args) 
	{
		start = System.currentTimeMillis();
		super.init(ag,args);
	}
}