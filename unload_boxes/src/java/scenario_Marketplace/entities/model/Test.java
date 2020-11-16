package scenario_Marketplace.entities.model;

import jason.asSyntax.Literal;
import jason.asSyntax.Structure;
import net.sourceforge.jFuzzyLogic.FIS;
import net.sourceforge.jFuzzyLogic.plot.JFuzzyChart;
import scenario_Marketplace.enums.FilePaths;
import scenario_Marketplace.environments.MarketFuzzyConfig;

public class Test 
{
	
	public static void main(String[] args) throws Exception 
	{
		MarketFuzzyConfig.createFuzzyFile(5, 5);
		FIS fis = loadFuzzyBlocks();
		showInputCharts(fis);
		Structure edges = getOutputValues(fis, 0.1, 0.5, 0.4, -0.4);	
	}	
	
	/**
	 * Load the fuzzy blocks from a input file (fuzzy_system.fcl)
	 * @return a Fuzzy inference system (FIS)
	 */
	private static FIS loadFuzzyBlocks()
	{
        FIS fis = FIS.load(FilePaths.FUZZY_VARS.getPath(), true);
        
        if( fis == null )
        	throw new Error("Can't load file: '" + FilePaths.FUZZY_VARS.getPath() + "'");
        
        return fis;
	}
	
	/**
	 * This methods compute the values for each fuzzy varible of trust model based on a flc file.
	 * @param FIS: a Fuzzy inference system (FIS) (loaded from flc file).
	 * @param urgencyValue: the urgency level of a buyer to buy a product..
	 * @param ownImpsValue: the buyer's imagem about a seller.
	 * @param otherImpsValue: the reputation of the seller.
	 * @param selfConfidentValue: buyer's self confident profile.
	 * @return the values that'll be associated the edges of the trust model.
	 */
	private static Structure getOutputValues(FIS fis, double urgencyValue, double ownImpsValue, 
			double otherImpsValue, double selfConfidentValue)
	{
		fis.setVariable("urgency", urgencyValue);
		fis.setVariable("own_imps", ownImpsValue);
		fis.setVariable("other_imps", otherImpsValue);
		fis.setVariable("self_confident", selfConfidentValue);
		fis.evaluate();
		
		Structure weights = new Structure("edges");
		
		weights.addTerm(Literal.parseLiteral("ability_effect("+ fis.getVariable("ability_effect").getValue() +")"));
		weights.addTerm(Literal.parseLiteral("availability_effect("+ fis.getVariable("availability_effect").getValue() +")"));
		weights.addTerm(Literal.parseLiteral("knowhow_effect("+ fis.getVariable("knowhow_effect").getValue() +")"));
		weights.addTerm(Literal.parseLiteral("ir_effect("+ fis.getVariable("ir_effect").getValue() +")"));
		weights.addTerm(Literal.parseLiteral("rep_effect("+ fis.getVariable("rep_effect").getValue() +")"));
		weights.addTerm(Literal.parseLiteral("img_effect("+ fis.getVariable("img_effect").getValue() +")"));
		
		return weights;
	}
	
	/**
	 * Show the inputs as fuzzy sets
	 * @param fis: a Fuzzy inference system.
	 */
	public static void showInputCharts(FIS fis)
	{
		JFuzzyChart.get().chart(fis);
	}
}