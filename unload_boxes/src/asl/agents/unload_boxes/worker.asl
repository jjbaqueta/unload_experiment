/* MODULES *************/

{ include("src/asl/modules/basicModule.asl") }			// rules and plans for social protocols
{ include("src/asl/modules/providingModule.asl") }		// rules and plans for providing a service
{ include("src/asl/modules/contractingModule.asl") }	// rules and plans for contracting a service
{ include("src/asl/modules/socialModule.asl") }			// rules and plans for social evaluations

/* RULES *************/

/**
 * This rule computes the number of answers for a given service
 * @param TeamId: id of team.
 * @param Invites: number of sent invites.
 * */ 
invites_received(TeamId, Invites) 
	:-	.count(service(TeamId, aborted)[source(_)], Aborted) &
		.count(service(TeamId, accepted)[source(_)], Accepted) &
		Invites == (Aborted + Accepted)
.

/* BEHAVIOR *************/

!start.

/**
 * Set initial beliefs for worker
 */
+!start: getMyName(Name)
	<-	scenario_unloadBoxes.actions.worker.initialize(Name);
		!register("requester_worker");
		!register("provider_worker", "requester_trucker");
		+busy(false);	
.

/**
 * When worker receives a proposal or a refuse from a helper, the worker adds this helper as a friend.
 * A friend is a helper with who the worker interacted at least once.
 */
+proposal(TeamId,_)[source(Helper)]: team(CNPId, TeamId,_,_) & task(CNPId,_, Cargo_type,_)
	<-	!getHelperSkills(Cargo_type, Skill);
		!initializeAvailability(Helper, Skill);
        !addFriend(Helper);
.

+refuse(TeamId)[source(Helper)]: team(CNPId, TeamId,_,_) & task(CNPId,_, Cargo_type,_)
	<-	!getHelperSkills(Cargo_type, Skill);
		!initializeAvailability(Helper, Skill);
        !addFriend(Helper);
.

/**
 *	When a helper is invited to join a team (make a service), he may either accept or reject the invite.
 */
+service(TeamId, Status)[source(Helper)]: team(CNPId, TeamId, _, _) & task(CNPId,_, Cargo_type,_)
	<-	-proposal(TeamId,_)[source(Helper)];
		.print("Helper: ", Helper, " answered: ", Status, " teamID: ", TeamId);
     
		!getHelperSkills(Cargo_type, Skill);

		if(Status == accepted)
		{
			!helping(Helper, Skill);
		}
.

/**
 * Call for proposal.
 * @param CNPId: id of the call.
 * @param task(Task_type): type of cargo.
 * @param task(Boxes_amount): number of boxes inside the truck.
 * @param task(Task_urgency): urgency to complete the task. 
 */
+cfp(CNPId, task(Task_type, Boxes_amount, Task_urgency))[source(Truck)]
	:	provider(Truck, "requester_trucker") & 
		specialization(My_specialty) &
		busy(false)
		
	<-	if(Task_type == fragile & (My_specialty == fragile_specialization | My_specialty == dual_specialization))
		{
			!makeOffer(CNPId, fragile, Boxes_amount, Task_urgency, Truck);
			
		}
		elif(Task_type == common & (My_specialty == common_specialization | My_specialty == dual_specialization))
		{
			!makeOffer(CNPId, common, Boxes_amount, Task_urgency, Truck);
		}
		else
		{
			.send(Truck, tell, refuse(CNPId));
			.concat("[TASK REFUSED - LACK OF SPECIALIZATION] - CNPId: ", CNPId, Message);
			!log(Message);
			!updatePosition;
		}
		-cfp(CNPId,_)[source(Truck)];
.

+cfp(CNPId,_)[source(Truck)]: busy(true)
	<-	.send(Truck, tell, refuse(CNPId));
		-cfp(CNPId,_)[source(Truck)];
.

/**
 * If the worker is busy, the can't accept the invite from trucker
 * @param CNPId: id of the call.
 */
