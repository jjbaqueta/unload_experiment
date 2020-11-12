package scenario_Marketplace.entities.behaviors;

/* 
 * This class implements a behavior that returns a value that increases according with x value decreases (linearly).
 */
public class LinearIncreasingBehavior extends Behavior
{
	public LinearIncreasingBehavior(int maxNumberInteractions) 
	{
		super(maxNumberInteractions);
	}
	
	@Override
	public double getBehaviorValueFor(int x) 
	{
		double y = 0.3 - (x/maxNumberInteractions);
		return checkInterval(y); 
	}

}
