/* MODULES *************/

{ include("src/asl/modules/basicModule.asl") }			// rules and plans for social protocols
{ include("src/asl/modules/contractingModule.asl") }	// rules and plans for contracting a service
{ include("src/asl/modules/socialModule.asl") }			// rules and plans for social evaluations

/* RULES *************/

/**
 * This rule computes the number of answers received from workers about a proposal of service.
 * @param CNPId: id of call
 * @param Answers: number of expected answers. 
 * */ 
received_answers(CNPId, Answers) 
	:-	.count(service(CNPId, accepted)[source(_)], Accepted) &
		.count(service(CNPId, rejected)[source(_)], Aborted) &
		Answers == (Accepted + Aborted).

/* BEHAVIOR *************/

request_counter(0). // counter to control the number of tasks that will be requested.
max_requests(2).    // limit for the number of requesting that can be done.
!start.             // signal to begin

+!start: getMyName(Me)
	<-	scenario_unloadBoxes.actions.trucker.initialize(Me);
		!register("requester_trucker");
.

/**
 * When a trucker receives a proposal or a refuse from a worker, this trucker adds the worker as a friend.
 * A friend is an agent with who the trucker interacted at least once.
 * @param CNPId: id of call.
 * @param Offer: offer received from a worker.
 */
+proposal(CNPId,_)[source(Worker)]: task(CNPId, task(Task_type,_,_,_))
	<-	!getWorkerSkills(Task_type, Skill);
		!initializeAvailability(Worker, Skill);
        !addFriend(Worker);
.

+refuse(CNPId)[source(Worker)]: task(CNPId, task(Task_type,_,_,_))
	<-	!getWorkerSkills(Task_type, Skill);
        !initializeAvailability(Worker, Skill);
        !addFriend(Worker);
.

/**
 * Reject answers of workers out of deadline
 * @param CNPId: CNPId of the call
 * @param status: service accepted, the worker want to begin the service.
 */
+service(CNPId, accepted)[source(Worker)]: deadline(CNPId, false)
    <-  !rejectWorker(CNPId, Worker)
.

/**
 * A worker informs the end of service.
 * @param CNPId: CNPId of the call.
 * @param results: performance data about the executed service.
 */
+report(CNPId, results(Team_size, Estimated_time, Delivered_boxes, Real_time))[source(Worker)] 
	<-	?cargo_amount(Boxes_amount);
        ?cargo_type(Task_type);
		.print("[REPORT]: ", Worker, "{team size: ", TeamSize, ", estimated time: ", Estimated_time, ", real time: ", Real_time,
        ", taken boxes: ", Boxes_amount, ", delivered boxes: ", Delivered_boxes, "}");		
		
        // Social evaluation
		scenario_unloadBoxes.actions.generic.evaluation(Boxes_amount, Delivered_boxes, Total_boxes);
		scenario_unloadBoxes.actions.generic.evaluation(Estimated_time, Real_time, Total_time);
		scenario_unloadBoxes.actions.trucker.getVisibleTruckers(Truckers);
		!getWorkerSkills(Task_type, Skill);
		!evaluateProvider(Worker, Skill, ["EXPERTISE", "TIME"], [Total_boxes, Total_time]);			
		!spreadImage(Worker, Skill, Truckers);
		
        // Clear memory
        -+cnp_state(CNPId, finished);
        -task(CNPId,_);
        -report(CNPId,_)[source(Worker)];
        !deleteAllPoposals(CNPId)
        !deleteAllRefuses(CNPId);
        !exit;
.

/**
 * Getting a possible skill from a worker.
 * @param: type of task.
 * @return the skill associated to worker based on task type
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
 * Generate an exclusive CNPId for a task
 * @return a CNPID
 */	
@t_1[atomic]
+!getNextCNPId(CNPId): getMyId(Id)
	<-	scenario_unloadBoxes.actions.trucker.getNextCNPId(Id, CNPId);
.

/**
 * Update trust value for each worker that sent an offer to truck.
 * @param Task_type: type of cargo.
 * @param Offers: list of received offers.
 */
