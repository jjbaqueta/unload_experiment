package scenario_unloadBoxes.entities.model;

import java.util.Random;

import scenario_unloadBoxes.entities.enums.CargoType;

public class Truck extends SimpleElement
{
	private Double urgency;			// Urgency to unload
	private Integer cargoAmount;	// Amount of boxes inside of the truck
	private CargoType cargoType;	// Defines the type of cargo transported by the truck
	private Boolean unloaded;		// Informs when the truck is unloaded
	
	public Truck(Integer posX, Integer posY, Double urgency, Integer cargoAmount, CargoType cargoType) 
	{
		super(posX, posY);
		this.setName("truck_" + id);
		this.urgency = urgency;
		this.cargoAmount = cargoAmount;
		this.cargoType = cargoType;
		this.unloaded = cargoAmount <= 0;
	}
	
	public Truck(Integer posX, Integer posY) 
	{	
		super(posX, posY);
		this.setName("truck_" + id);
		this.setProperties();
	}
	
	@Override
	public void setProperties() 
	{		
		Random rand = new Random();		
		
		this.urgency = rand.nextDouble();
		this.cargoType = CargoType.COMMON;
		this.minTrustBound = -0.2;
		
		if(rand.nextBoolean())
			this.cargoType = CargoType.FRAGILE;
		
		if(rand.nextBoolean())
			this.urgency = this.urgency * -1;
		
		this.setCargoAmount(1 + rand.nextInt(15));
		this.setVisible(false);
	}

	public Integer getCargoAmount() 
	{
		return cargoAmount;
	}

	public void setCargoAmount(Integer cargoAmount) 
	{
		this.cargoAmount = cargoAmount;
		this.unloaded = cargoAmount <= 0;
	}

	public Boolean isUnloaded() 
	{
		return unloaded;
	}

	public CargoType getCargoType() 
	{
		return cargoType;
	}

	public void setCargoType(CargoType cargoType) 
	{
		this.cargoType = cargoType;
	}
	
	public Double getUrgency() 
	{
		return urgency;
	}

	public void setUrgency(Double urgency) 
	{
		this.urgency = urgency;
	}

	@Override
	public String toString() 
	{
		return "Truck [" + super.toString() 
		+ ", cargoAmount=" + this.cargoAmount + ", unloaded=" + this.unloaded 
		+ ", cargoType=" + this.cargoType + ", urgency=" + this.urgency + "]";
	}
}