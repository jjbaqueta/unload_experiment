package scenario_Marketplace.environments;

import java.io.FileWriter;
import java.io.IOException;

import scenario_Marketplace.enums.FilePaths;
import trustModel.repAndImg.enums.Mnemonic;

public class MarketFuzzyConfig 
{
	// Defining variable urgency
	public static Double min_urgency;
	public static Double max_urgency;
	
	// Defining the variable self-confident (percentage of agents that are self-confident)
	public static Double min_confident;
	public static Double max_confident;
	
	// Defining the variable number of own impressions
	public static Integer min_own_impressions;
	public static Integer max_own_impressions;
	
	// Defining the variable number of third part impressions
	public static Integer min_other_impressions;
	public static Integer max_other_impressions;
	
	// Defining the variable number of references
	public static Integer min_references;
	public static Integer max_references;
	
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
	
	private static void setReferences()
	{
		min_references = 0;
		max_references = 1;
	}
	
	/**
	 * @param ownImpITM: intimate level of interactions for own impressions
	 */
	private static void setNumOwnImpressions(int ownImpITM)
	{
		min_own_impressions = 1;
		max_own_impressions = ownImpITM;
	}
	
	/**
	 * @param otherImpitm: intimate level of interactions for other impressions
	 */
	private static void setNumOtherImpressions(int otherImpITM)
	{
		min_other_impressions = 1;
		max_other_impressions = otherImpITM;
	}
	
