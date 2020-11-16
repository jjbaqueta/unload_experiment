package scenario_Marketplace.enums;

public enum FilePaths 
{
	LOAD_AGENTS("src/java/scenario_Marketplace/files/env.xml"),
	FUZZY_VARS("src/java/scenario_Marketplace/files/fuzzy_vars.fcl"),
	SALES("src/java/scenario_Marketplace/files/sales.txt");
	
	private String path;
	
	private FilePaths(String path) 
	{
		this.path = path;
	}
	
	public String getPath()
	{
		return path;
	}
}
