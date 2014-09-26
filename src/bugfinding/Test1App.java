package bugfinding;

import java.util.*;
import java.io.*;

public class Test1App
{
    public static void main(String[] args)
            throws java.io.IOException, java.io.FileNotFoundException
    {
        /** set up the program **/
        
        BufferedReader br 
                = new BufferedReader(new FileReader("myPrograms/11BitMux.pp"));
        String progString="",currentLine;
        while ((currentLine = br.readLine()) != null) 
        {
            progString += currentLine+"\n";
        }
                
        Program pp = new Program(progString);
        
        System.out.println();
        System.out.println("#################################################");
        System.out.println();
        
        /** set up the target **/
        
        ArrayList<Object> params = new ArrayList<Object>();
        //params.add(8);
        //Target tt = new Target("OddParity", params);
        params.add(11);
        Target tt = new Target("Mux", params);
        
        /** set up the multitrace **/

        MultiTrace mt3 = new MultiTrace((int)Math.pow(2,4),pp);
        mt3.generateOnCompleteSet();
        mt3.generateDifference(tt);
        System.out.println("Difference on the multitrace");
        System.out.println(mt3.toStringDifference());

        mt3.generateIGR();
        
        System.exit(0);
        
        System.out.println();
        mt3.generateCompressionLengths();
        System.out.println("Information distances of the differences");
        System.out.println(mt3.toStringCompressionLengthsAndProgramLines());
        System.out.println();
        System.out.println("The program is correct? "+mt3.isCorrect());
        System.out.println();
        
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
