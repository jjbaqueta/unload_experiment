package scenario_Marketplace.entities.behaviors;

/* 
 * This class implements a behavior that returns a value that decreases according with x value increases (exponentially).
 */
public class ExponantialDecreaseBehavior extends Behavior
{
	public ExponantialDecreaseBehavior(int maxNumberInteractions)
	{
		super(maxNumberInteractions);
	}
	
	@Override
	public double getBehaviorValueFor(int x) 
	{
		double y = Math.log(x)/maxNumberInteractions;
		return checkInterval(y);
	}
}
