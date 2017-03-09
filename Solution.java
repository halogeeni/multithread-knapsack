package aleksirasio.advancedalgorithms.lab06.multithread;

import java.util.Set;
import java.util.TreeSet;

public class Solution implements Comparable<Solution> {
    private Set<Item> items;
    private int value, weight;

    public Solution() {
        this.items = new TreeSet<>();
        this.value = 0;
        this.weight = 0;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Optimal solution found:\n");
        sb.append("Value: ");
        sb.append(value);
        sb.append("\tWeight: ");
        sb.append(weight);
        sb.append("\nItems:");
        for(Item i : items) {
            sb.append(" ");
            sb.append(i.getId());
        }
        return sb.toString();
    }

    // helper methods for adding items

    public void addItem(Item i) {
        items.add(i);
        value += i.getValue();
        weight += i.getWeight();
    }

    public void addItems(Set<Item> items) {
        this.items.addAll(items);
        for(Item i : items) {
            value += i.getValue();
            weight += i.getWeight();
        }
    }

    // getters & setters

    public Set<Item> getItems() {
        return items;
    }

    public void setItems(Set<Item> items) {
        this.items = items;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public int getWeight() {
        return weight;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }

    @Override
    public int compareTo(Solution o) {
        return o.getValue() - this.getValue();
    }
}
