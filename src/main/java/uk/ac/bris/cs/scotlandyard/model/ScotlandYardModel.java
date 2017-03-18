package uk.ac.bris.cs.scotlandyard.model;

import java.util.*;

import static java.util.Arrays.asList;
import static java.util.Collections.emptySet;
import static java.util.Collections.singletonList;
import static java.util.Collections.unmodifiableCollection;
import static java.util.Collections.unmodifiableList;
import static java.util.Collections.unmodifiableSet;
import static java.util.Objects.requireNonNull;
import static uk.ac.bris.cs.scotlandyard.model.Colour.*;
import static uk.ac.bris.cs.scotlandyard.model.Ticket.*;

import java.util.function.Consumer;
import java.util.concurrent.CopyOnWriteArrayList;

import com.sun.xml.internal.ws.policy.privateutil.PolicyUtils;
import uk.ac.bris.cs.gamekit.graph.Edge;
import uk.ac.bris.cs.gamekit.graph.Graph;
import uk.ac.bris.cs.gamekit.graph.ImmutableGraph;
import uk.ac.bris.cs.gamekit.graph.Graph;

public class ScotlandYardModel implements ScotlandYardGame {
	
	public List<Boolean> rounds;
	public int currentRound;
	public Graph<Integer, Transport> graph;
	public List<PlayerConfiguration> players;
	public HashMap<Colour, Integer> colours;
	public Colour currentPlayer;
	public Set<Colour> winningPlayers;
	
	public ScotlandYardModel(List<Boolean> rounds, Graph<Integer, Transport> graph,
			PlayerConfiguration mrX, PlayerConfiguration firstDetective,
			PlayerConfiguration... restOfTheDetectives) {
		
		players = new ArrayList<>();
		colours = new HashMap<>();
		colours.put(Black, 0);
		colours.put(Blue, 0);
		colours.put(Red, 0);
		colours.put(Green, 0);
		colours.put(Yellow, 0);
		colours.put(White, 0);
		
		this.rounds = requireNonNull(rounds);
		this.currentRound = 0; //initialised with 0 at first, will be changed later.
		this.graph = requireNonNull(graph);
		this.currentPlayer = Colour.Black;
		players.add(requireNonNull(mrX));
		players.add(requireNonNull(firstDetective));
		for(PlayerConfiguration current : restOfTheDetectives)
			players.add(requireNonNull(current));
		
		ValidMapCheck();
		ValidPlayersCheck();	// Should it be a throw function -- sisi, tova e za men da pitam
		ValidTicketSlotsCheck();
		ValidDetectiveTicketsCheck();

	}

	// Checks whether the graph and visible rounds are empty
	private void ValidMapCheck(){
		if(rounds.isEmpty()) throw new IllegalArgumentException("Rounds is empty");
		if(graph.isEmpty()) throw new IllegalArgumentException("Graph is empty");
	}
	
	//Checks if the players already exists and whether their position overlap with mrX
	private void ValidPlayersCheck (){
		if(!players.get(0).colour.isMrX()) throw new IllegalArgumentException("MrX is missing");
		
		for(PlayerConfiguration current : players){
			Colour currentColour = current.colour;
			colours.put(currentColour, colours.get(currentColour) + 1);
			if(colours.get(currentColour) > 1) throw new IllegalArgumentException("Players duplicate");
		}
		ValidPositionsCheck();
	}
	
	private void ValidPositionsCheck(){ // is this a good idea or it's not dry enough? -- pak iskam da pitam tam
		Iterator<PlayerConfiguration> detectives = players.listIterator(1);
		while(detectives.hasNext()){
			PlayerConfiguration detective = detectives.next();
			if(detective.location == players.get(0).location) 
				throw new IllegalArgumentException("Detective has spawned with the same location as mrX");
		}
	}
	
	//Checks whether all the players have all the ticket slots
	private void ValidTicketSlotsCheck(){
		for(PlayerConfiguration current : players){
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
		Iterator<PlayerConfiguration> detectives = players.listIterator(1);
		while(detectives.hasNext()){
			PlayerConfiguration detective = detectives.next();
			if(detective.tickets.get(Double) != 0) throw new IllegalArgumentException("Detective has double tickets");
			if(detective.tickets.get(Secret) != 0) throw new IllegalArgumentException("Detective has secret tickets");
		}
	}
	
	@Override
	public void registerSpectator(Spectator spectator) {
		// TODO
		throw new RuntimeException("Implement me");
	}

	@Override
	public void unregisterSpectator(Spectator spectator) {
		// TODO
		throw new RuntimeException("Implement me");
	}

	@Override
	public void startRotate() {
		// TODO
		throw new RuntimeException("Implement me");
	}

	@Override
	public Collection<Spectator> getSpectators() {
		// TODO
		throw new RuntimeException("Implement me");
	}

	@Override
	public List<Colour> getPlayers() {
		// TODO
		throw new RuntimeException("Implement me");
	}

	@Override
	public Set<Colour> getWinningPlayers() {
		return Collections.unmodifiableSet(this.winningPlayers);
	}

	@Override
	public int getPlayerLocation(Colour colour) {
		// TODO
		throw new RuntimeException("Implement me");
	}

	@Override
	public int getPlayerTickets(Colour colour, Ticket ticket) {
		// TODO
		throw new RuntimeException("Implement me");
	}

	@Override
	public boolean isGameOver() {
		return false; //will have to change this later
	}

	@Override
	public Colour getCurrentPlayer() {
		return this.currentPlayer;
	}

	@Override
	public int getCurrentRound() {
		return this.currentRound;
	}

	@Override
	public boolean isRevealRound() {
		// TODO
		throw new RuntimeException("Implement me");
	}

	@Override
	public List<Boolean> getRounds() {
		return Collections.unmodifiableList(rounds);
	}

	@Override
	public Graph<Integer, Transport> getGraph() {
		return new ImmutableGraph<>(graph);
	}

}
