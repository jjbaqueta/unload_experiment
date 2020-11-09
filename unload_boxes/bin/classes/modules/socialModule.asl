// Rules and plans for social evaluations

/* RULES *************/

// Get the agent's impressions about a target (direct experiences)
getMyImpressionsAbout(Impressions, Provider, Skill) 
	:- 	.findall(imp(Requester, Provider, Time, Skill, Criteria, Values), 
		imp(Requester, Provider, Time, Skill, Criteria, Values)[source(self)], 
		Impressions).

// Get the agent's image about a target
getMyImageAbout(Image, Provider, Skill)
	:-	.findall(img(Requester, Provider, Time, Skill, Criteria, Value),
		img(Requester, Provider, Time, Skill, Criteria, Value)[source(self)],
		Image).
		
// Get the stored reputation (already computed) about a target
getReputationOf(Reputation, Provider, Skill)
	:-	.findall(rep(Requester, Provider, Time, Skill, Criteria, Value),
		rep(Requester, Provider, Time, Skill, Criteria, Value)[source(_)],
		Reputation).
		
// Get images that came from the other agents
getThirdPartImages(Images, Provider, Skill) 
	:-	.findall(img(Requester, Provider, Time, Skill, Criteria, Values), 
		img(Requester, Provider, Time, Skill, Criteria, Values)[source(S)] & S \== self, 
		Images).

// Get the references of a target
getReferencesOf(Reference, Provider, Skill)
	:-	.findall(ref(Requester, Provider, Time, Skill, Criteria, Value),
		ref(Requester, Provider, Time, Skill, Criteria, Value)[source(_)],
		Reference).

// Get all impressions where agent played as a provider (evaluations about the agent)
getMyknowHow(Impressions, Skill)
	:-	.my_name(Provider) &
		.findall(imp(Requester, Provider, Time, Skill, Criteria, Values), 
		imp(Requester, Provider, Time, Skill, Criteria, Values)[source(S)] & S \== self, 
		Impressions).

// Get candidates able to perform a task
getCandidatesFor(Skill, Candidates)
	:-	.findall(trust(Provider, Value),
		trust(Provider, Skill, Value)[source(_)],
		Candidates).

/* PLANS *************/

/** 
 * Update the image of a provider, 
 * when a new impression about him is added in agent's belief base. 
 */
+imp(_,Provider,_,Skill,Criteria,_)[source(self)]: true
<-	!computeImage(Provider, Skill, Criteria).

/** 
 * Update the reputation of a provider, 
 * when a new image about him is added in agent's belief base. 
 */
+img(_,Provider,_,Skill,Criteria,_)[source(S)]
:	S \== self
<-	!computeReputation(Provider, Skill, Criteria).

/** 
 * update the agent's references based on new evaluations received from appraisers.
 */
+ref(List)[source(S)]: true
<-	.my_name(Requester);
	reputationAndImage.actions.knowhowAnalysis(Requester, S, List);
	-ref(_)[source(S)]. 

/**
 * Begins the availability level of an agent.
 * @param Agent: an agent..
 * @param Skill: the role played by this agent.
 */
+!initializeAvailability(Agent, Skill): not availability(Agent, Skill,_,_) 
    <-  +availability(Agent, Skill, 1, 1);
.

/**
 * Increase the number of ask for helping by one unit.
 * @param Agent: agent for who the help was asked. 
 */
@b_inc1 [atomic]
+!requestHelp(Agent, Skill): availability(Agent, Skill, N_askForHelping, N_Help)
	<-	-availability(Agent, Skill,_,_);
		+availability(Agent, Skill, N_askForHelping + 1, N_Help); 
.

+!requestHelp(Agent, Skill): not availability(Agent, Skill,_,_).

/**
 * Increase by one unit the helping counter of a helper.
 * @param Agent: agent who helped. 
 */
@b_inc2 [atomic]
+!helping(Agent, Skill): availability(Agent, Skill, N_askForHelping, N_Help)
	<-	-availability(Agent, Skill,_,_);
		+availability(Agent, Skill, N_askForHelping, N_Help + 1);
.

+!helping(Agent, Skill): not availability(Agent, Skill,_,_).

/**
 * Compute the availability of a Helper
 * @param Agent: agent for who the availability will be computed. 
 * @return the availability value
 */
+!computeAvailability(Agent, Skill, Availability): availability(Agent, Skill, N_askForHelping, N_Help)
	<-	if(N_askForHelping == 0)
		{
			Availability = -1;
		}
		else
		{
			Value = N_Help / N_askForHelping;
			
			if(Value <= 0)
			{
				Availability = -1;
			}
			elif (Value >= 1)
			{
				Availability = 1;
			}
			else
			{
				Availability = 2 * Value - 1;
			}
		}
