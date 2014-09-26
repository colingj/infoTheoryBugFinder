package bugfinding;

import java.io.*;
import java.util.ArrayList;

public class Test2App
{
    public static void main(String[] args)
            throws java.io.IOException, java.io.FileNotFoundException
    {
        System.out.println("Running Test2App\n");
        /** set up the program and target **/
        
        BufferedReader br;
        ArrayList<Object> params = new ArrayList<Object>();    
        Target tt;
        
        /*
        br = new BufferedReader(new FileReader("myPrograms/8BitOddParity.pp"));
        params.add(8);
        tt = new Target("OddParity", params);        
        */
        
        br = new BufferedReader(new FileReader("myPrograms/11BitMux.pp"));    
        params.add(3);
        tt = new Target("Mux", params);        
        
        /** set up and run the bugfinder **/
        
        String progString="",currentLine;
        while ((currentLine = br.readLine()) != null)  { progString += currentLine+"\n"; }    
        BugFinder bf = new BugFinder(progString,tt,"IGR");
        bf.localizeBug();
        System.out.println(bf.getResults());
    }
}
