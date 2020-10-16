/* MODULES *************/

{ include("src/asl/modules/basicModule.asl") }			// rules and plans for social protocols
{ include("src/asl/modules/providingModule.asl") }		// rules and plans for providing a service
{ include("src/asl/modules/contractingModule.asl") }	// rules and plans for contracting a service
{ include("src/asl/modules/socialModule.asl") }					// rules and plans for social evaluations

/* RULES *************/

/**
 * This rule computes the number of answers to a service invite
 * @param TeamId: id of team
 * @param NI (number of received invites) 
 * */ 
invites_received(TeamId, NI) 
	:-	.count(service(TeamId, aborted)[source(_)], ABT) &
		.count(service(TeamId, accepted)[source(_)], ACP) &
		NI == (ABT + ACP) &
		.print("Number of services aborted: ", ABT) &
		.print("Number of services accepted: ", ACP)
.

/* BEHAVIOR *************/

!start.

/**
 * Set initial beliefs for worker
 */
+!start: getMyName(Name)
	<-	actions.worker.initialize(Name);
		!register("requester_worker");
		!register("provider_worker", "requester_trucker");
		+busy(false);	
.

/**
 * Answering the call for proposal.
 * @param CNPId: id of the call.
 * @param task(Type): type of cargo.
 * @param task(Nb_boxes): number of boxes inside the truck.
 * @param task(Unload_Time): time to perform the task. 
 */
+cfp(CNPId, task(Type, Nb_boxes, Unload_Time))[source(Truck)]
	:	provider(Truck, "requester_trucker") & 
		specialization(My_specialty) &
		busy(false)
		
	<-	if(Type == fragile & (My_specialty == fragile_specialization | My_specialty == dual_specialization))
		{
			!make_offer(CNPId, fragile, Nb_boxes, Unload_Time, Truck);
			
		}
		elif(Type == common & (My_specialty == common_specialization | My_specialty == dual_specialization))
		{
			!make_offer(CNPId, common, Nb_boxes, Unload_Time, Truck);
		}
		else
		{
			.send(Truck, tell, refuse(CNPId));
			-cfp(CNPId,_)[source(Truck)];
			.concat("[TASK REFUSED - LACK OF SPECIALIZATION] - CNPId: ", CNPId, Message);
			!log(Message);
			!update_position;
		}
.

+cfp(CNPId, task(Type, Nb_boxes, Unload_Time))[source(Truck)]: provider(Truck, "requester_trucker") & busy(true)
	<-	.send(Truck, tell, refuse(CNPId));
		-cfp(CNPId,_)[source(Truck)];
.

/**
 * Attempt for making an offer for the trucker.
 * Before to answer the call, the worker will try to hire helpers to perform the service.
 * @param CNPId: id of the call.
 * @param Cargo_type: type of cargo.
 * @param Nb_boxes: number of boxes inside the truck.
 * @param Unload_Time: time to perform the task. 
 * @param Truck: name of trucker that requested the service.
 */
+!make_offer(CNPId, Cargo_type, Nb_boxes, Unload_Time, Truck): getMyName(Me)
	<-	actions.worker.getTeamID(Me, CNPId, TeamId);
		actions.generic.getTargetPosition(Truck, pos(X, Y));
		.findall(depot(Name), depot(Name), Depots);
		!getTheNearestTarget(Depots, pos(X, Y), Depot);
		+team(CNPId, TeamId, Truck, Depot);
		+task(CNPId, Nb_boxes, Unload_Time, Cargo_type);
		!create_team(TeamId);
		!start_cnp(CNPId);
.

/**
 * Start the CNP, the worker sends a call for proposal to helpers.
 * @param CNPId: id of the call.
 */
