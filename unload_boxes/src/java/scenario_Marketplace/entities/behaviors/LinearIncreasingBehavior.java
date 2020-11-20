package scenario_Marketplace.entities.behaviors;

import scenario_Marketplace.enums.BehaviorPattern;

/* 
 * This class implements a behavior that returns a value that increases according with x value decreases (linearly).
 */
public class LinearIncreasingBehavior extends Behavior
{
	public LinearIncreasingBehavior(int maxNumberInteractions) 
	{
		super(BehaviorPattern.LINEAR_INCREASING.name(), maxNumberInteractions);
	}
	
	/**
	 * This function returns 0 for the first interaction and 1 for the last interaction.
	 * For the other interactions the value of function is reduced linearly, 
	 * [FUNCTION]: (x/maxNumberInteractions).
	 */
	@Override
	public double getBehaviorValueFor(int x) 
	{
		double y = x / (double) maxNumberInteractions;
		return checkInterval(y); 
	}

}
