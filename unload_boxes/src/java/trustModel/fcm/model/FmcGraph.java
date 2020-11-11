package trustModel.fcm.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.jgrapht.graph.DefaultDirectedGraph;

/**
 * This class implements a Fuzzy Cognitive Map. 
 */
public class FmcGraph 
{
	private DefaultDirectedGraph<Integer, FmcEdge> graph;	// The fuzzy cognitive map
	private FmcContentMap<FmcNode> nodesMap;			// Map of nodes
	private FmcContentMap<FmcEdge> edgesMap;			// Map of edges
	private Set<FmcNode> inputs;							// Fuzzy map's inputs
	private FmcNode output;								// Fuzzy map's output (only one)
	
	public FmcGraph() 
	{
		this.graph = new DefaultDirectedGraph <Integer, FmcEdge>(FmcEdge.class);
		this.nodesMap = new FmcContentMap<FmcNode>();
		this.edgesMap = new FmcContentMap<FmcEdge>();
		this.inputs = new TreeSet<FmcNode>();
		this.output = null;
	}

	/**
	 * This method inserts a node in the fuzzy cognitive map.
	 * @param node: a node to be inserted.
	 */
	public void insertNode(FmcNode node)
	{
		this.graph.addVertex(node.getId());
		this.nodesMap.insertElement(node.getId(), node);
	}
	
	/**
	 * This method inserts an edge in the fuzzy cognitive map.
	 * @param node: an edge to be inserted.
	 */
	public void insertEdge(FmcEdge edge)
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
	public Set<FmcNode> getNonInputs()
	{
		Set<FmcNode> nonInputs = new TreeSet<FmcNode>();
		
		for(FmcNode node : this.nodesMap.getValues())
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
	public List<FmcNode> getNodesByName(String nodeName)
	{
		List<FmcNode> nodes = new ArrayList<FmcNode>();
		
		for(FmcNode node : this.nodesMap.getValues())
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
	public List<FmcEdge> getEdgesByName(String edgeName)
	{
		List<FmcEdge> edges = new ArrayList<FmcEdge>();
		
		for(FmcEdge edge : this.edgesMap.getValues())
		{
			if(edge.getName().equals(edgeName))
				edges.add(edge);
		}
		return edges;
	}
	
	public DefaultDirectedGraph<Integer, FmcEdge> getGraph() 
	{
		return graph;
	}

	public FmcContentMap<FmcNode> getNodesMap() 
	{
		return nodesMap;
	}

	public FmcContentMap<FmcEdge> getEdgesMap() 
	{
		return edgesMap;
	}

	public Set<FmcNode> getInputs() 
	{
		return inputs;
	}

	public FmcNode getOutput() 
	{
		return output;
	}
	
	/**
	 * Interface used by the design pattern Visitor.
	 * The idea is separate the map of its operations.
	 */
    public void accept(FmcVisitor visitor) 
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
