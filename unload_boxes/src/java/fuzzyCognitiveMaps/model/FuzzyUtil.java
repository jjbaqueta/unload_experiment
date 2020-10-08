package fuzzyCognitiveMaps.model;

import java.util.HashMap;
import java.util.Map;

/**
 * This class instances some Fuzzy Cognitive maps for testing
 */
public class FuzzyUtil 
{	
	public static FuzzyGraph simpleGraph()
	{
		FuzzyGraph fuzzyMap = new FuzzyGraph();
		
		fuzzyMap.insertNode(new FuzzyNode(0, "n1", 15.0));
		fuzzyMap.insertNode(new FuzzyNode(1, "n2", 3.0));
		fuzzyMap.insertNode(new FuzzyNode(2, "n3", 12.0));
		
		fuzzyMap.insertEdge(new FuzzyEdge(0, "e1", 0.5, 0, 1));
		fuzzyMap.insertEdge(new FuzzyEdge(1, "e2", -0.1, 1, 2));
		fuzzyMap.insertEdge(new FuzzyEdge(2, "e3", 0.4, 0, 2));
		
		fuzzyMap.setAsInput(0);
		fuzzyMap.setAsOutput(2);
		
		return fuzzyMap;
	}
	
	public static Map<Integer, Double> getInputsSimpleGraph()
	{	
		Map<Integer, Double> inputs = new HashMap<Integer, Double>();
		inputs.put(0, 0.3);
		return inputs;
	}
	
