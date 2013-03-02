package ch.noisette.jugl.knapsack;

import java.util.HashSet;
import java.util.List;
import java.util.Set;


import org.junit.Assert;
import org.junit.Test;

public class KnapTest {

    /**
     * Test input
     *   [
     *           {
     *                   "name" : "coca-light",
     *                   "value" : 1
     *           },
     *   
     *           {
     *                   "name" : "coca",
     *                   "value" : 138
     *           },
     *   
     *           {
     *                   "name" : "au-travail-a-velo",
     *                   "value" : -113
     *           },
     *   
     *           {
     *                   "name" : "guitar-hero",
     *                   "value" : -181
     *           }
     *   ]
     *
     */
    
    @Test
    public void testSimpleCase()
    {
        
        final Set<Knap.Sac> items = new HashSet<Knap.Sac>();
        
        items.add(new Knap.Sac("i1", 1));
        items.add(new Knap.Sac("i2", 2));
        items.add(new Knap.Sac("i3", -4));
        items.add(new Knap.Sac("i4", -10));
        items.add(new Knap.Sac("i5", 7));
        
        final Knap knap = new Knap();
        
        List<String> selected = knap.solve(items, 0);
        Assert.assertNotNull( selected );
        Assert.assertEquals( 4, selected.size() );
        Assert.assertTrue( selected.contains( "i1" ) );
        Assert.assertTrue( selected.contains( "i2" ) );
        Assert.assertTrue( selected.contains( "i4" ) );
        Assert.assertTrue( selected.contains( "i5" ) );
    }
    
    @Test
    public void testNoSolution()
    {
        
        final Set<Knap.Sac> items = new HashSet<Knap.Sac>();
        
        items.add(new Knap.Sac("i1", 1));
        items.add(new Knap.Sac("i2", 2));
        items.add(new Knap.Sac("i3", 3));
        items.add(new Knap.Sac("i4", 3));
        items.add(new Knap.Sac("i5", 4));
        
        final Knap knap = new Knap();
        
        List<String> selected = knap.solve(items, 0);
        
        Assert.assertNull( selected );
    }
}