+can_start(CNPId)[source(Truck)]: busy(true)
	<-	.print(Truck ," sent an invite to me, but I'm already busy.");
		!finishService(CNPId); 
		.send(Truck, tell, service(CNPId, rejected));
 		!end_call(CNPId);
.

/**
 * If the worker isn't busy, he can accept the invite from trucker
 * @param CNPId: id of the call.
 */
+can_start(CNPId)[source(Truck)]: busy(false)
	<-	.print("I can accept the invite from ", Truck);
		.send(Truck, tell, service(CNPId, accepted)); 
.

/**
 * The worker won the call and he must perform the service
 * @param CNPId: id of the call
 */
 @w_1 [atomic]
+accept_proposal(CNPId)[source(Truck)]
	:	busy(false) &
		provider(Truck, "requester_trucker") & 
		team(CNPId, TeamId, _, _) &
		getMyName(Me) &
		getReceivedOffers(CNPId, Offers)
		
	<-	-+cnp_state(CNPId, contract);
		-+busy(true);
		+unloaded_boxes(CNPId, 0);
		+client(CNPId, Truck);
		.print("I won the CNP ", CNPId);
		scenario_unloadBoxes.actions.worker.getTeam(TeamId, Me, Team);
		.print("My team: ", Team, " CNPId: ", CNPId);
		.length(Team, N_members);
		+number_reports(TeamId, N_members);
		.findall(call(CNPId), my_proposal(CNPId, _), Calls);
		!dispenseTeams(CNPId, Calls);
		scenario_unloadBoxes.actions.generic.getTime(Time);
		+unload_time(CNPId, Time);
		!start_activities(TeamId, Team);
.

/**
 * The worker won the call, but he is doing another service for someone
 * @param CNPId: id of the call
 */
 @h_2 [atomic]
+accept_proposal(CNPId)[source(Truck)]:	busy(true)
	<-	.print("My proposal was accepted by ", Truck ,", but I'm already busy.");
		!finishService(CNPId); 
		.send(Truck, tell, service(CNPId, aborted));
 		!end_call(CNPId);
.

/**
 * The agent lost the CNP, so he breaks off the contracts with helpers.
 * @param CNPId: id of required service 
 */
+reject_proposal(CNPId)[source(Truck)]: team(CNPId, TeamId, _, _) & getMyName(Me)
   	<-	scenario_unloadBoxes.actions.worker.getTeam(TeamId, Me, Team)

   		if(Team \== [])
   		{
	   		.print("I lost CNP ", CNPId, ".");
	 		!finishService(CNPId); 
	 		!end_call(CNPId);
	 		.concat("[TASK CANCELED - OFFER WAS REJECTED] - CNPId: ", CNPId, Message);
			!log(Message);
	 		!updatePosition;   		
   		}
.

/**
 * After all helpers sent their reports, the worker ends the service and send his report to trucker.
 * @param TeamId: id of team.
 * @param Delivered_boxes: number of boxes delivered by helper
 * @param Taken_boxes: number of boxes that the helper took from truck.
 * @param Time: Time that the helper took to perform the task.
 */
