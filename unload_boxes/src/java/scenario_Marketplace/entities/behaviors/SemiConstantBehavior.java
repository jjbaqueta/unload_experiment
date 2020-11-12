package scenario_Marketplace.entities.behaviors;

/* 
 * This class implements a behavior that returns a value partially constant.
 */
public class SemiConstantBehavior extends Behavior
{
	public SemiConstantBehavior(int maxNumberInteractions) 
	{
		super(maxNumberInteractions);
	}

	@Override
	public double getBehaviorValueFor(int x) 
	{
		double y = (Math.log(x)/2)/maxNumberInteractions;
		return checkInterval(y); 
	}
}