/* MODULES *************/

{ include("src/asl/modules/basicModule.asl") }			// rules and plans for social protocols
{ include("src/asl/modules/contractingModule.asl") }	// rules and plans for contracting a service
{ include("src/asl/modules/socialModule.asl") }			// rules and plans for social evaluations

/* BEHAVIOR *************/

count(0).
!start.

/**
 * Set initial beliefs for trucker
 */
+!start: getMyName(Me)
	<-	actions.trucker.initialize(Me);
		!register("requester_trucker");
.

/**
 * Get the possible skill for a worker
 */
+!getWorkerSkills(Task_type, Skill)
	<-	if(Task_type == fragile)
		{
			Skill = "FRAGILE_SPECIALIZATION";		
		}
		else
		{
			Skill = "COMMON_SPECIALIZATION";	
		}
.

/**
 * When worker receives a proposal or a refuse from a helper, the worker adds this helper as a friend.
 * A friend is a helper with who the worker interacted at least once.
 */
+proposal(CNPId,_)[source(Worker)]: task(CNPId, task(Task_type,_,_,_))
	<-	if(not friend(Worker))
		{
			+friend(Worker);
		}
		
		!getWorkerSkills(Task_type, Skill);
		
		if(not availability(Worker, Skill,_,_))
		{
			+availability(Worker, Skill, 1, 1);	
		}	
.

+refuse(CNPId)[source(Worker)]: task(CNPId, task(Task_type,_,_,_))
	<-	if(not friend(Worker))
		{
			+friend(Worker);
		}
		
		!getWorkerSkills(Task_type, Skill);
		
		if(not availability(Worker, Skill,_,_))
		{
			+availability(Worker, Skill, 1, 1);	
		}
.

/**
 * The worker cannot start the service because he is busy.
 * @param CNPId: CNPId of the call
 * @param status: service aborted, the worker is already doing another service for someone.
 */
+service(CNPId, aborted)[source(Worker)]
	<-	-worker(CNPId, Worker,_,_);
		!contract(CNPId);
.

/**
 * The worker started the service.
 * The trucker informs to others workers that he's just hired someone.
 * @param CNPId: CNPId of the call
 * @param status: service started, the worker hired some helpers and started the unload process.
 */
+service(CNPId, started)[source(Worker)]: getReceivedOffers(CNPId, Offers) & task(CNPId, task(Task_type,_,_,_))
	<-	.print("Worker: ", Worker, " answered: started, CNPId: ", CNPId);
		-+cnp_state(CNPId, contract);
		!getWorkerSkills(Task_type, Skill);
		!inc_help(Worker, Skill);
		!reject_offers(CNPId, Worker, Offers);
.

/**
 * Define when a truck can star the unload process.
 */
+!unload: getMyName(Me) & visible(true)
	<-	.print("Truck ", Me ," added on the system");
		?cargo_type(Task_type);
		?qtd_things(Number_of_boxes);
		?unload_time(Unload_time);
		actions.trucker.getUrgency(Me, Urgency);
		!start_cnp("provider_worker", task(Task_type, Number_of_boxes, Unload_time, Urgency));
.

+!unload: getMyName(Me) & visible(false).

/**
 * Start the CNP.
 * @param Providers: define the class of agents that will provide the service.
 * @param Task: description of service.
 */	
+!start_cnp(Providers, Task)
	<-	!getNextCNPId(CNPId);
		+cnp_state(CNPId, propose);
		+task(CNPId, Task);
		!call(CNPId, Task, Providers, Participants);
		!bid(CNPId, Participants);
		!contract(CNPId)
.

/**
 * Generate an exclusive CNPId for the task
 * @return CNPID
 */	
@t_1[atomic]
+!getNextCNPId(CNPId): getMyId(Id)
	<-	actions.trucker.getNextCNPId(Id, CNPId);
		.print("NEW CALL - CNPID: ", CNPId);
.

/**
 * Attempt of contracting workers for the service.
 * Case no worker is available, the request for the service is  restarted.
 * @param CNPId: CNPId of the call
 */	
@t_cont1[atomic]
+!contract(CNPId)
	:	getMyName(Me) & 
		cnp_state(CNPId, propose) & 
		getReceivedOffers(CNPId, Offers) &
		task(CNPId, task(Task_type,_,_,_))
		
	<-	if(Offers \== [])	// try to hire a worker
      	{
      		?cargo_type(Task_type);
      		!update_trust(Task_type, Offers);
      		actions.trucker.chooseBestOffer(Offers, Task_type, Winner);
      		!getWorkerSkills(Task_type, Skill)
      		!invite_worker(CNPId, Skill, Winner, Offers);
      	}
      	else	// end the call
      	{
      		.print("It was not posible to find avaliable workers, going out the system.");
      		!end_call(CNPId);	
      	}
