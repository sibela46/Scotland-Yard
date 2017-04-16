package uk.ac.bris.cs.scotlandyard.model;

import uk.ac.bris.cs.gamekit.graph.Edge;
import uk.ac.bris.cs.gamekit.graph.Graph;
import uk.ac.bris.cs.gamekit.graph.ImmutableGraph;

import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Consumer;

import static java.util.Objects.requireNonNull;
import static uk.ac.bris.cs.scotlandyard.model.Colour.*;
import static uk.ac.bris.cs.scotlandyard.model.Ticket.*;
import static uk.ac.bris.cs.scotlandyard.model.Ticket.Double;

public class ScotlandYardModel implements ScotlandYardGame, Consumer<Move> {
	
	private final List<Boolean> rounds;
	private final Graph<Integer, Transport> graph;
	private int lastMrLocation;
	private final List<PlayerConfiguration> configurations;
	private Colour currentPlayer;
	private int currentRound;
	private Set<Colour> winningPlayers;
	private List<ScotlandYardPlayer> players;
	private final List<Colour> players_asColours;
	private final MoveVisitor ticketLogic;
	private Boolean mrXisCaptured;
	private final List<Spectator> spectators;
	private final ScotlandYardModel view;
	
	public ScotlandYardModel(List<Boolean> rounds, Graph<Integer, Transport> graph,
			PlayerConfiguration mrX, PlayerConfiguration firstDetective,
			PlayerConfiguration... restOfTheDetectives) {
		
		winningPlayers = new HashSet<>();
		spectators = new CopyOnWriteArrayList<>();
		
		view = this;
		this.rounds = requireNonNull(rounds);
		currentRound = NOT_STARTED;
		this.graph = requireNonNull(graph);
		
		configurations = new ArrayList<>();
		configurations.add(requireNonNull(mrX));
		configurations.add(requireNonNull(firstDetective));
		for(PlayerConfiguration current : restOfTheDetectives)
			configurations.add(requireNonNull(current));
				
		ValidMapCheck();
		ValidConfigurationsCheck();
		ValidTicketSlotsCheck();
		ValidDetectiveTicketsCheck();
		
		players = new ArrayList<>();
		for(PlayerConfiguration current : configurations)
			players.add(new ScotlandYardPlayer(current.player, current.colour, current.location, current.tickets));
		
		players_asColours = new ArrayList<>();
		for(ScotlandYardPlayer current : players)
			players_asColours.add(current.colour());

		currentPlayer = players_asColours.get(0);
		lastMrLocation = 0; 
		mrXisCaptured = false;
		
		ticketLogic = new MoveVisitor(){
			ScotlandYardPlayer mrX = getCurrentPlayerFromColour(Black);
			
			public void visit(PassMove move){
				for(Spectator current : spectators)
					current.onMoveMade(view, move);
			}
			public void visit(TicketMove move){
				ScotlandYardPlayer curPlayer = getCurrentPlayerFromColour(currentPlayer);
				
				curPlayer.location(move.destination());	//changes to new location	
				curPlayer.removeTicket(move.ticket());	//removes the ticket, which was used
				
				if(currentPlayer.isDetective()){		//gives the ticket to mrX when a detective moves
					mrX.addTicket(move.ticket());	
					if(curPlayer.location() == mrX.location())
						mrXisCaptured = true;
					
					for (Spectator current : spectators){
						current.onMoveMade(view, move);
					}
				}
				if(currentPlayer.isMrX()) {
					TicketMove mrXHiddenMove = new TicketMove(curPlayer.colour(), move.ticket(), lastMrLocation);
					currentRound++;
					
					for (Spectator current : spectators){
						current.onRoundStarted(view, currentRound);
						if (isRevealRound()) current.onMoveMade(view, move);
						else current.onMoveMade(view, mrXHiddenMove);
					}
				}
				
				
			}
			public void visit(DoubleMove move){
				currentRound++;
				
				notifyOnRevealRound(move);
				firstMoveOfDoubleMove(move);
				secondMoveOfDoubleMove(move);
				
				getCurrentPlayerFromColour(currentPlayer).location(move.finalDestination());	//moves mrX location
				mrX.removeTicket(Double);	//removes the ticket used
		    }
			
			public void notifyOnRevealRound(DoubleMove move){
				
				if(isRevealRound()) { //FIRST MOVE IS REVEAL ROUND.
					currentRound++;
					if (isRevealRound()){ //SECOND MOVE IS REVEAL ROUND.
						currentRound = currentRound - 2;
						for(Spectator current : spectators)
							current.onMoveMade(view, move);
					}
					else { //SECOND MOVE IS HIDDEN ROUND.
						currentRound = currentRound - 2;
						for(Spectator current : spectators){
							current.onMoveMade(view, new DoubleMove(Black, move.firstMove(), new TicketMove(Black, move.secondMove().ticket(), move.firstMove().destination())));
						}
					}
				}

				else { //FIRST MOVE IS HIDDEN ROUND.
					currentRound++;
					if(isRevealRound()){ //SECOND MOVE IS REVEAL ROUND.
						currentRound = currentRound - 2;
						for(Spectator current : spectators){
							current.onMoveMade(view, new DoubleMove(Black, new TicketMove(Black, move.firstMove().ticket(), lastMrLocation), move.secondMove()));		
						}
					}
					else { //SECOND MOVE IS HIDDEN ROUND.
						currentRound = currentRound - 2;
						for(Spectator current : spectators){
							current.onMoveMade(view, new DoubleMove(Black, move.firstMove().ticket(), lastMrLocation, move.secondMove().ticket(), lastMrLocation));
						}
					}
				}
			}

			private void firstMoveOfDoubleMove(DoubleMove move){
				TicketMove mrXhiddenMove = new TicketMove(mrX.colour(), move.firstMove().ticket(), lastMrLocation);
				mrX.removeTicket(move.firstMove().ticket());
				currentRound++;
				mrX.location(move.firstMove().destination());
				if (isRevealRound()) lastMrLocation = mrX.location();
				for (Spectator current : spectators){
					current.onRoundStarted(view, currentRound);
					if(isRevealRound()) current.onMoveMade(view, move.firstMove());
					else current.onMoveMade(view, mrXhiddenMove);	
				}
			}
			
			private void secondMoveOfDoubleMove(DoubleMove move){
				TicketMove mrXhiddenMove = new TicketMove(mrX.colour(), move.secondMove().ticket(), lastMrLocation);
				mrX.removeTicket(move.secondMove().ticket());
				currentRound++;
				mrX.location(move.secondMove().destination());
				if (isRevealRound()) lastMrLocation = mrX.location();
				for (Spectator current : spectators){
					current.onRoundStarted(view, currentRound);
					if(isRevealRound()) current.onMoveMade(view, move.secondMove());
					else current.onMoveMade(view, mrXhiddenMove);
				}
			}
		};
	}

