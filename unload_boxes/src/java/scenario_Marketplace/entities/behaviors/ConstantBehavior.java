package scenario_Marketplace.entities.behaviors;

import scenario_Marketplace.enums.BehaviorPattern;

/* 
 * This class implements a behavior that returns a constant value.
 */
public class ConstantBehavior extends Behavior
{
	public ConstantBehavior(int maxNumberInteractions) 
	{
		super(BehaviorPattern.CONSTANT.name(), maxNumberInteractions);
	}
	
	@Override
	public double getBehaviorValueFor(int x) 
	{
		return 1.0;
	}
}
