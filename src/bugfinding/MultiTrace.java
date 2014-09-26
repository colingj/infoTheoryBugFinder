package bugfinding;

import java.text.DecimalFormat;
import java.util.*;

public class MultiTrace
{
    Boolean[][] theMultiTrace;
    Boolean[][] differenceMultiTrace;
    int[] compressionLengths;
    double[] IGRbyLine;
    int numberOfTestCases,programLength;
    Program pp;
    boolean[][] testCaseInputs; //[indexing the test cases][variables within each test case]
    Map<String,Integer> variableNameToPosition;
    
    public MultiTrace(int numberOfTestCasesp, Program ppp)
    {
        numberOfTestCases = numberOfTestCasesp;
        pp = ppp;
        programLength = pp.getLength();
        theMultiTrace = new Boolean[programLength][numberOfTestCases];
        variableNameToPosition = new HashMap<String,Integer>();
        String[] variableNames = pp.getVariableNames();
        for (int v=0;v<variableNames.length;v++)
        {
            variableNameToPosition.put(variableNames[v],v);
        }        
    }
    
    private void generate(ArrayList<Map<String,Boolean>> variableValues)
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
        testCaseInputs
                = BoolUtils.generateBoolSequencesBigEndian(variableNames.length);
        //this returns [indexing the text cases][variables within each test case]
        for (int tc=0;tc<(int)Math.pow(2,variableNames.length);tc++)
        {
            Map<String,Boolean> temp = new HashMap<String,Boolean>();
            for (int v=0;v<variableNames.length;v++)
            {
                temp.put(variableNames[v],testCaseInputs[tc][v]);
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
        
        //test print
        //System.out.println("*** Starting generateIGR\n");
        //end of test print

        IGRbyLine = new double[programLength];
        
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
        
        //test print
        /*
        for (ArrayList<String> als: dependenciesByLine)
        {
            for (String dd: als)
            {
                System.out.print(dd+" ");
            }
            System.out.println();
        }
        */
        //end of test print
        
        /** create the groups **/
        
        String[] variableNames = pp.getVariableNames();
        for (int pLine=0;pLine<programLength;pLine++)        
        {
            //test print
            //System.out.println("**** New program line\n");
            //end of test print
            Map<String,ArrayList<Boolean>> groups 
                = new HashMap<String,ArrayList<Boolean>>(); 
            ArrayList<String> currentDeps = dependenciesByLine.get(pLine);
            //this could be done better using a map function
            ArrayList<Integer> currentIntDeps = new ArrayList<Integer>();
            for (String dd: currentDeps) { currentIntDeps.add(variableNameToPosition.get(dd)); }            
            for (int tCase=0;tCase<(int)Math.pow(2,variableNames.length);tCase++)
            {
                                Map<String,Boolean> temp = new HashMap<String,Boolean>();
                String label = new String();
                for (int v=0;v<variableNames.length;v++)
                {
                    if (!currentIntDeps.contains(v))
                    {
                        label += testCaseInputs[tCase][v]?1:0;
                    }
                }
                if (!groups.containsKey(label))
                {
                    ArrayList<Boolean> newGroup = new ArrayList<Boolean>();
                    newGroup.add(differenceMultiTrace[pLine][tCase]);
                    groups.put(label,newGroup);
                }
                else
                {
                    groups.get(label).add(differenceMultiTrace[pLine][tCase]);
                }
            }
            
            //test print
            /*
            for (Map.Entry<String,ArrayList<Boolean>> entry : groups.entrySet()) {
                String key = entry.getKey();
                ArrayList<Boolean> group = entry.getValue();
                System.out.print(key+": ");
                for (Boolean bb: group)
                {
                    System.out.print(bb?1:0);
                }
                System.out.println();
            }
            System.out.println();
            */
            //end of test print
            
            /** calculate the IGR itself **/
            
            /** now calculate the IG based on the groups **/
            //notation: BS = before split, AS = after split

            int numberOfGroups = groups.size();
            double countTrueBS = 0.0;
            double countFalseBS = 0.0;
            double[] countTrueAS = new double[numberOfGroups];
            double[] countFalseAS = new double[numberOfGroups];
            double total = 0.0;
            double[] totalByGroup = new double[numberOfGroups];
            for (int j=0;j<numberOfGroups;j++)
            {
                countTrueAS[j] = 0.0;
                countFalseAS[j] = 0.0;
                totalByGroup[j] = 0.0;
            }
            
            int groupIdx = 0;
            for (Map.Entry<String,ArrayList<Boolean>> entry : groups.entrySet()) 
            {
                ArrayList<Boolean> group = entry.getValue();
                
                for (Boolean bb: group) 
                {
                    total += 1.0;
                    totalByGroup[groupIdx] += 1.0;
                    if (bb)
                    {
                        countTrueBS += 1.0;
                        countTrueAS[groupIdx] += 1.0;
                    }
                    else
                    {
                        countFalseBS += 1.0;
                        countFalseAS[groupIdx] += 1.0;
                    }
                }
                groupIdx++;
            }
            
            //test print
            /*
            System.out.println("Overall, countTrue is "+countTrueBS);
            System.out.println("Overall, countFalse is "+countFalseBS);
            System.out.println("Overall, total is "+total);
            for (int j=0;j<numberOfGroups;j++)
            {
                System.out.println("For group "+j+" countTrue is "+countTrueAS[j]);
                System.out.println("For group "+j+" countFalse is "+countFalseAS[j]);
                System.out.println("For group "+j+" total is "+totalByGroup[j]);
            }
            System.out.println();
            */
            //end of test print
            
            double entropyBS = 0.0;
            entropyBS = -((countTrueBS/total) * log2(countTrueBS/total))
                    -((countFalseBS/total) * log2(countTrueBS/total));
            //test print
            //System.out.println("Entropy before split is: "+entropyBS);
            //end of test print
            
            double[] entropyASbyPA = new double[numberOfGroups];
            for (int j=0;j<numberOfGroups;j++)
            {
                entropyASbyPA[j] = -((countTrueAS[j]/totalByGroup[j]) * log2(countTrueAS[j]/totalByGroup[j]))
                        -((countFalseAS[j]/totalByGroup[j]) * log2(countFalseAS[j]/totalByGroup[j]));
            }
            double entropyAS = 0.0;
            for (int j=0;j<numberOfGroups;j++)
            {
                entropyAS += (totalByGroup[j]/total)*entropyASbyPA[j];
            }
            //test print
            //System.out.println("Entropy after split is: "+entropyAS);
            //end of test print
            
            double informationGain = entropyBS - entropyAS;
            //test print
            //System.out.println("Information gain is: "+informationGain+"\n");
            //end of test print

            //wibble: next line rule of thumb
            if (numberOfGroups==1) { informationGain= 1.0; }
            IGRbyLine[pLine] = informationGain;

            //wibble: for now we end here and just return IG rather than IGR
            
            /** calculate the IG ratio **/
            double intrinsicValue = 0.0;
            for (int j=0;j<numberOfGroups;j++)
            {
                intrinsicValue += (totalByGroup[j]/total)*log2(totalByGroup[j]/total);
            }
            //intrinsicValue = -intrinsicValue;
                        
            double informationGainRatio = informationGain/intrinsicValue;
            //wibble: next line rule of thumb
            if (numberOfGroups==1) { informationGainRatio = 1.0; }

            //System.out.println("IGR is "+informationGainRatio);
            //IGRbyLine[pLine] = informationGain;
            
        }//end of iterating through program lines            
        
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
    
    /** helper method **/
    private double log2(double x)
    {
        double answer;
        if (x==0.0)
        { answer = 0.0; }
        else
        { answer = Math.log(x)/Math.log(2); }
        return answer;
    }
        
    public Program getProgram()
    {
        return pp;
    }
    
    public int getCompressionLength(int i)
    {
        return compressionLengths[i];
    }
    
    public double getIGR(int i)
    {
        return IGRbyLine[i];
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
    
    public String toStringIGRsAndProgramLines(int bugLocation)
    {
        DecimalFormat df = new DecimalFormat("#0.000");
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
                ans += df.format(IGRbyLine[substantiveLine])+"\t"
                        +pp.getLine(pLine);
                if (bugLocation!=-999)
                {
                    if (substantiveLine==bugLocation)
                    {
                        ans += "  **** Bug Location";
                    }
                }
            }
            ans += "\n";
        }
        return ans;   
    }
}
