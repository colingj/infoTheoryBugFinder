package bugfinding;

import java.io.*;
import java.util.ArrayList;

public class Test2App
{
    public static void main(String[] args)
            throws java.io.IOException, java.io.FileNotFoundException
    {
    /** set up the program **/
    
    BufferedReader br 
           // = new BufferedReader(new FileReader("myPrograms/8BitOddParity.pp"));
            = new BufferedReader(new FileReader("myPrograms/3BitMux.pp"));
    String progString="",currentLine;
    while ((currentLine = br.readLine()) != null)  { progString += currentLine+"\n"; }
    
    /** set up the target **/    
        
    ArrayList<Object> params = new ArrayList<Object>();
    params.add(8);
    //Target tt = new Target("OddParity", params);
    Target tt = new Target("3BitMux", null);
    
    /** set up and run the bugfinder **/
    
    BugFinder bf = new BugFinder(progString,tt);
    bf.localizeBug();
    System.out.println(bf.getResults());
    }
}
