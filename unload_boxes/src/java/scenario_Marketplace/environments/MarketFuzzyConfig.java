package scenario_Marketplace.environments;

import java.io.FileWriter;
import java.io.IOException;

import scenario_Marketplace.enums.FilePaths;

public class MarketFuzzyConfig 
{
	// Defining variable urgency
	public static Double min_urgency;
	public static Double max_urgency;
	
	// Defining variable self-confident (percentage of agents that are self-confident)
	public static Double min_confident;
	public static Double max_confident;
	
	// Defining variable number of own impressions
	public static Integer min_own_imps;
	public static Integer max_own_imps;
	
	// Defining variable number of third part impressions
	public static Integer min_other_imps;
	public static Integer max_other_imps;
	
	private static void setUrgency()
	{
		min_urgency = -1.0;
		max_urgency = 1.0;
	}
	
	private static void setSelfConfident()
	{
		min_confident = -1.0;
		max_confident = 1.0;
	}
	
	/**
	 * @param ownImpITM: intimate level of interactions for own impressions
	 */
	private static void setNumOwnImpressions(int ownImpITM)
	{
		min_own_imps = 0;
		max_own_imps = ownImpITM;
	}
	
	/**
	 * @param otherImpitm: intimate level of interactions for other impressions
	 */
	private static void setNumOtherImpressions(int otherImpITM)
	{
		min_other_imps = 0;
		max_other_imps = otherImpITM;
	}
	
	public static void createFuzzyFile(int ownImpITM, int otherImpITM)
	{
		setUrgency();
		setSelfConfident();
		setNumOwnImpressions(otherImpITM);
		setNumOtherImpressions(otherImpITM);
		
		StringBuffer sb = new StringBuffer();
		
		// Inputs
		sb.append("FUNCTION_BLOCK marketplace\n\n");
		sb.append("VAR_INPUT\n");
		sb.append("\turgency : REAL;\n");
		sb.append("\town_imps : REAL;\n");
		sb.append("\tother_imps : REAL;\n");
		sb.append("\tself_confident : REAL;\n");
		sb.append("END_VAR\n\n");
		
		// Output
		sb.append("VAR_OUTPUT\n");
		sb.append("\tability_effect : REAL;\n");
		sb.append("\tavailability_effect : REAL;\n");
		sb.append("\tknowhow_effect : REAL;\n");
		sb.append("\tir_effect : REAL;\n");
		sb.append("\trep_effect : REAL;\n");
		sb.append("\timg_effect : REAL;\n");
		sb.append("END_VAR\n\n");
		
		sb.append(getInputFuzzify3Block("urgency", min_urgency, max_urgency));
		sb.append(getInputFuzzify3Block("self_confident", min_confident, max_confident));
		sb.append(getInputFuzzify4Block("own_imps", min_own_imps, max_own_imps));
		sb.append(getInputFuzzify4Block("other_imps", min_other_imps, max_other_imps));
		
		sb.append(getOutputFuzzifyBlock("ability_effect"));
		sb.append(getOutputFuzzifyBlock("availability_effect"));
		sb.append(getOutputFuzzifyBlock("knowhow_effect"));
		sb.append(getOutputFuzzifyBlock("ir_effect"));
		sb.append(getOutputFuzzifyBlock("rep_effect"));
		sb.append(getOutputFuzzifyBlock("img_effect"));

		sb.append(getFuzzyBR());
		
		FileWriter file;
		try 
		{
			file = new FileWriter(FilePaths.FUZZY_VARS.getPath());
			file.write(sb.toString());
			file.close();
		} 
		catch (IOException e) 
		{
			System.out.println(e.getMessage());
			e.printStackTrace();
		}
	}
	
	@SuppressWarnings("unused")
	private static String getInputFuzzify2Block(String varName, double min, double max)
	{	
		StringBuffer sb = new StringBuffer();
		
		sb.append("FUZZIFY " + varName).append("\n");
		sb.append("\tTERM false := ("+ min +", 1) ("+ 0.2 +", 0);\n");
		sb.append("\tTERM true := ("+ -0.2 +", 0) ("+ max +", 1);\n");
		sb.append("END_FUZZIFY\n\n");
		
		return sb.toString();
	}
	