+!updateTrust(Task_type, [offer(_, Worker)|T]): getMyName(Me)
	<- 	?cargo_amount(Boxes_amount);
        ?task_urgency(Task_urgency);
        ?self_confident(Confident_profile);
        !getWorkerSkills(Task_type, Skill);
		!computeAvailability(Worker, Skill, Availability);
		!checkTrust(Worker, Skill, Availability, Task_urgency, Boxes_amount, Confident_profile);
		!updateTrust(Task_type, T);
.

+!updateTrust(Task_type, []).

/**
 * Starting a task requisition.
 * A trucker only may begin a task requisition if it is visible on the system.
 */
+!unload: getMyName(Me) & visible(true)
	<-	.print("[NEW ENTRY]: Truck: ", Me ," was added to the system");
		?cargo_type(Task_type);
		?cargo_amount(Boxes_amount);
        ?task_urgency(Task_urgency);
		!startCNP("provider_worker", task(Task_type, Boxes_amount, Task_urgency));
.

+!unload: getMyName(Me) & visible(false).

/**
 * Starting the contract-net-protocol (CNP).
 * Every time a CNP is started, a new call for a task is sent to service providers.
 * @param Providers: set of agents that will provide a service (performing the task).
 * @param Task: service description.
 */	
+!startCNP(Providers, Task)
	<-	!getNextCNPId(CNPId);
		+cnp_state(CNPId, propose);
		+task(CNPId, Task);
		!call(CNPId, Task, Providers, Participants);
		!bid(CNPId, Participants);
		!contract(CNPId);
.

/**
 * Attempt of contracting workers for a task.
 * Case, there are no workers available, the trucker ends the call end go out from the system.
 * @param CNPId: id of call.
 */	
+!contract(CNPId)
	:	getMyName(Me) & 
		cnp_state(CNPId, propose) & 
		getReceivedOffers(CNPId, Offers) &
		task(CNPId, task(Task_type,_,_))
		
	<-	if(Offers \== [])
      	{
      		!updateTrust(Task_type, Offers);
            scenario_unloadBoxes.actions.trucker.evaluateOffers(Me, Offers, Task_type, workers(Winners, Losers));
            !rejectWorkers(CNPId, Losers);
            
            if(Winners \== [])
            {
                +deadline(CNPId, true);
                !getWorkerSkills(Task_type, Skill);
                !inviteWinners(CNPId, Skill, Winners);
                .length(Winners, Answers);
                .wait(received_answers(CNPId, Answers), 4000,_);
                -+deadline(CNPId, false);
                
                .findall(Worker, service(CNPId, accepted)[source(Worker)], Candidates);

                if(Candidates \== [])
                {      		                    
                    !acceptWinner(CNPId, Skill, Candidates, Winners, Winner);
                    !rejectOthers(CNPId, Winner, Winners);
                    -+cnp_state(CNPId, contract);
                }
                else
                {
                    !rejectWorkers(CNPId, Winners);
                    .print("No worker answered my invite, so I'm going out from the system.");            
                    -+cnp_state(CNPId, interrupted);
                    -task(CNPId,_);
                    !deleteAllPoposals(CNPId)
                    !deleteAllRefuses(CNPId);
                    !exit;
                }
            }
            else
            {
                .print("It wasn't possible to find trustworthiness workers, so I'm going out from the system.");
                -+cnp_state(CNPId, aborted);
                -task(CNPId,_);
                !deleteAllPoposals(CNPId);
                !deleteAllRefuses(CNPId);
                !exit;
            }
      	}
      	else
      	{
      		.print("It wasn't possible to find available workers, so I'm going out from the system.");            
		    -+cnp_state(CNPId, canceled);
		    -task(CNPId,_);
			!deleteAllRefuses(CNPId);	    
            !exit;
      	}
.

/**
 * Inform to winners that their offers was accepted.
 * If the list of winners is empty, the trucker goes out the system because the lack of good partners.
 * @param CNPId: id of call.
 * @param Skill: the skill for which the worker is evaluated.
 * @param Winners: list of workers that won the call.
 */
