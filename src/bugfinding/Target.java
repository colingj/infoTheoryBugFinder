package bugfinding;

import java.util.*;

public class Target
{
    boolean[] tt;
    
    public Target(String type, ArrayList<Object> params)
    {
        if (type.equals("4BitOddParity"))
        {
            tt = new boolean[]{
            false,true,true,false,
            true,false,false,true,
            true,false,false,true,
            false,true,true,false};
        }
        else if (type.equals("OddParity"))
        {
            //odd parity arbitrary size
            int size = (Integer)params.get(0);
            int noInputs = (int) Math.pow(2, size);             
            boolean[][] boolStrings = new boolean[noInputs][size];
            boolStrings = BoolUtils.generateBoolSequences(size);
            tt = new boolean[noInputs];
            for (int i=0;i<noInputs;i++)
            {
                int total = 0;
                for (int j=0;j<size;j++)
                {
                    if (boolStrings[i][j]) { total++; }
                }
                if (total%2==1) { tt[i] = true; }
            }
        }
        else 
        {
            System.err.println("In Target, type not known.");
            System.exit(1);
        }
    }
    
    public boolean get(int i)
    {
        return tt[i];
    }
    
    @Override
    public String toString()
    {
        String ans = new String();
        for (boolean b: tt)
        {
            ans += b+" ";
        }
        ans += "\n";
        return ans;
    }
}
