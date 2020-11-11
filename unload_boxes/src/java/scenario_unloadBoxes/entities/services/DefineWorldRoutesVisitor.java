package scenario_unloadBoxes.entities.services;

import scenario_unloadBoxes.entities.model.Artifact;
import scenario_unloadBoxes.entities.model.Truck;
import scenario_unloadBoxes.entities.model.World;
import scenario_unloadBoxes.entities.model.WorldVisitor;

public class DefineWorldRoutesVisitor implements WorldVisitor 
{
	public void visit(World world) 
	{
		for(Truck t : world.getTruckMap().values())
			world.addRouteTo(t);
		
		for(Artifact d : world.getDepotsMap().values())
			world.addRouteTo(d);
		
		for(Artifact g : world.getGarageMap().values())
			world.addRouteTo(g);
		
		for(Artifact r : world.getRechargeMap().values())
			world.addRouteTo(r);
	}
}