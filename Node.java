import java.util.Arrays;

public class Node {
    int city, cost, count;
    boolean[] visited;

    public Node(int city, int cost, boolean[] visited) {
        this(city, cost, visited, 1);
    }

    public Node(int city, int cost, boolean[] visited, int count) {
        this.city = city;
        this.cost = cost;
        this.visited = visited;
        this.count = count;
    }
}