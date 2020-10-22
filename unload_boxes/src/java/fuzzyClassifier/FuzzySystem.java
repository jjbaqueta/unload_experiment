package fuzzyClassifier;

import net.sourceforge.jFuzzyLogic.FIS;
import net.sourceforge.jFuzzyLogic.plot.JFuzzyChart;

/**
 * This class implements some basic operations used during the experiments.
 * For instance: operations to open and load input files.
 */
public abstract class FuzzySystem 
{
	public static FIS fis;
	/**
	 * Load the fuzzy blocks from a input file (fuzzy_system.fcl)
	 * @return a Fuzzy inference system (FIS)
	 */
	public static FIS loadFuzzyBlocks()
	{
		String fileName = "src/java/fuzzyClassifier/fuzzy_system.fcl";
        FIS fis = FIS.load(fileName, true);
        
        if( fis == null )
        	throw new Error("Can't load file: '" + fileName + "'");
        
        return fis;
	}
	
	/**
	 * Show the inputs as fuzzy sets
	 * @param fis: a Fuzzy inference system.
	 */
	public static void showInputCharts(FIS fis)
	{
		JFuzzyChart.get().chart(fis);
	}
	
	public static double[] getOutputValues(double urgencyValue, 
			double numBoxesValue, double ownImpsValue, double otherImpsValue, double selfConfidentValue)
	{
		fis.setVariable("urgency", urgencyValue);
		fis.setVariable("num_boxes", numBoxesValue);
		fis.setVariable("own_imps", ownImpsValue);
		fis.setVariable("other_imps", otherImpsValue);
		fis.setVariable("self_confident", selfConfidentValue);
		fis.evaluate();
		
		double[] outputs = new double[6];
		
		outputs[0] = fis.getVariable("ability_effect").getValue();
		outputs[1] = fis.getVariable("availability_effect").getValue();
		outputs[2] = fis.getVariable("knowhow_effect").getValue();
		outputs[3] = fis.getVariable("ir_effect").getValue();
		outputs[4] = fis.getVariable("rep_effect").getValue();
		outputs[5] = fis.getVariable("img_effect").getValue();
		
		return outputs;
	}
	
	public static void main(String[] args) 
	{
		FuzzySystem.fis = loadFuzzyBlocks();
		double[] values = FuzzySystem.getOutputValues(0.5, 15, 5, 15, 0.0);
		
		for(Double v : values)
		{
			System.out.println(v);
		}
	}
}
