package bugfinding;

import java.io.*;
import java.util.zip.*;

public class gzipCompression
{

    public static int compress(boolean[] boolArray)
    {
        String str = new String();
        for (boolean b : boolArray)
        {
            str += b?"0":"1";
        }
        //System.out.println(str);
        return compress(str);
    }

    public static int compress(String str)
    {
        String outStr = new String();
        try
        {
            //System.out.println("String length : " + str.length());
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            GZIPOutputStream gzip = new GZIPOutputStream(out);
            gzip.write(str.getBytes());
            gzip.close();
            outStr = out.toString("ISO-8859-1");
            //System.out.println("Output string: "+outStr);
            //System.out.println("Output String length : " + outStr.length());
        }
        catch (java.io.IOException ee)
        {
            System.err.println("Error in evaluateQuality.");
            System.exit(1);
        }
        return outStr.length();
     }
}