	// Checks whether the graph and visible rounds are empty
	private void ValidMapCheck(){
		if(rounds.isEmpty()) throw new IllegalArgumentException("Rounds is empty");
		if(graph.isEmpty()) throw new IllegalArgumentException("Graph is empty");
	}
	
	//Checks if the players already exists and whether their position overlaps with mrX
	private void ValidConfigurationsCheck (){
		if(!configurations.get(0).colour.isMrX()) throw new IllegalArgumentException("MrX is missing");

		HashMap<Colour, Integer> colours = new HashMap<>();
		colours.put(Black, 0);
		colours.put(Blue, 0);
		colours.put(Red, 0);
		colours.put(Green, 0);
		colours.put(Yellow, 0);
		colours.put(White, 0);
		
		for(PlayerConfiguration current : configurations){
			Colour currentColour = current.colour;
			colours.put(currentColour, colours.get(currentColour) + 1);
			if(colours.get(currentColour) > 1) throw new IllegalArgumentException("Players duplicate");
		}
		ValidPositionsCheck();
	}
	
	private void ValidPositionsCheck(){ 
		Iterator<PlayerConfiguration> detectives = configurations.listIterator(1);
		while(detectives.hasNext()){
			PlayerConfiguration detective = detectives.next();
			if(detective.location == configurations.get(0).location) 
				throw new IllegalArgumentException("Detective has spawned with the same location as mrX");
		}
	}
	
	//Checks whether all the players have all the ticket slots
	private void ValidTicketSlotsCheck(){
		for(PlayerConfiguration current : configurations){
			Map<Ticket, Integer> tickets = current.tickets;
			if(!(tickets.containsKey(Bus)  	  	 &&
				 tickets.containsKey(Taxi) 	  	 &&
				 tickets.containsKey(Underground)&&
				 tickets.containsKey(Double)     &&
				 tickets.containsKey(Secret)))
					throw new IllegalArgumentException("One of the players doesn't have all tickets!");	
			
		}
	}
	
