package fuzzyCognitiveMaps.services;

import java.util.LinkedList;
import java.util.Set;
import java.util.TreeSet;

import fuzzyCognitiveMaps.model.FuzzyEdge;
import fuzzyCognitiveMaps.model.FuzzyGraph;
import fuzzyCognitiveMaps.model.FuzzyNode;
import fuzzyCognitiveMaps.model.FuzzyVisitor;

/**
 * This class implements the Breadth First Search (bfs). 
 * As output, the bfs shows all reached nodes from the inputs of given a Fuzzy Cognitive map.
 */
public class BfsVisitor implements FuzzyVisitor 
{	
	public void visit(FuzzyGraph fuzzyGraph) 
	{
		for (FuzzyNode input : fuzzyGraph.getInputs())
		{
			System.out.println("Path tracking for node id{"+ input.getId() +"}:");
			System.out.println(input);
			bfs(input, fuzzyGraph);
			System.out.println();
		}
	} 

	/**
	 * Implement the Breadth First Search (BFS).
	 * This method traverses the fuzzy map from a given input node.
	 * @param startNode: the initial node for the search
	 * @param fuzzyGraph: the cognitive fuzzy map 
	 */ 
   private void bfs(FuzzyNode startNode, FuzzyGraph fuzzyGraph) 
   {  
       Set<Integer> visited = new TreeSet<Integer>();
       LinkedList<FuzzyNode> queue = new LinkedList<FuzzyNode>(); 
       visited.add(startNode.getId());
       queue.add(startNode); 
 
       /*
        * Get all adjacent nodes of the currentNode.
        * If an adjacent node has not been visited yet,
        * mark it as visited and enqueue the node
        */
       while (queue.size() != 0) 
       {
    	   FuzzyNode currentNode = queue.poll(); 
           Set<FuzzyEdge> adjacentEdges = fuzzyGraph.getGraph().outgoingEdgesOf(currentNode.getId());
           
           for (FuzzyEdge edge : adjacentEdges)
           {
        	   FuzzyNode targetNode = fuzzyGraph.getNodesMap().getValue(
			   fuzzyGraph.getGraph().getEdgeTarget(edge));
   	
        	   if(!visited.contains(targetNode.getId()))
        	   {
        		   queue.add(targetNode);
        		   visited.add(targetNode.getId());
        		   System.out.println(targetNode);
        	   }
           }
       }
   }
}
