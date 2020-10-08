package fuzzyCognitiveMaps.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.jgrapht.graph.DefaultDirectedGraph;

/**
 * This class implements a Fuzzy Cognitive Map. 
 */
public class FuzzyGraph 
{
	private DefaultDirectedGraph<Integer, FuzzyEdge> graph;	// The fuzzy cognitive map
	private FuzzyContentMap<FuzzyNode> nodesMap;			// Map of nodes
	private FuzzyContentMap<FuzzyEdge> edgesMap;			// Map of edges
	private Set<FuzzyNode> inputs;							// Fuzzy map's inputs
	private FuzzyNode output;								// Fuzzy map's output (only one)
	
	public FuzzyGraph() 
	{
		this.graph = new DefaultDirectedGraph <Integer, FuzzyEdge>(FuzzyEdge.class);
		this.nodesMap = new FuzzyContentMap<FuzzyNode>();
		this.edgesMap = new FuzzyContentMap<FuzzyEdge>();
		this.inputs = new TreeSet<FuzzyNode>();
		this.output = null;
	}

	/**
	 * This method inserts a node in the fuzzy cognitive map.
	 * @param node: a node to be inserted.
	 */
	public void insertNode(FuzzyNode node)
	{
		this.graph.addVertex(node.getId());
		this.nodesMap.insertElement(node.getId(), node);
	}
	
	/**
	 * This method inserts an edge in the fuzzy cognitive map.
	 * @param node: an edge to be inserted.
	 */
	public void insertEdge(FuzzyEdge edge)
	{
		this.graph.addEdge(edge.getSource(), edge.getTarget(), edge);
		this.edgesMap.insertElement(edge.getId(), edge);
	}
	
	/**
	 * This method defines a node as an input of the Fuzzy map.
	 * @param nodeId: the id of the node to be defined as an input.
	 */
	public void setAsInput(Integer id)
	{
		this.inputs.add(this.nodesMap.getValue(id));		
	}
	
	/**
	 * This method defines a node as an output of the Fuzzy map.
	 * @param nodeName: the id of the node to be defined as output.
	 */
	public void setAsOutput(Integer id)
	{
		this.output = this.nodesMap.getValue(id);
	}
	
	/**
	 * @return a list of nodes that are not input nodes.
	 */
	public Set<FuzzyNode> getNonInputs()
	{
		Set<FuzzyNode> nonInputs = new TreeSet<FuzzyNode>();
		
		for(FuzzyNode node : this.nodesMap.getValues())
		{
			if(!this.inputs.contains(node))
			{
				nonInputs.add(node);
			}
		}
		return nonInputs;
	}
	
	/**
	 * This method search nodes by name.
	 * As different nodes may have the same name, this methods returns a list of nodes.
	 * @param nodeName: name of node to be found.
	 * @return a list of matching nodes, case there are not matching, this method returns an empty list.
	 */
	public List<FuzzyNode> getNodesByName(String nodeName)
	{
		List<FuzzyNode> nodes = new ArrayList<FuzzyNode>();
		
		for(FuzzyNode node : this.nodesMap.getValues())
		{
			if(node.getName().equals(nodeName))
				nodes.add(node);
		}
		return nodes;
	}
	
	/**
	 * This method search edges by name.
	 * As different edges may have the same name, this methods returns a list of edges.
	 * @param edgeName: name of edge to be found.
	 * @return a list of matching edges, case there are not matching, this method returns an empty list.
	 */
	public List<FuzzyEdge> getEdgesByName(String edgeName)
	{
		List<FuzzyEdge> edges = new ArrayList<FuzzyEdge>();
		
		for(FuzzyEdge edge : this.edgesMap.getValues())
		{
			if(edge.getName().equals(edgeName))
				edges.add(edge);
		}
		return edges;
	}
	
	public DefaultDirectedGraph<Integer, FuzzyEdge> getGraph() 
	{
		return graph;
	}

	public FuzzyContentMap<FuzzyNode> getNodesMap() 
	{
		return nodesMap;
	}

	public FuzzyContentMap<FuzzyEdge> getEdgesMap() 
	{
		return edgesMap;
	}

	public Set<FuzzyNode> getInputs() 
	{
		return inputs;
	}

	public FuzzyNode getOutput() 
	{
		return output;
	}
	
	/**
	 * Interface used by the design pattern Visitor.
	 * The idea is separate the map of its operations.
	 */
    public void accept(FuzzyVisitor visitor) 
    {
        visitor.visit(this);
    }
	
	@Override
	public String toString() 
	{
		StringBuilder strBuffer = new StringBuilder();
		strBuffer.append("NODES:\n");
		strBuffer.append(nodesMap);
		strBuffer.append("\n");
		
		strBuffer.append("EDGES:\n");
		strBuffer.append(edgesMap);
		strBuffer.append("\n");
		
		return strBuffer.toString();
	}
}
