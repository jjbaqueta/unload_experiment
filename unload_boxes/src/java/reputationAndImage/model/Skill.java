package reputationAndImage.model;

/**
 * This class implements the skill enum.
 * It is used to define the agents' specializations.
 */

public enum Skill 
{
	FRAGILE_SPECIALIZATION(1),
	COMMON_SPECIALIZATION(2),
	FRAGILE_LOADER(3),
	COMMON_LOADER(4);
	
	private Integer id;
	
	private Skill(Integer id) 
	{
		this.id = id;
	}
	
	public Integer getId()
	{
		return this.id;
	}
}