	public static void createFuzzyFile(int ownImpITM, int otherImpITM)
	{
		setUrgency();
		setSelfConfident();
		setReferences();
		setNumOwnImpressions(ownImpITM);
		setNumOtherImpressions(otherImpITM);
		
		StringBuffer sb = new StringBuffer();
		
		// Inputs
		sb.append("FUNCTION_BLOCK marketplace\n\n");
		sb.append("VAR_INPUT\n");
		sb.append("\t"+ Mnemonic.URGENCY.getMnemonic() +" : REAL;\n");
		sb.append("\t" + Mnemonic.OWN_IMPS.getMnemonic() + " : REAL;\n");
		sb.append("\t" + Mnemonic.OTHER_IMPS.getMnemonic() + " : REAL;\n");
		sb.append("\t" + Mnemonic.SELFCONFIDENT.getMnemonic() + " : REAL;\n");
		sb.append("\t" + Mnemonic.REFERENCES.getMnemonic() + " : REAL;\n");
		sb.append("END_VAR\n\n");
		
		// Output
		sb.append("VAR_OUTPUT\n");
		sb.append("\t" + Mnemonic.ABILITY_EFFECT.getMnemonic() + " : REAL;\n");
		sb.append("\t" + Mnemonic.AVAILABILITY_EFFECT.getMnemonic() + " : REAL;\n");
		sb.append("\t" + Mnemonic.KNOWHOW_EFFECT.getMnemonic() + " : REAL;\n");
		sb.append("\t" + Mnemonic.REASONING_EFFECT.getMnemonic() + " : REAL;\n");
		sb.append("\t" + Mnemonic.REP_EFFECT.getMnemonic() + " : REAL;\n");
		sb.append("\t" + Mnemonic.IMG_EFFECT.getMnemonic() + " : REAL;\n");
		sb.append("END_VAR\n\n");
		
		sb.append(getInputFuzzify3Block(Mnemonic.URGENCY.getMnemonic(), min_urgency, max_urgency));
		sb.append(getInputFuzzify3Block(Mnemonic.SELFCONFIDENT.getMnemonic(), min_confident, max_confident));
		sb.append(getInputFuzzify4Block(Mnemonic.OWN_IMPS.getMnemonic(), min_own_impressions, max_own_impressions));
		sb.append(getInputFuzzify4Block(Mnemonic.OTHER_IMPS.getMnemonic(), min_other_impressions, max_other_impressions));
		sb.append(getInputFuzzify2Block(Mnemonic.REFERENCES.getMnemonic(), min_references, max_references));
		
		sb.append(getOutputFuzzifyBlock(Mnemonic.ABILITY_EFFECT.getMnemonic()));
		sb.append(getOutputFuzzifyBlock(Mnemonic.AVAILABILITY_EFFECT.getMnemonic()));
		sb.append(getOutputFuzzifyBlock(Mnemonic.KNOWHOW_EFFECT.getMnemonic()));
		sb.append(getOutputFuzzifyBlock(Mnemonic.REASONING_EFFECT.getMnemonic()));
		sb.append(getOutputFuzzifyBlock(Mnemonic.REP_EFFECT.getMnemonic()));
		sb.append(getOutputFuzzifyBlock(Mnemonic.IMG_EFFECT.getMnemonic()));

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
	
	private static String getInputFuzzify2Block(String varName, double min, double max)
	{	
		StringBuffer sb = new StringBuffer();
		
		sb.append("FUZZIFY " + varName).append("\n");
		sb.append("\tTERM none := (" + min +", 1) (" + max +", 0);\n");
		sb.append("\tTERM has := (" + min + ", 0) (" + max + ", 1);\n");
		sb.append("END_FUZZIFY\n\n");
		
		return sb.toString();
	}
	
	private static String getInputFuzzify3Block(String varName, double min, double max)
	{
		StringBuffer sb = new StringBuffer();
		
		sb.append("FUZZIFY " + varName).append("\n");
		sb.append("\tTERM low := ("+ -1.0 +", 1) ("+ -0.75 +", 1) ("+ -0.25 +", 0);\n");
		sb.append("\tTERM middle := ("+ -0.5 +", 0) ("+ 0.0 +", 1) ("+ 0.5 +", 0);\n");
		sb.append("\tTERM high := ("+ 0.25 +", 0) ("+ 0.5 +", 1) ("+ 1.0 +", 1);\n");
		sb.append("END_FUZZIFY\n\n");
		
		return sb.toString();
	}
	
	private static String getInputFuzzify4Block(String varName, double min, double max)
	{
		StringBuffer sb = new StringBuffer();
		
		if(min == max)
		{
			sb.append("FUZZIFY " + varName).append("\n");
			sb.append("\tTERM none := (0, 1) (1, 0);\n");
			sb.append("\tTERM low := (0, 0);\n");
			sb.append("\tTERM middle := (0, 0);\n");
			sb.append("\tTERM high := (" + (max/2) + ", 0)(" + max + ", 1);\n");
			sb.append("END_FUZZIFY\n\n");
		}
		else
		{
			double area = max - min;
			sb.append("FUZZIFY " + varName).append("\n");
			sb.append("\tTERM none := (0, 1) (1, 0);\n");
			sb.append("\tTERM low := (0, 0) ("+ min +", 1) ("+ ((area * 0.2) + min) +", 1) ("+ ((area * 0.3) + min) +", 0);\n");
			sb.append("\tTERM middle := ("+ ((area * 0.2) + min) +", 0) ("+ ((area * 0.4) + min) +", 1) ("+ ((area * 0.6) + min) +", 0);\n");
			sb.append("\tTERM high := ("+ ((area * 0.5) + min) +", 0) ("+ ((area * 0.7) + min) +", 1) ("+ max +", 1);\n");
			sb.append("END_FUZZIFY\n\n");
		}
		
		return sb.toString();
	}
	
	/**
	 * This method defuzzify the outputs.
	 * Methods for defuzzification:
	 * METHOD COG: center of gravity.
	 * METHOD COGS: center of gravity singletons.
	 * METHOD COF: center of gravity functions.
	 * METHOD COA: center of area.
	 * METHOD LM: left most max.
	 * METHOD RM: right most max.
	 * @param varName: an output variable.
	 */
	private static String getOutputFuzzifyBlock(String varName)
	{	
		StringBuffer sb = new StringBuffer();
		
		sb.append("DEFUZZIFY " + varName).append("\n");
		sb.append("\tTERM negative := ("+ -1.0 +", 1) ("+ -0.75 +", 1) ("+ -0.25 +", 0);\n");
		sb.append("\tTERM neutral := ("+ -0.5 +", 0) ("+ 0.0 +", 1) ("+ 0.5 +", 0);\n");
		sb.append("\tTERM positive := ("+ 0.25 +", 0) ("+ 0.5 +", 1) ("+ 1.0 +", 1);\n");
		sb.append("\tMETHOD : RM;\n");
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
		
		// Defining availability and ability effects
		sb.append("\tRULE 1: IF " + Mnemonic.URGENCY.getMnemonic() + " IS low THEN " + Mnemonic.AVAILABILITY_EFFECT.getMnemonic() + " IS neutral;\n");
		sb.append("\tRULE 2: IF " + Mnemonic.URGENCY.getMnemonic() + " IS low THEN " + Mnemonic.ABILITY_EFFECT.getMnemonic() + " IS positive;\n");
		sb.append("\tRULE 3: IF " + Mnemonic.URGENCY.getMnemonic() + " IS middle THEN " + Mnemonic.AVAILABILITY_EFFECT.getMnemonic() + " IS positive;\n");
		sb.append("\tRULE 4: IF " + Mnemonic.URGENCY.getMnemonic() + " IS middle THEN " + Mnemonic.ABILITY_EFFECT.getMnemonic() + " IS positive;\n");
		sb.append("\tRULE 5: IF " + Mnemonic.URGENCY.getMnemonic() + " IS high THEN " + Mnemonic.AVAILABILITY_EFFECT.getMnemonic() + " IS positive;\n");
		sb.append("\tRULE 6: IF " + Mnemonic.URGENCY.getMnemonic() + " IS high THEN " + Mnemonic.ABILITY_EFFECT.getMnemonic() + " IS neutral;\n");				
		
		// Defining knowhow and reasoning effects
		sb.append("\tRULE 7: IF " + Mnemonic.REFERENCES.getMnemonic() + " IS none AND " + Mnemonic.OTHER_IMPS.getMnemonic() + " IS none AND " + Mnemonic.OWN_IMPS.getMnemonic() + " IS none THEN " + Mnemonic.KNOWHOW_EFFECT.getMnemonic() + " IS negative;\n");
		sb.append("\tRULE 8: IF " + Mnemonic.REFERENCES.getMnemonic() + " IS none AND " + Mnemonic.OTHER_IMPS.getMnemonic() + " IS none AND " + Mnemonic.OWN_IMPS.getMnemonic() + " IS none THEN " + Mnemonic.REASONING_EFFECT.getMnemonic() + " IS negative;\n");
		sb.append("\tRULE 9: IF " + Mnemonic.REFERENCES.getMnemonic() + " IS none AND " + Mnemonic.OTHER_IMPS.getMnemonic() + " IS none AND " + Mnemonic.OWN_IMPS.getMnemonic() + " IS low THEN " + Mnemonic.KNOWHOW_EFFECT.getMnemonic() + " IS negative;\n");
		sb.append("\tRULE 10: IF " + Mnemonic.REFERENCES.getMnemonic() + " IS none AND " + Mnemonic.OTHER_IMPS.getMnemonic() + " IS none AND " + Mnemonic.OWN_IMPS.getMnemonic() + " IS low THEN " + Mnemonic.REASONING_EFFECT.getMnemonic() + " IS positive;\n");
		sb.append("\tRULE 11: IF " + Mnemonic.REFERENCES.getMnemonic() + " IS none AND " + Mnemonic.OTHER_IMPS.getMnemonic() + " IS none AND " + Mnemonic.OWN_IMPS.getMnemonic() + " IS middle THEN " + Mnemonic.KNOWHOW_EFFECT.getMnemonic() + " IS negative;\n");
		sb.append("\tRULE 12: IF " + Mnemonic.REFERENCES.getMnemonic() + " IS none AND " + Mnemonic.OTHER_IMPS.getMnemonic() + " IS none AND " + Mnemonic.OWN_IMPS.getMnemonic() + " IS middle THEN " + Mnemonic.REASONING_EFFECT.getMnemonic() + " IS positive;\n");
		sb.append("\tRULE 13: IF " + Mnemonic.REFERENCES.getMnemonic() + " IS none AND " + Mnemonic.OTHER_IMPS.getMnemonic() + " IS none AND " + Mnemonic.OWN_IMPS.getMnemonic() + " IS high THEN " + Mnemonic.KNOWHOW_EFFECT.getMnemonic() + " IS negative;\n");
		sb.append("\tRULE 14: IF " + Mnemonic.REFERENCES.getMnemonic() + " IS none AND " + Mnemonic.OTHER_IMPS.getMnemonic() + " IS none AND " + Mnemonic.OWN_IMPS.getMnemonic() + " IS high THEN " + Mnemonic.REASONING_EFFECT.getMnemonic() + " IS positive;\n");
		
		sb.append("\tRULE 15: IF " + Mnemonic.REFERENCES.getMnemonic() + " IS none AND " + Mnemonic.OTHER_IMPS.getMnemonic() + " IS low AND " + Mnemonic.OWN_IMPS.getMnemonic() + " IS none THEN " + Mnemonic.KNOWHOW_EFFECT.getMnemonic() + " IS negative;\n");
		sb.append("\tRULE 16: IF " + Mnemonic.REFERENCES.getMnemonic() + " IS none AND " + Mnemonic.OTHER_IMPS.getMnemonic() + " IS low AND " + Mnemonic.OWN_IMPS.getMnemonic() + " IS none THEN " + Mnemonic.REASONING_EFFECT.getMnemonic() + " IS positive;\n");
		sb.append("\tRULE 17: IF " + Mnemonic.REFERENCES.getMnemonic() + " IS none AND " + Mnemonic.OTHER_IMPS.getMnemonic() + " IS low AND " + Mnemonic.OWN_IMPS.getMnemonic() + " IS low THEN " + Mnemonic.KNOWHOW_EFFECT.getMnemonic() + " IS negative;\n");
		sb.append("\tRULE 18: IF " + Mnemonic.REFERENCES.getMnemonic() + " IS none AND " + Mnemonic.OTHER_IMPS.getMnemonic() + " IS low AND " + Mnemonic.OWN_IMPS.getMnemonic() + " IS low THEN " + Mnemonic.REASONING_EFFECT.getMnemonic() + " IS positive;\n");
		sb.append("\tRULE 19: IF " + Mnemonic.REFERENCES.getMnemonic() + " IS none AND " + Mnemonic.OTHER_IMPS.getMnemonic() + " IS low AND " + Mnemonic.OWN_IMPS.getMnemonic() + " IS middle THEN " + Mnemonic.KNOWHOW_EFFECT.getMnemonic() + " IS negative;\n");
		sb.append("\tRULE 20: IF " + Mnemonic.REFERENCES.getMnemonic() + " IS none AND " + Mnemonic.OTHER_IMPS.getMnemonic() + " IS low AND " + Mnemonic.OWN_IMPS.getMnemonic() + " IS middle THEN " + Mnemonic.REASONING_EFFECT.getMnemonic() + " IS positive;\n");
		sb.append("\tRULE 21: IF " + Mnemonic.REFERENCES.getMnemonic() + " IS none AND " + Mnemonic.OTHER_IMPS.getMnemonic() + " IS low AND " + Mnemonic.OWN_IMPS.getMnemonic() + " IS high THEN " + Mnemonic.KNOWHOW_EFFECT.getMnemonic() + " IS negative;\n");
		sb.append("\tRULE 22: IF " + Mnemonic.REFERENCES.getMnemonic() + " IS none AND " + Mnemonic.OTHER_IMPS.getMnemonic() + " IS low AND " + Mnemonic.OWN_IMPS.getMnemonic() + " IS high THEN " + Mnemonic.REASONING_EFFECT.getMnemonic() + " IS positive;\n");
		
		sb.append("\tRULE 23: IF " + Mnemonic.REFERENCES.getMnemonic() + " IS none AND " + Mnemonic.OTHER_IMPS.getMnemonic() + " IS middle AND " + Mnemonic.OWN_IMPS.getMnemonic() + " IS none THEN " + Mnemonic.KNOWHOW_EFFECT.getMnemonic() + " IS negative;\n");
		sb.append("\tRULE 24: IF " + Mnemonic.REFERENCES.getMnemonic() + " IS none AND " + Mnemonic.OTHER_IMPS.getMnemonic() + " IS middle AND " + Mnemonic.OWN_IMPS.getMnemonic() + " IS none THEN " + Mnemonic.REASONING_EFFECT.getMnemonic() + " IS positive;\n");
		sb.append("\tRULE 25: IF " + Mnemonic.REFERENCES.getMnemonic() + " IS none AND " + Mnemonic.OTHER_IMPS.getMnemonic() + " IS middle AND " + Mnemonic.OWN_IMPS.getMnemonic() + " IS low THEN " + Mnemonic.KNOWHOW_EFFECT.getMnemonic() + " IS negative;\n");
		sb.append("\tRULE 26: IF " + Mnemonic.REFERENCES.getMnemonic() + " IS none AND " + Mnemonic.OTHER_IMPS.getMnemonic() + " IS middle AND " + Mnemonic.OWN_IMPS.getMnemonic() + " IS low THEN " + Mnemonic.REASONING_EFFECT.getMnemonic() + " IS positive;\n");
		sb.append("\tRULE 27: IF " + Mnemonic.REFERENCES.getMnemonic() + " IS none AND " + Mnemonic.OTHER_IMPS.getMnemonic() + " IS middle AND " + Mnemonic.OWN_IMPS.getMnemonic() + " IS middle THEN " + Mnemonic.KNOWHOW_EFFECT.getMnemonic() + " IS negative;\n");
		sb.append("\tRULE 28: IF " + Mnemonic.REFERENCES.getMnemonic() + " IS none AND " + Mnemonic.OTHER_IMPS.getMnemonic() + " IS middle AND " + Mnemonic.OWN_IMPS.getMnemonic() + " IS middle THEN " + Mnemonic.REASONING_EFFECT.getMnemonic() + " IS positive;\n");
		sb.append("\tRULE 29: IF " + Mnemonic.REFERENCES.getMnemonic() + " IS none AND " + Mnemonic.OTHER_IMPS.getMnemonic() + " IS middle AND " + Mnemonic.OWN_IMPS.getMnemonic() + " IS high THEN " + Mnemonic.KNOWHOW_EFFECT.getMnemonic() + " IS negative;\n");
		sb.append("\tRULE 30: IF " + Mnemonic.REFERENCES.getMnemonic() + " IS none AND " + Mnemonic.OTHER_IMPS.getMnemonic() + " IS middle AND " + Mnemonic.OWN_IMPS.getMnemonic() + " IS high THEN " + Mnemonic.REASONING_EFFECT.getMnemonic() + " IS positive;\n");
		
		sb.append("\tRULE 31: IF " + Mnemonic.REFERENCES.getMnemonic() + " IS none AND " + Mnemonic.OTHER_IMPS.getMnemonic() + " IS high AND " + Mnemonic.OWN_IMPS.getMnemonic() + " IS none THEN " + Mnemonic.KNOWHOW_EFFECT.getMnemonic() + " IS negative;\n");
		sb.append("\tRULE 32: IF " + Mnemonic.REFERENCES.getMnemonic() + " IS none AND " + Mnemonic.OTHER_IMPS.getMnemonic() + " IS high AND " + Mnemonic.OWN_IMPS.getMnemonic() + " IS none THEN " + Mnemonic.REASONING_EFFECT.getMnemonic() + " IS positive;\n");
		sb.append("\tRULE 33: IF " + Mnemonic.REFERENCES.getMnemonic() + " IS none AND " + Mnemonic.OTHER_IMPS.getMnemonic() + " IS high AND " + Mnemonic.OWN_IMPS.getMnemonic() + " IS low THEN " + Mnemonic.KNOWHOW_EFFECT.getMnemonic() + " IS negative;\n");
		sb.append("\tRULE 34: IF " + Mnemonic.REFERENCES.getMnemonic() + " IS none AND " + Mnemonic.OTHER_IMPS.getMnemonic() + " IS high AND " + Mnemonic.OWN_IMPS.getMnemonic() + " IS low THEN " + Mnemonic.REASONING_EFFECT.getMnemonic() + " IS positive;\n");
		sb.append("\tRULE 35: IF " + Mnemonic.REFERENCES.getMnemonic() + " IS none AND " + Mnemonic.OTHER_IMPS.getMnemonic() + " IS high AND " + Mnemonic.OWN_IMPS.getMnemonic() + " IS middle THEN " + Mnemonic.KNOWHOW_EFFECT.getMnemonic() + " IS negative;\n");
		sb.append("\tRULE 36: IF " + Mnemonic.REFERENCES.getMnemonic() + " IS none AND " + Mnemonic.OTHER_IMPS.getMnemonic() + " IS high AND " + Mnemonic.OWN_IMPS.getMnemonic() + " IS middle THEN " + Mnemonic.REASONING_EFFECT.getMnemonic() + " IS positive;\n");
		sb.append("\tRULE 37: IF " + Mnemonic.REFERENCES.getMnemonic() + " IS none AND " + Mnemonic.OTHER_IMPS.getMnemonic() + " IS high AND " + Mnemonic.OWN_IMPS.getMnemonic() + " IS high THEN " + Mnemonic.KNOWHOW_EFFECT.getMnemonic() + " IS negative;\n");
		sb.append("\tRULE 38: IF " + Mnemonic.REFERENCES.getMnemonic() + " IS none AND " + Mnemonic.OTHER_IMPS.getMnemonic() + " IS high AND " + Mnemonic.OWN_IMPS.getMnemonic() + " IS high THEN " + Mnemonic.REASONING_EFFECT.getMnemonic() + " IS positive;\n");
		
		sb.append("\tRULE 39: IF " + Mnemonic.REFERENCES.getMnemonic() + " IS has AND " + Mnemonic.OTHER_IMPS.getMnemonic() + " IS none AND " + Mnemonic.OWN_IMPS.getMnemonic() + " IS none THEN " + Mnemonic.KNOWHOW_EFFECT.getMnemonic() + " IS positive;\n");
		sb.append("\tRULE 40: IF " + Mnemonic.REFERENCES.getMnemonic() + " IS has AND " + Mnemonic.OTHER_IMPS.getMnemonic() + " IS none AND " + Mnemonic.OWN_IMPS.getMnemonic() + " IS none THEN " + Mnemonic.REASONING_EFFECT.getMnemonic() + " IS negative;\n");
		sb.append("\tRULE 41: IF " + Mnemonic.REFERENCES.getMnemonic() + " IS has AND " + Mnemonic.OTHER_IMPS.getMnemonic() + " IS none AND " + Mnemonic.OWN_IMPS.getMnemonic() + " IS low THEN " + Mnemonic.KNOWHOW_EFFECT.getMnemonic() + " IS positive;\n");
		sb.append("\tRULE 42: IF " + Mnemonic.REFERENCES.getMnemonic() + " IS has AND " + Mnemonic.OTHER_IMPS.getMnemonic() + " IS none AND " + Mnemonic.OWN_IMPS.getMnemonic() + " IS low THEN " + Mnemonic.REASONING_EFFECT.getMnemonic() + " IS positive;\n");
		sb.append("\tRULE 43: IF " + Mnemonic.REFERENCES.getMnemonic() + " IS has AND " + Mnemonic.OTHER_IMPS.getMnemonic() + " IS none AND " + Mnemonic.OWN_IMPS.getMnemonic() + " IS middle THEN " + Mnemonic.KNOWHOW_EFFECT.getMnemonic() + " IS positive;\n");
		sb.append("\tRULE 44: IF " + Mnemonic.REFERENCES.getMnemonic() + " IS has AND " + Mnemonic.OTHER_IMPS.getMnemonic() + " IS none AND " + Mnemonic.OWN_IMPS.getMnemonic() + " IS middle THEN " + Mnemonic.REASONING_EFFECT.getMnemonic() + " IS positive;\n");
		sb.append("\tRULE 45: IF " + Mnemonic.REFERENCES.getMnemonic() + " IS has AND " + Mnemonic.OTHER_IMPS.getMnemonic() + " IS none AND " + Mnemonic.OWN_IMPS.getMnemonic() + " IS high THEN " + Mnemonic.KNOWHOW_EFFECT.getMnemonic() + " IS positive;\n");
		sb.append("\tRULE 46: IF " + Mnemonic.REFERENCES.getMnemonic() + " IS has AND " + Mnemonic.OTHER_IMPS.getMnemonic() + " IS none AND " + Mnemonic.OWN_IMPS.getMnemonic() + " IS high THEN " + Mnemonic.REASONING_EFFECT.getMnemonic() + " IS positive;\n");
		
		sb.append("\tRULE 47: IF " + Mnemonic.REFERENCES.getMnemonic() + " IS has AND " + Mnemonic.OTHER_IMPS.getMnemonic() + " IS low AND " + Mnemonic.OWN_IMPS.getMnemonic() + " IS none THEN " + Mnemonic.KNOWHOW_EFFECT.getMnemonic() + " IS positive;\n");
		sb.append("\tRULE 48: IF " + Mnemonic.REFERENCES.getMnemonic() + " IS has AND " + Mnemonic.OTHER_IMPS.getMnemonic() + " IS low AND " + Mnemonic.OWN_IMPS.getMnemonic() + " IS none THEN " + Mnemonic.REASONING_EFFECT.getMnemonic() + " IS positive;\n");
		sb.append("\tRULE 49: IF " + Mnemonic.REFERENCES.getMnemonic() + " IS has AND " + Mnemonic.OTHER_IMPS.getMnemonic() + " IS low AND " + Mnemonic.OWN_IMPS.getMnemonic() + " IS low THEN " + Mnemonic.KNOWHOW_EFFECT.getMnemonic() + " IS positive;\n");
		sb.append("\tRULE 50: IF " + Mnemonic.REFERENCES.getMnemonic() + " IS has AND " + Mnemonic.OTHER_IMPS.getMnemonic() + " IS low AND " + Mnemonic.OWN_IMPS.getMnemonic() + " IS low THEN " + Mnemonic.REASONING_EFFECT.getMnemonic() + " IS positive;\n");
		sb.append("\tRULE 51: IF " + Mnemonic.REFERENCES.getMnemonic() + " IS has AND " + Mnemonic.OTHER_IMPS.getMnemonic() + " IS low AND " + Mnemonic.OWN_IMPS.getMnemonic() + " IS middle THEN " + Mnemonic.KNOWHOW_EFFECT.getMnemonic() + " IS positive;\n");
		sb.append("\tRULE 52: IF " + Mnemonic.REFERENCES.getMnemonic() + " IS has AND " + Mnemonic.OTHER_IMPS.getMnemonic() + " IS low AND " + Mnemonic.OWN_IMPS.getMnemonic() + " IS middle THEN " + Mnemonic.REASONING_EFFECT.getMnemonic() + " IS positive;\n");
		sb.append("\tRULE 53: IF " + Mnemonic.REFERENCES.getMnemonic() + " IS has AND " + Mnemonic.OTHER_IMPS.getMnemonic() + " IS low AND " + Mnemonic.OWN_IMPS.getMnemonic() + " IS high THEN " + Mnemonic.KNOWHOW_EFFECT.getMnemonic() + " IS positive;\n");
		sb.append("\tRULE 54: IF " + Mnemonic.REFERENCES.getMnemonic() + " IS has AND " + Mnemonic.OTHER_IMPS.getMnemonic() + " IS low AND " + Mnemonic.OWN_IMPS.getMnemonic() + " IS high THEN " + Mnemonic.REASONING_EFFECT.getMnemonic() + " IS positive;\n");
		
		sb.append("\tRULE 55: IF " + Mnemonic.REFERENCES.getMnemonic() + " IS has AND " + Mnemonic.OTHER_IMPS.getMnemonic() + " IS middle AND " + Mnemonic.OWN_IMPS.getMnemonic() + " IS none THEN " + Mnemonic.KNOWHOW_EFFECT.getMnemonic() + " IS positive;\n");
		sb.append("\tRULE 56: IF " + Mnemonic.REFERENCES.getMnemonic() + " IS has AND " + Mnemonic.OTHER_IMPS.getMnemonic() + " IS middle AND " + Mnemonic.OWN_IMPS.getMnemonic() + " IS none THEN " + Mnemonic.REASONING_EFFECT.getMnemonic() + " IS positive;\n");
		sb.append("\tRULE 57: IF " + Mnemonic.REFERENCES.getMnemonic() + " IS has AND " + Mnemonic.OTHER_IMPS.getMnemonic() + " IS middle AND " + Mnemonic.OWN_IMPS.getMnemonic() + " IS low THEN " + Mnemonic.KNOWHOW_EFFECT.getMnemonic() + " IS positive;\n");
		sb.append("\tRULE 58: IF " + Mnemonic.REFERENCES.getMnemonic() + " IS has AND " + Mnemonic.OTHER_IMPS.getMnemonic() + " IS middle AND " + Mnemonic.OWN_IMPS.getMnemonic() + " IS low THEN " + Mnemonic.REASONING_EFFECT.getMnemonic() + " IS positive;\n");
		sb.append("\tRULE 59: IF " + Mnemonic.REFERENCES.getMnemonic() + " IS has AND " + Mnemonic.OTHER_IMPS.getMnemonic() + " IS middle AND " + Mnemonic.OWN_IMPS.getMnemonic() + " IS middle THEN " + Mnemonic.KNOWHOW_EFFECT.getMnemonic() + " IS positive;\n");
		sb.append("\tRULE 60: IF " + Mnemonic.REFERENCES.getMnemonic() + " IS has AND " + Mnemonic.OTHER_IMPS.getMnemonic() + " IS middle AND " + Mnemonic.OWN_IMPS.getMnemonic() + " IS middle THEN " + Mnemonic.REASONING_EFFECT.getMnemonic() + " IS positive;\n");
		sb.append("\tRULE 61: IF " + Mnemonic.REFERENCES.getMnemonic() + " IS has AND " + Mnemonic.OTHER_IMPS.getMnemonic() + " IS middle AND " + Mnemonic.OWN_IMPS.getMnemonic() + " IS high THEN " + Mnemonic.KNOWHOW_EFFECT.getMnemonic() + " IS positive;\n");
		sb.append("\tRULE 62: IF " + Mnemonic.REFERENCES.getMnemonic() + " IS has AND " + Mnemonic.OTHER_IMPS.getMnemonic() + " IS middle AND " + Mnemonic.OWN_IMPS.getMnemonic() + " IS high THEN " + Mnemonic.REASONING_EFFECT.getMnemonic() + " IS positive;\n");
		
		sb.append("\tRULE 63: IF " + Mnemonic.REFERENCES.getMnemonic() + " IS has AND " + Mnemonic.OTHER_IMPS.getMnemonic() + " IS high AND " + Mnemonic.OWN_IMPS.getMnemonic() + " IS none THEN " + Mnemonic.KNOWHOW_EFFECT.getMnemonic() + " IS positive;\n");
		sb.append("\tRULE 64: IF " + Mnemonic.REFERENCES.getMnemonic() + " IS has AND " + Mnemonic.OTHER_IMPS.getMnemonic() + " IS high AND " + Mnemonic.OWN_IMPS.getMnemonic() + " IS none THEN " + Mnemonic.REASONING_EFFECT.getMnemonic() + " IS positive;\n");
		sb.append("\tRULE 65: IF " + Mnemonic.REFERENCES.getMnemonic() + " IS has AND " + Mnemonic.OTHER_IMPS.getMnemonic() + " IS high AND " + Mnemonic.OWN_IMPS.getMnemonic() + " IS low THEN " + Mnemonic.KNOWHOW_EFFECT.getMnemonic() + " IS positive;\n");
		sb.append("\tRULE 66: IF " + Mnemonic.REFERENCES.getMnemonic() + " IS has AND " + Mnemonic.OTHER_IMPS.getMnemonic() + " IS high AND " + Mnemonic.OWN_IMPS.getMnemonic() + " IS low THEN " + Mnemonic.REASONING_EFFECT.getMnemonic() + " IS positive;\n");
		sb.append("\tRULE 67: IF " + Mnemonic.REFERENCES.getMnemonic() + " IS has AND " + Mnemonic.OTHER_IMPS.getMnemonic() + " IS high AND " + Mnemonic.OWN_IMPS.getMnemonic() + " IS middle THEN " + Mnemonic.KNOWHOW_EFFECT.getMnemonic() + " IS positive;\n");
		sb.append("\tRULE 68: IF " + Mnemonic.REFERENCES.getMnemonic() + " IS has AND " + Mnemonic.OTHER_IMPS.getMnemonic() + " IS high AND " + Mnemonic.OWN_IMPS.getMnemonic() + " IS middle THEN " + Mnemonic.REASONING_EFFECT.getMnemonic() + " IS positive;\n");
		sb.append("\tRULE 69: IF " + Mnemonic.REFERENCES.getMnemonic() + " IS has AND " + Mnemonic.OTHER_IMPS.getMnemonic() + " IS high AND " + Mnemonic.OWN_IMPS.getMnemonic() + " IS high THEN " + Mnemonic.KNOWHOW_EFFECT.getMnemonic() + " IS positive;\n");
		sb.append("\tRULE 70: IF " + Mnemonic.REFERENCES.getMnemonic() + " IS has AND " + Mnemonic.OTHER_IMPS.getMnemonic() + " IS high AND " + Mnemonic.OWN_IMPS.getMnemonic() + " IS high THEN " + Mnemonic.REASONING_EFFECT.getMnemonic() + " IS positive;\n");
		
		// Defining reputation and image effects
		sb.append("\tRULE 71: IF " + Mnemonic.SELFCONFIDENT.getMnemonic() + " IS low AND " + Mnemonic.OTHER_IMPS.getMnemonic() + " IS none AND " + Mnemonic.OWN_IMPS.getMnemonic() + " IS none THEN " + Mnemonic.REP_EFFECT.getMnemonic() + " IS negative;\n");
		sb.append("\tRULE 72: IF " + Mnemonic.SELFCONFIDENT.getMnemonic() + " IS low AND " + Mnemonic.OTHER_IMPS.getMnemonic() + " IS none AND " + Mnemonic.OWN_IMPS.getMnemonic() + " IS none THEN " + Mnemonic.IMG_EFFECT.getMnemonic() + " IS neutral;\n");
		sb.append("\tRULE 73: IF " + Mnemonic.SELFCONFIDENT.getMnemonic() + " IS low AND " + Mnemonic.OTHER_IMPS.getMnemonic() + " IS none AND " + Mnemonic.OWN_IMPS.getMnemonic() + " IS low THEN " + Mnemonic.REP_EFFECT.getMnemonic() + " IS negative;\n");
		sb.append("\tRULE 74: IF " + Mnemonic.SELFCONFIDENT.getMnemonic() + " IS low AND " + Mnemonic.OTHER_IMPS.getMnemonic() + " IS none AND " + Mnemonic.OWN_IMPS.getMnemonic() + " IS low THEN " + Mnemonic.IMG_EFFECT.getMnemonic() + " IS neutral;\n");
		sb.append("\tRULE 75: IF " + Mnemonic.SELFCONFIDENT.getMnemonic() + " IS low AND " + Mnemonic.OTHER_IMPS.getMnemonic() + " IS none AND " + Mnemonic.OWN_IMPS.getMnemonic() + " IS middle THEN " + Mnemonic.REP_EFFECT.getMnemonic() + " IS negative;\n");
		sb.append("\tRULE 76: IF " + Mnemonic.SELFCONFIDENT.getMnemonic() + " IS low AND " + Mnemonic.OTHER_IMPS.getMnemonic() + " IS none AND " + Mnemonic.OWN_IMPS.getMnemonic() + " IS middle THEN " + Mnemonic.IMG_EFFECT.getMnemonic() + " IS neutral;\n");
		sb.append("\tRULE 78: IF " + Mnemonic.SELFCONFIDENT.getMnemonic() + " IS low AND " + Mnemonic.OTHER_IMPS.getMnemonic() + " IS none AND " + Mnemonic.OWN_IMPS.getMnemonic() + " IS high THEN " + Mnemonic.REP_EFFECT.getMnemonic() + " IS negative;\n");
		sb.append("\tRULE 79: IF " + Mnemonic.SELFCONFIDENT.getMnemonic() + " IS low AND " + Mnemonic.OTHER_IMPS.getMnemonic() + " IS none AND " + Mnemonic.OWN_IMPS.getMnemonic() + " IS high THEN " + Mnemonic.IMG_EFFECT.getMnemonic() + " IS neutral;\n");
		
		sb.append("\tRULE 80: IF " + Mnemonic.SELFCONFIDENT.getMnemonic() + " IS low AND " + Mnemonic.OTHER_IMPS.getMnemonic() + " IS low AND " + Mnemonic.OWN_IMPS.getMnemonic() + " IS none THEN " + Mnemonic.REP_EFFECT.getMnemonic() + " IS positive;\n");
		sb.append("\tRULE 81: IF " + Mnemonic.SELFCONFIDENT.getMnemonic() + " IS low AND " + Mnemonic.OTHER_IMPS.getMnemonic() + " IS low AND " + Mnemonic.OWN_IMPS.getMnemonic() + " IS none THEN " + Mnemonic.IMG_EFFECT.getMnemonic() + " IS neutral;\n");
		sb.append("\tRULE 82: IF " + Mnemonic.SELFCONFIDENT.getMnemonic() + " IS low AND " + Mnemonic.OTHER_IMPS.getMnemonic() + " IS low AND " + Mnemonic.OWN_IMPS.getMnemonic() + " IS low THEN " + Mnemonic.REP_EFFECT.getMnemonic() + " IS positive;\n");
		sb.append("\tRULE 83: IF " + Mnemonic.SELFCONFIDENT.getMnemonic() + " IS low AND " + Mnemonic.OTHER_IMPS.getMnemonic() + " IS low AND " + Mnemonic.OWN_IMPS.getMnemonic() + " IS low THEN " + Mnemonic.IMG_EFFECT.getMnemonic() + " IS neutral;\n");
		sb.append("\tRULE 84: IF " + Mnemonic.SELFCONFIDENT.getMnemonic() + " IS low AND " + Mnemonic.OTHER_IMPS.getMnemonic() + " IS low AND " + Mnemonic.OWN_IMPS.getMnemonic() + " IS middle THEN " + Mnemonic.REP_EFFECT.getMnemonic() + " IS positive;\n");
		sb.append("\tRULE 85: IF " + Mnemonic.SELFCONFIDENT.getMnemonic() + " IS low AND " + Mnemonic.OTHER_IMPS.getMnemonic() + " IS low AND " + Mnemonic.OWN_IMPS.getMnemonic() + " IS middle THEN " + Mnemonic.IMG_EFFECT.getMnemonic() + " IS neutral;\n");
		sb.append("\tRULE 86: IF " + Mnemonic.SELFCONFIDENT.getMnemonic() + " IS low AND " + Mnemonic.OTHER_IMPS.getMnemonic() + " IS low AND " + Mnemonic.OWN_IMPS.getMnemonic() + " IS high THEN " + Mnemonic.REP_EFFECT.getMnemonic() + " IS positive;\n");
		sb.append("\tRULE 87: IF " + Mnemonic.SELFCONFIDENT.getMnemonic() + " IS low AND " + Mnemonic.OTHER_IMPS.getMnemonic() + " IS low AND " + Mnemonic.OWN_IMPS.getMnemonic() + " IS high THEN " + Mnemonic.IMG_EFFECT.getMnemonic() + " IS neutral;\n");
		
		sb.append("\tRULE 88: IF " + Mnemonic.SELFCONFIDENT.getMnemonic() + " IS low AND " + Mnemonic.OTHER_IMPS.getMnemonic() + " IS middle AND " + Mnemonic.OWN_IMPS.getMnemonic() + " IS none THEN " + Mnemonic.REP_EFFECT.getMnemonic() + " IS positive;\n");
		sb.append("\tRULE 89: IF " + Mnemonic.SELFCONFIDENT.getMnemonic() + " IS low AND " + Mnemonic.OTHER_IMPS.getMnemonic() + " IS middle AND " + Mnemonic.OWN_IMPS.getMnemonic() + " IS none THEN " + Mnemonic.IMG_EFFECT.getMnemonic() + " IS neutral;\n");
		sb.append("\tRULE 90: IF " + Mnemonic.SELFCONFIDENT.getMnemonic() + " IS low AND " + Mnemonic.OTHER_IMPS.getMnemonic() + " IS middle AND " + Mnemonic.OWN_IMPS.getMnemonic() + " IS low THEN " + Mnemonic.REP_EFFECT.getMnemonic() + " IS positive;\n");
		sb.append("\tRULE 91: IF " + Mnemonic.SELFCONFIDENT.getMnemonic() + " IS low AND " + Mnemonic.OTHER_IMPS.getMnemonic() + " IS middle AND " + Mnemonic.OWN_IMPS.getMnemonic() + " IS low THEN " + Mnemonic.IMG_EFFECT.getMnemonic() + " IS neutral;\n");
		sb.append("\tRULE 92: IF " + Mnemonic.SELFCONFIDENT.getMnemonic() + " IS low AND " + Mnemonic.OTHER_IMPS.getMnemonic() + " IS middle AND " + Mnemonic.OWN_IMPS.getMnemonic() + " IS middle THEN " + Mnemonic.REP_EFFECT.getMnemonic() + " IS positive;\n");
		sb.append("\tRULE 93: IF " + Mnemonic.SELFCONFIDENT.getMnemonic() + " IS low AND " + Mnemonic.OTHER_IMPS.getMnemonic() + " IS middle AND " + Mnemonic.OWN_IMPS.getMnemonic() + " IS middle THEN " + Mnemonic.IMG_EFFECT.getMnemonic() + " IS neutral;\n");
		sb.append("\tRULE 94: IF " + Mnemonic.SELFCONFIDENT.getMnemonic() + " IS low AND " + Mnemonic.OTHER_IMPS.getMnemonic() + " IS middle AND " + Mnemonic.OWN_IMPS.getMnemonic() + " IS high THEN " + Mnemonic.REP_EFFECT.getMnemonic() + " IS positive;\n");
		sb.append("\tRULE 95: IF " + Mnemonic.SELFCONFIDENT.getMnemonic() + " IS low AND " + Mnemonic.OTHER_IMPS.getMnemonic() + " IS middle AND " + Mnemonic.OWN_IMPS.getMnemonic() + " IS high THEN " + Mnemonic.IMG_EFFECT.getMnemonic() + " IS neutral;\n");
		
		sb.append("\tRULE 96: IF " + Mnemonic.SELFCONFIDENT.getMnemonic() + " IS low AND " + Mnemonic.OTHER_IMPS.getMnemonic() + " IS high AND " + Mnemonic.OWN_IMPS.getMnemonic() + " IS none THEN " + Mnemonic.REP_EFFECT.getMnemonic() + " IS positive;\n");
		sb.append("\tRULE 97: IF " + Mnemonic.SELFCONFIDENT.getMnemonic() + " IS low AND " + Mnemonic.OTHER_IMPS.getMnemonic() + " IS high AND " + Mnemonic.OWN_IMPS.getMnemonic() + " IS none THEN " + Mnemonic.IMG_EFFECT.getMnemonic() + " IS neutral;\n");
		sb.append("\tRULE 98: IF " + Mnemonic.SELFCONFIDENT.getMnemonic() + " IS low AND " + Mnemonic.OTHER_IMPS.getMnemonic() + " IS high AND " + Mnemonic.OWN_IMPS.getMnemonic() + " IS low THEN " + Mnemonic.REP_EFFECT.getMnemonic() + " IS positive;\n");
		sb.append("\tRULE 99: IF " + Mnemonic.SELFCONFIDENT.getMnemonic() + " IS low AND " + Mnemonic.OTHER_IMPS.getMnemonic() + " IS high AND " + Mnemonic.OWN_IMPS.getMnemonic() + " IS low THEN " + Mnemonic.IMG_EFFECT.getMnemonic() + " IS neutral;\n");
		sb.append("\tRULE 100: IF " + Mnemonic.SELFCONFIDENT.getMnemonic() + " IS low AND " + Mnemonic.OTHER_IMPS.getMnemonic() + " IS high AND " + Mnemonic.OWN_IMPS.getMnemonic() + " IS middle THEN " + Mnemonic.REP_EFFECT.getMnemonic() + " IS positive;\n");
		sb.append("\tRULE 101: IF " + Mnemonic.SELFCONFIDENT.getMnemonic() + " IS low AND " + Mnemonic.OTHER_IMPS.getMnemonic() + " IS high AND " + Mnemonic.OWN_IMPS.getMnemonic() + " IS middle THEN " + Mnemonic.IMG_EFFECT.getMnemonic() + " IS neutral;\n");
		sb.append("\tRULE 102: IF " + Mnemonic.SELFCONFIDENT.getMnemonic() + " IS low AND " + Mnemonic.OTHER_IMPS.getMnemonic() + " IS high AND " + Mnemonic.OWN_IMPS.getMnemonic() + " IS high THEN " + Mnemonic.REP_EFFECT.getMnemonic() + " IS positive;\n");
		sb.append("\tRULE 103: IF " + Mnemonic.SELFCONFIDENT.getMnemonic() + " IS low AND " + Mnemonic.OTHER_IMPS.getMnemonic() + " IS high AND " + Mnemonic.OWN_IMPS.getMnemonic() + " IS high THEN " + Mnemonic.IMG_EFFECT.getMnemonic() + " IS neutral;\n");
		
		sb.append("\tRULE 104: IF " + Mnemonic.SELFCONFIDENT.getMnemonic() + " IS middle AND " + Mnemonic.OTHER_IMPS.getMnemonic() + " IS none AND " + Mnemonic.OWN_IMPS.getMnemonic() + " IS none THEN " + Mnemonic.REP_EFFECT.getMnemonic() + " IS negative;\n");
		sb.append("\tRULE 105: IF " + Mnemonic.SELFCONFIDENT.getMnemonic() + " IS middle AND " + Mnemonic.OTHER_IMPS.getMnemonic() + " IS none AND " + Mnemonic.OWN_IMPS.getMnemonic() + " IS none THEN " + Mnemonic.IMG_EFFECT.getMnemonic() + " IS negative;\n");
		sb.append("\tRULE 106: IF " + Mnemonic.SELFCONFIDENT.getMnemonic() + " IS middle AND " + Mnemonic.OTHER_IMPS.getMnemonic() + " IS none AND " + Mnemonic.OWN_IMPS.getMnemonic() + " IS low THEN " + Mnemonic.REP_EFFECT.getMnemonic() + " IS negative;\n");
		sb.append("\tRULE 107: IF " + Mnemonic.SELFCONFIDENT.getMnemonic() + " IS middle AND " + Mnemonic.OTHER_IMPS.getMnemonic() + " IS none AND " + Mnemonic.OWN_IMPS.getMnemonic() + " IS low THEN " + Mnemonic.IMG_EFFECT.getMnemonic() + " IS positive;\n");
		sb.append("\tRULE 108: IF " + Mnemonic.SELFCONFIDENT.getMnemonic() + " IS middle AND " + Mnemonic.OTHER_IMPS.getMnemonic() + " IS none AND " + Mnemonic.OWN_IMPS.getMnemonic() + " IS middle THEN " + Mnemonic.REP_EFFECT.getMnemonic() + " IS negative;\n");
		sb.append("\tRULE 109: IF " + Mnemonic.SELFCONFIDENT.getMnemonic() + " IS middle AND " + Mnemonic.OTHER_IMPS.getMnemonic() + " IS none AND " + Mnemonic.OWN_IMPS.getMnemonic() + " IS middle THEN " + Mnemonic.IMG_EFFECT.getMnemonic() + " IS positive;\n");
		sb.append("\tRULE 110: IF " + Mnemonic.SELFCONFIDENT.getMnemonic() + " IS middle AND " + Mnemonic.OTHER_IMPS.getMnemonic() + " IS none AND " + Mnemonic.OWN_IMPS.getMnemonic() + " IS high THEN " + Mnemonic.REP_EFFECT.getMnemonic() + " IS negative;\n");
		sb.append("\tRULE 111: IF " + Mnemonic.SELFCONFIDENT.getMnemonic() + " IS middle AND " + Mnemonic.OTHER_IMPS.getMnemonic() + " IS none AND " + Mnemonic.OWN_IMPS.getMnemonic() + " IS high THEN " + Mnemonic.IMG_EFFECT.getMnemonic() + " IS positive;\n");
		
		sb.append("\tRULE 112: IF " + Mnemonic.SELFCONFIDENT.getMnemonic() + " IS middle AND " + Mnemonic.OTHER_IMPS.getMnemonic() + " IS low AND " + Mnemonic.OWN_IMPS.getMnemonic() + " IS none THEN " + Mnemonic.REP_EFFECT.getMnemonic() + " IS positive;\n");
		sb.append("\tRULE 113: IF " + Mnemonic.SELFCONFIDENT.getMnemonic() + " IS middle AND " + Mnemonic.OTHER_IMPS.getMnemonic() + " IS low AND " + Mnemonic.OWN_IMPS.getMnemonic() + " IS none THEN " + Mnemonic.IMG_EFFECT.getMnemonic() + " IS negative;\n");
		sb.append("\tRULE 114: IF " + Mnemonic.SELFCONFIDENT.getMnemonic() + " IS middle AND " + Mnemonic.OTHER_IMPS.getMnemonic() + " IS low AND " + Mnemonic.OWN_IMPS.getMnemonic() + " IS low THEN " + Mnemonic.REP_EFFECT.getMnemonic() + " IS positive;\n");
		sb.append("\tRULE 115: IF " + Mnemonic.SELFCONFIDENT.getMnemonic() + " IS middle AND " + Mnemonic.OTHER_IMPS.getMnemonic() + " IS low AND " + Mnemonic.OWN_IMPS.getMnemonic() + " IS low THEN " + Mnemonic.IMG_EFFECT.getMnemonic() + " IS positive;\n");
		sb.append("\tRULE 116: IF " + Mnemonic.SELFCONFIDENT.getMnemonic() + " IS middle AND " + Mnemonic.OTHER_IMPS.getMnemonic() + " IS low AND " + Mnemonic.OWN_IMPS.getMnemonic() + " IS middle THEN " + Mnemonic.REP_EFFECT.getMnemonic() + " IS positive;\n");
		sb.append("\tRULE 117: IF " + Mnemonic.SELFCONFIDENT.getMnemonic() + " IS middle AND " + Mnemonic.OTHER_IMPS.getMnemonic() + " IS low AND " + Mnemonic.OWN_IMPS.getMnemonic() + " IS middle THEN " + Mnemonic.IMG_EFFECT.getMnemonic() + " IS positive;\n");
		sb.append("\tRULE 118: IF " + Mnemonic.SELFCONFIDENT.getMnemonic() + " IS middle AND " + Mnemonic.OTHER_IMPS.getMnemonic() + " IS low AND " + Mnemonic.OWN_IMPS.getMnemonic() + " IS high THEN " + Mnemonic.REP_EFFECT.getMnemonic() + " IS positive;\n");
		sb.append("\tRULE 119: IF " + Mnemonic.SELFCONFIDENT.getMnemonic() + " IS middle AND " + Mnemonic.OTHER_IMPS.getMnemonic() + " IS low AND " + Mnemonic.OWN_IMPS.getMnemonic() + " IS high THEN " + Mnemonic.IMG_EFFECT.getMnemonic() + " IS positive;\n");
		
		sb.append("\tRULE 120: IF " + Mnemonic.SELFCONFIDENT.getMnemonic() + " IS middle AND " + Mnemonic.OTHER_IMPS.getMnemonic() + " IS middle AND " + Mnemonic.OWN_IMPS.getMnemonic() + " IS none THEN " + Mnemonic.REP_EFFECT.getMnemonic() + " IS positive;\n");
		sb.append("\tRULE 121: IF " + Mnemonic.SELFCONFIDENT.getMnemonic() + " IS middle AND " + Mnemonic.OTHER_IMPS.getMnemonic() + " IS middle AND " + Mnemonic.OWN_IMPS.getMnemonic() + " IS none THEN " + Mnemonic.IMG_EFFECT.getMnemonic() + " IS negative;\n");
		sb.append("\tRULE 122: IF " + Mnemonic.SELFCONFIDENT.getMnemonic() + " IS middle AND " + Mnemonic.OTHER_IMPS.getMnemonic() + " IS middle AND " + Mnemonic.OWN_IMPS.getMnemonic() + " IS low THEN " + Mnemonic.REP_EFFECT.getMnemonic() + " IS positive;\n");
		sb.append("\tRULE 123: IF " + Mnemonic.SELFCONFIDENT.getMnemonic() + " IS middle AND " + Mnemonic.OTHER_IMPS.getMnemonic() + " IS middle AND " + Mnemonic.OWN_IMPS.getMnemonic() + " IS low THEN " + Mnemonic.IMG_EFFECT.getMnemonic() + " IS positive;\n");
		sb.append("\tRULE 124: IF " + Mnemonic.SELFCONFIDENT.getMnemonic() + " IS middle AND " + Mnemonic.OTHER_IMPS.getMnemonic() + " IS middle AND " + Mnemonic.OWN_IMPS.getMnemonic() + " IS middle THEN " + Mnemonic.REP_EFFECT.getMnemonic() + " IS positive;\n");
		sb.append("\tRULE 125: IF " + Mnemonic.SELFCONFIDENT.getMnemonic() + " IS middle AND " + Mnemonic.OTHER_IMPS.getMnemonic() + " IS middle AND " + Mnemonic.OWN_IMPS.getMnemonic() + " IS middle THEN " + Mnemonic.IMG_EFFECT.getMnemonic() + " IS positive;\n");
		sb.append("\tRULE 126: IF " + Mnemonic.SELFCONFIDENT.getMnemonic() + " IS middle AND " + Mnemonic.OTHER_IMPS.getMnemonic() + " IS middle AND " + Mnemonic.OWN_IMPS.getMnemonic() + " IS high THEN " + Mnemonic.REP_EFFECT.getMnemonic() + " IS positive;\n");
		sb.append("\tRULE 127: IF " + Mnemonic.SELFCONFIDENT.getMnemonic() + " IS middle AND " + Mnemonic.OTHER_IMPS.getMnemonic() + " IS middle AND " + Mnemonic.OWN_IMPS.getMnemonic() + " IS high THEN " + Mnemonic.IMG_EFFECT.getMnemonic() + " IS positive;\n");
		
		sb.append("\tRULE 128: IF " + Mnemonic.SELFCONFIDENT.getMnemonic() + " IS middle AND " + Mnemonic.OTHER_IMPS.getMnemonic() + " IS high AND " + Mnemonic.OWN_IMPS.getMnemonic() + " IS none THEN " + Mnemonic.REP_EFFECT.getMnemonic() + " IS positive;\n");
		sb.append("\tRULE 129: IF " + Mnemonic.SELFCONFIDENT.getMnemonic() + " IS middle AND " + Mnemonic.OTHER_IMPS.getMnemonic() + " IS high AND " + Mnemonic.OWN_IMPS.getMnemonic() + " IS none THEN " + Mnemonic.IMG_EFFECT.getMnemonic() + " IS negative;\n");
		sb.append("\tRULE 130: IF " + Mnemonic.SELFCONFIDENT.getMnemonic() + " IS middle AND " + Mnemonic.OTHER_IMPS.getMnemonic() + " IS high AND " + Mnemonic.OWN_IMPS.getMnemonic() + " IS low THEN " + Mnemonic.REP_EFFECT.getMnemonic() + " IS positive;\n");
		sb.append("\tRULE 131: IF " + Mnemonic.SELFCONFIDENT.getMnemonic() + " IS middle AND " + Mnemonic.OTHER_IMPS.getMnemonic() + " IS high AND " + Mnemonic.OWN_IMPS.getMnemonic() + " IS low THEN " + Mnemonic.IMG_EFFECT.getMnemonic() + " IS positive;\n");
		sb.append("\tRULE 132: IF " + Mnemonic.SELFCONFIDENT.getMnemonic() + " IS middle AND " + Mnemonic.OTHER_IMPS.getMnemonic() + " IS high AND " + Mnemonic.OWN_IMPS.getMnemonic() + " IS middle THEN " + Mnemonic.REP_EFFECT.getMnemonic() + " IS positive;\n");
		sb.append("\tRULE 133: IF " + Mnemonic.SELFCONFIDENT.getMnemonic() + " IS middle AND " + Mnemonic.OTHER_IMPS.getMnemonic() + " IS high AND " + Mnemonic.OWN_IMPS.getMnemonic() + " IS middle THEN " + Mnemonic.IMG_EFFECT.getMnemonic() + " IS positive;\n");
		sb.append("\tRULE 134: IF " + Mnemonic.SELFCONFIDENT.getMnemonic() + " IS middle AND " + Mnemonic.OTHER_IMPS.getMnemonic() + " IS high AND " + Mnemonic.OWN_IMPS.getMnemonic() + " IS high THEN " + Mnemonic.REP_EFFECT.getMnemonic() + " IS positive;\n");
		sb.append("\tRULE 135: IF " + Mnemonic.SELFCONFIDENT.getMnemonic() + " IS middle AND " + Mnemonic.OTHER_IMPS.getMnemonic() + " IS high AND " + Mnemonic.OWN_IMPS.getMnemonic() + " IS high THEN " + Mnemonic.IMG_EFFECT.getMnemonic() + " IS positive;\n");

		sb.append("\tRULE 136: IF " + Mnemonic.SELFCONFIDENT.getMnemonic() + " IS high AND " + Mnemonic.OTHER_IMPS.getMnemonic() + " IS none AND " + Mnemonic.OWN_IMPS.getMnemonic() + " IS none THEN " + Mnemonic.REP_EFFECT.getMnemonic() + " IS neutral;\n");
		sb.append("\tRULE 137: IF " + Mnemonic.SELFCONFIDENT.getMnemonic() + " IS high AND " + Mnemonic.OTHER_IMPS.getMnemonic() + " IS none AND " + Mnemonic.OWN_IMPS.getMnemonic() + " IS none THEN " + Mnemonic.IMG_EFFECT.getMnemonic() + " IS negative;\n");
		sb.append("\tRULE 138: IF " + Mnemonic.SELFCONFIDENT.getMnemonic() + " IS high AND " + Mnemonic.OTHER_IMPS.getMnemonic() + " IS none AND " + Mnemonic.OWN_IMPS.getMnemonic() + " IS low THEN " + Mnemonic.REP_EFFECT.getMnemonic() + " IS neutral;\n");
		sb.append("\tRULE 139: IF " + Mnemonic.SELFCONFIDENT.getMnemonic() + " IS high AND " + Mnemonic.OTHER_IMPS.getMnemonic() + " IS none AND " + Mnemonic.OWN_IMPS.getMnemonic() + " IS low THEN " + Mnemonic.IMG_EFFECT.getMnemonic() + " IS positive;\n");
		sb.append("\tRULE 140: IF " + Mnemonic.SELFCONFIDENT.getMnemonic() + " IS high AND " + Mnemonic.OTHER_IMPS.getMnemonic() + " IS none AND " + Mnemonic.OWN_IMPS.getMnemonic() + " IS middle THEN " + Mnemonic.REP_EFFECT.getMnemonic() + " IS neutral;\n");
		sb.append("\tRULE 141: IF " + Mnemonic.SELFCONFIDENT.getMnemonic() + " IS high AND " + Mnemonic.OTHER_IMPS.getMnemonic() + " IS none AND " + Mnemonic.OWN_IMPS.getMnemonic() + " IS middle THEN " + Mnemonic.IMG_EFFECT.getMnemonic() + " IS positive;\n");
		sb.append("\tRULE 142: IF " + Mnemonic.SELFCONFIDENT.getMnemonic() + " IS high AND " + Mnemonic.OTHER_IMPS.getMnemonic() + " IS none AND " + Mnemonic.OWN_IMPS.getMnemonic() + " IS high THEN " + Mnemonic.REP_EFFECT.getMnemonic() + " IS neutral;\n");
		sb.append("\tRULE 143: IF " + Mnemonic.SELFCONFIDENT.getMnemonic() + " IS high AND " + Mnemonic.OTHER_IMPS.getMnemonic() + " IS none AND " + Mnemonic.OWN_IMPS.getMnemonic() + " IS high THEN " + Mnemonic.IMG_EFFECT.getMnemonic() + " IS positive;\n");
		
		sb.append("\tRULE 144: IF " + Mnemonic.SELFCONFIDENT.getMnemonic() + " IS high AND " + Mnemonic.OTHER_IMPS.getMnemonic() + " IS low AND " + Mnemonic.OWN_IMPS.getMnemonic() + " IS none THEN " + Mnemonic.REP_EFFECT.getMnemonic() + " IS neutral;\n");
		sb.append("\tRULE 145: IF " + Mnemonic.SELFCONFIDENT.getMnemonic() + " IS high AND " + Mnemonic.OTHER_IMPS.getMnemonic() + " IS low AND " + Mnemonic.OWN_IMPS.getMnemonic() + " IS none THEN " + Mnemonic.IMG_EFFECT.getMnemonic() + " IS positive;\n");
		sb.append("\tRULE 146: IF " + Mnemonic.SELFCONFIDENT.getMnemonic() + " IS high AND " + Mnemonic.OTHER_IMPS.getMnemonic() + " IS low AND " + Mnemonic.OWN_IMPS.getMnemonic() + " IS low THEN " + Mnemonic.REP_EFFECT.getMnemonic() + " IS neutral;\n");
		sb.append("\tRULE 147: IF " + Mnemonic.SELFCONFIDENT.getMnemonic() + " IS high AND " + Mnemonic.OTHER_IMPS.getMnemonic() + " IS low AND " + Mnemonic.OWN_IMPS.getMnemonic() + " IS low THEN " + Mnemonic.IMG_EFFECT.getMnemonic() + " IS positive;\n");
		sb.append("\tRULE 148: IF " + Mnemonic.SELFCONFIDENT.getMnemonic() + " IS high AND " + Mnemonic.OTHER_IMPS.getMnemonic() + " IS low AND " + Mnemonic.OWN_IMPS.getMnemonic() + " IS middle THEN " + Mnemonic.REP_EFFECT.getMnemonic() + " IS neutral;\n");
		sb.append("\tRULE 149: IF " + Mnemonic.SELFCONFIDENT.getMnemonic() + " IS high AND " + Mnemonic.OTHER_IMPS.getMnemonic() + " IS low AND " + Mnemonic.OWN_IMPS.getMnemonic() + " IS middle THEN " + Mnemonic.IMG_EFFECT.getMnemonic() + " IS positive;\n");
		sb.append("\tRULE 150: IF " + Mnemonic.SELFCONFIDENT.getMnemonic() + " IS high AND " + Mnemonic.OTHER_IMPS.getMnemonic() + " IS low AND " + Mnemonic.OWN_IMPS.getMnemonic() + " IS high THEN " + Mnemonic.REP_EFFECT.getMnemonic() + " IS neutral;\n");
		sb.append("\tRULE 151: IF " + Mnemonic.SELFCONFIDENT.getMnemonic() + " IS high AND " + Mnemonic.OTHER_IMPS.getMnemonic() + " IS low AND " + Mnemonic.OWN_IMPS.getMnemonic() + " IS high THEN " + Mnemonic.IMG_EFFECT.getMnemonic() + " IS positive;\n");
		
		sb.append("\tRULE 152: IF " + Mnemonic.SELFCONFIDENT.getMnemonic() + " IS high AND " + Mnemonic.OTHER_IMPS.getMnemonic() + " IS middle AND " + Mnemonic.OWN_IMPS.getMnemonic() + " IS none THEN " + Mnemonic.REP_EFFECT.getMnemonic() + " IS neutral;\n");
		sb.append("\tRULE 153: IF " + Mnemonic.SELFCONFIDENT.getMnemonic() + " IS high AND " + Mnemonic.OTHER_IMPS.getMnemonic() + " IS middle AND " + Mnemonic.OWN_IMPS.getMnemonic() + " IS none THEN " + Mnemonic.IMG_EFFECT.getMnemonic() + " IS negative;\n");
		sb.append("\tRULE 154: IF " + Mnemonic.SELFCONFIDENT.getMnemonic() + " IS high AND " + Mnemonic.OTHER_IMPS.getMnemonic() + " IS middle AND " + Mnemonic.OWN_IMPS.getMnemonic() + " IS low THEN " + Mnemonic.REP_EFFECT.getMnemonic() + " IS neutral;\n");
		sb.append("\tRULE 155: IF " + Mnemonic.SELFCONFIDENT.getMnemonic() + " IS high AND " + Mnemonic.OTHER_IMPS.getMnemonic() + " IS middle AND " + Mnemonic.OWN_IMPS.getMnemonic() + " IS low THEN " + Mnemonic.IMG_EFFECT.getMnemonic() + " IS positive;\n");
		sb.append("\tRULE 156: IF " + Mnemonic.SELFCONFIDENT.getMnemonic() + " IS high AND " + Mnemonic.OTHER_IMPS.getMnemonic() + " IS middle AND " + Mnemonic.OWN_IMPS.getMnemonic() + " IS middle THEN " + Mnemonic.REP_EFFECT.getMnemonic() + " IS neutral;\n");
		sb.append("\tRULE 157: IF " + Mnemonic.SELFCONFIDENT.getMnemonic() + " IS high AND " + Mnemonic.OTHER_IMPS.getMnemonic() + " IS middle AND " + Mnemonic.OWN_IMPS.getMnemonic() + " IS middle THEN " + Mnemonic.IMG_EFFECT.getMnemonic() + " IS positive;\n");
		sb.append("\tRULE 158: IF " + Mnemonic.SELFCONFIDENT.getMnemonic() + " IS high AND " + Mnemonic.OTHER_IMPS.getMnemonic() + " IS middle AND " + Mnemonic.OWN_IMPS.getMnemonic() + " IS high THEN " + Mnemonic.REP_EFFECT.getMnemonic() + " IS neutral;\n");
		sb.append("\tRULE 159: IF " + Mnemonic.SELFCONFIDENT.getMnemonic() + " IS high AND " + Mnemonic.OTHER_IMPS.getMnemonic() + " IS middle AND " + Mnemonic.OWN_IMPS.getMnemonic() + " IS high THEN " + Mnemonic.IMG_EFFECT.getMnemonic() + " IS positive;\n");
		
		sb.append("\tRULE 160: IF " + Mnemonic.SELFCONFIDENT.getMnemonic() + " IS high AND " + Mnemonic.OTHER_IMPS.getMnemonic() + " IS high AND " + Mnemonic.OWN_IMPS.getMnemonic() + " IS none THEN " + Mnemonic.REP_EFFECT.getMnemonic() + " IS neutral;\n");
		sb.append("\tRULE 161: IF " + Mnemonic.SELFCONFIDENT.getMnemonic() + " IS high AND " + Mnemonic.OTHER_IMPS.getMnemonic() + " IS high AND " + Mnemonic.OWN_IMPS.getMnemonic() + " IS none THEN " + Mnemonic.IMG_EFFECT.getMnemonic() + " IS negative;\n");
		sb.append("\tRULE 162: IF " + Mnemonic.SELFCONFIDENT.getMnemonic() + " IS high AND " + Mnemonic.OTHER_IMPS.getMnemonic() + " IS high AND " + Mnemonic.OWN_IMPS.getMnemonic() + " IS low THEN " + Mnemonic.REP_EFFECT.getMnemonic() + " IS neutral;\n");
		sb.append("\tRULE 163: IF " + Mnemonic.SELFCONFIDENT.getMnemonic() + " IS high AND " + Mnemonic.OTHER_IMPS.getMnemonic() + " IS high AND " + Mnemonic.OWN_IMPS.getMnemonic() + " IS low THEN " + Mnemonic.IMG_EFFECT.getMnemonic() + " IS positive;\n");
		sb.append("\tRULE 164: IF " + Mnemonic.SELFCONFIDENT.getMnemonic() + " IS high AND " + Mnemonic.OTHER_IMPS.getMnemonic() + " IS high AND " + Mnemonic.OWN_IMPS.getMnemonic() + " IS middle THEN " + Mnemonic.REP_EFFECT.getMnemonic() + " IS neutral;\n");
		sb.append("\tRULE 165: IF " + Mnemonic.SELFCONFIDENT.getMnemonic() + " IS high AND " + Mnemonic.OTHER_IMPS.getMnemonic() + " IS high AND " + Mnemonic.OWN_IMPS.getMnemonic() + " IS middle THEN " + Mnemonic.IMG_EFFECT.getMnemonic() + " IS positive;\n");
		sb.append("\tRULE 166: IF " + Mnemonic.SELFCONFIDENT.getMnemonic() + " IS high AND " + Mnemonic.OTHER_IMPS.getMnemonic() + " IS high AND " + Mnemonic.OWN_IMPS.getMnemonic() + " IS high THEN " + Mnemonic.REP_EFFECT.getMnemonic() + " IS neutral;\n");
		sb.append("\tRULE 167: IF " + Mnemonic.SELFCONFIDENT.getMnemonic() + " IS high AND " + Mnemonic.OTHER_IMPS.getMnemonic() + " IS high AND " + Mnemonic.OWN_IMPS.getMnemonic() + " IS high THEN " + Mnemonic.IMG_EFFECT.getMnemonic() + " IS positive;\n");
		
		sb.append("END_RULEBLOCK\n");
		sb.append("END_FUNCTION_BLOCK\n");
		
		return sb.toString();
	}
}