	private static String getInputFuzzify3Block(String varName, double min, double max)
	{
		double middle = ((max - min) / 2) + min;
		double middleRight = ((max - middle) / 2) + middle;
		double middleLeft = ((middle - min) / 2) + min;
		
		StringBuffer sb = new StringBuffer();
		
		sb.append("FUZZIFY " + varName).append("\n");
		sb.append("\tTERM low := ("+ min +", 1) ("+ middleLeft +", 1) ("+ middle +", 0);\n");
		sb.append("\tTERM middle := ("+ middleLeft +", 0) ("+ middle +", 1) ("+ middleRight +", 0);\n");
		sb.append("\tTERM high := ("+ middle +", 0) ("+ middleRight +", 1) ("+ max +", 1);\n");
		sb.append("END_FUZZIFY\n\n");
		
		return sb.toString();
	}
	
	private static String getInputFuzzify4Block(String varName, double min, double max)
	{
		double middle = ((max - min) / 2) + min;
		double middleRight = ((max - middle) / 2) + middle;
		double middleLeft = ((middle - min) / 2) + min;
		
		StringBuffer sb = new StringBuffer();
		
		sb.append("FUZZIFY " + varName).append("\n");
		sb.append("\tTERM none := (0, 1) (1, 0);\n");
		sb.append("\tTERM low := ("+ min +", 1) ("+ middleLeft +", 1) ("+ middle +", 0);\n");
		sb.append("\tTERM middle := ("+ middleLeft +", 0) ("+ middle +", 1) ("+ middleRight +", 0);\n");
		sb.append("\tTERM high := ("+ middle +", 0) ("+ middleRight +", 1) ("+ max +", 1);\n");
		sb.append("END_FUZZIFY\n\n");
		
		return sb.toString();
	}
	
	private static String getOutputFuzzifyBlock(String varName)
	{
		double middle = 0;
		double middleRight = 0.5;
		double middleLeft = -0.5;
		
		StringBuffer sb = new StringBuffer();
		
		sb.append("DEFUZZIFY " + varName).append("\n");
		sb.append("\tTERM low := ("+ -1.0 +", 1) ("+ middleLeft +", 1) ("+ middle +", 0);\n");
		sb.append("\tTERM middle := ("+ middleLeft +", 0) ("+ middle +", 1) ("+ middleRight +", 0);\n");
		sb.append("\tTERM high := ("+ middle +", 0) ("+ middleRight +", 1) ("+ 1.0 +", 1);\n");
		sb.append("\tMETHOD : COG;\n");
		sb.append("\tDEFAULT := 0;\n");		
		sb.append("END_DEFUZZIFY\n\n");
		
		return sb.toString();
	}
	
