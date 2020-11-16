package scenario_Marketplace.enums;

public enum FilePaths 
{
	LOAD_AGENTS("src/java/scenario_Marketplace/files/env.xml"),
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