@w_3 [atomic]
+report(TeamId, results(Delivered_boxes, Taken_boxes, Real_time))[source(Helper)]
	: 	team(CNPId, TeamId, _, _) &
		task(CNPId, _, Cargo_type,_) &
		client(CNPId, Truck) & 
		getMyName(Me) &
		my_proposal(CNPId, team(Team_size, Estimated_unload_time))		
	
	<-	scenario_unloadBoxes.actions.worker.getHelperEstimatedLoad(TeamId, Me, Helper, Estimated_load);
		scenario_unloadBoxes.actions.worker.getHelperEstimatedTime(TeamId, Me, Helper, Estimated_time);

        .print("[REPORT]: ", Helper, "{estimated time: ", Estimated_time, ", real time: ", Real_time,
        ", taken boxes: ", Estimated_load, ", delivered boxes: ", Delivered_boxes, "}");
		
		scenario_unloadBoxes.actions.generic.evaluation(Estimated_load, Taken_boxes, Load_rating);
		scenario_unloadBoxes.actions.generic.evaluation(Estimated_time, Real_time, Time_rating);
		
		scenario_unloadBoxes.actions.worker.getNeighborhood(Me, Workers);
		!getHelperSkills(Cargo_type, Skill);
		!evaluateProvider(Helper, Skill, ["EXPERTISE", "TIME"], [Load_rating, Time_rating]);
		!spreadImage(Helper, Skill, Workers);
		
		?unloaded_boxes(CNPId, Boxes);
		?number_reports(TeamId, N_reports)
		B = Boxes + Delivered_boxes;
		N = N_reports - 1;		
		
		if(N == 0)
		{		
			?unload_time(CNPId, S_time);
			scenario_unloadBoxes.actions.generic.getTime(F_time);
			T = F_time - S_time;

			.send(Truck, tell, report(CNPId, results(Team_size, Estimated_unload_time, B, T)));
			.print("The service was concluded, CNPId: ", CNPId);
			!log("[TASK COMPLETED]");
			!updatePosition;
			!finishService(CNPId);
			-+busy(false);
            -+cnp_state(CNPId, finished);
			!end_call(CNPId);
		}
		else
		{
			-+unloaded_boxes(CNPId, B);
			-+number_reports(TeamId, N);	
		}
.

/**
 * Get the possible skills for a helper
 */
+!getHelperSkills(Cargo_type, Skill)
	<- 	if(Cargo_type == fragile)
		{
			Skill = "FRAGILE_LOADER";
		}
		else
		{
			Skill = "COMMON_LOADER";
		}
.

/**
 * Get the possible skills for a worker
 */
+!getMySkills(Cargo_type, Skill)
	<-	if(Cargo_type == fragile)
		{
			Skill = "FRAGILE_SPECIALIZATION";		
		}
		else
		{
			Skill = "COMMON_SPECIALIZATION";	
		}
.

/**
 * Send the call for proposal to possible participants 
 * @param CNPId: id of required service
 * @param Task: the service to be done
 * @param Participants: list of participants for the calling
 */  
+!callNearby(CNPId, Task, Participants)
	<-	.print("Waiting for participants: ", Task, "...", " CNPId: ", CNPId);
		.wait(2000);
		.print("Sending call for proposal to ", Participants, " CNPId: ", CNPId);
		.send(Participants, tell, cfp(CNPId, Task));
.

/**
 * Update trust value for each helper that sent an offer to worker.
 * @param Cargo_type: type of cargo.
 * @param Offers: list of received offers.
 */
+!updateTrust(Cargo_type, Boxes_amount, Task_urgency, [offer(_, Helper)|T]): getMyName(Me)
	<-	?self_confident(Confident_profile);
		!getHelperSkills(Cargo_type, Skill);
		!computeAvailability(Helper, Skill, Availability);
		!checkTrust(Helper, Skill, Availability, Task_urgency, Boxes_amount, Confident_profile);
		!updateTrust(Cargo_type, Boxes_amount, Task_urgency, T);
.

+!updateTrust(Cargo_type, Boxes_amount, Task_urgency, []).

/**
 * Attempt for making an offer for the trucker.
 * Before to answer the call, the worker will try to hire helpers to perform the service.
 * @param CNPId: id of the call.
 * @param Cargo_type: type of cargo.
 * @param Nb_boxes: number of boxes inside the truck.
 * @param Unload_Time: time to perform the task. 
 * @param Truck: name of trucker that requested the service.
 */
