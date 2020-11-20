package scenario_Marketplace.entities.behaviors;

import scenario_Marketplace.enums.BehaviorPattern;

/* 
 * This class implements a behavior that returns a value that decreases according with x value increases (exponentially).
 */
public class ExponantialDecreaseBehavior extends Behavior
{
	public ExponantialDecreaseBehavior(int maxNumberInteractions)
	{
		super(BehaviorPattern.EXPONENTIAL_DECREASING.name(), maxNumberInteractions);
	}
	/**
	 * One third of the function's values are defined as 0. 
	 * In this case, only the last one third values are 0. 
	 * The other values decrease exponentially, and the first value is 1. 
	 * [FUNCTION]: -(e^(x + maxNumberInteractions/3) / (e^maxNumberInteractions)) + 1
	 */
	@Override
	public double getBehaviorValueFor(int x) 
	{
		double y = -(Math.exp((double) x + maxNumberInteractions/3) / Math.exp(maxNumberInteractions)) + 1;
		return checkInterval(y);
	}
}