	//Checks whether the detective has double/secret tickets
	private void ValidDetectiveTicketsCheck(){
		Iterator<PlayerConfiguration> detectives = configurations.listIterator(1);
		while(detectives.hasNext()){
			PlayerConfiguration detective = detectives.next();
			if(detective.tickets.get(Double) != 0) throw new IllegalArgumentException("Detective has double tickets");
			if(detective.tickets.get(Secret) != 0) throw new IllegalArgumentException("Detective has secret tickets");
		}
	}
	
	@Override
	public void registerSpectator(Spectator spectator) {
		requireNonNull(spectator);
		if(!spectators.contains(spectator)){
			this.spectators.add(spectator);
		}
		else throw new IllegalArgumentException("This spectator is already registered.");
	}

	@Override
	public void unregisterSpectator(Spectator spectator) {
		requireNonNull(spectator);
		if(!spectators.isEmpty()){
			this.spectators.remove(spectator);
		}
		else throw new IllegalArgumentException("No spectators to unregister.");	
	}

	@Override
	public void startRotate() {
		currentPlayer = players.get(0).colour();
		if(isGameOver() == true)
			throw new IllegalStateException("Game is not over initially.");
		makeMoveCurrentPlayer();
	}
	
	public Set<Move> validMoves(ScotlandYardPlayer player){
		Set<Move> valid = new HashSet<>();
		Collection<Edge<Integer, Transport>> edgesFromLocation = graph.getEdgesFrom(graph.getNode(player.location()));

		if(player.isDetective()){
				valid.addAll(AddTicketMoves(player, edgesFromLocation));
				if(valid.isEmpty()) 							// produces a pass for the detective if no moves are available
					valid.add(new PassMove(player.colour()));
			}

		
		else if(player.isMrX()){
				valid.addAll(AddTicketMoves(player, edgesFromLocation));
				
				if(player.hasTickets(Secret))													
					valid.addAll(AddSecretMoves(player, edgesFromLocation));
				
				if(player.hasTickets(Double) && getCurrentRound() < getRounds().size() - 1 )	//the if also checks if there are enough rounds to play a double move
					valid.addAll(AddDoubleMoves(player, valid));	
		}
		return valid;
	}
	
	private Set<Move> AddTicketMoves(ScotlandYardPlayer player, Collection<Edge<Integer, Transport>> edgesFromLocation){
		Set<Move> ticketMoves = new HashSet<>();

		for(Edge<Integer, Transport> currentEdge : edgesFromLocation){
			int destination = currentEdge.destination().value();
			
			if(player.hasTickets(fromTransport(currentEdge.data())) && !LocationOccupiedByDetective(destination)){
				TicketMove move = new TicketMove(player.colour(), fromTransport(currentEdge.data()), destination);
				ticketMoves.add(move);
				}
		}
		return ticketMoves;
	}
	
	private Set<Move> AddSecretMoves(ScotlandYardPlayer player, Collection<Edge<Integer, Transport>> edgesFromLocation){
		Set<Move> ticketMoves = new HashSet<>();

		for(Edge<Integer, Transport> currentEdge : edgesFromLocation){
			int destination = currentEdge.destination().value();
			
			if(player.hasTickets(Secret) && !LocationOccupiedByDetective(destination)){
				TicketMove move = new TicketMove(player.colour(), Secret, destination);
				ticketMoves.add(move);
				}
		}
		return ticketMoves;
	}
	
	private Set<Move> AddDoubleMoves(ScotlandYardPlayer player, Set<Move> firstSetOfMoves){
		Set<Move> ticketMoves = new HashSet<>();
		
		for(Move move : firstSetOfMoves){
			TicketMove tMove = (TicketMove)move;
			player.removeTicket(tMove.ticket()); 	// It removes the ticket used for the first move so that we can check that we are not using the same ticket twice
			int destination = tMove.destination();
			Collection<Edge<Integer, Transport>> edgesFromDestination = graph.getEdgesFrom(graph.getNode(destination));
			
			for(Edge<Integer, Transport> currentEdge : edgesFromDestination){
				int destinationLocation = currentEdge.destination().value();
				if(!LocationOccupiedByDetective(destinationLocation)){
					if(player.hasTickets(fromTransport(currentEdge.data()))){
						DoubleMove doubleMove = new DoubleMove (player.colour(), tMove.ticket(), destination, 
																				fromTransport(currentEdge.data()), destinationLocation);
						ticketMoves.add(doubleMove);
					}
					if(player.hasTickets(Secret)  && destinationLocation != destination){
						DoubleMove doubleMove = new DoubleMove (player.colour(), tMove.ticket(), destination, 
																				Secret, destinationLocation);
						
						ticketMoves.add(doubleMove);
					}
				}
			}
			player.addTicket(tMove.ticket());	// returns the ticket that we removed so we don't change anything
		}
		
		return ticketMoves;
	}

