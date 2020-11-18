// Internal action code for project Trust_scenarios

package scenario_Marketplace.actions.generic;

import jason.asSemantics.DefaultInternalAction;
import jason.asSemantics.TransitionSystem;
import jason.asSemantics.Unifier;
import jason.asSyntax.StringTerm;
import jason.asSyntax.Term;
import scenario_Marketplace.enums.ReportType;
import scenario_Marketplace.environments.Files;

/**
 * This action save in file a content passed as parameter.
 */
public class saveContent extends DefaultInternalAction 
{
	private static final long serialVersionUID = 1L;

	/**
	 * Arguments (come from parameter args):
	 * @param args[0]: agent's name.
	 * @param args[1]: type of report.
	 * @param args[2]: content to be written.
	 */
    @Override
    public Object execute(TransitionSystem ts, Unifier un, Term[] args) throws Exception 
    {
    	String agentName = args[0].toString();
    	StringTerm reportType = (StringTerm) args[1];
    	String content = args[2].toString();
    	
    	Files.writeReportInFile(agentName, ReportType.valueOf(reportType.getString()), content);
        return true;
    }
}
