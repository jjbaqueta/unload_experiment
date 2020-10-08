package fuzzyCognitiveMaps.services;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DecimalFormat;

import fuzzyCognitiveMaps.model.FuzzyEdge;
import fuzzyCognitiveMaps.model.FuzzyGraph;
import fuzzyCognitiveMaps.model.FuzzyNode;
import fuzzyCognitiveMaps.model.FuzzyVisitor;

/**
 * This class generates a file.gv from a Fuzzy Map. 
 * The file.gv is used to generate a view of the Fuzzy Map.
 */
public class GenerateDotFileVisitor implements FuzzyVisitor 
{	
	private String fileName;
	
	public GenerateDotFileVisitor(String fileName) 
	{
		this.fileName = fileName;
	}
	
	/**
	 * Create a file.gv from the fuzzy map.
	 * @param fuzzyMap: the cognitive fuzzy map.
	 */
	public void visit(FuzzyGraph fuzzyMap) 
	{
		StringBuilder fileContent = new StringBuilder();
		DecimalFormat df = new DecimalFormat("0.00");
		
		fileContent.append("digraph FCM_net{\n");
		fileContent.append("\tnode [shape=record, style=rounded]\n");
		
		// Defining the input nodes
		for (FuzzyNode input : fuzzyMap.getInputs())
		{			
			fileContent.append("\t" + input.getId() 
			+ " [label=\"{" + input.getName() 
			+ " | " + df.format(input.getValue()) 
			+ "}\", style=bold, penwidth=2, peripheries=2];\n");					  
		}
		
		// Defining the non input nodes
		for (FuzzyNode node : fuzzyMap.getNonInputs())
		{
			fileContent.append("\t" + node.getId() 
			+ " [label=\"{" + node.getName() 
			+ " | " + df.format(node.getValue()) 
			+ "}\"];\n");
		}
		
		// Defining edges
		for (FuzzyEdge edge : fuzzyMap.getEdgesMap().getValues())
		{
			if(edge.getValue() < 0)
			{
				fileContent.append("\t" + edge.getSource() + " -> " + edge.getTarget() 
				+ " [label = \""+ df.format(edge.getValue()) + "\", style=dotted];\n");
			}
			else
			{
				fileContent.append("\t" + edge.getSource() + " -> " + edge.getTarget() 
				+ " [label = \""+ df.format(edge.getValue()) + "\"];\n");
			}
		}
		
		fileContent.append("}");
		
		writeDotFile(fileContent.toString());
	}
	
	private void writeDotFile(String fileContent)
	{
		try 
		{
			FileWriter fw = new FileWriter("dots/" + this.fileName + ".dot");
			BufferedWriter bw = new BufferedWriter(fw);
			
	        bw.write(fileContent);
	        bw.close();
	        fw.close();
		} 
		catch (IOException e) 
		{
			System.out.println(e.getMessage());
			e.printStackTrace();
		}
	}
}