+!makeOffer(CNPId, Cargo_type, Boxes_amount, Task_urgency, Truck): getMyName(Me)
	<-	scenario_unloadBoxes.actions.worker.getTeamID(Me, CNPId, TeamId);
		scenario_unloadBoxes.actions.generic.getTargetPosition(Truck, pos(X, Y));
		.findall(depot(Name), depot(Name), Depots);
		!getTheNearestTarget(Depots, pos(X, Y), Depot);
		+team(CNPId, TeamId, Truck, Depot);
		+task(CNPId, Boxes_amount, Cargo_type, Task_urgency);
		!createTeam(TeamId);
		!startCnp(CNPId);
.

/**
 * Start the CNP, the worker sends a call for proposal to helpers.
 * @param CNPId: id of the call.
 */
+!startCnp(CNPId)
	: 	team(CNPId, TeamId, Truck, Depot) &
		task(CNPId,_, Cargo_type,_) &
		getMyName(Me)
		
	<-	scenario_unloadBoxes.actions.worker.getHelpersNearby(Me, Nearby_Helpers);
		.findall(Helper, friend(Helper), Friends);
		scenario_unloadBoxes.actions.worker.concatToSet(Friends, Nearby_Helpers, Participants);
		.print("participants: ", Participants, " CNPId: ", CNPId);
		
		if(Participants \== [])
		{
			+cnp_state(CNPId, propose);
			!callNearby(TeamId, task(Truck, Depot, Cargo_type), Participants);
			!bid(TeamId, Participants);
			!inviteHelpers(CNPId);			
		}
		else
		{
			.send(Truck, tell, refuse(CNPId));
			-team(CNPId,_,_,_);
			-task(CNPId,_,_,_);
			!removeTeam(TeamId);
			.concat("[TASK CANCELED - NO PARTICIPANTS] - CNPId: ", CNPId, Message);
			!log(Message);
			!updatePosition;
		}
.

/**
 * The worker tries to hire helpers for the task.
 * @param CNPId: id of the call.
 */
+!inviteHelpers(CNPId)
	:	team(CNPId, TeamId, Truck, Depot) & 
		task(CNPId, Boxes_amount, Cargo_type, Task_urgency) &
		getReceivedOffers(TeamId, Offers) & 		
		getMyName(Me)
		
	<- 	if(Offers \== [])
      	{	      		
      		!updateTrust(Cargo_type, Boxes_amount, Task_urgency, Offers);
	 		!updateTeam(TeamId, Offers, Cargo_type, NotReadyMembers);
	 		!getHelperSkills(Cargo_type, Skill)
	 		!inviteTeam(TeamId, Skill, NotReadyMembers);
	 		!waitAnswers(TeamId);
	 		!updateHelpersStatus(TeamId);
	 		!checkTeam(TeamId);
		}
		
		if(Offers == [])
		{
			scenario_unloadBoxes.actions.worker.getTeam(TeamId, Me, Members);
			
			if(Members == [])
			{
				.send(Truck, tell, refuse(CNPId));
				!removeTeam(TeamId);
				-team(CNPId,_,_,_);
				-task(CNPId,_,_,_);
				-cnp_state(CNPId,_);
				!deleteAllRefuses(CNPId);
				!deleteAllServices(CNPId);
				-invitation(TeamId,_);
				.concat("[TASK CANCELED - NO OFFERS] - CNPId: ", CNPId, Message);
				!log(Message);
			}
			else
			{
				!checkTeam(TeamId);
			}
		}
.

/**
 * Check if the team is ready for starting the job.
 * If the team is ready, the worker send an offer to trucker.
 * @param TeamId: id of team that will be hired to perform the task (it is a exclusive id).
 */
 
