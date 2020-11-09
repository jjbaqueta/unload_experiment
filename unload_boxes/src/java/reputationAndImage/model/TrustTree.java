package reputationAndImage.model;

import fuzzyCognitiveMaps.model.FuzzyEdge;
import fuzzyCognitiveMaps.model.FuzzyGraph;
import fuzzyCognitiveMaps.model.FuzzyNode;

/**
 * This class implements the trust tree used into trust computing process.
 * The trust computing process is implemented by the action: @link{computeTrust.java}
 */
public class TrustTree 
{
	private FuzzyGraph fuzzyMap;
	private Integer idNode;
	private Integer idEdge;
	
	// AVAILABILITY
	public TrustTree() 
	{
		fuzzyMap = new FuzzyGraph();
		idNode = 0;
		idEdge = 0;
		
		fuzzyMap.insertNode(new FuzzyNode(idNode++, Mnemonic.IMAGE.name(), 0.0));
		fuzzyMap.insertNode(new FuzzyNode(idNode++, Mnemonic.REPUTATION.name(), 0.0));
		fuzzyMap.insertNode(new FuzzyNode(idNode++, Mnemonic.KNOWHOW.name(), 0.0));
		fuzzyMap.insertNode(new FuzzyNode(idNode++, "i/r", 0.0));
		fuzzyMap.insertNode(new FuzzyNode(idNode++, "ability", 0.0));
		fuzzyMap.insertNode(new FuzzyNode(idNode++, "availability", 0.0));
		fuzzyMap.insertNode(new FuzzyNode(idNode++, "internalFactors", 0.0));
		fuzzyMap.insertNode(new FuzzyNode(idNode++, "risk", 0.0));
		fuzzyMap.insertNode(new FuzzyNode(idNode++, "opportunity", 0.0));
		fuzzyMap.insertNode(new FuzzyNode(idNode++, "externalFactors", 0.0));
		fuzzyMap.insertNode(new FuzzyNode(idNode++, "Trustfulness", 0.0));
		
		fuzzyMap.insertEdge(new FuzzyEdge(idEdge++, "img_effect", 0.5, 0, 3));				//img -> i/r		
		fuzzyMap.insertEdge(new FuzzyEdge(idEdge++, "rep_effect", 0.5, 1, 3));				//rep -> i/r
		fuzzyMap.insertEdge(new FuzzyEdge(idEdge++, "ir_effect", 0.5, 3, 4));				//i/r -> ability
		fuzzyMap.insertEdge(new FuzzyEdge(idEdge++, "knowhow_effect", 0.5, 2, 4));			//ref -> ability
		fuzzyMap.insertEdge(new FuzzyEdge(idEdge++, "ability_effect", 0.1, 4, 6));			//ability -> internal factors
		fuzzyMap.insertEdge(new FuzzyEdge(idEdge++, "availability_effect", 1.0, 5, 6));		//availability -> internal factors
		fuzzyMap.insertEdge(new FuzzyEdge(idEdge++, "e", 0.5, 6, 10));		//internal factors -> trustfulness
		fuzzyMap.insertEdge(new FuzzyEdge(idEdge++, "e", 0.5, 7, 9));		//risk -> external factors
		fuzzyMap.insertEdge(new FuzzyEdge(idEdge++, "e", 0.2, 8, 9));		//opportunity -> external factors 
		fuzzyMap.insertEdge(new FuzzyEdge(idEdge++, "e", 0.5, 9, 10)); 		//external factors -> trustfulness 
		
		fuzzyMap.setAsOutput(10);
	}
	
