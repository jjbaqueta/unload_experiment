package scenario_Marketplace.entities.behaviors;

import scenario_Marketplace.enums.BehaviorPattern;

/* 
 * This class implements a behavior that returns a value that increases according with x value decreases (exponentially).
 */
public class ExponentialIncreaseBehavior extends Behavior 
{
	public ExponentialIncreaseBehavior(int maxNumberInteractions)
	{
		super(BehaviorPattern.EXPONENTIAL_INCREASING.name(), maxNumberInteractions);
	}
	
	/**
	 * One third of the function's values are defined as 1. 
	 * In this case, only the last one third values are 1. 
	 * The other values increases exponentially, and the first value is 0. 
	 * [FUNCTION]: (e^(x + maxNumberInteractions/3) / (e^maxNumberInteractions)).
	 */
	@Override
	public double getBehaviorValueFor(int x) 
	{
		double y = Math.exp(x + (double) maxNumberInteractions/3) / Math.exp(maxNumberInteractions);
		return checkInterval(y); 
	}
}
