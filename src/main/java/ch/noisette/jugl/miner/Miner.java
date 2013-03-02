package ch.noisette.jugl.miner;

import java.util.List;

public class Miner
{
    private int rows;
    private int columns;
    private final Character[][] miner;
    
    public Miner(final int rows, final int columns, List<String> rowsData)
    {

        this.rows = rows;
        this.columns = columns;
        
        this.miner = new Character[rows][];
        
        for (int r = 0; r < rows; r++)
        {
            miner[r] = new Character[columns];
            
            for (int c = 0; c < columns; c++)
            {
                miner[r][c] = rowsData.get( r ).charAt( c );
            }
        }
        
    }
    
    @Override
    public String toString()
    {
        

        StringBuilder sb = new StringBuilder( rows * columns );
        
        for (int r = 0; r < rows; r++)
        {
            for (int c = 0; c < columns; c++)
            {
                if (miner[r][c].equals('*'))
                {
                    sb.append('*');
                }
                else
                {
                    int count = 0;
                    
                    if (r > 0)
                    {
                        count += count( miner, r - 1, c );
                    }
                    count += count( miner, r, c );
                    if (r < rows - 1)
                    {
                        count += count( miner, r + 1, c );
                    }
                    sb.append( count );
                }
            }
            if (r < rows - 1)
            {
                sb.append("\n");
            }
        }
        
        return sb.toString();
    }
    
    private int count(Character[][] miner, int rowIndex, int columIndex )
    {
        Character[] row = miner[rowIndex];
        
        int count = 0;
        if (columIndex > 0)
        {
            if (row[columIndex - 1].equals('*')) {
                count++;
            }
        }
        if (row[columIndex].equals('*')) {
            count++;
        }
        if (columIndex < row.length - 1)
        {
            if (row[columIndex + 1].equals('*')) {
                count++;
            }
        }
        return count;
    }
}
