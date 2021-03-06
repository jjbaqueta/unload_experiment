package scenario_Marketplace.entities.behaviors;

/**
 * This class implements the seller behavior.
 * A behavior is associated to a sale criterion.
 * Each behavior is associated to a control factor.
 * The control factor is defined based on the number of interactions between the seller and the buyers.
 */
public abstract class Behavior 
{
	protected String name;
	protected Integer maxNumberInteractions;
	
	public Behavior(String name, int maxNumberInteractions)
	{
		this.name = name;
		this.maxNumberInteractions = maxNumberInteractions;
	}
	
	/**
	 * This method implements a behavior function.
	 * @param x: number of the current interaction.
	 * @return the behavior value.
	 */
	public abstract double getBehaviorValueFor(int x);
	
	/**
	 * This method squashes the behavior value into the 0,1 interval.
	 * @param behaviorValue: value associated to agent behavior for a interaction x;
	 * @return a squashed value.
	 */
	protected double checkInterval(double behaviorValue)
	{
		if (behaviorValue <= 0)	
			return 2.0;
		
		else if (behaviorValue >= 1) 
			return 1.0;
		
		else
			return -behaviorValue + 2.0; 
	}
	
	public double getMaxNumberInteractions() 
	{
		return maxNumberInteractions;
	}

	public void setMaxNumberInteractions(int maxNumberInteractions) 
	{
		this.maxNumberInteractions = maxNumberInteractions;
	}
	
	public String getName()
	{
		return this.name;
	}

	@Override
	public String toString() 
	{
		return "[name=" + name + ", maxNumberInteractions=" + maxNumberInteractions + "]";
	}
}
