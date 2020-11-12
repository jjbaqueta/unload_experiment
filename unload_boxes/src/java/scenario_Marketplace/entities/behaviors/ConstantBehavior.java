package scenario_Marketplace.entities.behaviors;

/* 
 * This class implements a behavior that returns a constant value.
 */
public class ConstantBehavior extends Behavior
{
	public ConstantBehavior(int maxNumberInteractions) 
	{
		super(maxNumberInteractions);
	}
	
	@Override
	public double getBehaviorValueFor(int x) 
	{
		return 0.0;
	}
}