	public static FuzzyGraph fuzzyMap1()
	{
		FuzzyGraph fuzzyMap = new FuzzyGraph();
		
		// NODES
		fuzzyMap.insertNode(new FuzzyNode(0, "de", 0.0));
		fuzzyMap.insertNode(new FuzzyNode(1, "c", 0.0));
		fuzzyMap.insertNode(new FuzzyNode(2, "r", 0.0));
		fuzzyMap.insertNode(new FuzzyNode(3, "or", 0.0));
		
		fuzzyMap.insertNode(new FuzzyNode(4, "de", 0.0));
		fuzzyMap.insertNode(new FuzzyNode(5, "c", 0.0));
		fuzzyMap.insertNode(new FuzzyNode(6, "r", 0.0));
		fuzzyMap.insertNode(new FuzzyNode(7, "or", 0.0));
		
		fuzzyMap.insertNode(new FuzzyNode(8, "de", 0.0));
		fuzzyMap.insertNode(new FuzzyNode(9, "c", 0.0));
		fuzzyMap.insertNode(new FuzzyNode(10, "r", 0.0));
		fuzzyMap.insertNode(new FuzzyNode(11, "or", 0.0));
		
		fuzzyMap.insertNode(new FuzzyNode(12, "de", 0.0));
		fuzzyMap.insertNode(new FuzzyNode(13, "c", 0.0));
		fuzzyMap.insertNode(new FuzzyNode(14, "r", 0.0));
		fuzzyMap.insertNode(new FuzzyNode(15, "or", 0.0));
		
		fuzzyMap.insertNode(new FuzzyNode(16, "de", 0.0));
		fuzzyMap.insertNode(new FuzzyNode(17, "c", 0.0));
		fuzzyMap.insertNode(new FuzzyNode(18, "r", 0.0));
		fuzzyMap.insertNode(new FuzzyNode(19, "or", 0.0));
		
		fuzzyMap.insertNode(new FuzzyNode(20, "Ability", 0.0));
		fuzzyMap.insertNode(new FuzzyNode(21, "Availability", 0.0));
		fuzzyMap.insertNode(new FuzzyNode(22, "Unharmfulness", 0.0));
		fuzzyMap.insertNode(new FuzzyNode(23, "Opportunity", 0.0));
		fuzzyMap.insertNode(new FuzzyNode(24, "Danger", 0.0));
		fuzzyMap.insertNode(new FuzzyNode(25, "ExternalFactors", 0.0));
		fuzzyMap.insertNode(new FuzzyNode(26, "InternalFactors", 0.0));
		
		fuzzyMap.insertNode(new FuzzyNode(27, "Trustfulness", 0.0));
		
		// EDGES
		fuzzyMap.insertEdge(new FuzzyEdge(0, "e1", 0.5, 0, 20));
		fuzzyMap.insertEdge(new FuzzyEdge(1, "e2", 0.5, 1, 20));
		fuzzyMap.insertEdge(new FuzzyEdge(2, "e3", 0.5, 2, 20));
		fuzzyMap.insertEdge(new FuzzyEdge(3, "e4", 0.5, 3, 20));
		
		fuzzyMap.insertEdge(new FuzzyEdge(4, "e5", 0.5, 4, 21));
		fuzzyMap.insertEdge(new FuzzyEdge(5, "e6", 0.5, 5, 21));
		fuzzyMap.insertEdge(new FuzzyEdge(6, "e7", 0.5, 6, 21));
		fuzzyMap.insertEdge(new FuzzyEdge(7, "e8", 0.5, 7, 21));
		
		fuzzyMap.insertEdge(new FuzzyEdge(8, "e9", 0.5, 8, 22));
		fuzzyMap.insertEdge(new FuzzyEdge(9, "e10", 0.5, 9, 22));
		fuzzyMap.insertEdge(new FuzzyEdge(10, "e11", 0.5, 10, 22));
		fuzzyMap.insertEdge(new FuzzyEdge(11, "e12", 0.5, 11, 22));
		
		fuzzyMap.insertEdge(new FuzzyEdge(12, "e13", 0.5, 12, 23));
		fuzzyMap.insertEdge(new FuzzyEdge(13, "e14", 0.5, 13, 23));
		fuzzyMap.insertEdge(new FuzzyEdge(14, "e15", 0.5, 14, 23));
		fuzzyMap.insertEdge(new FuzzyEdge(15, "e16", 0.5, 15, 23));
		
		fuzzyMap.insertEdge(new FuzzyEdge(16, "e17", 0.5, 16, 24));
		fuzzyMap.insertEdge(new FuzzyEdge(17, "e18", 0.5, 17, 24));
		fuzzyMap.insertEdge(new FuzzyEdge(18, "e19", 0.5, 18, 24));
		fuzzyMap.insertEdge(new FuzzyEdge(19, "e20", 0.5, 19, 24));
		
		fuzzyMap.insertEdge(new FuzzyEdge(20, "e21", 1.0, 20, 26));
		fuzzyMap.insertEdge(new FuzzyEdge(21, "e22", 0.1, 21, 26));
		fuzzyMap.insertEdge(new FuzzyEdge(22, "e23", 0.4, 22, 26));
		fuzzyMap.insertEdge(new FuzzyEdge(23, "e24", 0.1, 23, 25));
		fuzzyMap.insertEdge(new FuzzyEdge(24, "e25", 0.2, 24, 25));
		fuzzyMap.insertEdge(new FuzzyEdge(25, "e26", 0.5, 25, 27));
		fuzzyMap.insertEdge(new FuzzyEdge(26, "e27", 0.5, 26, 27));
		
		fuzzyMap.setAsInput(0);
		fuzzyMap.setAsInput(1);
		fuzzyMap.setAsInput(2);
		fuzzyMap.setAsInput(3);
		
		fuzzyMap.setAsInput(4);
		fuzzyMap.setAsInput(5);
		fuzzyMap.setAsInput(6);
		fuzzyMap.setAsInput(7);
		
		fuzzyMap.setAsInput(8);
		fuzzyMap.setAsInput(9);
		fuzzyMap.setAsInput(10);
		fuzzyMap.setAsInput(11);
		
		fuzzyMap.setAsInput(12);
		fuzzyMap.setAsInput(13);
		fuzzyMap.setAsInput(14);
		fuzzyMap.setAsInput(15);
		
		fuzzyMap.setAsInput(16);
		fuzzyMap.setAsInput(17);
		fuzzyMap.setAsInput(18);
		fuzzyMap.setAsInput(19);
		
		fuzzyMap.setAsOutput(27);
		
		return fuzzyMap;
	}
	
	public static Map<Integer, Double> getInputsFuzzyMap1()
	{
		Map<Integer, Double> inputs = new HashMap<Integer, Double>();
		
		inputs.put(0, 0.3);
		inputs.put(1, 0.7);
		inputs.put(2, 0.0);
		inputs.put(3, 0.0);
		
		inputs.put(4, -0.3);
		inputs.put(5, 0.0);
		inputs.put(6, 0.0);
		inputs.put(7, 0.0);
		
		inputs.put(8, 0.0);
		inputs.put(9, -0.2);
		inputs.put(10, 0.0);
		inputs.put(11, 0.0);
		
		inputs.put(12, 0.0);
		inputs.put(13, 0.0);
		inputs.put(14, 0.2);
		inputs.put(15, 0.0);
		
		inputs.put(16, 0.0);
		inputs.put(17, 0.0);
		inputs.put(18, -0.2);
		inputs.put(19, 0.0);
		
		return inputs;
	}
	
