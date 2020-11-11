package scenario_unloadBoxes.environments;

import java.io.FileWriter;
import java.io.IOException;

public class FuzzySystemConfig 
{
	// Defining variable urgency
	public static Double min_urgency;
	public static Double max_urgency;
	
	// Defining variable number of boxes
	public static Integer min_num_boxes;
	public static Integer max_num_boxes;
	
	// Defining variable self-confident (percentage of agents that are self-confident)
	public static Double self_confident;
	
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
	
	private static void setNumBoxes()
	{
		min_num_boxes = 5;
		max_num_boxes = 20;
	}
	
	private static void setSelfConfident()
	{
		self_confident = 0.5;
	}
	
	private static void setNumOwnImpressions(int numberTruckers, int numberWorkers , int numberHelpers)
	{
		min_own_imps = 0;
		max_own_imps = (numberTruckers * numberWorkers * numberHelpers) / 3;
	}
	
	private static void setNumOtherImpressions(int numberTruckers, int numberWorkers , int numberHelpers)
	{
		min_other_imps = 0;
		max_other_imps = numberTruckers * numberWorkers * numberHelpers;
	}
	
	public static void createFuzzyFile(int numberTruckers, int numberWorkers , int numberHelpers)
	{
		setUrgency();
		setNumBoxes();
		setSelfConfident();
		setNumOwnImpressions(numberTruckers, numberWorkers, numberHelpers);
		setNumOtherImpressions(numberTruckers, numberWorkers, numberHelpers);
		
		StringBuffer sb = new StringBuffer();
		
		// Inputs
		sb.append("FUNCTION_BLOCK unload_scenario\n\n");
		sb.append("VAR_INPUT\n");
		sb.append("\turgency : REAL;\n");
		sb.append("\tnum_boxes : REAL;\n");
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
		
		sb.append(getInputFuzzifyBlock("urgency", min_urgency, max_urgency));
		sb.append(getInputFuzzifyBlock("num_boxes", min_num_boxes, max_num_boxes));
		sb.append(getInputFuzzifyBlock("own_imps", min_own_imps, max_own_imps));
		sb.append(getInputFuzzifyBlock("other_imps", min_other_imps, max_other_imps));
		
		sb.append("FUZZIFY self_confident").append("\n");
		sb.append("\tTERM false := (0.0, 0.0) ;\n");
		sb.append("\tTERM true := (1.0, 1.0);\n");
		sb.append("END_FUZZIFY\n\n");
		
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
			file = new FileWriter("src/java/scenario_unloadBoxes/environments/unloadBoxes.fcl");
			file.write(sb.toString());
			file.close();
		} 
		catch (IOException e) 
		{
			System.out.println(e.getMessage());
			e.printStackTrace();
		}
	}
	
	private static String getInputFuzzifyBlock(String varName, double min, double max)
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
		
		sb.append("\tRULE 1: IF urgency IS low AND num_boxes IS low THEN availability_effect IS low;\n");
		sb.append("\tRULE 2: IF urgency IS low AND num_boxes IS low THEN ability_effect IS high;\n");		
		sb.append("\tRULE 3: IF urgency IS low AND num_boxes IS middle THEN ability_effect IS high;\n");
		sb.append("\tRULE 4: IF urgency IS low AND num_boxes IS middle THEN availability_effect IS low;\n");
		sb.append("\tRULE 5: IF urgency IS low AND num_boxes IS high THEN ability_effect IS high;\n");
		sb.append("\tRULE 6: IF urgency IS low AND num_boxes IS high THEN availability_effect IS low;\n");
		sb.append("\tRULE 7: IF urgency IS middle AND num_boxes IS low THEN ability_effect IS middle;\n");
		sb.append("\tRULE 8: IF urgency IS middle AND num_boxes IS low THEN availability_effect IS middle;\n");
		sb.append("\tRULE 9: IF urgency IS middle AND num_boxes IS middle THEN ability_effect IS middle;\n");
		sb.append("\tRULE 10: IF urgency IS middle AND num_boxes IS middle THEN availability_effect IS high;\n");
		sb.append("\tRULE 11: IF urgency IS middle AND num_boxes IS high THEN ability_effect IS middle;\n");
		sb.append("\tRULE 12: IF urgency IS middle AND num_boxes IS high THEN availability_effect IS high;\n");
		sb.append("\tRULE 13: IF urgency IS high AND num_boxes IS low THEN ability_effect IS middle;\n");
		sb.append("\tRULE 14: IF urgency IS high AND num_boxes IS low THEN availability_effect IS high;\n");
		sb.append("\tRULE 15: IF urgency IS high AND num_boxes IS middle THEN ability_effect IS low;\n");
		sb.append("\tRULE 16: IF urgency IS high AND num_boxes IS middle THEN availability_effect IS high;\n");
		sb.append("\tRULE 17: IF urgency IS high AND num_boxes IS high THEN ability_effect IS low;\n");
		sb.append("\tRULE 18: IF urgency IS high AND num_boxes IS high THEN availability_effect IS high;\n");
		sb.append("\tRULE 19: IF other_imps IS low AND own_imps IS low THEN knowhow_effect IS high;\n");
		sb.append("\tRULE 20: IF other_imps IS low AND own_imps IS low THEN ir_effect IS low;\n");		
		sb.append("\tRULE 21: IF other_imps IS low AND own_imps IS middle THEN knowhow_effect IS high;\n");
		sb.append("\tRULE 22: IF other_imps IS low AND own_imps IS middle THEN ir_effect IS middle;\n");
		sb.append("\tRULE 23: IF other_imps IS low AND own_imps IS high THEN knowhow_effect IS high;\n");
		sb.append("\tRULE 24: IF other_imps IS low AND own_imps IS high THEN ir_effect IS low;\n");
		sb.append("\tRULE 25: IF other_imps IS middle AND own_imps IS low THEN knowhow_effect IS middle;\n");
		sb.append("\tRULE 26: IF other_imps IS middle AND own_imps IS low THEN ir_effect IS middle;\n");
		sb.append("\tRULE 27: IF other_imps IS middle AND own_imps IS middle THEN knowhow_effect IS high;\n");
		sb.append("\tRULE 28: IF other_imps IS middle AND own_imps IS middle THEN ir_effect IS middle;\n");
		sb.append("\tRULE 29: IF other_imps IS middle AND own_imps IS high THEN knowhow_effect IS middle;\n");
		sb.append("\tRULE 30: IF other_imps IS middle AND own_imps IS high THEN ir_effect IS middle;\n");
		sb.append("\tRULE 31: IF other_imps IS high AND own_imps IS low THEN knowhow_effect IS low;\n");
		sb.append("\tRULE 32: IF other_imps IS high AND own_imps IS low THEN ir_effect IS high;\n");
		sb.append("\tRULE 33: IF other_imps IS high AND own_imps IS middle THEN knowhow_effect IS low;\n");
		sb.append("\tRULE 34: IF other_imps IS high AND own_imps IS middle THEN ir_effect IS middle;\n");
		sb.append("\tRULE 35: IF other_imps IS high AND own_imps IS high THEN knowhow_effect IS low;\n");
		sb.append("\tRULE 36: IF other_imps IS high AND own_imps IS high THEN ir_effect IS high;\n");
		sb.append("\tRULE 37: IF self_confident IS true AND other_imps IS low AND own_imps IS low THEN rep_effect IS low;\n");
		sb.append("\tRULE 38: IF self_confident IS true AND other_imps IS low AND own_imps IS low THEN img_effect IS high;\n");
		sb.append("\tRULE 39: IF self_confident IS true AND other_imps IS low AND own_imps IS middle THEN img_effect IS middle;\n");
		sb.append("\tRULE 40: IF self_confident IS true AND other_imps IS low AND own_imps IS middle THEN rep_effect IS low;\n");
		sb.append("\tRULE 41: IF self_confident IS true AND other_imps IS low AND own_imps IS high THEN rep_effect IS low;\n");
		sb.append("\tRULE 42: IF self_confident IS true AND other_imps IS low AND own_imps IS high THEN img_effect IS high;\n");
		sb.append("\tRULE 43: IF self_confident IS true AND other_imps IS middle AND own_imps IS low THEN rep_effect IS middle;\n");
		sb.append("\tRULE 44: IF self_confident IS true AND other_imps IS middle AND own_imps IS low THEN img_effect IS low;\n");
		sb.append("\tRULE 45: IF self_confident IS true AND other_imps IS middle AND own_imps IS middle THEN rep_effect IS low;\n");
		sb.append("\tRULE 46: IF self_confident IS true AND other_imps IS middle AND own_imps IS middle THEN img_effect IS high;\n");
		sb.append("\tRULE 47: IF self_confident IS true AND other_imps IS middle AND own_imps IS high THEN rep_effect IS middle;\n");
		sb.append("\tRULE 48: IF self_confident IS true AND other_imps IS middle AND own_imps IS high THEN img_effect IS high;\n");
		sb.append("\tRULE 49: IF self_confident IS true AND other_imps IS high AND own_imps IS low THEN rep_effect IS high;\n");
		sb.append("\tRULE 50: IF self_confident IS true AND other_imps IS high AND own_imps IS low THEN img_effect IS low;\n");
		sb.append("\tRULE 51: IF self_confident IS true AND other_imps IS high AND own_imps IS middle THEN rep_effect IS high;\n");
		sb.append("\tRULE 52: IF self_confident IS true AND other_imps IS high AND own_imps IS middle THEN img_effect IS middle;\n");
		sb.append("\tRULE 53: IF self_confident IS true AND other_imps IS high AND own_imps IS high THEN rep_effect IS low;\n");
		sb.append("\tRULE 54: IF self_confident IS true AND other_imps IS high AND own_imps IS high THEN img_effect IS high;\n");
		sb.append("\tRULE 55: IF self_confident IS false AND other_imps IS low AND own_imps IS low THEN rep_effect IS high;\n");
		sb.append("\tRULE 56: IF self_confident IS false AND other_imps IS low AND own_imps IS low THEN img_effect IS low;\n");
		sb.append("\tRULE 57: IF self_confident IS false AND other_imps IS low AND own_imps IS middle THEN rep_effect IS low;\n");
		sb.append("\tRULE 58: IF self_confident IS false AND other_imps IS low AND own_imps IS middle THEN img_effect IS middle;\n");
		sb.append("\tRULE 59: IF self_confident IS false AND other_imps IS low AND own_imps IS high THEN rep_effect IS low;\n");
		sb.append("\tRULE 60: IF self_confident IS false AND other_imps IS low AND own_imps IS high THEN img_effect IS high;\n");
		sb.append("\tRULE 61: IF self_confident IS false AND other_imps IS middle AND own_imps IS low THEN rep_effect IS middle;\n");
		sb.append("\tRULE 62: IF self_confident IS false AND other_imps IS middle AND own_imps IS low THEN img_effect IS low;\n");
		sb.append("\tRULE 63: IF self_confident IS false AND other_imps IS middle AND own_imps IS middle THEN rep_effect IS high;\n");
		sb.append("\tRULE 64: IF self_confident IS false AND other_imps IS middle AND own_imps IS middle THEN img_effect IS low;\n");
		sb.append("\tRULE 65: IF self_confident IS false AND other_imps IS middle AND own_imps IS high THEN rep_effect IS middle;\n");
		sb.append("\tRULE 66: IF self_confident IS false AND other_imps IS middle AND own_imps IS high THEN img_effect IS high;\n");
		sb.append("\tRULE 67: IF self_confident IS false AND other_imps IS high AND own_imps IS low THEN rep_effect IS high;\n");
		sb.append("\tRULE 68: IF self_confident IS false AND other_imps IS high AND own_imps IS low THEN img_effect IS low;\n");
		sb.append("\tRULE 69: IF self_confident IS false AND other_imps IS high AND own_imps IS middle THEN rep_effect IS high;\n");
		sb.append("\tRULE 70: IF self_confident IS false AND other_imps IS high AND own_imps IS middle THEN img_effect IS middle;\n");
		sb.append("\tRULE 71: IF self_confident IS false AND other_imps IS high AND own_imps IS high THEN rep_effect IS high;\n");
		sb.append("\tRULE 72: IF self_confident IS false AND other_imps IS high AND own_imps IS high THEN img_effect IS low;\n\n");
		
		sb.append("END_RULEBLOCK\n");
		sb.append("END_FUNCTION_BLOCK\n");
		
		return sb.toString();
	}
}