.

/**
 * Update trust value for each worker that sent a offer to truck.
 * @param Task_type: type of cargo.
 * @param Offers: list of received offers.
 */
+!update_trust(Task_type, [offer(_, Worker)|T]): getMyName(Me)
	<- 	!getWorkerSkills(Task_type, Skill);
		!computeAvailability(Worker, Skill, Availability);
		?qtd_things(Num_boxes);
		actions.trucker.getUrgency(Me, Urgency);
		actions.generic.getSelfConfident(Me, Self_confident);
		!check_trust(Worker, Skill, Availability, Urgency, Num_boxes, Self_confident);
		!update_trust(Task_type, T);
.

+!update_trust(Task_type, []).

/**
 * Inform to worker that his offer was accepted.
 * @param CNPId: CNPId of the call
 * @param Winner: the worker that wins the call.
 * @param Offers: list of received offers.
 */
+!invite_worker(CNPId, Skill, Winner, [offer(_, Worker)|T]) 
	<-	if(Worker == Winner)
		{
			.send(Worker, tell, accept_proposal(CNPId));
			-proposal(CNPId, team(TeamSize, UnloadTime))[source(Worker)];
			+worker(CNPId, Worker, TeamSize, UnloadTime);
 			!inc_askForHelping(Worker, Skill);   	
		}
		else
		{
      		!invite_worker(CNPId, Skill, Winner, T);
      	}
.
      
+!invite_team(_,_,_,[]).

/**
 * Inform the rejection to workers that weren't selected for job.
 * @param CNPId: CNPId of the call
 * @param Winner: the worker that wins the call.
 * @param Offers: list of received offers.
 */
+!reject_offers(CNPId, Winner, [offer(_, Worker)|T])
	<-	if(Winner \== Worker)
		{
			.print("Rejecting: ", Worker);
			.send(Worker, tell, reject_proposal(CNPId));
			-proposal(CNPId, _)[source(Worker)];			
		}
		!reject_offers(CNPId, Winner, T);
.

+!reject_offers(_,_,[]).

/**
 * The worker informs the end of service.
 * @param CNPId: CNPId of the call
 * @param results: performance data about the service execution.
 */
+report(CNPId, results(Unload_Boxes, Time))[source(Worker)]: worker(CNPId, Worker, TeamSize, UnloadTime) 
	<-	?qtd_things(Number_of_boxes);
		.print("The worker has just finished the service ");		
		.print("REPORT - ", Worker , ": team size: ", TeamSize);
		.print("REPORT - ", Worker ,": number boxes - INITIAL: ", Number_of_boxes, "; number boxes - REAL: ", Unload_Boxes);
		.print("REPORT - ", Worker ,": unload time - ESTIMATED: ", UnloadTime, "; unload time - REAL: ", Time);
		
		actions.generic.evaluation(Number_of_boxes, Unload_Boxes, Total_boxes);
		actions.generic.evaluation(UnloadTime, Time, Total_Time);
		
		?count(C);
		K = C + 1;
		-count(C);
		+count(K);
		
		actions.trucker.getVisibleTruckers(Truckers);
		.print("visible truckers: ", Truckers);
		?cargo_type(Task_type);
		!getWorkerSkills(Task_type, Skill);
		!evaluateProvider(Worker, Skill, ["EXPERTISE", "TIME"], [Total_boxes, Total_Time]);			
		!spread_image(Worker, Skill, Truckers);
		
		-worker(CNPId, Worker,_,_);
		!end_call(CNPId);
.

/*
 * The truck go away (leaves the system)
 */
+!end_call(CNPId): true
	<-	-id(_);
		-pos(_,_);
		-qtd_things(_);
		-cargo_type(_);
		-visible(_);
		-unload_time(_);
		-service(CNPId,_)[source(_)];
		-report(CNPId,_)[source(_)];
		-proposal(CNPId, _)[source(_)];
		-refuse(CNPId)[source(_)];
		-cnp_state(CNPId,_);
		-task(CNPId,_);
		
		?count(C);
		if(C < 3)
		{
			.print("####### ROUND: ", C);
			.send(manager, achieve, quit);
		}
		else
		{
			.print("End of executions");
		}
.