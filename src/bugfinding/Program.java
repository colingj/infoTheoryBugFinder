package bugfinding;

import java.util.*;

public class Program
{
    ArrayList<Expression> functionList;//this is the program
    boolean output;
    int programLength;
    ArrayList<String> programText;
    Boolean[] trace;

    public Program()
    {
        functionList = new ArrayList<Expression>();
    }
    
    public Program(String progString)
    {
        functionList = new ArrayList<Expression>();
        initialise(progString);
    }
    
    public void initialise(String progString)
    {
        Scanner scanner = new Scanner(progString);
        int lineNumber = 0;
        programText = new ArrayList<String>();
        while (scanner.hasNextLine()) 
        {
            String line = scanner.nextLine();
            programText.add(line);
            if (line.isEmpty()||line.substring(0,1).equals("#"))
            {
                Expression ee = new Expression("blank","COMMENT",null,lineNumber);
            }
            else
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
            }
            lineNumber++;
        }
        programLength = lineNumber;
        scanner.close();
    }
    
    public Boolean[] run(Map<String,Boolean> variableValues)
    {
        trace = new Boolean[programLength];
        Map<String,Boolean> values = new HashMap<String,Boolean>();
        for (Expression e: functionList)
        {
            if (e.isComment())
            {
                trace[e.getLineNumber()] = null;
                //do nothing
            }
            else if (e.isVar())
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
    
    public boolean getOutput()
    {
        return output;
    }
    
    public int getLength()
    {
        return programLength;
    }
    
    public String getLine(int pLine)
    {
        return programText.get(pLine);
    }
}
