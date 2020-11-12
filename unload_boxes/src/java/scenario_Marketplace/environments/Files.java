package scenario_Marketplace.environments;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

import scenario_Marketplace.entities.model.Seller;
import scenario_Marketplace.enums.FilePaths;

public abstract class Files 
{
	/**
	 * This method writes in an output file 'sales.txt' the current sale made for the seller.
	 * @param seller: represents the seller who will be write his sale in file.
	 * @param time: the time that the sale happened.
	 */
	public static void writeSaleStatus(Seller seller, long time)
	{
		try 
		{
			BufferedWriter writer = new BufferedWriter(new FileWriter(FilePaths.SALES.getPath(), true));
			writer.append(seller.getName() + ";" + time +"\n");			     
		    writer.close();
		} 
		catch (IOException e) 
		{
			System.out.println(e.getMessage());
			e.printStackTrace();
		}
	}
}