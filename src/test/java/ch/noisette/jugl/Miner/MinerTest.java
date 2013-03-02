package ch.noisette.jugl.Miner;

import java.util.Arrays;

import org.junit.Assert;
import org.junit.Test;

import ch.noisette.jugl.miner.Miner;

public class MinerTest
{

    @Test
    public void test1()
    {
        final Miner miner = new Miner(4, 4, Arrays.asList( new String[] { ".*.*", "....", "..**", "*..." } ) );
        Assert.assertEquals( "1*2*\n1243\n12**\n*222", miner.toString() );
    }

    
    @Test
    public void test2()
    {
        final Miner miner = new Miner(3, 4, Arrays.asList( new String[] { "****", "****", "****" } ) );
        Assert.assertEquals( "****\n****\n****", miner.toString() );
    }

    @Test
    public void test3()
    {
        final Miner miner = new Miner(1, 1, Arrays.asList( new String[] { "*" } ) );
        Assert.assertEquals( "*", miner.toString() );
    }
    
    @Test
    public void test4()
    {
        final Miner miner = new Miner(1, 1, Arrays.asList( new String[] { "." } ) );
        Assert.assertEquals( "0", miner.toString() );
    }

    @Test
    public void test5()
    {
        final Miner miner = new Miner(3, 4, Arrays.asList( new String[] { "****", "....", "****" } ) );
        Assert.assertEquals( "****\n4664\n****", miner.toString() );
    }

    @Test
    public void test6()
    {
        final Miner miner = new Miner(3, 4, Arrays.asList( new String[] { "*.*.", ".*.*", "*.*." } ) );
        Assert.assertEquals( "*3*2\n3*4*\n*3*2", miner.toString() );
    }

    
}
