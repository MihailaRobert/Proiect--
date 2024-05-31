import java.util.*;

public class TSP {

    public static void main(String[] args) {
        TSP tsp = new TSP();

        // Set de date non-triviale
        System.out.println("Set de date non-trivial:");
        runAndMeasureTime(tsp, createNonTrivialDataSet());

        // Seturi de date din TSP-LIB
        List<List<City>> tspLibData = createTSPDataSets();

        for (int i = 0; i < tspLibData.size(); i++) {
            System.out.println("\nSet de date din TSP-LIB " + (i + 1) + ":");
            runAndMeasureTime(tsp, tspLibData.get(i));
        }
    }

    static List<City> createNonTrivialDataSet() {
        List<City> cities = new ArrayList<>();
        cities.add(new City(0, 0));
        cities.add(new City(1, 2));
        cities.add(new City(3, 1));
        cities.add(new City(2, 4));
        cities.add(new City(5, 3));
        return cities;
    }

    static List<List<City>> createTSPDataSets() {
        List<List<City>> tspLibData = new ArrayList<>();

        tspLibData.add(Arrays.asList(new City(0, 0), new City(2, 3), new City(5, 1), new City(4, 6), new City(1, 5)));
        tspLibData.add(Arrays.asList(new City(0, 0), new City(3, 2), new City(6, 4), new City(5, 7), new City(8, 6)));

        return tspLibData;
    }

    static void runAndMeasureTime(TSP tsp, List<City> cities) {
        // Afisare grila cu pozitiile oraselor
        System.out.println("Pozitiile oraselor pe grila:");
        tsp.displayGrid(cities);

        long startTime, endTime;
        int result;

        startTime = System.nanoTime();
        result = tsp.exhaustiveSearch(cities);
        endTime = System.nanoTime();
        System.out.println("Exhaustive Search: " + result + " - Time: " + (endTime - startTime) + " nanoseconds");

        startTime = System.nanoTime();
        result = tsp.dfsSearch(cities);
        endTime = System.nanoTime();
        System.out.println("DFS Search: " + result + " - Time: " + (endTime - startTime) + " nanoseconds");

        startTime = System.nanoTime();
        result = tsp.uniformSearch(cities);
        endTime = System.nanoTime();
        System.out.println("Uniform Cost Search: " + result + " - Time: " + (endTime - startTime) + " nanoseconds");

        startTime = System.nanoTime();
        result = tsp.aStarSearch(cities);
        endTime = System.nanoTime();
        System.out.println("A* Search: " + result + " - Time: " + (endTime - startTime) + " nanoseconds");
    }

