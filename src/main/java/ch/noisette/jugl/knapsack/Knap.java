package ch.noisette.jugl.knapsack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

public class Knap {

    public static class Sac implements Comparable<Sac>
    {
        private final String name;
        private final int weight;
        
        public Sac(final String name, final int weight)
        {
            this.name = name;
            this.weight = weight;
        }
        
        public String getName()
        {
            return name;
        }
        
        public int getWeight()
        {
            return weight;
        }

        @Override
        public int compareTo(Sac arg0) {
            if (weight < arg0.weight)
            {
                return -1;
            }
            else if (weight > arg0.weight)
            {
                return 1;
            }
            return 0;
        }
    }
    
    public List<String> solve(Set<Sac> items, int maxWeigth)
    {
        Sac[] arrays = new Sac[items.size()];
        
        int i = 0;
        for (Sac item: items)
        {
            arrays[i] = item;
            i++;
        }
        
        Arrays.sort(arrays);
        
        int[] selected = stack(arrays, new int[0], 0, maxWeigth);
        
        if (selected == null)
        {
            return null;
        }
        List<String> results = new ArrayList<String>(selected.length);
        for (int j = 0; j < selected.length; j++)
        {
            results.add( arrays[selected[j]].name );
        }
        return results;
    }
    
    private int[] stack(Sac[] items, int[] stack, int currentWeight, int maxWeight)
    {
        int firstIndex = 0;
        if (stack.length > 0) {
            firstIndex = stack[stack.length - 1] + 1;
        }
        for (int i = firstIndex; i < items.length; i++)
        {
            int newWeight = currentWeight + items[i].weight;
            if (newWeight > maxWeight)
            {
                return null;
            }
            
            int[] newStack = new int[stack.length + 1];
            System.arraycopy(stack, 0, newStack, 0, stack.length);
            newStack[newStack.length - 1] = i;
            
            if (newWeight == maxWeight)
            {
                return newStack;
            }
            else
            {
                int[] returnedStack = stack(items, newStack, newWeight, maxWeight);
                if (returnedStack != null)
                {
                    return returnedStack;
                }
            }
        }
        return null;
    }
}
