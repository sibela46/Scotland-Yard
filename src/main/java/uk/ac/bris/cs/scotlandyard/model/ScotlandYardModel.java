package uk.ac.bris.cs.scotlandyard.model;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import static java.util.Arrays.asList;
import static java.util.Collections.emptySet;
import static java.util.Collections.singletonList;
import static java.util.Collections.unmodifiableCollection;
import static java.util.Collections.unmodifiableList;
import static java.util.Collections.unmodifiableSet;
import static java.util.Objects.requireNonNull;
import static uk.ac.bris.cs.scotlandyard.model.Colour.Black;
import static uk.ac.bris.cs.scotlandyard.model.Ticket.Double;
import static uk.ac.bris.cs.scotlandyard.model.Ticket.Secret;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;
import uk.ac.bris.cs.gamekit.graph.Edge;
import uk.ac.bris.cs.gamekit.graph.Graph;
import uk.ac.bris.cs.gamekit.graph.ImmutableGraph;
import uk.ac.bris.cs.gamekit.graph.Graph;

// TODO implement all methods and pass all tests
public class ScotlandYardModel implements ScotlandYardGame {
	
	public PlayerConfiguration mrX, firstDetective;
	public List<Boolean> rounds;
	public Graph<Integer, Transport> graph;
	public ArrayList<PlayerConfiguration> restOfTheDetectives;

	public ScotlandYardModel(List<Boolean> rounds, Graph<Integer, Transport> graph,
			PlayerConfiguration mrX, PlayerConfiguration firstDetective,
			PlayerConfiguration... restOfTheDetectives) {
		
		this.mrX = requireNonNull(mrX);
		if(!mrX.colour.isMrX()) throw new IllegalArgumentException("No MrX.");
		
		this.firstDetective = requireNonNull(firstDetective);
		if(this.firstDetective.colour.isMrX()) throw new IllegalArgumentException("More than one MrX.");
		if(this.mrX.location == this.firstDetective.location) throw new IllegalArgumentException("Detective has the same location as MrX.");
		if(this.firstDetective.tickets.get(Double) != 0) throw new IllegalArgumentException("Detective has double tickets.");
		if(this.firstDetective.tickets.get(Secret) != 0) throw new IllegalArgumentException("Detective has a secret ticket."); //checks if detective has a secret ticket.
		if(this.firstDetective.tickets.isEmpty()) throw new IllegalArgumentException("Detective has no tickets.");
		
		for (PlayerConfiguration one : restOfTheDetectives){
			if(this.firstDetective.colour.equals(one.colour)) throw new IllegalArgumentException("This detective already exists.");
			if(one.colour.isMrX()) throw new IllegalArgumentException("More than one MrX.");
			if(this.mrX.location == one.location) throw new IllegalArgumentException("Detective has the same location as MrX.");
			this.restOfTheDetectives.add(requireNonNull(one));
		}
		
		this.rounds = requireNonNull(rounds);
		this.graph = requireNonNull(graph);
		
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
		// TODO
		throw new RuntimeException("Implement me");
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
		// TODO
		throw new RuntimeException("Implement me");
	}

	@Override
	public Colour getCurrentPlayer() {
		// TODO
		throw new RuntimeException("Implement me");
	}

	@Override
	public int getCurrentRound() {
		// TODO
		throw new RuntimeException("Implement me");
	}

	@Override
	public boolean isRevealRound() {
		// TODO
		throw new RuntimeException("Implement me");
	}

	@Override
	public List<Boolean> getRounds() {
		// TODO
		throw new RuntimeException("Implement me");
	}

	@Override
	public Graph<Integer, Transport> getGraph() {
		// TODO
		throw new RuntimeException("Implement me");
	}

}
