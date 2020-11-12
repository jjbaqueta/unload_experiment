package scenario_Marketplace.entities.services;

import scenario_Marketplace.entities.behaviors.Behavior;
import scenario_Marketplace.entities.behaviors.ConstantBehavior;
import scenario_Marketplace.entities.behaviors.ExponantialDecreaseBehavior;
import scenario_Marketplace.entities.behaviors.ExponentialIncreaseBehavior;
import scenario_Marketplace.entities.behaviors.LinearDecreaseBehavior;
import scenario_Marketplace.entities.behaviors.LinearIncreasingBehavior;
import scenario_Marketplace.entities.behaviors.SemiConstantBehavior;
import scenario_Marketplace.enums.BehaviorPattern;

/*
 * This class implements a factory of behaviors.
 */
public abstract class BehaviorFactory 
{
	public static Behavior factoryMethod(BehaviorPattern pattern, int maxNumberInteractions) throws Exception
	{
		switch (pattern)
		{
			case CONSTANT:
				return new ConstantBehavior(maxNumberInteractions);
			
			case SEMICONSTANT:
				return new SemiConstantBehavior(maxNumberInteractions);
			
			case LINEAR_INCREASING:
				return new LinearIncreasingBehavior(maxNumberInteractions);
			
			case LINEAR_DECREASING:
				return new LinearDecreaseBehavior(maxNumberInteractions);
			
			case EXPONENTIAL_INCREASING:
				return new ExponentialIncreaseBehavior(maxNumberInteractions);
			
			case EXPONENTIAL_DECREASING:
				return new ExponantialDecreaseBehavior(maxNumberInteractions);
			
			default:
				throw new Exception("Pattern is not valid:" + pattern);
		}
	}
}