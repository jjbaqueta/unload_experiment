package trustExperiment;

import java.io.FileNotFoundException;

import fuzzyCognitiveMaps.model.FuzzyGraph;
import fuzzyCognitiveMaps.model.FuzzyUtil;
import fuzzyCognitiveMaps.services.GenerateDotFileVisitor;
import fuzzyCognitiveMaps.services.GenerateLinguisticTermsVisitor;
import fuzzyCognitiveMaps.services.PropagateInputsVisitor;

public class Main {

	public static void main(String[] args) 
	{	
		try 
		{
			FuzzyGraph fuzzyMap = FuzzyUtil.fuzzyMap1();
			
			// Starting the services
			PropagateInputsVisitor propagateVisitor = new PropagateInputsVisitor(FuzzyUtil.getInputsFuzzyMap1());
			GenerateDotFileVisitor dotVisitor = new GenerateDotFileVisitor("fuzzyMap");
			GenerateLinguisticTermsVisitor lingTermsVisitor = new GenerateLinguisticTermsVisitor();
		
			fuzzyMap.accept(propagateVisitor);
			fuzzyMap.accept(dotVisitor);
			fuzzyMap.accept(lingTermsVisitor);
			
		} catch (FileNotFoundException e) {
			System.out.println(e.getMessage());
			e.printStackTrace();
		}
	}
}
