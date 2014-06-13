package bugfinding;

import java.util.*;

public class BugFinder
{
    MultiTrace mt;
    Program pp;
    boolean bugFound; //a bug has been found
    int bugLocation; //location of first bug
    
    
    public BugFinder(String progString, Target tt)
    {
        pp = new Program(progString);
        int numberOfTestCases = (int)Math.pow(2,pp.getNumberOfVariables());
        mt = new MultiTrace(numberOfTestCases,pp);
        mt.generateOnCompleteSet();
        mt.generateDifference(tt);
        System.out.println();
        mt.generateCompressionLengths();
        bugFound = false;
    }
    
    public void localizeBug()
    {
        //mostRecentlySeen stores the entropy value
        // for the most recent line at which a particular
        // variable or function has changed value
        //for functional-style programs this will be a 
        // write-once data structure
        Map<String,Integer> mostRecentlySeen
                = new HashMap<String,Integer>();
        for (int pLine=0;pLine<pp.getLength();pLine++)
        {
            Expression ee = pp.getFunction(pLine);
            int cc = mt.getCompressionLength(pLine);
            mostRecentlySeen.put(ee.getName(),cc);
            if (ee.isFunction())
            {
                System.out.println("***");
                ArrayList<String> inputs = ee.getInputs();
                int maxCompress = Integer.MIN_VALUE;
                for (String s: inputs)
                {
                    System.out.println("input is "+s
                            +" with value "+mostRecentlySeen.get(s));
                    int val = mostRecentlySeen.get(s);
                    if (val>maxCompress) { maxCompress = val; }
                }
                System.out.println("minCompress is"+maxCompress);
                System.out.println("cc is"+cc);
                if (cc>=maxCompress)
                {
                    bugFound = true;
                    bugLocation = pLine;
                    break;
                }
            }
        }
    }
    
    public String getResults()
    {
        String ans = new String();
        if (bugFound)
        {
            ans += mt.toStringCompressionLengthsAndProgramLines(bugLocation);
        }
        else
        {
            ans += mt.toStringCompressionLengthsAndProgramLines();
        }
        ans += "\n";
        ans += "Bug has been found? "+bugFound+"\n";
        if (bugFound)
        {
            ans += "First bug found at substantive line "+bugLocation+"\n\n";
        }
        ans += "The program is correct? "+mt.isCorrect()+"\n";
        return ans;
    }
}
