package bugfinding;

public class Target
{
    boolean[] tt;
    
    public Target(String type)
    {
        if (type.equals("4BitOddParity"))
        {
            tt = new boolean[]{
            false,true,true,false,
            true,false,false,true,
            true,false,false,true,
            false,true,true,false};
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
}