    int[][] computeDistances(List<City> cities) {
        int n = cities.size();
        int[][] dist = new int[n][n];
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                dist[i][j] = cities.get(i).distanceTo(cities.get(j));
            }
        }
        return dist;
    }

    int exhaustiveSearch(List<City> cities) {
        int n = cities.size();
        int[][] dist = computeDistances(cities);
        List<Integer> route = new ArrayList<>();
        for (int i = 0; i < n; i++) route.add(i);

        int minCost = Integer.MAX_VALUE;
        do {
            int cost = 0;
            for (int i = 0; i < n - 1; i++) {
                cost += dist[route.get(i)][route.get(i + 1)];
            }
            cost += dist[route.get(n - 1)][route.get(0)];
            minCost = Math.min(minCost, cost);
        } while (nextPermutation(route));

        return minCost;
    }

    int dfsSearch(List<City> cities) {
        int n = cities.size();
        int[][] dist = computeDistances(cities);
        boolean[] visited = new boolean[n];
        return dfs(dist, visited, 0, 1, 0, Integer.MAX_VALUE);
    }

    int dfs(int[][] dist, boolean[] visited, int current, int count, int cost, int minCost) {
        int n = dist.length;
        if (count == n && dist[current][0] > 0) {
            minCost = Math.min(minCost, cost + dist[current][0]);
            return minCost;
        }

        for (int i = 0; i < n; i++) {
            if (!visited[i] && dist[current][i] > 0) {
                visited[i] = true;
                minCost = dfs(dist, visited, i, count + 1, cost + dist[current][i], minCost);
                visited[i] = false;
            }
        }
        return minCost;
    }

    int uniformSearch(List<City> cities) {
        int n = cities.size();
        int[][] dist = computeDistances(cities);
        PriorityQueue<Node> pq = new PriorityQueue<>(Comparator.comparingInt(a -> a.cost));
        pq.add(new Node(0, 0, new boolean[n]));
        int minCost = Integer.MAX_VALUE;

        while (!pq.isEmpty()) {
            Node node = pq.poll();
            if (node.count == n && dist[node.city][0] > 0) {
                minCost = Math.min(minCost, node.cost + dist[node.city][0]);
                continue;
            }

            for (int i = 0; i < n; i++) {
                if (!node.visited[i] && dist[node.city][i] > 0) {
                    boolean[] visited = Arrays.copyOf(node.visited, n);
                    visited[i] = true;
                    pq.add(new Node(i, node.cost + dist[node.city][i], visited, node.count + 1));
                }
            }
        }

        return minCost;
    }

    int aStarSearch(List<City> cities) {
        int n = cities.size();
        int[][] dist = computeDistances(cities);
        PriorityQueue<Node> pq = new PriorityQueue<>(Comparator.comparingInt(a -> a.cost + heuristic(a, dist, cities)));
        pq.add(new Node(0, 0, new boolean[n]));
        int minCost = Integer.MAX_VALUE;

        while (!pq.isEmpty()) {
            Node node = pq.poll();
            if (node.count == n && dist[node.city][0] > 0) {
                minCost = Math.min(minCost, node.cost + dist[node.city][0]);
                continue;
            }

            for (int i = 0; i < n; i++) {
                if (!node.visited[i] && dist[node.city][i] > 0) {
                    boolean[] visited = Arrays.copyOf(node.visited, n);
                    visited[i] = true;

               pq.add(new Node(i, node.cost + dist[node.city][i], visited, node.count + 1));
        }
        }
        }

        return minCost;
    }

int heuristic(Node node, int[][] dist, List<City> cities) {
    int remainingCost = 0;
    boolean[] visited = node.visited.clone();
    int currentCity = node.city;

    for (int i = 0; i < cities.size(); i++) {
        int closestDist = Integer.MAX_VALUE;
        int closestCity = -1;
        for (int j = 0; j < cities.size(); j++) {
            if (!visited[j] && dist[currentCity][j] < closestDist) {
                closestDist = dist[currentCity][j];
                closestCity = j;
            }
        }
        if (closestCity != -1) {
            remainingCost += closestDist;
            visited[closestCity] = true;
            currentCity = closestCity;
        }
    }

    remainingCost += dist[currentCity][0];

    return remainingCost;
}

boolean nextPermutation(List<Integer> nums) {
    int i = nums.size() - 2;
    while (i >= 0 && nums.get(i) >= nums.get(i + 1)) i--;
    if (i == -1) return false;

    int j = nums.size() - 1;
    while (nums.get(j) <= nums.get(i)) j--;

    Collections.swap(nums, i, j);
    Collections.reverse(nums.subList(i + 1, nums.size()));
    return true;
}

void displayGrid(List<City> cities) {
    int maxX = cities.stream().mapToInt(c -> c.x).max().orElse(0);
    int maxY = cities.stream().mapToInt(c -> c.y).max().orElse(0);

    char[][] grid = new char[maxY + 1][maxX + 1];
    for (char[] row : grid) {
        Arrays.fill(row, '.');
    }

    for (int i = 0; i < cities.size(); i++) {
        City city = cities.get(i);
        grid[city.y][city.x] = (char) ('A' + i);
    }

    for (char[] row : grid) {
        for (char cell : row) {
            System.out.print(cell + " ");
        }
        System.out.println();
    }
}
}
