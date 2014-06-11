package bugfinding;

import java.util.*;
import java.io.*;

public class Test1App
{
    public static void main(String[] args)
            throws java.io.IOException, java.io.FileNotFoundException
    {
        BufferedReader br 
                = new BufferedReader(new FileReader("myPrograms/4BitOddParity.pp"));
        String progString="",currentLine;
        while ((currentLine = br.readLine()) != null) 
        {
            progString += currentLine+"\n";
        }
                
        Program pp = new Program(progString);

        System.out.println();
        System.out.println("#################################################");
        System.out.println();
        
        String[] variableNames = new String[4];
        variableNames[0] = "v0";
        variableNames[1] = "v1";
        variableNames[2] = "v2";
        variableNames[3] = "v3";
        MultiTrace mt2 = new MultiTrace((int)Math.pow(2,4),pp);
        mt2.generateOnCompleteSet(variableNames);
        System.out.println("Multitrace");
        System.out.println(mt2);
        
        System.out.println();
        Target tt = new Target("4BitOddParity");
        mt2.generateDifference(tt);
        System.out.println("Difference on the multitrace");
        System.out.println(mt2.toStringDifference());

        System.out.println();
        mt2.generateCompressionLengths();
        System.out.println("Compression lengths of the differences");
        System.out.println(mt2.toStringCompressionLengthsAndProgramLines());
        
        
        /*
        ArrayList<String> inputStrings = new ArrayList<String>();
        inputStrings.add("v_0");
        inputStrings.add("v_1");
        Expression e1 = new Expression("not","NOT",inputStrings);
        
        System.out.println(e1);
        ArrayList<Boolean> inputs;
        
        inputs = new ArrayList<Boolean>();
        inputs.add(true);
        inputs.add(true);
        System.out.println("TT: "+e1.eval(inputs));
        
        inputs = new ArrayList<Boolean>();
        inputs.add(true);
        inputs.add(false);
        System.out.println("TF: "+e1.eval(inputs));
        
        inputs = new ArrayList<Boolean>();
        inputs.add(false);
        inputs.add(true);
        System.out.println("FT: "+e1.eval(inputs));
        
        inputs = new ArrayList<Boolean>();
        inputs.add(false);
        inputs.add(false);
        System.out.println("FF: "+e1.eval(inputs));
        * */
        
            /*
        Map<String,Boolean> inputs = new HashMap<String,Boolean>();
        inputs.put("v0", Boolean.TRUE);
        inputs.put("v1", Boolean.TRUE);
        inputs.put("v2", Boolean.TRUE);
        inputs.put("v3", Boolean.TRUE);
        pp.run(inputs);
        System.out.println("*********** output");
        System.out.println(pp.getOutput());
        */
        
        /*
        System.out.println();
        System.out.println("#################################################");
        System.out.println();
        
        ArrayList<Map<String,Boolean>> inputList 
                = new ArrayList<Map<String,Boolean>>();
        
        Map<String,Boolean> il1 = new HashMap<String,Boolean>();
        il1.put("v0", Boolean.TRUE);
        il1.put("v1", Boolean.TRUE);
        il1.put("v2", Boolean.TRUE);
        il1.put("v3", Boolean.TRUE);
        inputList.add(il1);
        
        Map<String,Boolean> il2 = new HashMap<String,Boolean>();
        il2.put("v0", Boolean.TRUE);
        il2.put("v1", Boolean.FALSE);
        il2.put("v2", Boolean.FALSE);
        il2.put("v3", Boolean.TRUE);
        inputList.add(il2);
        
        MultiTrace mt = new MultiTrace(2,pp);
        mt.generate(inputList);
        System.out.println("Multitrace");
        System.out.println(mt);
        */
    }
    
}
