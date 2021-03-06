package scenario_unloadBoxes.actions.generic;

import jason.asSemantics.*;
import jason.asSyntax.*;
import scenario_unloadBoxes.entities.model.SimpleElement;
import scenario_unloadBoxes.environments.UnloadEnv;

/**
 * This action updates the position of an agent.
 */
public class updateAgentPosition extends DefaultInternalAction 
{
	private static final long serialVersionUID = 1L;

	/**
	 * Arguments (come from parameter args):
	 * @param args[0]: agent's name
	 */
	@Override
    public Object execute(TransitionSystem ts, Unifier un, Term[] args) throws Exception 
    {
		String agentName = args[0].toString();
		SimpleElement element = UnloadEnv.model.getElement(Integer.parseInt(agentName.split("_")[1]));
		
		// Update the position of agent in its belief base
		ts.getAg().delBel(Literal.parseLiteral("pos(_,_)"));
		ts.getAg().addBel(Literal.parseLiteral("pos("+ element.getPos().x + "," + element.getPos().y + ")"));
        return true;
    }
}