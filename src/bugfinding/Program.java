package bugfinding;

import java.util.*;

public class Program
{
    ArrayList<Expression> functionList;//this is the program substance
    boolean output;
    int programLength,programLengthWithComments;
    ArrayList<String> programText;
    Map<Integer,Integer> commentedToCommentFree;
    //maps the line numbers with comments to those without
    Boolean[] trace;
    int numberOfVariables;
    String[] variableNames;
    
    public Program(String progString)
    {
        functionList = new ArrayList<Expression>();
        commentedToCommentFree = new HashMap<Integer,Integer>();
        initialise(progString);
    }
    
    public void initialise(String progString)
    {
        Scanner scanner = new Scanner(progString);
        int lineNumber = 0; //without comments
        int lineNumberWithComments = 0;
        programText = new ArrayList<String>();
        while (scanner.hasNextLine()) 
        {
            String line = scanner.nextLine();
            programText.add(line);
            
            //if not a comment or blank
            if (!(line.isEmpty()||line.substring(0,1).equals("#")))
            {
                String[] tokens = line.split("\\s+");
                if (tokens[0].equals("variable"))
                {
                    Expression ee = new Expression(tokens[1],"VAR",null,lineNumber);
                    functionList.add(ee);
                }
                else if (tokens[0].equals("output"))
                {
                    Expression ee = new Expression(tokens[1],"OUT",null,lineNumber);
                    functionList.add(ee);
                }
                else
                {
                    ArrayList<String> inputs = new ArrayList<String>();
                    for (int i=3;i<tokens.length;i++)
                    {
                        inputs.add(tokens[i]);
                    }
                    Expression ee = new Expression(tokens[0], tokens[2],
                            inputs, lineNumber);
                    functionList.add(ee);
                }
                commentedToCommentFree.put(lineNumberWithComments, lineNumber);
                lineNumber++;
            }
            else
            {
                //if the line is a comment or empty
                commentedToCommentFree.put(lineNumberWithComments, -999);
            }
            lineNumberWithComments++;
        }
        programLength = lineNumber;
        programLengthWithComments = lineNumberWithComments;
        scanner.close();
        
        /** count and store the variable names **/
        
        numberOfVariables = 0;
        for (Expression e: functionList) { if (e.isVar()) { numberOfVariables++; } }
        variableNames = new String[numberOfVariables];
        int varIdx = 0;
        for (Expression e: functionList)
        {
            if (e.isVar())
            {
                variableNames[varIdx] = e.getVar();
                varIdx++;
            }
        }
    }
    
    public Boolean[] run(Map<String,Boolean> variableValues)
    {
        trace = new Boolean[programLength];
        Map<String,Boolean> values = new HashMap<String,Boolean>();
        for (Expression e: functionList)
        {
            if (e.isVar())
            {
                values.put(e.getVar(),variableValues.get(e.getVar()));
                trace[e.getLineNumber()] = variableValues.get(e.getVar());
            }
            else if (e.isOutput())
            {
                output = values.get(e.getOutput());
                trace[e.getLineNumber()] = values.get(e.getOutput());
            }
            else 
            {
                ArrayList<Boolean> inputs = new ArrayList<Boolean>();
                ArrayList<String> inputVars = e.getInputs();
                for (String iv: inputVars)
                {
                    inputs.add(values.get(iv));
                }
                values.put(e.getName(),e.eval(inputs));
                trace[e.getLineNumber()] = e.eval(inputs);
            }
            
            /*
            System.out.println("** current state of program: ");
            System.out.print("   ");
            for (String key: values.keySet())
            {
                String value = values.get(key).toString();  
                System.out.print(key+" "+value+";  ");
            }
            System.out.println();
            */
            
        }
        return trace;
    }
    
    public String[] getVariableNames()
    {
        return variableNames;
    }
    
    public int getNumberOfVariables()
    {
        return numberOfVariables;
    }
    
    public boolean getOutput()
    {
        return output;
    }
    
    public int getLength()
    {
        return programLength;
    }
    
    public int getLengthWithComments()
    {
        return programLengthWithComments;
    }
    
    public int getSubstantiveLine(int line)
    {
        return commentedToCommentFree.get(line);
    }
        
    public String getLine(int pLine)
    {
        return programText.get(pLine);
    }
    
    public Expression getFunction(int i)
    {
        return functionList.get(i);
    }
    
    @Override
    public String toString()
    {
        String ans = new String();
        //wibble
        for (int pLine=0;pLine<programLengthWithComments;pLine++)
        {
            int line = getSubstantiveLine(pLine);
            if (line==-999)
            {
                ans += programText.get(pLine)+"\n";
            }
            else
            {
                ans += functionList.get(line).toString()+"\n";
            }
        }
        
        return ans;
    }
}
