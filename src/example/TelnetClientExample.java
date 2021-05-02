/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package example;

/**
 *
 * @author Sahab
 */

import java.io.InputStream;
import java.io.IOException;
import org.apache.commons.net.telnet.TelnetClient;


/***
 * This is a simple example of use of TelnetClient.
 * An external option handler (SimpleTelnetOptionHandler) is used.
 * Initial configuration requested by TelnetClient will be:
 * WILL ECHO, WILL SUPPRESS-GA, DO SUPPRESS-GA.
 * VT100 terminal type will be subnegotiated.
 * <p>
 * Also, use of the sendAYT(), getLocalOptionState(), getRemoteOptionState()
 * is demonstrated.
 * When connected, type AYT to send an AYT command to the server and see
 * the result.
 * Type OPT to see a report of the state of the first 25 options.
 * <p>
 * @author Bruno D'Avanzo
 ***/
public class TelnetClientExample implements Runnable
{
    static TelnetClient tc = null;

    /***
     * Main for the TelnetClientExample.
     ***/
    public static void main(String[] args) throws IOException
    {
        String remoteip = "127.0.0.1";

        int remoteport;

        if (args.length > 1)
        {
            remoteport = (new Integer(args[1])).intValue();
        }
        else
        {
            remoteport = 23;
        }

        tc = new TelnetClient();
        
        tc.connect(remoteip, remoteport);

        Thread reader = new Thread (new TelnetClientExample());

        reader.start();
    }

    /***
     * Reader thread.
     * Reads lines from the TelnetClient and echoes them
     * on the screen.
     ***/
    public void run()
    {
        InputStream instr = tc.getInputStream();

        try
        {
            byte[] buff = new byte[1024];
            int ret_read = 0;

            do
            {
                ret_read = instr.read(buff);
                if(ret_read > 0)
                {
                    System.out.print(new String(buff, 0, ret_read));
                }
            }
            while (ret_read >= 0);
        }
        catch (Exception e)
        {
            System.err.println("Exception while reading socket:" + e.getMessage());
        }

        try
        {
            tc.disconnect();
        }
        catch (Exception e)
        {
            System.err.println("Exception while closing telnet:" + e.getMessage());
        }
    }
}
