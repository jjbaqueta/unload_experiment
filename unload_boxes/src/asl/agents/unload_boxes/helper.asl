/* MODULES *************/

{ include("src/asl/modules/basicModule.asl") }		// rules and plans for social protocols
{ include("src/asl/modules/providingModule.asl") }	// rules and plans for providing a service
{ include("src/asl/modules/socialModule.asl") }		// rules and plans for social evaluations

task_counter(0).
!start.

/*
 * Set initial beliefs for helper
 */
+!start: getMyName(Me)
	<-	scenario_unloadBoxes.actions.helper.initialize(Me);
		!register("provider_helper", "requester_worker");
		+busy(false);
		?safety(Safety_default);
		?battery(Battery_default);
		+current_safety(Safety_default);
		+current_battery(Battery_default);
		+taken_boxes(0);
		+delivered_boxes(0);
.

/* BEHAVIOR  **********************/

/**
 * The helper moves to the truck indicated by requester agent (a worker).
 * When the helper arrives at the truck position, he takes some boxes.
 */
+!goToTruck: truck(Truck)
	<-	!at(Truck);
		!takeBoxesFrom(Truck);
		?carrying(Carried_boxes);
		?taken_boxes(Taken_boxes);
		-+taken_boxes(Taken_boxes + Carried_boxes);
		.print("Number of boxes taken from truck: ", Carried_boxes);
		!goToDepot;
.

/**
 * One helper at time takes an amount of boxes from truck.
 * @param Truck: the truck indicated by requester agent (a worker)
 */
@h_b1[atomic]
+!takeBoxesFrom(Truck): getMyName(Me)
	<-	scenario_unloadBoxes.actions.helper.takeBoxes(Me, Truck);
.

/**
 * The helper moves to the indicated depot by requester agent (a worker).
 * When the helper arrives at the depot position, he drops the boxes off there.
 */
+!goToDepot: empty_truck(false) & depot(Depot)
	<-	!at(Depot);
		?carrying(Carried_boxes);
		?delivered_boxes(Delivered_boxes);
		-+delivered_boxes(Delivered_boxes + Carried_boxes);
		-+carrying(0);
		!checkBattery;
		!checkFailure;
		!goToTruck;
.

// Check if the service was concluded.
+!goToDepot: client(CNPId, _) & empty_truck(true)  
	<-	!goToGarage;
		!finish_service(CNPId);
.

/**
 * The helper moves to the nearest garage from his current position.
 * When a helper arrives at a garage, he recharges his battery and receives maintenance.
 * In this case, the recharging of battery and the maintenance process don't have cost for helper.
 */
+!goToGarage: safety(Safety_default) & battery(Battery_default)
	<-	.findall(garage(Name), garage(Name), Garages);
		!getTheNearestFromMe(Garages, Garage);	
		!at(Garage);
		-+current_safety(Safety_default);
		-+current_battery(Battery_default);
.

/**
 * The helper moves to the nearest recharge point from his current position.
 * If the helper stops his task to recharge his battery, he is penalized. This process cost 2 seconds.
 */
+!goToRecharge: battery(Battery_default)
	<-	.findall(recharge(Name), recharge(Name), Recharges);
		!getTheNearestFromMe(Recharges, Recharge);
		!at(Recharge);
		.wait(2000);
		-+current_battery(Battery_default);
.

/**
 * Check if the battery level of agent is low.
 */
+!checkBattery
	<-	?current_battery(Battery);
		?energy_cost(Cost);
		Battery_level = Battery - Cost;
		-+current_battery(Battery_level);
		
		if(Battery_level <= 0)
		{
			.print("I don't have battery. I'm going to a recharge point.");
			!goToRecharge;
		}
		.print("My battery level is: ", Battery_level);
.

/**
 * Check if there is something wrong (a failure).
 * When the helper failures he goes to the garage, he receives maintenance and is penalized at 6 seconds.  
 */
+!checkFailure: failure_prob(Probability)
	<-	.random(P);
		?current_safety(Count);
		C = Count - 1;
		-+current_safety(C)
		
		if(P <= Probability & C <= 0)
		{
			.print("I broken, I will stop for maintenance. I'm going to the garage.");
			!goToGarage;
			.wait(6000);
		}
		.print("My safety count is: ", C);
.

/**
 * Check if a box fell on the floor and the type of box.
 * If the box that fell to the ground is fragile, this box is destroyed and the helper is penalized at 1 second.
 * Otherwise, if the box is common, the agent is just penalized at 1 second.
 */
+!checkAccident: dexterity(Dex) & cargo_type(Type)
	<-	.random(D);
		
		if(D > Dex)
		{
			.print("ACCIDENT: I dropped a box on the floor.");
			
			if(Type == fragile)
			{
				?carrying(Carried_boxes);
				-+carrying(Carried_boxes - 1);
			}
			.wait(1000);
		}
.

/**
 * When the helper is carrying one or more boxes and he is moving, he must check if an accident happened.
 * The accidents happen when helpers fell a box to the ground.
 * The frequency of accidents depends on the helper's dexterity level.
 * @param somewhere: the current position of helper.
 */
