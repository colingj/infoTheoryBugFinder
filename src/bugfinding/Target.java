package bugfinding;

import java.util.*;

public class Target
{
    boolean[] tt;
    
    public Target(String type, ArrayList<Object> params)
    {
        if (type.equals("OddParity"))
        {
            //odd parity arbitrary size
            int size = (Integer)params.get(0);
            int noInputs = (int) Math.pow(2, size);             
            boolean[][] boolStrings = new boolean[noInputs][size];
            boolStrings = BoolUtils.generateBoolSequencesBigEndian(size);
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
        else if (type.equals("3BitMux"))
        {
            tt = new boolean[]{false,false,true,true,false,true,false,true};
        }
        else if (type.equals("Mux"))
        {
            //addressSize is number of address variables
            int addressSize = (Integer)params.get(0);
            int size = addressSize + (int)Math.pow(2,addressSize);
            int noInputs = (int) Math.pow(2, size);             
            boolean[][] boolStrings = new boolean[noInputs][size];
            boolStrings = BoolUtils.generateBoolSequencesBigEndian(size);
            tt = new boolean[noInputs];
            for (int i=0;i<noInputs;i++)
            {
                int idx = 0;
                for (int j=0;j<addressSize;j++)
                {
                    idx += (int)Math.pow(2,j)*(boolStrings[i][j]?1:0);
                }
                tt[i] = boolStrings[i][addressSize+idx];
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
