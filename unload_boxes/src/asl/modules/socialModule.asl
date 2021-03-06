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

// Get the trust value of a target
getTrustOf(Trust, Provider, Skill)
	:-	.findall(trust(Provider, Skill, Value),
		trust(Provider, Skill, Value)[source(self)],
		Trust).

// Get the trust value of all agents that play the role defined by informed skill
getTrustBySkill(List, Skill)
	:-	.findall(trust(Agent, Skill, Value), trust(Agent, Skill, Value), List).

// Get candidates able to perform a task
getCandidatesFor(Skill, Candidates)
	:-	.findall(trust(Provider, Value),
		trust(Provider, Skill, Value)[source(_)],
		Candidates).

// Get the availability of a target
getAvailabilityOf(Availability, Provider, Skill)
	:-	.findall(availability(Provider, Skill, N_askForHelping, N_Help),
		availability(Provider, Skill, N_askForHelping, N_Help)[source(self)],
		Availability).		

/* PLANS *************/

/** 
 * Update the image of a provider, 
 * when a new impression about him is added in agent's belief base. 
 */
 @ms1[atomic]
+imp(_,Provider,_,Skill,Criteria,_)[source(self)]: true
<-	!computeImage(Provider, Skill, Criteria).

/** 
 * Update the reputation of a provider, 
 * when a new image about him is added in agent's belief base. 
 */
 @ms2[atomic]
+img(_,Provider,_,Skill,Criteria,_)[source(_)]
<-	!computeReputation(Provider, Skill, Criteria).

/** 
 * update the agent's references based on new evaluations received from appraisers.
 */
 @ms3[atomic]
+refs(List)[source(S)]: itm(knowhow, Value)
<-	.my_name(Requester);
	trustModel.repAndImg.actions.knowhowAnalysis(Requester, S, List, Value);
	-refs(_)[source(S)]
. 

/**
 * Begins the availability level of an agent.
 * @param Agent: an agent..
 * @param Skill: the role played by this agent.
 */
 @ms4[atomic]
+!initializeAvailability(Agent, Skill): not availability(Agent, Skill,_,_) 
    <-  +availability(Agent, Skill, 1, 1);
.

+!initializeAvailability(Agent, Skill): availability(Agent, Skill,_,_).

/**
 * Increase the number of ask for helping by one unit.
 * @param Agent: agent for who the help was asked. 
 */
@ms5 [atomic]
+!requestHelp(Agent, Skill): availability(Agent, Skill, N_askForHelping, N_Help)
	<-	-availability(Agent, Skill,_,_);
		+availability(Agent, Skill, N_askForHelping + 1, N_Help); 
.

+!requestHelp(Agent, Skill): not availability(Agent, Skill,_,_).

/**
 * Increase by one unit the helping counter of an agent.
 * @param Agent: agent who helped. 
 */
@ms6 [atomic]
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
	<- 	+availability(Agent, Skill, 1, 1);
		Availability = 1;
.

/** 
 * Evaluate the provider. 
 * The evaluation is stored by the requester agent and sent to the provider
 */
+!evaluateProvider(Provider, Skill, Criteria, Values):	true
	<-	.my_name(Requester);
		trustModel.repAndImg.actions.addImpression(Requester, Provider, Skill, Criteria, Values, ImpReference);
		.send(Provider, tell, ImpReference);
.

/** 
 * Update the agent's image about a provider. 
 * If there is not an image about a provider a new image is computed and stored in the belief base.
 * Otherwise, the image is updated.
 */
+!computeImage(Provider, Skill, Criteria)
	:	getMyImpressionsAbout(Impressions, Provider, Skill) &
		itm(image, Value)
		
	<-	.my_name(Requester);
		trustModel.repAndImg.actions.computeImage(Impressions, Requester, Provider, Skill, Criteria, Value);
.

/** 
 * Send the agent's image about a provider for a target. 
 */
+!sendImage(Provider, Target, Skill): getMyImageAbout(Image, Provider, Skill)
	<- 	.send(Target, tell, Image);
.

/** 
 * Compute the reputation of a provider based on the third party opinions.
 * The provider's reputation is stored in the belief base of agent (requester). 
 */
+!computeReputation(Provider, Skill, Criteria)
	: 	getThirdPartImages(Third_part_images, Provider, Skill) &
		getMyImageAbout(Own_image, Provider, Skill) &
		itm(reputation, Value)
	
	<-	.my_name(Requester);
		.concat(Own_image, Third_part_images, Images)
		trustModel.repAndImg.actions.computeReputation(Images, Requester, Provider, Skill, Criteria, Value);
.

/** 
 * Send a list of the provided services that agent has performed so far. 
 */
+!sendMyknowHow(Skill, Target):	getMyknowHow(Impressions, Skill)
	<-	if(Impressions \== [])
		{
			.send(Target, tell, refs(Impressions));
		}
.

/** 
 * Compute a trust measure about a provider using image, reputation and know-how.
 * The trust value is computed considering the context (Skill) 
 */
+!computeTrust(Provider, Skill, Image, Reputation, Reference, Availability, EdgesValues, Trust)		
	<-	.my_name(Requester);
		trustModel.repAndImg.actions.computeTrust(Requester, Provider, Skill, Image, Reputation, Reference, Availability, EdgesValues, Trust);
.

/**
 * Spread your own image to others
 * @param Appraised: evaluated agent
 * @param Skill: skill to be evaluated
 * @param Targets: list of agents that will received the image
 */
+!spreadImage(Appraised, Skill, [Target|T]): getMyName(Me)
	<-	if(Me \== Target)
		{
			!sendImage(Appraised, Target, Skill);
		}
		!spreadImage(Appraised, Skill, T)
.

+!spreadImage(_,_,[]).