package scenario_Marketplace.entities.behaviors;

import scenario_Marketplace.enums.BehaviorPattern;

/* 
 * This class implements a behavior that returns a value that decreases according with x value increases (linearly).
 */
public class LinearDecreaseBehavior extends Behavior
{
	public LinearDecreaseBehavior(int maxNumberInteractions) 
	{
		super(BehaviorPattern.LINEAR_DECREASING.name(), maxNumberInteractions);
	}

	/**
	 * This function returns 1 for the first interaction and 0 for the last interaction.
	 * For the other interactions the value of function is reduced linearly, 
	 * [FUNCTION]: 1 + (-x/maxNumberInteractions).
	 */
	@Override
	public double getBehaviorValueFor(int x) 
	{
		double y = 1 + (-x/maxNumberInteractions);
		return checkInterval(y); 
	}
}