+!start_cnp(CNPId)
	: 	team(CNPId, TeamId, Truck, Depot) &
		task(CNPId, Nb_boxes, Unload_Time, Cargo_type) &
		getMyName(Me)
		
	<-	actions.worker.getHelpersNearby(Me, Nearby_Helpers);
		.findall(Helper, friend(Helper,_,_), Friends);
		actions.worker.concatToSet(Friends, Nearby_Helpers, Participants);
		.print("participants: ", Participants, " CNPId: ", CNPId);
		
		if(Participants \== [])
		{
			+cnp_state(CNPId, propose);
			!callNearby(TeamId, task(Truck, Depot, Cargo_type), Participants);
			!bid(TeamId, Participants);
			!invite_helpers(CNPId);			
		}
		else
		{
			.send(Truck, tell, refuse(CNPId));
			-team(CNPId,_,_,_);
			-task(CNPId,_,_,_);
			-cfp(CNPId,_)[source(_)];
			!remove_team(TeamId);
			.concat("[TASK CANCELED - NO PARTICIPANTS] - CNPId: ", CNPId, Message);
			!log(Message);
			!update_position;
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
 * When worker receives a proposal or a refuse from a helper, the worker adds this helper as a friend.
 * A friend is a helper with who the worker interacted at least once.
 */
+proposal(_,_)[source(Helper)]: not friend(Helper,_,_)
	<-	+friend(Helper, 0, 0);
.

+refuse(_)[source(Helper)]:  not friend(Helper,_,_)
	<-	+friend(Helper, 0, 0);
.

/**
 *	When a helper is invited to join a team (make a service), he may either accept or reject the invite.
 */
+service(TeamId, Status)[source(Helper)]
	<-	-proposal(TeamId,_)[source(Helper)];
		.print("Helper: ", Helper, " answer: ", Status, " teamID: ", TeamId);
     
		if(Status == accepted)
		{
			!inc_help(Helper);
		}
.

/**
 * The worker try to hire helpers for the task.
 * @param CNPId: id of the call.
 */
+!invite_helpers(CNPId)
	:	team(CNPId, TeamId, Truck, Depot) &
		task(CNPId, Nb_boxes, Unload_Time, Cargo_type) & 	
		getReceivedOffers(TeamId, Offers) & 		
		getMyName(Me)
		
	<- 	if(Offers \== [])
      	{	
	 		!update_team(TeamId, Offers, Nb_boxes, Unload_Time, Cargo_type, NotReadyMembers);
	 		!invite_team(TeamId, NotReadyMembers);
	 		!wait_answers(TeamId);
	 		!update_helpers_status(TeamId);
	 		!check_team(TeamId);
		}
		
		if(Offers == [])
		{
			actions.worker.getTeam(TeamId, Me, Members);
			
			if(Members == [])
			{
				.send(Truck, tell, refuse(CNPId));
				-team(CNPId,_,_,_);
				-task(CNPId,_,_,_);
				-cnp_state(CNPId,_);
				-refuse(CNPId)[source(_)];
				-cfp(CNPId,_)[source(_)];
				-service(TeamId,_)[source(_)];	
				-invitation(TeamId,_);
				!remove_team(TeamId);
				.concat("[TASK CANCELED - NO OFFERS] - CNPId: ", CNPId, Message);
				!log(Message);
			}
			else
			{
				!check_team(TeamId);
			}
		}
.

/**
 * Check if the team is ready for starting the job.
 * If the team is ready, the worker send an offer to trucker.
 * @param TeamId: id of team that will be hired to perform the task (it is a exclusive id).
 */
 
+!check_team(TeamId)
	: 	team(CNPId, TeamId, Truck, Depot) &
		task(CNPId, Nb_boxes, Unload_Time, Cargo_type) & 
		getReceivedOffers(TeamId, Offers) &
		getMyName(Me)
		
	<-	if(actions.worker.teamIsReady(TeamId, Me, Offers) & actions.worker.getTeam(TeamId, Me, Team) & Team \== [])
		{
			// check risk profile.
			!team_ready(TeamId);
			actions.worker.proposeOffer(TeamId, Me, Offer);
			.send(Truck, tell, proposal(CNPId, Offer));
			!send_resume(Truck, Cargo_type);			
			+my_proposal(CNPId, Offer);
			-service(TeamId,_)[source(_)];
			-invitation(TeamId,_);
				
			if(Offers \== [])
			{
				!reject_offers(TeamId, Offers);
			}
		}
		else
		{
			.print("The team ", TeamId," is not ready yet!", " CNPId: ", CNPId);
			!invite_helpers(CNPId);	
		}
.

/**
 * Send a knowhow information to client
 * @param Client: who requested the service
 * @param Cargo_type: type of cargo (fragile or common).
 */
+!send_resume(Client, Cargo_type)
	<-	if(Cargo_type == fragile)
		{
			!sendMyknowHow("FRAGILE_SPECIALIZATION", Client);		
		}
		else
		{
			!sendMyknowHow("COMMON_SPECIALIZATION", Client);	
		}	
.

/**
 * Inform to helpers who makes part of the team.
 * @param TeamId: id of team that will be hired to perform the task (it is a exclusive id).
 * @param Helpers: list of helpers from current team.
 */
+!invite_team(TeamId, [Helper|T]) 
	<-	.send(Helper, tell, accept_proposal(TeamId));
		+invitation(TeamId, Helper);
      	.print("invite sent for: ", Helper, " TeamId: ", TeamId);
      	
      	!computeAvailability(Helper, Availability)
		.print(" ---------------- Availability: ", Availability);
      	
 		!inc_askForHelping(Helper);     	
      	!invite_team(TeamId, T);
.
      
+!invite_team(_,[]).

/**
 * Increase the number of ask for helping by one unit.
 * @param Helper: helper for who the help was asked. 
 */
@w_inc1 [atomic]
+!inc_askForHelping(Helper): friend(Helper, N_askForHelping, N_Help)
	<-	-friend(Helper,_,_);
		+friend(Helper, N_askForHelping + 1, N_Help); 
.

+!inc_askForHelping(Helper): not friend(Helper,_,_).

/**
 * Increase by one unit the helping counter of a helper.
 * @param Helper: helper who helped. 
 */
@w_inc2 [atomic]
+!inc_help(Helper): friend(Helper, N_askForHelping, N_Help)
	<-	-friend(Helper,_,_);
		+friend(Helper, N_askForHelping, N_Help + 1);
.

+!inc_help(Helper): not friend(Helper,_,_).

/**
 * Compute the availability of a Helper
 * @param Helper: helper for who the availability will be computed. 
 * @return the availability value
 */
+!computeAvailability(Helper, Availability): friend(Helper, N_askForHelping, N_Help)
	<-	Availability = N_Help / N_askForHelping;
.

/**
 * Wait for answers of guests (helpers that makes part of a team)
 * @param TeamId: id of team
 * @return Guest_list (list of helpers) 
 */
+!wait_answers(TeamId): getMyName(Me) 
	<-	.findall(guest(Helper), invitation(TeamId, Helper), Guest_list);
		.print("Guest list: ", Guest_list, " TeamId: ", TeamId);
		.wait(invites_received(TeamId, .length(Guest_list)));
		.print("All guests answered the invite for .", TeamId);
.

/**
 * Update the status of each helper of a team
 * @param TeamId: id of team 
 */
+!update_helpers_status(TeamId)
	<-	.findall(member(Helper), service(TeamId, accepted)[source(Helper)], Members);
		.findall(other(Helper), service(TeamId, aborted)[source(Helper)], Others);
		!hire_helpers(TeamId, Members);	
		!fire_helpers(TeamId, Others);
.

/**
 * hire all helpers that have accepted the task
 * @param TeamId: id of team
 * @param Members: list of helpers 
 */
+!hire_helpers(TeamId, [member(Helper)|T])
	<-	.print(Helper ," was hired to Team ", TeamId ,".");
		!hire_helper(TeamId, Helper);
		!hire_helpers(TeamId, T);
.

+!hire_helpers(_,[]).

/**
 * fire all helpers that have not accepted the task
 * @param TeamId: id of team
 * @param Others: list not members 
 */
+!fire_helpers(TeamId, [other(Helper)|T])
	<-	.print("Removing ", Helper ," from team ", TeamId ,". He is busy.");
		!fire_helper(TeamId, Helper);
		!fire_helpers(TeamId, T);
.

+!fire_helpers(_,[]).

/**
 * Inform the rejection to helpers that weren't selected for job.
 * @param TeamId: id of team.
 * @param Helpers: list of not selected helpers.
 */
+!reject_offers(TeamId, [offer(_, Helper)|T])
	<-	.send(Helper, tell, reject_proposal(TeamId));
		!reject_offers(TeamId, T);
.

+!reject_offers(_,[]).

/**
 * The worker won the call and he must perform the service
 * @param CNPId: id of the call
 */
 @w_cnp3 [atomic]
+accept_proposal(CNPId)[source(Truck)]
	:	busy(false) &
		provider(Truck, "requester_trucker") & 
		team(CNPId, TeamId, _, _) &
		getMyName(Me) &
		getReceivedOffers(CNPId, Offers)
		
	<-	-+cnp_state(CNPId, contract);
		-+busy(true);
		+unloadedBoxes(CNPId, 0);
		+client(CNPId, Truck);
		.send(Truck, tell, service(CNPId, started))
		.print("I won the CNP ", CNPId);
		actions.worker.getTeam(TeamId, Me, Team);
		.print("My team: ", Team, " CNPId: ", CNPId);
		.length(Team, N_members);
		+number_reports(TeamId, N_members);
		.findall(call(CNPId), my_proposal(CNPId, _), Calls);
		!dispense_teams(CNPId, Calls);
		actions.generic.getTime(Time);
		+unloadTime(CNPId, Time);
		!start_activities(TeamId, Team);
.

/**
 * The worker won the call, but he is doing another service for someone
 * @param CNPId: id of the call
 */
 @h_cnp4 [atomic]
+accept_proposal(CNPId)[source(Truck)]:	busy(true)
	<-	.print("My proposal was accepted by ", Truck ,", but I'm already busy.");
		.send(Truck, tell, service(CNPId, aborted));
		!cancel_service(CNPId); 
 		!end_call(CNPId);
.

/**
 * The others teams are dispensed when the worker accepts a job.
 * @Current_CNPId: id of the call for which the worker accepted the job.
 * @Calls: list of calls where the worker is enrolled as participant
 */
+!dispense_teams(Current_CNPId, [call(CNPId)|T])
	<-	if(Current_CNPId \== CNPId)
		{
			!cancel_service(CNPId);	
		}
		!dispense_teams(Current_CNPId, T);
.

+!dispense_teams(Current_CNPId, []).

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

/**
 * The agent lost the CNP, so he breaks off the contract with helpers.
 * @param CNPId: id of required service 
 */
+reject_proposal(CNPId)[source(Truck)]
	:	team(CNPId, TeamId, _, _) & 
		getMyName(Me)
		
   	<-	.print("I lost CNP ", CNPId, ".");
 		!cancel_service(CNPId); 
 		!end_call(CNPId);
 		.concat("[TASK CANCELED - OFFER WAS REJECTED] - CNPId: ", CNPId, Message);
		!log(Message);
 		!update_position;
.

/**
 * Cancel the job assigned to a team.
 * @param CNPId: id of the call 
 */
+!cancel_service(CNPId)
	:	team(CNPId, TeamId, _, _) &
		getMyName(Me)
		
	<-	actions.worker.getTeam(TeamId, Me, Team);
		.print("Deleting team: ", TeamId);
   		!break_contract(TeamId, Team);
 		!remove_team(TeamId);
.

/**
 * Break off the contracts done with helpers.
 * @param TeamId: id of team.
 * @param Rejected_helpers: list of helpers not selected to job.
 */
+!break_contract(TeamId, [Helper|T])
	<-	.send(Helper, tell, service(TeamId, canceled));
		!fire_helper(TeamId, Helper);
		!break_contract(TeamId, T);
.

+!break_contract(_,[]).

/**
 * After all helper sent their reports, the worker ends the service and send his report to trucker.
 * @param TeamId: id of team.
 * @param Delivered_boxes: number of boxes delivered by helper
 * @param Taken_boxes: number of boxes that the helper took from truck.
 * @param Time: Time that the helper took to perform the task.
 */
@w_cnp5 [atomic]
+report(TeamId, results(Delivered_boxes, Taken_boxes, Task_time))[source(Helper)]
	: 	team(CNPId, TeamId, _, _) &
		task(CNPId, _,_, Cargo_type) &
		client(CNPId, Truck) & 
		getMyName(Me)		
	
	<-	// action: evaluation process - give notes by service
		
		actions.worker.getNeighborhood(Me, Workers);
			
		if(Cargo_type == fragile)
		{
			!evaluateProvider(Helper, "FRAGILE_LOADER", ["EXPERTISE", "TIME" ,"RISK"], [-0.1, 0.5, 0.4]);
			!spread_image(Helper, "FRAGILE_LOADER", Workers);
			!computeTrust(Helper, "FRAGILE_LOADER");
		}
		else
		{
			!evaluateProvider(Helper, "COMMON_LOADER", ["EXPERTISE", "TIME" ,"RISK"], [-0.1, 0.5, 0.4]);
			!spread_image(Helper, "COMMON_LOADER", Workers);
			!computeTrust(Helper, "COMMON_LOADER");
		}
		
		?unloadedBoxes(CNPId, Boxes);
		?number_reports(TeamId, N_reports)
		B = Boxes + Delivered_boxes;
		N = N_reports - 1;		
		
		if(N == 0)
		{
			?unloadTime(CNPId, Stime);
			actions.generic.getTime(Ftime);
			T = Ftime - Stime;
			.send(Truck, tell, report(CNPId, results(B, T)));
			.print("The service was concluded, CNPId: ", CNPId);
			!log("[TASK COMPLETED]");
			!update_position;
			!cancel_service(CNPId);
			-+busy(false);
			!end_call(CNPId);
		}
		else
		{
			-+unloadedBoxes(CNPId, B);
			-+number_reports(TeamId, N);	
		}
.

/*
 * The position of work is changed randomly
 */
@w_cnp6 [atomic]
+!update_position: getMyName(Me)
	<-	move_worker;
		actions.generic.updateAgentPosition(Me);		
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
		-unloadTime(CNPId,_);
		-unloadedBoxes(CNPId,_);
		-client(CNPId,_);
		-my_proposal(CNPId,_);
		-proposal(CNPId, _)[source(_)];
		-refuse(CNPId)[source(_)];
		-cnp_state(CNPId,_);
		-team(CNPId, TeamId,_,_,_);
		-task(CNPId,_,_,_);
		-cfp(CNPId,_)[source(_)];
.

/* TEAM OPERATIONS *************/

/**
 * Record in log file when a team is created.
 * @param TeamId: id of the team.
 */
 @w_cnp7 [atomic]
+!create_team(TeamId): getMyName(Me)
	<-	actions.worker.createTeam(TeamId, Me);	 		
	 	.concat("ADD TEAM: ", TeamId, Message);
	 	actions.worker.saveLog(Me, Message);
.

/**
 * Record in log file when a team is removed.
 * @param TeamId: id of the team.
 */
 @w_cnp8 [atomic]
+!remove_team(TeamId): getMyName(Me)
	<-	actions.worker.deleteTeam(TeamId, Me);
	   	.concat("DELETE TEAM: ", TeamId, Message);
	   	actions.worker.saveLog(Me, Message);
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
 @w_cnp9 [atomic]
+!update_team(TeamId, Offers, Nb_boxes, Unload_Time, Cargo_type, NotReadyMembers): getMyName(Me)
	<-	actions.worker.updateTeam(TeamId, Me, Offers, Nb_boxes, Unload_Time, Cargo_type, NotReadyMembers);
	   	.concat("UPDATE TEAM: ", TeamId, Message)
	   	actions.worker.saveLog(Me, Message);
.

/**
 * Record in log file when a team is ready.
 * @param TeamId: id of the team.
 */
+!team_ready(TeamId): getMyName(Me)
	<-	.concat("TEAM READY: ", TeamId, Message)
		actions.worker.saveLog(Me, Message);
.

/**
 * Record in log file when a team is ready.
 * @param TeamId: id of the team.
 * @param Helper: helper to be removed from team.
 */
 @w_cnp10 [atomic]
+!fire_helper(TeamId, Helper): getMyName(Me)
	<-	actions.worker.deleteHelperFromTeam(TeamId, Me, Helper);
		.concat("FIRING HELPER: ", Helper, Message);
		actions.worker.saveLog(Me, Message);
		-invitation(TeamId, Helper);
		-service(TeamId, _)[source(Helper)];
.

/**
 * Record in log file when a team is ready.
 * @param TeamId: id of the team.
 * @param Helper: helper to be removed from team.
 */
 @w_cnp11 [atomic]
+!hire_helper(TeamId, Helper): getMyName(Me)
	<-	actions.worker.hireHelper(TeamId, Me, Helper);
		.concat("HIRING HELPER: ", Helper, Message);
		actions.worker.saveLog(Me, Message);
		-invitation(TeamId, Helper);
		-service(TeamId, _)[source(Helper)];
.


/**
 * Record a message in the log file.
 * @param Message: the message to be wrote.
 */
+!log(Message): getMyName(Me)
	<-	actions.worker.saveLog(Me, Message);
.