	public static FuzzyGraph trustDecision()
	{
		FuzzyGraph fuzzyMap = new FuzzyGraph();
		
		// NODES
		fuzzyMap.insertNode(new FuzzyNode(0, "v1", 0.0));
		fuzzyMap.insertNode(new FuzzyNode(1, "v2", 0.0));
		fuzzyMap.insertNode(new FuzzyNode(2, "v3", 0.0));
		
		fuzzyMap.insertNode(new FuzzyNode(3, "v4", 0.0));
		fuzzyMap.insertNode(new FuzzyNode(4, "v5", 0.0));
		fuzzyMap.insertNode(new FuzzyNode(5, "v6", 0.0));
		
		fuzzyMap.insertNode(new FuzzyNode(6, "v7", 0.0));
		fuzzyMap.insertNode(new FuzzyNode(7, "v8", 0.0));
		fuzzyMap.insertNode(new FuzzyNode(8, "v9", 0.0));
		
		fuzzyMap.insertNode(new FuzzyNode(9, "img", 0.0));
		fuzzyMap.insertNode(new FuzzyNode(10, "rep", 0.0));
		fuzzyMap.insertNode(new FuzzyNode(11, "ref", 0.0));
		
		fuzzyMap.insertNode(new FuzzyNode(12, "i/r", 0.0));
		fuzzyMap.insertNode(new FuzzyNode(13, "ability", 0.0));
		fuzzyMap.insertNode(new FuzzyNode(14, "availability", 0.0));
		fuzzyMap.insertNode(new FuzzyNode(15, "internalFactors", 0.0));
		
		fuzzyMap.insertNode(new FuzzyNode(16, "risk", 0.0));
		fuzzyMap.insertNode(new FuzzyNode(17, "opportunity", 0.0));
		fuzzyMap.insertNode(new FuzzyNode(18, "externalFactors", 0.0));
		
		fuzzyMap.insertNode(new FuzzyNode(19, "Trustfulness", 0.0));
		
		// EDGES
		fuzzyMap.insertEdge(new FuzzyEdge(0, "e1", 1.0, 0, 9));
		fuzzyMap.insertEdge(new FuzzyEdge(1, "e2", 1.0, 1, 9));
		fuzzyMap.insertEdge(new FuzzyEdge(2, "e3", 1.0, 2, 9));
		
		fuzzyMap.insertEdge(new FuzzyEdge(3, "e4", 1.0, 3, 10));
		fuzzyMap.insertEdge(new FuzzyEdge(4, "e5", 1.0, 4, 10));
		fuzzyMap.insertEdge(new FuzzyEdge(5, "e6", 1.0, 5, 10));
		
		fuzzyMap.insertEdge(new FuzzyEdge(6, "e7", 1.0, 6, 11));
		fuzzyMap.insertEdge(new FuzzyEdge(7, "e8", 1.0, 7, 11));
		fuzzyMap.insertEdge(new FuzzyEdge(8, "e9", 1.0, 8, 11));
		
		fuzzyMap.insertEdge(new FuzzyEdge(9, "e10", 1.0, 9, 12));
		fuzzyMap.insertEdge(new FuzzyEdge(10, "e11", 0.5, 10, 12));
		
		fuzzyMap.insertEdge(new FuzzyEdge(11, "e12", 1.0, 12, 13));
		fuzzyMap.insertEdge(new FuzzyEdge(12, "e13", 0.2, 14, 13));
		fuzzyMap.insertEdge(new FuzzyEdge(13, "e14", 1.0, 13, 15));
		
		fuzzyMap.insertEdge(new FuzzyEdge(14, "e15", 0.5, 16, 18));
		fuzzyMap.insertEdge(new FuzzyEdge(15, "e16", 0.2, 17, 18));
		
		fuzzyMap.insertEdge(new FuzzyEdge(16, "e17", 0.5, 15, 19));
		fuzzyMap.insertEdge(new FuzzyEdge(17, "e18", 0.5, 18, 19));
		
		fuzzyMap.setAsInput(0);
		fuzzyMap.setAsInput(1);
		fuzzyMap.setAsInput(2);
		fuzzyMap.setAsInput(3);
		fuzzyMap.setAsInput(4);
		fuzzyMap.setAsInput(5);
		fuzzyMap.setAsInput(6);
		fuzzyMap.setAsInput(7);
		fuzzyMap.setAsInput(8);
		
		fuzzyMap.setAsOutput(19);
		
		return fuzzyMap;
	}
}
