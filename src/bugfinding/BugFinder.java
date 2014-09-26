package bugfinding;

import java.text.DecimalFormat;
import java.util.*;

public class BugFinder
{
    MultiTrace mt;
    Program pp;
    boolean bugFound; //a bug has been found
    int bugLocation; //location of first bug
    String type;
    
    public BugFinder(String progString, Target tt, String typep)
    {
        type = typep;
        pp = new Program(progString);
        int numberOfTestCases = (int)Math.pow(2,pp.getNumberOfVariables());
        mt = new MultiTrace(numberOfTestCases,pp);
        mt.generateOnCompleteSet();
        //System.out.println();
        //System.out.println(mt);
        //System.out.println();
        mt.generateDifference(tt);
        if (type.equals("compression"))
        {
            mt.generateCompressionLengths();
        }
        else if (type.equals("IGR"))
        {
            mt.generateIGR();
        }
        else { System.err.println("Error in BugFinder."); System.exit(1); }
        bugFound = false;
    }
    
    public void localizeBug()
    {
        if (type.equals("compression"))
        {
            localizeBugUsingCompression();
        }
        else if (type.equals("IGR"))
        {
            localizeBugUsingIGR();
        }
        else { System.err.println("Error in BugFinder."); System.exit(1); }
    }
    
    private void localizeBugUsingCompression()
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
                //System.out.println("***");
                ArrayList<String> inputs = ee.getInputs();
                int maxCompress = Integer.MIN_VALUE;
                for (String s: inputs)
                {
                    //System.out.println("input is "+s
                    //        +" with value "+mostRecentlySeen.get(s));
                    int val = mostRecentlySeen.get(s);
                    if (val>maxCompress) { maxCompress = val; }
                }
                //System.out.println("mostSigInput is"+mostSigInput);
                //System.out.println("cc is"+cc);
                if (cc<maxCompress)
                {
                    bugFound = true;
                    bugLocation = pLine;
                    break;
                }
            }
        }
    }
    
    private void localizeBugUsingIGR()
    {
        DecimalFormat df = new DecimalFormat("#0.000");
        //mostRecentlySeen stores the entropy value
        // for the most recent line at which a particular
        // variable or function has changed value
        //for functional-style programs this will be a 
        // write-once data structure
        Map<String,Double> mostRecentlySeen
                = new HashMap<String,Double>();
        for (int pLine=0;pLine<pp.getLength();pLine++)
        {
            Expression ee = pp.getFunction(pLine);
            double cc = mt.getIGR(pLine);
            mostRecentlySeen.put(ee.getName(),cc);
            if (ee.isFunction())
            {
                //System.out.println("***");
                ArrayList<String> inputs = ee.getInputs();
                Double mostSigInput = Double.MAX_VALUE;
                for (String s: inputs)
                {
                    double val = mostRecentlySeen.get(s);
                    if (val<mostSigInput) { mostSigInput = val; }
                }
                System.out.println("mostSigInput is "+df.format(mostSigInput));
                System.out.println("cc is "+df.format(cc));
                if (cc<mostSigInput)
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
        if (type.equals("compression"))
        {
            if (bugFound)
            {
                ans += mt.toStringCompressionLengthsAndProgramLines(bugLocation);
            }
            else
            {
                ans += mt.toStringCompressionLengthsAndProgramLines();
            }
        }
        else if (type.equals("IGR"))
        {
            if (bugFound)
            {
                ans += mt.toStringIGRsAndProgramLines(bugLocation);
            }
            else
            {
                ans += mt.toStringIGRsAndProgramLines(-999);
            }
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