	private static String getFuzzyBR()
	{
		StringBuffer sb = new StringBuffer();
		
		sb.append("RULEBLOCK No1\n");
		sb.append("\tAND : MIN;\n");
		sb.append("\tACT : MIN;\n");
		sb.append("\tACCU : MAX;\n\n");
		
		sb.append("\tRULE 1: IF urgency IS low THEN availability_effect IS low;\n");
		sb.append("\tRULE 2: IF urgency IS middle THEN ability_effect IS middle;\n");
		sb.append("\tRULE 3: IF urgency IS high THEN ability_effect IS high;\n");
				
		sb.append("\tRULE 4: IF other_imps IS none AND own_imps IS none THEN knowhow_effect IS high;\n");
		sb.append("\tRULE 5: IF other_imps IS none AND own_imps IS none THEN ir_effect IS low;\n");
		sb.append("\tRULE 6: IF other_imps IS none AND own_imps IS low THEN knowhow_effect IS middle;\n");
		sb.append("\tRULE 7: IF other_imps IS none AND own_imps IS low THEN ir_effect IS middle;\n");
		sb.append("\tRULE 8: IF other_imps IS none AND own_imps IS middle THEN knowhow_effect IS low;\n");
		sb.append("\tRULE 9: IF other_imps IS none AND own_imps IS middle THEN ir_effect IS high;\n");
		sb.append("\tRULE 10: IF other_imps IS none AND own_imps IS high THEN knowhow_effect IS low;\n");
		sb.append("\tRULE 11: IF other_imps IS none AND own_imps IS high THEN ir_effect IS high;\n");
		
		sb.append("\tRULE 12: IF other_imps IS low AND own_imps IS none THEN knowhow_effect IS middle;\n");
		sb.append("\tRULE 13: IF other_imps IS low AND own_imps IS none THEN ir_effect IS middle;\n");
		sb.append("\tRULE 14: IF other_imps IS low AND own_imps IS low THEN knowhow_effect IS middle;\n");
		sb.append("\tRULE 15: IF other_imps IS low AND own_imps IS low THEN ir_effect IS middle;\n");
		sb.append("\tRULE 16: IF other_imps IS low AND own_imps IS middle THEN knowhow_effect IS low;\n");
		sb.append("\tRULE 17: IF other_imps IS low AND own_imps IS middle THEN ir_effect IS high;\n");
		sb.append("\tRULE 18: IF other_imps IS low AND own_imps IS high THEN knowhow_effect IS low;\n");
		sb.append("\tRULE 19: IF other_imps IS low AND own_imps IS high THEN ir_effect IS high;\n");
		
		sb.append("\tRULE 20: IF other_imps IS middle AND own_imps IS none THEN knowhow_effect IS middle;\n");
		sb.append("\tRULE 21: IF other_imps IS middle AND own_imps IS none THEN ir_effect IS middle;\n");
		sb.append("\tRULE 22: IF other_imps IS middle AND own_imps IS low THEN knowhow_effect IS middle;\n");
		sb.append("\tRULE 23: IF other_imps IS middle AND own_imps IS low THEN ir_effect IS middle;\n");
		sb.append("\tRULE 24: IF other_imps IS middle AND own_imps IS middle THEN knowhow_effect IS low;\n");
		sb.append("\tRULE 25: IF other_imps IS middle AND own_imps IS middle THEN ir_effect IS high;\n");
		sb.append("\tRULE 26: IF other_imps IS middle AND own_imps IS high THEN knowhow_effect IS low;\n");
		sb.append("\tRULE 27: IF other_imps IS middle AND own_imps IS high THEN ir_effect IS high;\n");
		
		sb.append("\tRULE 28: IF other_imps IS high AND own_imps IS none THEN knowhow_effect IS low;\n");
		sb.append("\tRULE 29: IF other_imps IS high AND own_imps IS none THEN ir_effect IS middle;\n");
		sb.append("\tRULE 30: IF other_imps IS high AND own_imps IS low THEN knowhow_effect IS low;\n");
		sb.append("\tRULE 31: IF other_imps IS high AND own_imps IS low THEN ir_effect IS high;\n");
		sb.append("\tRULE 32: IF other_imps IS high AND own_imps IS middle THEN knowhow_effect IS low;\n");
		sb.append("\tRULE 33: IF other_imps IS high AND own_imps IS middle THEN ir_effect IS high;\n");
		sb.append("\tRULE 34: IF other_imps IS high AND own_imps IS high THEN knowhow_effect IS low;\n");
		sb.append("\tRULE 35: IF other_imps IS high AND own_imps IS high THEN ir_effect IS high;\n");
		
		sb.append("\tRULE 36: IF self_confident IS low AND other_imps IS none AND own_imps IS none THEN rep_effect IS low;\n");
		sb.append("\tRULE 37: IF self_confident IS low AND other_imps IS none AND own_imps IS none THEN img_effect IS low;\n");
		sb.append("\tRULE 38: IF self_confident IS low AND other_imps IS none AND own_imps IS low THEN rep_effect IS low;\n");
		sb.append("\tRULE 39: IF self_confident IS low AND other_imps IS none AND own_imps IS low THEN img_effect IS high;\n");
		sb.append("\tRULE 40: IF self_confident IS low AND other_imps IS none AND own_imps IS middle THEN rep_effect IS low;\n");
		sb.append("\tRULE 41: IF self_confident IS low AND other_imps IS none AND own_imps IS middle THEN img_effect IS high;\n");
		sb.append("\tRULE 42: IF self_confident IS low AND other_imps IS none AND own_imps IS high THEN rep_effect IS low;\n");
		sb.append("\tRULE 43: IF self_confident IS low AND other_imps IS none AND own_imps IS high THEN img_effect IS high;\n");
		
		sb.append("\tRULE 44: IF self_confident IS low AND other_imps IS low AND own_imps IS none THEN rep_effect IS high;\n");
		sb.append("\tRULE 45: IF self_confident IS low AND other_imps IS low AND own_imps IS none THEN img_effect IS low;\n");
		sb.append("\tRULE 46: IF self_confident IS low AND other_imps IS low AND own_imps IS low THEN rep_effect IS high;\n");
		sb.append("\tRULE 47: IF self_confident IS low AND other_imps IS low AND own_imps IS low THEN img_effect IS low;\n");
		sb.append("\tRULE 48: IF self_confident IS low AND other_imps IS low AND own_imps IS middle THEN rep_effect IS high;\n");
		sb.append("\tRULE 49: IF self_confident IS low AND other_imps IS low AND own_imps IS middle THEN img_effect IS middle;\n");
		sb.append("\tRULE 50: IF self_confident IS low AND other_imps IS low AND own_imps IS high THEN rep_effect IS high;\n");
		sb.append("\tRULE 51: IF self_confident IS low AND other_imps IS low AND own_imps IS high THEN img_effect IS middle;\n");
		
		sb.append("\tRULE 52: IF self_confident IS low AND other_imps IS middle AND own_imps IS none THEN rep_effect IS high;\n");
		sb.append("\tRULE 53: IF self_confident IS low AND other_imps IS middle AND own_imps IS none THEN img_effect IS low;\n");
		sb.append("\tRULE 54: IF self_confident IS low AND other_imps IS middle AND own_imps IS low THEN rep_effect IS high;\n");
		sb.append("\tRULE 55: IF self_confident IS low AND other_imps IS middle AND own_imps IS low THEN img_effect IS low;\n");
		sb.append("\tRULE 56: IF self_confident IS low AND other_imps IS middle AND own_imps IS middle THEN rep_effect IS high;\n");
		sb.append("\tRULE 57: IF self_confident IS low AND other_imps IS middle AND own_imps IS middle THEN img_effect IS middle;\n");
		sb.append("\tRULE 58: IF self_confident IS low AND other_imps IS middle AND own_imps IS high THEN rep_effect IS high;\n");
		sb.append("\tRULE 59: IF self_confident IS low AND other_imps IS middle AND own_imps IS high THEN img_effect IS middle;\n");
		
		sb.append("\tRULE 60: IF self_confident IS low AND other_imps IS high AND own_imps IS none THEN rep_effect IS high;\n");
		sb.append("\tRULE 61: IF self_confident IS low AND other_imps IS high AND own_imps IS none THEN img_effect IS low;\n");
		sb.append("\tRULE 62: IF self_confident IS low AND other_imps IS high AND own_imps IS low THEN rep_effect IS high;\n");
		sb.append("\tRULE 63: IF self_confident IS low AND other_imps IS high AND own_imps IS low THEN img_effect IS middle;\n");
		sb.append("\tRULE 64: IF self_confident IS low AND other_imps IS high AND own_imps IS middle THEN rep_effect IS high;\n");
		sb.append("\tRULE 65: IF self_confident IS low AND other_imps IS high AND own_imps IS middle THEN img_effect IS middle;\n");
		sb.append("\tRULE 66: IF self_confident IS low AND other_imps IS high AND own_imps IS high THEN rep_effect IS high;\n");
		sb.append("\tRULE 67: IF self_confident IS low AND other_imps IS high AND own_imps IS high THEN img_effect IS middle;\n");
		
		sb.append("\tRULE 68: IF self_confident IS middle AND other_imps IS none AND own_imps IS none THEN rep_effect IS low;\n");
		sb.append("\tRULE 69: IF self_confident IS middle AND other_imps IS none AND own_imps IS none THEN img_effect IS low;\n");
		sb.append("\tRULE 70: IF self_confident IS middle AND other_imps IS none AND own_imps IS low THEN rep_effect IS low;\n");
		sb.append("\tRULE 71: IF self_confident IS middle AND other_imps IS none AND own_imps IS low THEN img_effect IS high;\n");
		sb.append("\tRULE 72: IF self_confident IS middle AND other_imps IS none AND own_imps IS middle THEN rep_effect IS low;\n");
		sb.append("\tRULE 73: IF self_confident IS middle AND other_imps IS none AND own_imps IS middle THEN img_effect IS high;\n");
		sb.append("\tRULE 74: IF self_confident IS middle AND other_imps IS none AND own_imps IS high THEN rep_effect IS low;\n");
		sb.append("\tRULE 75: IF self_confident IS middle AND other_imps IS none AND own_imps IS high THEN img_effect IS high;\n");
		
		sb.append("\tRULE 76: IF self_confident IS middle AND other_imps IS low AND own_imps IS none THEN rep_effect IS high;\n");
		sb.append("\tRULE 77: IF self_confident IS middle AND other_imps IS low AND own_imps IS none THEN img_effect IS low;\n");
		sb.append("\tRULE 78: IF self_confident IS middle AND other_imps IS low AND own_imps IS low THEN rep_effect IS middle;\n");
		sb.append("\tRULE 79: IF self_confident IS middle AND other_imps IS low AND own_imps IS low THEN img_effect IS middle;\n");
		sb.append("\tRULE 80: IF self_confident IS middle AND other_imps IS low AND own_imps IS middle THEN rep_effect IS middle;\n");
		sb.append("\tRULE 81: IF self_confident IS middle AND other_imps IS low AND own_imps IS middle THEN img_effect IS high;\n");
		sb.append("\tRULE 82: IF self_confident IS middle AND other_imps IS low AND own_imps IS high THEN rep_effect IS middle;\n");
		sb.append("\tRULE 83: IF self_confident IS middle AND other_imps IS low AND own_imps IS high THEN img_effect IS high;\n");
		
		sb.append("\tRULE 84: IF self_confident IS middle AND other_imps IS middle AND own_imps IS none THEN rep_effect IS high;\n");
		sb.append("\tRULE 85: IF self_confident IS middle AND other_imps IS middle AND own_imps IS none THEN img_effect IS low;\n");
		sb.append("\tRULE 86: IF self_confident IS middle AND other_imps IS middle AND own_imps IS low THEN rep_effect IS high;\n");
		sb.append("\tRULE 87: IF self_confident IS middle AND other_imps IS middle AND own_imps IS low THEN img_effect IS middle;\n");
		sb.append("\tRULE 88: IF self_confident IS middle AND other_imps IS middle AND own_imps IS middle THEN rep_effect IS middle;\n");
		sb.append("\tRULE 89: IF self_confident IS middle AND other_imps IS middle AND own_imps IS middle THEN img_effect IS middle;\n");
		sb.append("\tRULE 90: IF self_confident IS middle AND other_imps IS middle AND own_imps IS high THEN rep_effect IS middle;\n");
		sb.append("\tRULE 91: IF self_confident IS middle AND other_imps IS middle AND own_imps IS high THEN img_effect IS high;\n");
		
		sb.append("\tRULE 92: IF self_confident IS middle AND other_imps IS high AND own_imps IS none THEN rep_effect IS high;\n");
		sb.append("\tRULE 93: IF self_confident IS middle AND other_imps IS high AND own_imps IS none THEN img_effect IS low;\n");
		sb.append("\tRULE 94: IF self_confident IS middle AND other_imps IS high AND own_imps IS low THEN rep_effect IS high;\n");
		sb.append("\tRULE 95: IF self_confident IS middle AND other_imps IS high AND own_imps IS low THEN img_effect IS middle;\n");
		sb.append("\tRULE 96: IF self_confident IS middle AND other_imps IS high AND own_imps IS middle THEN rep_effect IS high;\n");
		sb.append("\tRULE 97: IF self_confident IS middle AND other_imps IS high AND own_imps IS middle THEN img_effect IS high;\n");
		sb.append("\tRULE 98: IF self_confident IS middle AND other_imps IS high AND own_imps IS high THEN rep_effect IS high;\n");
		sb.append("\tRULE 99: IF self_confident IS middle AND other_imps IS high AND own_imps IS high THEN img_effect IS high;\n");

		sb.append("\tRULE 100: IF self_confident IS high AND other_imps IS none AND own_imps IS none THEN rep_effect IS low;\n");
		sb.append("\tRULE 101: IF self_confident IS high AND other_imps IS none AND own_imps IS none THEN img_effect IS low;\n");
		sb.append("\tRULE 102: IF self_confident IS high AND other_imps IS none AND own_imps IS low THEN rep_effect IS low;\n");
		sb.append("\tRULE 103: IF self_confident IS high AND other_imps IS none AND own_imps IS low THEN img_effect IS high;\n");
		sb.append("\tRULE 104: IF self_confident IS high AND other_imps IS none AND own_imps IS middle THEN rep_effect IS low;\n");
		sb.append("\tRULE 105: IF self_confident IS high AND other_imps IS none AND own_imps IS middle THEN img_effect IS high;\n");
		sb.append("\tRULE 106: IF self_confident IS high AND other_imps IS none AND own_imps IS high THEN rep_effect IS low;\n");
		sb.append("\tRULE 107: IF self_confident IS high AND other_imps IS none AND own_imps IS high THEN img_effect IS high;\n");
		
		sb.append("\tRULE 108: IF self_confident IS high AND other_imps IS low AND own_imps IS none THEN rep_effect IS high;\n");
		sb.append("\tRULE 109: IF self_confident IS high AND other_imps IS low AND own_imps IS none THEN img_effect IS low;\n");
		sb.append("\tRULE 110: IF self_confident IS high AND other_imps IS low AND own_imps IS low THEN rep_effect IS low;\n");
		sb.append("\tRULE 111: IF self_confident IS high AND other_imps IS low AND own_imps IS low THEN img_effect IS high;\n");
		sb.append("\tRULE 112: IF self_confident IS high AND other_imps IS low AND own_imps IS middle THEN rep_effect IS low;\n");
		sb.append("\tRULE 113: IF self_confident IS high AND other_imps IS low AND own_imps IS middle THEN img_effect IS high;\n");
		sb.append("\tRULE 114: IF self_confident IS high AND other_imps IS low AND own_imps IS high THEN rep_effect IS low;\n");
		sb.append("\tRULE 115: IF self_confident IS high AND other_imps IS low AND own_imps IS high THEN img_effect IS high;\n");
		
		sb.append("\tRULE 116: IF self_confident IS high AND other_imps IS middle AND own_imps IS none THEN rep_effect IS high;\n");
		sb.append("\tRULE 117: IF self_confident IS high AND other_imps IS middle AND own_imps IS none THEN img_effect IS low;\n");
		sb.append("\tRULE 118: IF self_confident IS high AND other_imps IS middle AND own_imps IS low THEN rep_effect IS middle;\n");
		sb.append("\tRULE 119: IF self_confident IS high AND other_imps IS middle AND own_imps IS low THEN img_effect IS high;\n");
		sb.append("\tRULE 120: IF self_confident IS high AND other_imps IS middle AND own_imps IS middle THEN rep_effect IS middle;\n");
		sb.append("\tRULE 121: IF self_confident IS high AND other_imps IS middle AND own_imps IS middle THEN img_effect IS high;\n");
		sb.append("\tRULE 122: IF self_confident IS high AND other_imps IS middle AND own_imps IS high THEN rep_effect IS middle;\n");
		sb.append("\tRULE 123: IF self_confident IS high AND other_imps IS middle AND own_imps IS high THEN img_effect IS high;\n");
		
		sb.append("\tRULE 124: IF self_confident IS high AND other_imps IS high AND own_imps IS none THEN rep_effect IS high;\n");
		sb.append("\tRULE 125: IF self_confident IS high AND other_imps IS high AND own_imps IS none THEN img_effect IS low;\n");
		sb.append("\tRULE 126: IF self_confident IS high AND other_imps IS high AND own_imps IS low THEN rep_effect IS middle;\n");
		sb.append("\tRULE 127: IF self_confident IS high AND other_imps IS high AND own_imps IS low THEN img_effect IS high;\n");
		sb.append("\tRULE 128: IF self_confident IS high AND other_imps IS high AND own_imps IS middle THEN rep_effect IS middle;\n");
		sb.append("\tRULE 129: IF self_confident IS high AND other_imps IS high AND own_imps IS middle THEN img_effect IS high;\n");
		sb.append("\tRULE 130: IF self_confident IS high AND other_imps IS high AND own_imps IS high THEN rep_effect IS middle;\n");
		sb.append("\tRULE 131: IF self_confident IS high AND other_imps IS high AND own_imps IS high THEN img_effect IS high;\n");
		
		sb.append("END_RULEBLOCK\n");
		sb.append("END_FUNCTION_BLOCK\n");
		
		return sb.toString();
	}
}