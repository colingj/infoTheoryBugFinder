package bugfinding;

import java.util.*;

public class MultiTrace
{
    Boolean[][] theMultiTrace;
    Boolean[][] differenceMultiTrace;
    int[] compressionLengths;
    int numberOfTestCases,programLength;
    Program pp;
    //this is [program line number][test case]
    
    public MultiTrace(int numberOfTestCasesp, Program ppp)
    {
        numberOfTestCases = numberOfTestCasesp;
        pp = ppp;
        programLength = pp.getLength();
        theMultiTrace = new Boolean[programLength][numberOfTestCases];
    }
    
    public void generate(ArrayList<Map<String,Boolean>> variableValues)
    {
        int tc = 0;
        for (Map<String,Boolean> vv: variableValues)
        {
            Boolean[] tr = pp.run(vv);
            for (int i=0;i<tr.length;i++)
            {
                theMultiTrace[i][tc] = tr[i];
            }
            tc++;
        }
    }
    
    public void generateOnCompleteSet(String[] variableNames)
    {
        ArrayList<Map<String,Boolean>> vv 
                = new ArrayList<Map<String,Boolean>>();
        boolean[][] boolCombns
                = BoolUtils.generateBoolSequences(variableNames.length);
        //this returns [indexing the text cases][variables within each test case]
        for (int tc=0;tc<(int)Math.pow(2,variableNames.length);tc++)
        {
            Map<String,Boolean> temp = new HashMap<String,Boolean>();
            for (int v=0;v<variableNames.length;v++)
            {
                temp.put(variableNames[v],boolCombns[tc][v]);
            }
            vv.add(temp);
        }
        generate(vv);
    }
    
    public void generateDifference(Target tt)
    {
        differenceMultiTrace = new Boolean[programLength][numberOfTestCases];
        for (int pLine=0;pLine<programLength;pLine++)
        {
            for (int tCase=0;tCase<numberOfTestCases;tCase++)
            {
                if (theMultiTrace[pLine][tCase]==null)
                {
                    differenceMultiTrace[pLine][tCase] = null;
                }
                else
                {
                differenceMultiTrace[pLine][tCase]
                        = theMultiTrace[pLine][tCase]==tt.get(tCase);
                }
            }
        }
    }
    
    public void generateCompressionLengths()
    {
        compressionLengths = new int[programLength];
        for (int pLine=0;pLine<programLength;pLine++)
        {
            if (differenceMultiTrace[pLine][0]==null)
            {
                compressionLengths[pLine] = -999;//marker for comment line
            }
            else
            {
                boolean[] currentTraceLine = new boolean[numberOfTestCases];
                for (int tCase=0;tCase<numberOfTestCases;tCase++)
                {
                    currentTraceLine[tCase] 
                            = differenceMultiTrace[pLine][tCase];
                }
                compressionLengths[pLine] 
                        = gzipCompression.compress(currentTraceLine);
            }
        }
    }

    @Override
    public String toString()
    {
        String ans = new String();
        for (int pLine=0;pLine<programLength;pLine++)
        {
            for (int tCase=0;tCase<numberOfTestCases;tCase++)
            {
                if (theMultiTrace[pLine][tCase]==null) { ans += "-"; }
                else if (theMultiTrace[pLine][tCase]==true) { ans += "1"; }
                else if (theMultiTrace[pLine][tCase]==false) { ans += "0"; }
            }
            ans += "\n";
        }
        return ans;
    }
    
    public String toStringDifference()
    {
        String ans = new String();
        for (int pLine=0;pLine<programLength;pLine++)
        {
            for (int tCase=0;tCase<numberOfTestCases;tCase++)
            {
                if (differenceMultiTrace[pLine][tCase]==null) { ans += "-"; }
                else if (differenceMultiTrace[pLine][tCase]==true) { ans += "1"; }
                else if (differenceMultiTrace[pLine][tCase]==false) { ans += "0"; }
            }
            ans += "\n";
        }
        return ans;
    }
    
    public String toStringCompressionLengths()
    {
        String ans = new String();
        for (int pLine=0;pLine<programLength;pLine++)
        {
            if (compressionLengths[pLine]==-999)
            {
                ans += "-";
            }
            else
            {
                ans += compressionLengths[pLine];
            }
            ans += "\n";
        }
        return ans;   
    }
    
    public String toStringCompressionLengthsAndProgramLines()
    {
        String ans = new String();
        for (int pLine=0;pLine<programLength;pLine++)
        {
            if (compressionLengths[pLine]==-999)
            {
                ans += "-\t"+pp.getLine(pLine);
            }
            else
            {
                ans += compressionLengths[pLine]+"\t"
                        +pp.getLine(pLine);
            }
            ans += "\n";
        }
        return ans;   
    }
}