+at(somewhere): carrying(Carried_boxes) & Carried_boxes > 0
	<- 	!checkAccident
.

 /* *************************************
  * COMMUNICATION 
  ***************************************/

/**
 * Answer to call for proposal.
 * @param CNPId: id of required service.
 * @param Task: the service to be done.
 */
+cfp(CNPId, task(Truck, Depot, Cargo_type))[source(Worker)]
	:	provider(Worker, "requester_worker") & 
		getMyName(Me) &
		busy(false) &
		not client(_,_)[source(_)]
		
	<-	scenario_unloadBoxes.actions.helper.proposeOffer(Me, Truck, Depot, Offer);
		+my_proposal(CNPId, task(Truck, Depot, Cargo_type), Offer);
      	.send(Worker, tell, proposal(CNPId, Offer));
      	
      	if(Cargo_type == fragile)
		{
			!sendMyknowHow("FRAGILE_LOADER", Worker);		
		}
		else
		{
			!sendMyknowHow("COMMON_LOADER", Worker);	
		}
		-cfp(CNPId,_)[source(Worker)];	
.

+cfp(CNPId, Task)[source(Worker)]: provider(Worker, "requester_worker") & busy(true)
	<-	.send(Worker, tell, refuse(CNPId));
		-cfp(CNPId,_)[source(Worker)];
.

/**
 * The agent won the CNP and he must perform the requested task.
 * After won the CNP, the helper keeps waiting for the execution signal from worker.
 * @param CNPId: id of required service.
 */
@h_cnp1 [atomic]
+accept_proposal(CNPId)[source(Worker)]
	:	provider(Worker, "requester_worker") & 
		my_proposal(CNPId, _, _) & 
		busy(false) &
		not client(_,_)[source(_)]
		
	<-	-+busy(true);
		.print("My proposal was accepted by worker: ", Worker);
		.send(Worker, tell, service(CNPId, accepted));
		-accept_proposal(CNPId)[source(Worker)];
.

/**
 * The agent won the CNP, but he is already busy and must reject the service.
 * @param CNPId: id of required service.
 */
+accept_proposal(CNPId)[source(Worker)]: busy(true)
	<-	.print("My proposal was accepted by ", Worker ,", but I'm already busy.");
		.send(Worker, tell, service(CNPId, aborted));
		-my_proposal(CNPId,_,_);
		-accept_proposal(CNPId)[source(Worker)];
.

/**
 * The agent lost the CNP, so he must clear his memory
 * @param CNPId: id of required service 
 */
 @h_cnp3 [atomic]
+reject_proposal(CNPId)[source(Worker)]
   <-	.print("I lost CNP ", CNPId, ".");
		-my_proposal(CNPId,_,_);
		-reject_proposal(CNPId)[source(Worker)];
 .

/**
 * Execution signal for the helper starts the service.
 * @param CNPId: id of required service.
 */
+execute(CNPId)[source(Worker)]
	<-	+client(CNPId, Worker);
		!start_service(CNPId);
		-execute(CNPId)[source(Worker)];
.

/**
 * The helper is free to accept offers from other workers.
 * This situation represents the scenario where the worker lost the CNP.
 * Thus, he cancels the contract with helper.
 * @param CNPId: id of required service.
 * @param service_status: the current status of service.
 */ 
 @h_cnp4 [atomic]
+service(CNPId, canceled)[source(Worker)]
	<-	-my_proposal(CNPId,_,_);
		-+busy(false);
		-service(CNPId, _)[source(Worker)];
.

/**
 * The helper stars the service.
 * @param CNPId: id of required service.
 */	
+!start_service(CNPId):	my_proposal(CNPId, task(Truck, Depot, Cargo_type), _)
	<-	+truck(Truck);
		+depot(Depot);
		+empty_truck(false);
		+cargo_type(Cargo_type);
		-+taken_boxes(0);
		-+delivered_boxes(0);
		scenario_unloadBoxes.actions.generic.getTime(Time);
		+task_time(Time);
		!goToTruck;
.

/**
 * The helper finishes the service and informs it to worker (requester).
 * @param CNPId: id of required service.
 */
+!finish_service(CNPId)
	:	delivered_boxes(Delivered_boxes) & 
		taken_boxes(Taken_boxes) & 
		client(CNPId, Client)
		
	<-	?task_time(Stime);
		?task_counter(TC);
		-+task_counter(TC + 1);
		
		scenario_unloadBoxes.actions.generic.getTime(Ftime);
		Time = Ftime - Stime;
		
		.print("[REPORT]: Boxes taken: ", Taken_boxes, 
			", Boxes delivered: ", Delivered_boxes, 
			", Task Time: ", Time);
			
		.send(Client, tell, report(CNPId, results(Delivered_boxes, Taken_boxes, Time)));			
		-truck(_);
		-depot(_);
		-empty_truck(_);
		-cargo_type(_);
		-+taken_boxes(0);
		-+delivered_boxes(0);
		-task_time(_);
		-client(CNPId,_);
		-my_proposal(CNPId,_,_);
		-+busy(false);
.