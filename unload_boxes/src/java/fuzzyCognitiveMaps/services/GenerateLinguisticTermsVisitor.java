package fuzzyCognitiveMaps.services;

import java.io.FileNotFoundException;

import fuzzyCognitiveMaps.model.FuzzyGraph;
import fuzzyCognitiveMaps.model.FuzzyNode;
import fuzzyCognitiveMaps.model.FuzzyVisitor;
import net.sourceforge.jFuzzyLogic.FIS;
import net.sourceforge.jFuzzyLogic.FunctionBlock;
import net.sourceforge.jFuzzyLogic.rule.Variable;

public class GenerateLinguisticTermsVisitor implements FuzzyVisitor
{

	private FunctionBlock fb;
	
	public GenerateLinguisticTermsVisitor() throws FileNotFoundException 
	{
		String filename = "jfuzzy_files/trustOnMe.fcl";
		FIS fis = FIS.load(filename, true);

		if (fis == null) 
		{
			throw new FileNotFoundException("Can't load file: '" + filename + "'");
		}

		fb = fis.getFunctionBlock(null);
	}
	
	public void visit(FuzzyGraph fuzzyMap) 
	{
		for(FuzzyNode node : fuzzyMap.getNodesMap().getValues())
		{
			System.out.println("NODE: id{" + node.getId()
			+ "}, value{" + node.getValue()
			+ "}, name{" + node.getName() + "}");
			
			System.out.println(fuzzyfy(node.getValue()));
		}
		System.out.println("OUTPUT NODE: id{" + fuzzyMap.getOutput().getId()
		+ "}, value{" + fuzzyMap.getOutput().getValue()
		+ "}, name{" + fuzzyMap.getOutput().getName() + "}");
		
		System.out.println(defuzzyfy());
	}
		   
   /**
    * This method fuzzyfy an input value according to fuzzy intervals
    * @param value: real input value
    * @return a classification based on the linguistic terms (quite, middle, good)
    */
   private String fuzzyfy(double value)
   {
		fb.setVariable("value", value);
		fb.evaluate();
		
		StringBuilder sb = new StringBuilder();
		Variable var = fb.getVariable("value");
		
		sb.append("\tquite: " + var.getMembership("quite")).append("\n");
		sb.append("\tmiddle: " + var.getMembership("middle")).append("\n");
		sb.append("\tgood: " + var.getMembership("good")).append("\n");
		
		return sb.toString();
   }
   
   /**
    * @return a classification based on the linguistic terms (not_delegate, delegate)
    */
   private String defuzzyfy()
   {
		StringBuilder sb = new StringBuilder();
		Variable var = fb.getVariable("decision");
		var.defuzzify();
		
		sb.append("\tnot_delegate: " + var.getMembership("not_delegate")).append("\n");
		sb.append("\tdelegate: " + var.getMembership("delegate")).append("\n");
		
		return sb.toString();
   }
}
