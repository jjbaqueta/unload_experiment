package trustModel.fcm.service;

import java.util.LinkedList;
import java.util.Set;
import java.util.TreeSet;

import trustModel.fcm.model.FmcEdge;
import trustModel.fcm.model.FmcGraph;
import trustModel.fcm.model.FmcNode;
import trustModel.fcm.model.FmcVisitor;

/**
 * This class implements the Breadth First Search (bfs). 
 * As output, the bfs shows all reached nodes from the inputs of given a Fuzzy Cognitive map.
 */
public class BfsVisitor implements FmcVisitor 
{	
	public void visit(FmcGraph fuzzyGraph) 
	{
		for (FmcNode input : fuzzyGraph.getInputs())
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
   private void bfs(FmcNode startNode, FmcGraph fuzzyGraph) 
   {  
       Set<Integer> visited = new TreeSet<Integer>();
       LinkedList<FmcNode> queue = new LinkedList<FmcNode>(); 
       visited.add(startNode.getId());
       queue.add(startNode); 
 
       /*
        * Get all adjacent nodes of the currentNode.
        * If an adjacent node has not been visited yet,
        * mark it as visited and enqueue the node
        */
       while (queue.size() != 0) 
       {
    	   FmcNode currentNode = queue.poll(); 
           Set<FmcEdge> adjacentEdges = fuzzyGraph.getGraph().outgoingEdgesOf(currentNode.getId());
           
           for (FmcEdge edge : adjacentEdges)
           {
        	   FmcNode targetNode = fuzzyGraph.getNodesMap().getValue(
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
