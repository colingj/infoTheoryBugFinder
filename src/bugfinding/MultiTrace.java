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
    
    public void generateOnCompleteSet()
    {
        String[] variableNames = pp.getVariableNames();
        ArrayList<Map<String,Boolean>> vv 
                = new ArrayList<Map<String,Boolean>>();
        boolean[][] boolCombns
                = BoolUtils.generateBoolSequencesBigEndian(variableNames.length);
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
                if (theMultiTrace[pLine][tCase]==null)//wibble: might be redundant
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
    
    public void generateIGR()
    {
        //assumes that we have run "generate difference"

        /** generate dependencies on each program line **/
        
        //to store dependencies by program line
        ArrayList<ArrayList<String>> dependenciesByLine 
                = new ArrayList<ArrayList<String>>();
        //to store dependency lists for functions
        Map<String,ArrayList<String>> dependencyMap 
                = new HashMap<String,ArrayList<String>>();
        for (int pLine=0;pLine<programLength;pLine++)
        {
            Expression ee = pp.getFunction(pLine);
            if (ee.isVar())
            {
                ArrayList<String> variable = new ArrayList<String>();
                variable.add(ee.getVar());
                dependenciesByLine.add(variable);
            }
            else if (ee.isFunction())
            {
                ArrayList<String> variables = new ArrayList<String>();
                ArrayList<String> inputs = ee.getInputs();
                String name = ee.getName();
                for (String ff: inputs)
                {
                    if (dependencyMap.containsKey(ff))
                    {
                        //add all of the previously dependent variables.
                        variables.addAll(dependencyMap.get(ff));
                    }
                    else
                    {
                        variables.add(ff);
                    }
                }
                dependenciesByLine.add(variables);
                dependencyMap.put(name,variables);
            }
            else if (ee.isOutput()) 
            {
                String name = ee.getName();
                ArrayList<String> temp = dependencyMap.get(name);
                dependenciesByLine.add(temp);
            }
        }
        
        for (ArrayList<String> als: dependenciesByLine)
        {
            for (String dd: als)
            {
                System.out.print(dd+" ");
            }
            System.out.println();
        }
    }

    //this method calculates whether the program is giving the correct output.
    //assumes that the last substantive line is the output line
    //needs differenceMultiTrace to have been calculated
    public boolean isCorrect()
    {
        boolean ans = true;
        for (int i=0;i<numberOfTestCases;i++)
        {
            if (differenceMultiTrace[programLength-1][i]==false)
            {
                ans = false;
            }
        }
        return ans;
    }
        
    public Program getProgram()
    {
        return pp;
    }
    
    public int getCompressionLength(int i)
    {
        return compressionLengths[i];
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
                if (differenceMultiTrace[pLine][tCase]==true) { ans += "1"; }
                else if (differenceMultiTrace[pLine][tCase]==false) { ans += "0"; }
            }
            ans += "\n";
        }
        return ans;
    }
    
    public String toStringCompressionLengthsAndProgramLines()
    {
        String ans = new String();
        int programLengthWithComments = pp.getLengthWithComments();
        for (int pLine=0;pLine<programLengthWithComments;pLine++)
        {
            int substantiveLine = pp.getSubstantiveLine(pLine);
            if (substantiveLine==-999)
            {
                ans += "-\t"+pp.getLine(pLine);
            }
            else
            {
                ans += compressionLengths[substantiveLine]+"\t"
                        +pp.getLine(pLine);
            }
            ans += "\n";
        }
        return ans;   
    }
    
    public String toStringCompressionLengthsAndProgramLines(int bugLocation)
    {
        String ans = new String();
        int programLengthWithComments = pp.getLengthWithComments();
        for (int pLine=0;pLine<programLengthWithComments;pLine++)
        {
            int substantiveLine = pp.getSubstantiveLine(pLine);
            if (substantiveLine==-999)
            {
                ans += "-\t"+pp.getLine(pLine);
            }
            else
            {
                ans += compressionLengths[substantiveLine]+"\t"
                        +pp.getLine(pLine);
                if (substantiveLine==bugLocation)
                {
                    ans += "  **** Bug Location";
                }
            }
            ans += "\n";
        }
        return ans;   
    }
}
