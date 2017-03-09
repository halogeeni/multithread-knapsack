package aleksirasio.advancedalgorithms.lab06.multithread;

public class Item implements Comparable<Item> {
    private final int id, value, weight;

    public Item(int id, int value, int weight) {
        this.id = id;
        this.value = value;
        this.weight = weight;
    }

    public int getId() {
        return id;
    }

    public int getValue() {
        return value;
    }

    public int getWeight() {
        return weight;
    }

    @Override
    public int compareTo(Item o) {
        return this.getId() - o.getId();
    }
}