+!checkTeam(TeamId)
	: 	team(CNPId, TeamId, Truck, Depot) &
		task(CNPId,_, Cargo_type,_) &
		getReceivedOffers(TeamId, Offers) &
		getMyName(Me)
		
	<-	scenario_unloadBoxes.actions.worker.getTeam(TeamId, Me, Team);
		
		if(scenario_unloadBoxes.actions.worker.teamIsReady(TeamId, Me, Offers) & Team \== [])
		{
			scenario_unloadBoxes.actions.worker.proposeOffer(TeamId, Me, Truck, Offer);
			scenario_unloadBoxes.actions.worker.removeWorkloadZero(TeamId, Me, RemovedHelpers);
			!removeZero(TeamId, RemovedHelpers);
			!teamReady(TeamId);
			.send(Truck, tell, proposal(CNPId, Offer));
			!sendResume(Truck, Cargo_type);			
			+teams_truck(Truck, TeamId);
			+my_proposal(CNPId, Offer);
			-service(TeamId,_)[source(_)];
			-invitation(TeamId,_);
				
			if(Offers \== [])
			{
				!rejectOffers(TeamId, Offers);
			}
		}
		else
		{
			.print("The team ", TeamId," is not ready yet!", " CNPId: ", CNPId);
			!inviteHelpers(CNPId);	
		}
.

/**
 * Break off the contracts done with helpers.
 * @param TeamId: id of team.
 * @param Rejected_helpers: list of helpers not selected to job.
 */
+!removeZero(TeamId, [Helper|T])
	<-	.send(Helper, tell, service(TeamId, canceled));
		!fireHelper(TeamId, Helper);
		!removeZero(TeamId, T);
.

+!removeZero(_,[]).

/**
 * Send a knowhow information to client
 * @param Client: who requested the service
 * @param Cargo_type: type of cargo (fragile or common).
 */
+!sendResume(Client, Cargo_type)
	<-	!getMySkills(Cargo_type, My_skill);
		!sendMyknowHow(Skill, Client);
.

/**
 * Inform to helpers who makes part of the team.
 * @param TeamId: id of team that will be hired to perform the task (it is a exclusive id).
 * @param Helpers: list of helpers from current team.
 */
+!inviteTeam(TeamId, Skill, [Helper|T])
	<-	.send(Helper, tell, accept_proposal(TeamId));
		+invitation(TeamId, Helper);
      	.print("invite sent for: ", Helper, " TeamId: ", TeamId);      	
 		!requestHelp(Helper, Skill);
      	!inviteTeam(TeamId, Skill, T);
.
      
+!inviteTeam(_,_,[]).

/**
 * Wait for answers of guests (helpers that makes part of a team)
 * @param TeamId: id of team
 * @return Guest_list (list of helpers) 
 */
+!waitAnswers(TeamId): getMyName(Me) 
	<-	.findall(guest(Helper), invitation(TeamId, Helper), Guest_list);
		.print("Guest list: ", Guest_list, " TeamId: ", TeamId);
		.length(Guest_list, Size)
		.wait(invites_received(TeamId, Size), 4000,_);
		.print("All guests answered the invite for .", TeamId);
.

/**
 * Update the status of each helper of a team
 * @param TeamId: id of team 
 */
+!updateHelpersStatus(TeamId)
	<-	.findall(member(Helper), service(TeamId, accepted)[source(Helper)], Members);
		.findall(other(Helper), service(TeamId, aborted)[source(Helper)], Others);
		!hireHelpers(TeamId, Members);	
		!fireHelpers(TeamId, Others);
.

/**
 * hire all helpers that have accepted the task
 * @param TeamId: id of team
 * @param Members: list of helpers 
 */
+!hireHelpers(TeamId, [member(Helper)|T])
	<-	.print(Helper ," was hired to Team ", TeamId ,".");
		!hireHelper(TeamId, Helper);
		!hireHelpers(TeamId, T);
.

+!hireHelpers(_,[]).

/**
 * fire all helpers that have not accepted the task
 * @param TeamId: id of team
 * @param Others: list not members 
 */
+!fireHelpers(TeamId, [other(Helper)|T])
	<-	.print("Removing ", Helper ," from team ", TeamId ,". He is busy.");
		!fireHelper(TeamId, Helper);
		!fireHelpers(TeamId, T);
.

+!fireHelpers(_,[]).

