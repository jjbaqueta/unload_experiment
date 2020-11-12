package scenario_Marketplace.entities.behaviors;

/* 
 * This class implements a behavior that returns a value that decreases according with x value increases (linearly).
 */
public class LinearDecreaseBehavior extends Behavior
{
	public LinearDecreaseBehavior(int maxNumberInteractions) 
	{
		super(maxNumberInteractions);
	}

	@Override
	public double getBehaviorValueFor(int x) 
	{
		double y = (x)/maxNumberInteractions;
		return checkInterval(y); 
	}
}
