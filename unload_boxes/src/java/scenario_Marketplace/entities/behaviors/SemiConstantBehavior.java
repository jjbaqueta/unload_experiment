package scenario_Marketplace.entities.behaviors;

import scenario_Marketplace.enums.BehaviorPattern;

/* 
 * This class implements a behavior that returns a value partially constant.
 */
public class SemiConstantBehavior extends Behavior
{
	public SemiConstantBehavior(int maxNumberInteractions) 
	{
		super(BehaviorPattern.SEMICONSTANT.name(), maxNumberInteractions);
	}

	/**
	 * This function returns 1 for all almost interactions, 
	 * only the ends interactions the value of function falls
	 * [FUNCTION]: 1 + ((-e^x)/ e^maxNumberInteractions)
	 */
	@Override
	public double getBehaviorValueFor(int x) 
	{		
		double y = 1 + (-(Math.exp(x))/ Math.exp(maxNumberInteractions));
		return checkInterval(y); 
	}
}