/**
 * Inform the rejection to helpers that weren't selected for job.
 * @param TeamId: id of team.
 * @param Helpers: list of not selected helpers.
 */
+!rejectOffers(TeamId, [offer(_, Helper)|T])
	<-	.send(Helper, tell, reject_proposal(TeamId));
		!rejectOffers(TeamId, T);
.

+!rejectOffers(_,[]).


/**
 * The others teams are dispensed when the worker accepts a job.
 * @Current_CNPId: id of the call for which the worker accepted the job.
 * @Calls: list of calls where the worker is enrolled as participant
 */
+!dispenseTeams(Current_CNPId, [call(CNPId)|T])
	<-	if(Current_CNPId \== CNPId)
		{
			!finishService(CNPId);	
		}
		!dispenseTeams(Current_CNPId, T);
.

+!dispenseTeams(Current_CNPId, []).

/**
 * Inform to team that the task must be started.
 * @param TeamId: id of team that will be hired to perform the task (it is a exclusive id).
 * @param Team: team of helpers.
 */
+!start_activities(TeamId, [Helper|T]) 
	<-	.send(Helper, tell, execute(TeamId));
      	!start_activities(TeamId, T);
.
      
+!start_activities(_,[]).

+!destroy_team(CNPId)[source(Truck)]
   	<-	.findall(TeamId, teams_truck(Truck, TeamId), Teams);
   		!del_team(Teams);
.

+!del_team([TeamId|T]): getMyName(Me)
	<-	.print("<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<< id: ", TeamId);
		scenario_unloadBoxes.actions.worker.getTeam(TeamId, Me, Team);
		!breakContract(TeamId, Team);
 		!removeTeam(TeamId);
	 	.concat("[TEAM DESTROYED - CNPId: ", CNPId, Message);
		!log(Message);
		-teams_truck(Truck, TeamId);
		!del_team(T);
.

+!del_team([]).

/**
 * Cancel the job assigned to a team.
 * @param CNPId: id of the call 
 */
+!finishService(CNPId)
	:	team(CNPId, TeamId, _, _) &
		getMyName(Me)
		
	<-	scenario_unloadBoxes.actions.worker.getTeam(TeamId, Me, Team);
		.print("Deleting team: ", TeamId);
   		!breakContract(TeamId, Team);
 		!removeTeam(TeamId);
.

/**
 * Break off the contracts done with helpers.
 * @param TeamId: id of team.
 * @param Rejected_helpers: list of helpers not selected to job.
 */
+!breakContract(TeamId, [Helper|T])
	<-	.send(Helper, tell, service(TeamId, canceled));
		!fireHelper(TeamId, Helper);
		!breakContract(TeamId, T);
.

+!breakContract(_,[]).

/*
 * The position of work is changed randomly
 */
@w_4 [atomic]
+!updatePosition: getMyName(Me)
	<-	move_worker;
		scenario_unloadBoxes.actions.generic.updateAgentPosition(Me);		
.

/*
 * The work cleans his memory about data from last call
 */
+!end_call(CNPId): team(CNPId, TeamId, _, _)
	<-	-report(TeamId,_)[source(_)];
		-reject_proposal(CNPId)[source(_)];
		-accept_proposal(CNPId)[source(_)];
		-service(TeamId,_)[source(_)];
		-number_reports(TeamId,_);
		-unload_time(CNPId,_);
		-unloaded_boxes(CNPId,_);
		-client(CNPId,_);
		-my_proposal(CNPId,_);
		!deleteAllPoposals(CNPId);
		!deleteAllRefuses(CNPId);
		-team(CNPId, TeamId,_,_,_);
		-task(CNPId,_,_,_,_);
.

/* TEAM OPERATIONS *************/

/**
 * Record in log file when a team is created.
 * @param TeamId: id of the team.
 */
 @w_5 [atomic]
+!createTeam(TeamId): getMyName(Me)
	<-	scenario_unloadBoxes.actions.worker.createTeam(TeamId, Me);	 		
	 	.concat("ADD TEAM: ", TeamId, Message);
	 	scenario_unloadBoxes.actions.worker.saveLog(Me, Message);