+!inviteWinners(CNPId, Skill, [Worker|T]) 
	<-	.send(Worker, tell, can_start(CNPId));
        !requestHelp(Winner, Skill);
  		!inviteWinners(CNPId, Skill, T);
.

+!inviteWinners(CNPId,_,[]).

/**
 * Inform that a worker was accepted to do a task.
 * @param CNPId: CNPId of the call
 * @param Skill: The skill for which the worker is evaluated.
 * @param Worker: the worker will be accepted.
 */
+!acceptWorker(CNPId, Skill, Worker)
    <-  .print("Hiring : ", Worker);
		!helping(Worker, Skill);
        .send(Worker, tell, accept_proposal(CNPId));
		-proposal(CNPId,_)[source(Worker)];
        -service(CNPId, accepted)[source(Worker)];
.

/**
 * Inform the rejection to a given worker.
 * @param CNPId: CNPId of the call
 * @param Worker: the worker will be rejected.
 */
+!rejectWorker(CNPId, Worker)
    <-  .print("Rejecting: ", Worker);
		.send(Worker, tell, reject_proposal(CNPId));
		-proposal(CNPId,_)[source(Worker)];
        -service(CNPId,_)[source(Worker)];
.

/**
 * Inform the accept to the best worker.
 * @param CNPId: CNPId of the call
 * @param Skill: The skill for which the worker is evaluated.
 * @param Candidates: list of workers that answered the call 'can_start'
 * @param Winners: list of worker that was pre-accepted to the task.
 * @return the chosen worker.
 */
+!acceptWinner(CNPId, Skill, Candidates, [Worker|T], Winner)
    <-  if(.member(Worker, Candidates))
        {
           	!acceptWorker(CNPId, Skill, Worker)
            Winner = Worker;
        }
        else
        {
            !acceptWinner(CNPId, Skill, Candidates, T, Winner);
        }
.

+!acceptWinner(CNPId, Skill, Candidates, [], Winner).

/**
 * Inform the rejection to workers that lost the call.
 * @param CNPId: CNPId of the call
 * @param Losers: list of worker that lost the call.
 */
+!rejectWorkers(CNPId, [Worker|T])
	<-	!rejectWorker(CNPId, Worker);
		!rejectWorkers(CNPId, T);
.

+!rejectWorkers(_,[]).

/**
 * Inform the rejection to winners not selected.
 * @param CNPId: CNPId of the call.
 * @param Winner: the worker that won the call.
 * @param Others: list of winners that weren't selected.
 */
+!rejectOthers(CNPId, Winner, [Worker|T])
    <-  if(Winner \== Worker)
        {
       		!rejectWorker(CNPId, Worker);
            !rejectOthers(CNPId, Winner, T);
        }
.

+!rejectOthers(CNPId, Winner, []).

/**
 * Check the end of execution.
 */	
+!exit: getMyName(Me)
    <-  -+visible(false);
    	?request_counter(Current_Value);
        ?max_requests(Max_value)

	    if(Current_Value < Max_value)
	    {
		    .print("####### [DONE] REQUEST NUMBER ", Current_Value, " #######");
		    .send(manager, achieve, quit);
	    }
	    else
	    {
		    .send(manager, tell, stop(Me));
		    
		    // Final report 
		    .count(cnp_state(CNPId, interrupted), Interrupted);
		    .count(cnp_state(CNPId, finished), Finished);
		    .count(cnp_state(CNPId, aborted), Aborted);
		    .count(cnp_state(CNPId, canceled), Canceled);
		    
		    .print("[FINAL REPORT] Number of tasks: ", Finished + Canceled + Aborted + Interrupted, 
		    	", Finished tasks: ", Finished, 
		    	", Canceled tasks: ", Canceled,
		    	", Aborted tasks: ", Aborted,
		    	", Interrupted tasks: ", Interrupted);
	    }
        -+request_counter(Current_Value + 1);
.