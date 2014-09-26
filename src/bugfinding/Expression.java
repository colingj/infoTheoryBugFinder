package bugfinding;

import java.util.*;

public class Expression
{
    String name;
    String f;
    ArrayList<String> inputs;
    int lineNumber;
    
    public Expression(String namep, String fp, ArrayList<String> inputsp,
            int lineNumberp)
    {
        name = namep;
        f = fp;
        if (inputsp==null)
        {
            inputs = null;
        }
        else
        {
        inputs = new ArrayList<String>(inputsp); //using copy constructor
        }
        lineNumber = lineNumberp;
    }
    
    public boolean isFunction()
    {
        return (!(f.equals("VAR")||f.equals("OUT")));
    }
    
    public boolean eval(ArrayList<Boolean> i)
    {
        if (f.equals("AND")) { return i.get(0)&&i.get(1); }
        else if (f.equals("OR")) { return i.get(0)||i.get(1); }
        else if (f.equals("XOR")) { return ((i.get(0)&& !i.get(1)) 
                || (i.get(1) && !i.get(0))); }
        else if (f.equals("NOT")) { return !i.get(0); }
        else if (f.equals("IF")) { return i.get(0)?i.get(1):i.get(2); }
        else { System.err.println("Error in Expression."); System.exit(1); }
        return true;//unreachable default
    }
    
    public boolean isVar() { return f.equals("VAR"); }
    
    public String getVar()
    {
        if (!isVar()) {  System.err.println("Error in Expression."); System.exit(1); }
        else { return name; }
        return "unreachable";
    }
    
    public boolean isOutput() { return f.equals("OUT"); }
    
    public String getOutput()
    {
        if (!isOutput()) {  System.err.println("Error in Expression."); System.exit(1); }
        else { return name; }
        return "unreachable";
    }
    
    public String getName()
    {
        return name;
    }
    
    public ArrayList<String> getInputs()
    {
        return inputs;
    }
    
    public int getLineNumber()
    {
        return lineNumber;
    }
    
    @Override
    public String toString()
    {
        String ans = new String();
        ans += "( "+name+" "+f+" ";
        if (inputs!=null) { for (String s:inputs) { ans += s+" "; } }
        ans += ")";
        return ans;
    }
    
}
