package scenario_Marketplace.entities.behaviors;

/* 
 * This class implements a behavior that returns a value that increases according with x value decreases (exponentially).
 */
public class ExponentialIncreaseBehavior extends Behavior 
{
	public ExponentialIncreaseBehavior(int maxNumberInteractions)
	{
		super(maxNumberInteractions);
	}
	
	@Override
	public double getBehaviorValueFor(int x) 
	{
		double y = 0.25 - Math.log(x)/maxNumberInteractions;
		return checkInterval(y); 
	}
}