.

+!computeAvailability(Agent, Skill, Availability): not availability(Agent, Skill,_,_)
	<- .print("------------ AVAILABILITY NOT FOUND FOR AGENT: ", Agent, " AND SKILL: ", Skill, "; CREATING RECORD...");
		+availability(Agent, Skill, 1, 1);
		Availability = 1;
.

/** 
 * Evaluate the provider. 
 * The evaluation is stored by the requester agent and sent to the provider
 */
+!evaluateProvider(Provider, Skill, Criteria, Values):	true
	<-	.my_name(Requester);
		reputationAndImage.actions.addImpression(Requester, Provider, Skill, Criteria, Values, ImpReference);
		.send(Provider, tell, ImpReference).

/** 
 * Update the agent's image about a provider. 
 * If there is not an image about a provider a new image is computed and stored in the belief base.
 * Otherwise, the image is updated.
 */
+!computeImage(Provider, Skill, Criteria): getMyImpressionsAbout(Impressions, Provider, Skill) 
	<-	.my_name(Requester);
		reputationAndImage.actions.computeImage(Impressions, Requester, Provider, Skill, Criteria).

/** 
 * Send the agent's image about a provider for a target. 
 */
+!sendImage(Provider, Target, Skill): getMyImageAbout(Image, Provider, Skill)
	<- 	.send(Target, tell, Image).

/** 
 * Compute the reputation of a provider based on the third party opinions.
 * The provider's reputation is stored in the belief base of agent (requester). 
 */
+!computeReputation(Provider, Skill, Criteria): getThirdPartImages(Images, Provider, Skill)
	<-	.my_name(Requester);
		reputationAndImage.actions.computeReputation(Images, Requester, Provider, Skill, Criteria).

/** 
 * Send a list of the provided services that agent has performed so far. 
 */
+!sendMyknowHow(Skill, Target):	getMyknowHow(Impressions, Skill)
	<-	if(Impressions \== [])
		{
			.send(Target, tell, ref(Impressions));
		}
.

/** 
 * Check if there is a trust belief for a agent.
 * If there is no a trust belief a new trust belief is created with value 0.5 
 */
+!check_trust(Agent, Skill, Availability, Urgency, Num_boxes, Self_confident)
	:	trust(Agent, Skill,_) &
		getMyImpressionsAbout(Impressions, Agent, Skill) &
		getThirdPartImages(Images, Agent, Skill)
	<-	-trust(Agent, Skill,_);
		.length(Impressions, Own_imps);
		.length(Images, Other_imps);
		actions.generic.getFuzzyVariables(Urgency, Num_boxes, Own_imps, Other_imps, Self_confident, EdgesValues);
		!computeTrust(Agent, Skill, Availability, EdgesValues);
.

+!check_trust(Agent, Skill,_,_,_,_): not trust(Agent,Skill,_)
	<-	+trust(Agent, Skill, 0.5);
.

/** 
 * Compute a trust measure about a provider using image, reputation and know-how.
 * The trust value is computed considering the context (Skill) 
 */
+!computeTrust(Provider, Skill, Availability, EdgesValues)
	:	getMyImageAbout(Image, Provider, Skill) &
		getReputationOf(Reputation, Provider, Skill) &
		getReferencesOf(Reference, Provider, Skill)
	<-	.my_name(Requester);
		reputationAndImage.actions.computeTrust(Requester, Provider, Skill, Image, Reputation, Reference, Availability, EdgesValues).

/** 
 * Find the most trustworthy candidate for a given task. 
 */
+!getBestCandidate(Skill): getCandidatesFor(Skill, Candidates)
	<-	!theMostTrustworthy(Candidates, MaxValue, BestProvider);
		.print("Candidates for the task: ", Candidates);
		.print("The best provider: ", BestProvider, ", and the best value: ", MaxValue).

+!theMostTrustworthy([], MaxValue, BestProvider).

+!theMostTrustworthy([trust(Provider, Value)|T], MaxValue, BestProvider): T == []
	<-	MaxValue = Value;
		BestProvider = Provider.

+!theMostTrustworthy([trust(Provider, Value)|T], MaxValue, BestProvider): T \== []
	<-	!theMostTrustworthy(T, MaxValue, BestProvider);
		if (MaxValue < Value)
		{
			MaxValue = Value;
			BestProvider = Provider;
		}.