	//ABILITY
//	public TrustTree() 
//	{
//		fuzzyMap = new FuzzyGraph();
//		idNode = 0;
//		idEdge = 0;
//		
//		fuzzyMap.insertNode(new FuzzyNode(idNode++, Mnemonic.IMAGE.name(), 0.0));
//		fuzzyMap.insertNode(new FuzzyNode(idNode++, Mnemonic.REPUTATION.name(), 0.0));
//		fuzzyMap.insertNode(new FuzzyNode(idNode++, Mnemonic.KNOWHOW.name(), 0.0));
//		fuzzyMap.insertNode(new FuzzyNode(idNode++, "i/r", 0.0));
//		fuzzyMap.insertNode(new FuzzyNode(idNode++, "ability", 0.0));
//		fuzzyMap.insertNode(new FuzzyNode(idNode++, "availability", 0.0));
//		fuzzyMap.insertNode(new FuzzyNode(idNode++, "internalFactors", 0.0));
//		fuzzyMap.insertNode(new FuzzyNode(idNode++, "risk", 0.0));
//		fuzzyMap.insertNode(new FuzzyNode(idNode++, "opportunity", 0.0));
//		fuzzyMap.insertNode(new FuzzyNode(idNode++, "externalFactors", 0.0));
//		fuzzyMap.insertNode(new FuzzyNode(idNode++, "Trustfulness", 0.0));
//		
//		fuzzyMap.insertEdge(new FuzzyEdge(idEdge++, "img_effect", 1.0, 0, 3));				//img -> i/r		
//		fuzzyMap.insertEdge(new FuzzyEdge(idEdge++, "rep_effect", 1.0, 1, 3));				//rep -> i/r
//		fuzzyMap.insertEdge(new FuzzyEdge(idEdge++, "ir_effect", 1.0, 3, 4));				//i/r -> ability
//		fuzzyMap.insertEdge(new FuzzyEdge(idEdge++, "knowhow_effect", 1.0, 2, 4));			//ref -> ability
//		fuzzyMap.insertEdge(new FuzzyEdge(idEdge++, "ability_effect", 1.0, 4, 6));			//ability -> internal factors
//		fuzzyMap.insertEdge(new FuzzyEdge(idEdge++, "availability_effect", 0.1, 5, 6));		//availability -> internal factors
//		fuzzyMap.insertEdge(new FuzzyEdge(idEdge++, "e", 0.5, 6, 10));		//internal factors -> trustfulness
//		fuzzyMap.insertEdge(new FuzzyEdge(idEdge++, "e", 0.5, 7, 9));		//risk -> external factors
//		fuzzyMap.insertEdge(new FuzzyEdge(idEdge++, "e", 0.2, 8, 9));		//opportunity -> external factors 
//		fuzzyMap.insertEdge(new FuzzyEdge(idEdge++, "e", 0.5, 9, 10)); 		//external factors -> trustfulness 
//		
//		fuzzyMap.setAsOutput(10);
//	}

//	//FUZZY
//	public TrustTree() 
//	{
//		fuzzyMap = new FuzzyGraph();
//		idNode = 0;
//		idEdge = 0;
//		
//		fuzzyMap.insertNode(new FuzzyNode(idNode++, Mnemonic.IMAGE.name(), 0.0));
//		fuzzyMap.insertNode(new FuzzyNode(idNode++, Mnemonic.REPUTATION.name(), 0.0));
//		fuzzyMap.insertNode(new FuzzyNode(idNode++, Mnemonic.KNOWHOW.name(), 0.0));
//		fuzzyMap.insertNode(new FuzzyNode(idNode++, "i/r", 0.0));
//		fuzzyMap.insertNode(new FuzzyNode(idNode++, "ability", 0.0));
//		fuzzyMap.insertNode(new FuzzyNode(idNode++, "availability", 0.0));
//		fuzzyMap.insertNode(new FuzzyNode(idNode++, "internalFactors", 0.0));
//		fuzzyMap.insertNode(new FuzzyNode(idNode++, "risk", 0.0));
//		fuzzyMap.insertNode(new FuzzyNode(idNode++, "opportunity", 0.0));
//		fuzzyMap.insertNode(new FuzzyNode(idNode++, "externalFactors", 0.0));
//		fuzzyMap.insertNode(new FuzzyNode(idNode++, "Trustfulness", 0.0));
//		
//		fuzzyMap.insertEdge(new FuzzyEdge(idEdge++, "img_effect", 1.0, 0, 3));				//img -> i/r		
//		fuzzyMap.insertEdge(new FuzzyEdge(idEdge++, "rep_effect", 1.0, 1, 3));				//rep -> i/r
//		fuzzyMap.insertEdge(new FuzzyEdge(idEdge++, "ir_effect", 1.0, 3, 4));				//i/r -> ability
//		fuzzyMap.insertEdge(new FuzzyEdge(idEdge++, "knowhow_effect", 1.0, 2, 4));			//ref -> ability
//		fuzzyMap.insertEdge(new FuzzyEdge(idEdge++, "ability_effect", 1.0, 4, 6));			//ability -> internal factors
//		fuzzyMap.insertEdge(new FuzzyEdge(idEdge++, "availability_effect", 1.0, 5, 6));		//availability -> internal factors
//		fuzzyMap.insertEdge(new FuzzyEdge(idEdge++, "e", 0.5, 6, 10));		//internal factors -> trustfulness
//		fuzzyMap.insertEdge(new FuzzyEdge(idEdge++, "e", 0.5, 7, 9));		//risk -> external factors
//		fuzzyMap.insertEdge(new FuzzyEdge(idEdge++, "e", 0.2, 8, 9));		//opportunity -> external factors 
//		fuzzyMap.insertEdge(new FuzzyEdge(idEdge++, "e", 0.5, 9, 10)); 		//external factors -> trustfulness 
//		
//		fuzzyMap.setAsOutput(10);
//	}

	public FuzzyGraph getFuzzyMap() 
	{
		return fuzzyMap;
	}

	public Integer getIdNode() 
	{
		return idNode;
	}

	public Integer getIdEdge() 
	{
		return idEdge;
	}
	
	public int incNodeId()
	{
		return this.idNode++;
	}
	
	public int incEdgeId()
	{
		return this.idEdge++;
	}
	
	@Override
	public String toString() 
	{
		return fuzzyMap.toString();
	}
}