	private Boolean LocationOccupiedByDetective(int location){
		Boolean occupied = false;
		
			for (ScotlandYardPlayer detective : players)
				if (detective.isDetective())
					if(detective.location() == location)
						occupied = true;
		
		return occupied;
	}
	
	@Override
	public void accept(Move move) {
		if(!validMoves(getCurrentPlayerFromColour(currentPlayer)).contains(requireNonNull(move))) throw new IllegalArgumentException("Invalid move, move not contained in validMoves");		
		move.visit(ticketLogic);
		
		if(currentPlayer == players_asColours.get(players_asColours.size() - 1))	// makes sure to stop switching players after the rotation is done
			for(Spectator current : spectators){
				current.onRotationComplete(view);
				if (isGameOver()) current.onGameOver(view, winningPlayers);
			}
		
		else if(mrXisCaptured){
				for(Spectator current : spectators){
					current.onGameOver(view, winningPlayers);
				}
			}
			
		else {
			nextPlayer();
			makeMoveCurrentPlayer();
		}
	}
	
	private void nextPlayer(){
			currentPlayer = players_asColours.get(players_asColours.indexOf(currentPlayer) + 1);
	}
	
	private void makeMoveCurrentPlayer(){
    	ScotlandYardPlayer player = getCurrentPlayerFromColour(currentPlayer);
    	player.player().makeMove(this, player.location(), validMoves(player), this);  	
	}
	
	@Override
	public Collection<Spectator> getSpectators() {
		return Collections.unmodifiableList(spectators);
	}

	@Override
	public List<Colour> getPlayers() {
		return Collections.unmodifiableList(players_asColours);
	}

	@Override
	public Set<Colour> getWinningPlayers() {
		return Collections.unmodifiableSet(winningPlayers);
	}

	@Override
	public int getPlayerLocation(Colour colour) {
		if(colour.isMrX()){
			if(isRevealRound()){
				lastMrLocation = getCurrentPlayerFromColour(colour).location();
				return getCurrentPlayerFromColour(colour).location();
			}
			else
				return lastMrLocation;
		}
		
		return getCurrentPlayerFromColour(colour).location();
	}

	@Override
	public int getPlayerTickets(Colour colour, Ticket ticket) {
		return getCurrentPlayerFromColour(colour).tickets().get(ticket);
	}

	@Override
	public boolean isGameOver() {
		boolean detectivesAreStuck = true;
		Set<Colour> detectives = new HashSet<>();
		
		for(int i=1; i<players.size(); i++){ // iterating through the detectives only
			if(!validMoves(players.get(i)).contains(new PassMove(players.get(i).colour())))
				detectivesAreStuck = false;
			detectives.add(players.get(i).colour());	// just getting the detectives if they win
		}
		
		if(mrXisCaptured || validMoves(players.get(0)).isEmpty()){
			winningPlayers = detectives;
			return true;
		}
		
		if(detectivesAreStuck || currentRound > rounds.size() - 1){
			winningPlayers.add(Black);
			return true;
		}
		
			return false;
	}
	
	@Override
	public Colour getCurrentPlayer() {
		return this.currentPlayer;
	}

	@Override
	public int getCurrentRound() {
		return currentRound;
	}

	@Override
	public boolean isRevealRound() {
		if(currentRound == 0) return false;
		else return rounds.get(currentRound - 1);
	}

	@Override
	public List<Boolean> getRounds() {
		return Collections.unmodifiableList(rounds);
	}

	@Override
	public Graph<Integer, Transport> getGraph() {
		return new ImmutableGraph<>(graph);
	}

	private ScotlandYardPlayer getCurrentPlayerFromColour(Colour colour){
		return players.get(players_asColours.indexOf(colour));
	}
}
