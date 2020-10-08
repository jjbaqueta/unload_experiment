package fuzzyCognitiveMaps.services;

import java.util.Map;
import java.util.Set;

import fuzzyCognitiveMaps.model.FuzzyEdge;
import fuzzyCognitiveMaps.model.FuzzyGraph;
import fuzzyCognitiveMaps.model.FuzzyNode;
import fuzzyCognitiveMaps.model.FuzzyVisitor;

/**
 * This class propagates the values of input nodes for whole Fuzzy map. 
 */
public class PropagateInputsVisitor implements FuzzyVisitor 
{
	private Map<Integer, Double> inputs;	// Node's id and Node's value

	/**
	 * @param inputs: a map with the new values for each input of the Fuzzy map.
	 */
	public PropagateInputsVisitor(Map<Integer, Double> inputs) 
	{
		this.inputs = inputs;
	}
	
	/**
	 * Apply the new inputs' values
	 * @param fuzzyMap: the cognitive fuzzy map.
	 */
	public void visit(FuzzyGraph fuzzyMap) 
	{	 
		// Changing the input values
		for(FuzzyNode input : fuzzyMap.getInputs())
		{
			input.setValue(this.inputs.get(input.getId()));
		}
		
		// Propagating the changes into the net
		boolean isStable;
		
		do 
		{
			isStable = true;
			
			for(Integer nodeId : fuzzyMap.getGraph().vertexSet())
			{
				Set<FuzzyEdge> adjEdges = fuzzyMap.getGraph().incomingEdgesOf(nodeId);
				
				if(adjEdges.size() > 0)
				{
					FuzzyNode ni = fuzzyMap.getNodesMap().getValue(nodeId);
					double oldValue = ni.getValue();
					double sum = 0; 
					
					for(FuzzyEdge eij : adjEdges)
					{
						FuzzyNode nj = fuzzyMap.getNodesMap().getValue(eij.getSource());
						sum += eij.getValue() * nj.getValue();
					}
					
					// Threshold functions
					double newValue = thresholdTanH(sum);
//					double newValue = thresholdSin(sum);
//					double newValue = thresholdCos(sum);
//					double newValue = thresholdSigmoid(sum);
					
					if(oldValue != newValue)
					{
						ni.setValue(newValue);
						isStable = false;
					}	
				}
			}
		}while(!isStable);
	}
	
	/**
	 * Indicated when node's values are based on interval [-1, 1]
	 * @return a value in the interval [-1,1] as result of hyperbolic tangent operation
	 */
	private double thresholdTanH(double x) 
	{
		return Math.tanh(x);
	}
	
	@SuppressWarnings("unused")
	private double thresholdSin(double x) 
	{
		return Math.sin(x);
	}
	
	@SuppressWarnings("unused")
	private double thresholdCos(double x) 
	{
		return Math.cos(x);
	}	
	
	/**
	 * This functions must be used when the node's values are defined based on interval [0, 1]
	 * @return a value in the interval [0,1] as result of hyperbolic sine operation
	 */
	@SuppressWarnings("unused")
	private double thresholdSigmoid(double x) 
	{
		return 1 / 1 + Math.exp(-0.5 * x);
	}
}