.

/**
 * Record in log file when a team is removed.
 * @param TeamId: id of the team.
 */
 @w_6 [atomic]
+!removeTeam(TeamId): getMyName(Me)
	<-	scenario_unloadBoxes.actions.worker.deleteTeam(TeamId, Me);
	   	.concat("DELETE TEAM: ", TeamId, Message);
	   	scenario_unloadBoxes.actions.worker.saveLog(Me, Message);
.

/**
 * An update may add new helpers and return the members that are not ready yet 
 * Record in log file when a team is updated.
 * @param TeamId: id of the team.
 * @param Offers: list of received offers
 * @param Nb_boxes: number of boxes to unload
 * @param Unload_Time: time to perform the task
 * @return Team: not ready members (status not hire). 
 */
 @w_7 [atomic]
+!updateTeam(TeamId, Offers, Cargo_type, NotReadyMembers): getMyName(Me)
	<-	scenario_unloadBoxes.actions.worker.updateTeam(TeamId, Me, Offers, Cargo_type, NotReadyMembers);
	   	.concat("UPDATE TEAM: ", TeamId, Message)
	   	scenario_unloadBoxes.actions.worker.saveLog(Me, Message);
.

/**
 * Record in log file when a team is ready.
 * @param TeamId: id of the team.
 */
+!teamReady(TeamId): getMyName(Me)
	<-	.concat("TEAM READY: ", TeamId, Message)
		scenario_unloadBoxes.actions.worker.saveLog(Me, Message);
.

/**
 * Record in log file when a team is ready.
 * @param TeamId: id of the team.
 * @param Helper: helper to be removed from team.
 */
 @w_8 [atomic]
+!fireHelper(TeamId, Helper): getMyName(Me)
	<-	scenario_unloadBoxes.actions.worker.deleteHelperFromTeam(TeamId, Me, Helper);
		.concat("FIRING HELPER: ", Helper, Message);
		scenario_unloadBoxes.actions.worker.saveLog(Me, Message);
		-invitation(TeamId, Helper);
		-service(TeamId, _)[source(Helper)];
.

/**
 * Record in log file when a team is ready.
 * @param TeamId: id of the team.
 * @param Helper: helper to be removed from team.
 */
 @w_9 [atomic]
+!hireHelper(TeamId, Helper): getMyName(Me)
	<-	scenario_unloadBoxes.actions.worker.hireHelper(TeamId, Me, Helper);
		.concat("HIRING HELPER: ", Helper, Message);
		scenario_unloadBoxes.actions.worker.saveLog(Me, Message);
		-invitation(TeamId, Helper);
		-service(TeamId, _)[source(Helper)];
.


/**
 * Record a message in the log file.
 * @param Message: the message to be wrote.
 */
+!log(Message): getMyName(Me)
	<-	scenario_unloadBoxes.actions.worker.saveLog(Me, Message);
.

/** 
 * Check if there is a trust belief for a helper.
 * If there is no a trust belief a new trust belief is created with value 0.5 
 */
+!checkTrust(Agent, Skill, Availability, Urgency, Num_boxes, Self_confident)
	:	trust(Agent, Skill,_) &
		getMyImpressionsAbout(Impressions, Agent, Skill) &
		getThirdPartImages(Images, Agent, Skill)
	<-	-trust(Agent, Skill,_);
		.length(Impressions, Own_imps);
		.length(Images, Other_imps);
        scenario_unloadBoxes.actions.generic.getFuzzyVariables(Urgency, Num_boxes, Own_imps, Other_imps, Self_confident, EdgesValues);
		!computeTrust(Agent, Skill, Availability, EdgesValues);
.

+!checkTrust(Agent, Skill,_,_,_,_): not trust(Agent,Skill,_)
	<-	+trust(Agent, Skill, 0.5);
.