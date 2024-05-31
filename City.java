public class City {
    int x, y;

    public City(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public int distanceTo(City other) {
        return (int) Math.round(Math.sqrt((this.x - other.x) * (this.x - other.x) + (this.y - other.y) * (this.y - other.y)));
    }

    @Override
    public String toString() {
        return "(" + x + ", " + y + ")";
    }
}