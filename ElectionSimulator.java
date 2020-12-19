// Name: Mitchell Stapelman
// Section: DG
// Represents an ElectionSimulator that finds the fewest number 
// of popular votes required to win the Electoral College/presidency for an election year.

import java.util.*;
import java.io.*;

public class ElectionSimulator {
    private Map<Arguments, Set<State>> combinations;
    private List<State> states;

    // Constructs an ElectionSimulator object with the given list of states.
    public ElectionSimulator(List<State> states) {
        this.states = states;
        this.combinations = new HashMap<Arguments, Set<State>>();
    }

    // Returns the set of states that require the least/minimum number
    // of popular votes required to win the election.
    public Set<State> simulate() {
        return simulateHelper(minElectoralVotes(states), 0);
    }  

    private Set<State> simulateHelper(int electoralVote, int index) {
        Arguments argument = new Arguments(electoralVote, index);

        // Checks if combination is already in map.
        if (combinations.containsKey(argument)) { // basecase 1
            return combinations.get(argument);
        }
        // Checks if electoralVote is <= 0 and returns an empty set.
        else if (electoralVote <= 0) { // basecase 2
            return new HashSet<State>();
        }// If index exceeds the size of list, return null.
        else if (index >= states.size()) { // basecase 3
            return null;
        }
        
        Set<State> stateSetWith = simulateHelper(electoralVote - 
                                  states.get(index).electoralVotes, index + 1);

        if (stateSetWith == null) {
            return null;
        }
        Set<State> stateSetWithout = simulateHelper(electoralVote, index + 1);

        if (stateSetWithout != null && stateSetWith == null) {
            return stateSetWithout;
        }

        // Copy of with set to prevent set corruption
        Set<State> stateSetWithCopy = new HashSet<State>(stateSetWith);
        stateSetWithCopy.add(states.get(index));
        if (stateSetWithout == null || minPopularVotes(stateSetWithCopy) < minPopularVotes(stateSetWithout)) {
            combinations.put(argument, stateSetWithCopy);
            return stateSetWithCopy;
        }
        return stateSetWithout;
     
    }
    







    public static int minElectoralVotes(List<State> states) {
        int total = 0;
        for (State state : states) {
            total += state.electoralVotes;
        }
        return total / 2 + 1;
    }

    public static int minPopularVotes(Set<State> states) {
        int total = 0;
        for (State state : states) {
            total += state.popularVotes / 2 + 1;
        }
        return total;
    }

    private static class Arguments implements Comparable<Arguments> {
        public final int electoralVotes;
        public final int index;

        public Arguments(int electoralVotes, int index) {
            this.electoralVotes = electoralVotes;
            this.index = index;
        }

        public int compareTo(Arguments other) {
            int cmp = Integer.compare(this.electoralVotes, other.electoralVotes);
            if (cmp == 0) {
                cmp = Integer.compare(this.index, other.index);
            }
            return cmp;
        }

        public boolean equals(Object o) {
            if (this == o) {
                return true;
            } else if (!(o instanceof Arguments)) {
                return false;
            }
            Arguments other = (Arguments) o;
            return this.electoralVotes == other.electoralVotes && this.index == other.index;
        }

        public int hashCode() {
            return Objects.hash(electoralVotes, index);
        }
    }

    public static void main(String[] args) throws FileNotFoundException {
        List<State> states = new ArrayList<>(51);
        try (Scanner input = new Scanner(new File("data/1828.csv"))) {
            while (input.hasNextLine()) {
                states.add(State.fromCsv(input.nextLine()));
            }
        }
        Set<State> result = new ElectionSimulator(states).simulate();
        System.out.println(result);
        System.out.println(minPopularVotes(result) + " votes");
    }
}
