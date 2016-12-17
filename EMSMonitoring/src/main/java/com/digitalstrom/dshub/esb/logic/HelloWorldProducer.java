package com.digitalstrom.dshub.esb.logic;


import java.util.*;
import javax.jms.*;

public class HelloWorldProducer
{
    /*-----------------------------------------------------------------------
     * Parameters
     *----------------------------------------------------------------------*/

    String          serverUrl    = null;
    String          userName     = null;
    String          password     = null;
    String          name         = "topic.sample";
    Vector          data         = new Vector();
    boolean         useTopic     = true;
    boolean         useAsync     = false;

    /*-----------------------------------------------------------------------
     * Variables
     *----------------------------------------------------------------------*/
    Connection      connection   = null;
    Session         session      = null;
    MessageProducer msgProducer  = null;
    Destination     destination  = null;
    
    TibjmsCompletionListener completionListener = null;
    
    class TibjmsCompletionListener implements CompletionListener
    {
        // Note:  Use caution when modifying a message in a completion
        // listener to avoid concurrent message use.

        public void onCompletion(Message msg)
        {
            try 
            {
                System.err.printf("Successfully sent message %s.\n",
                    ((TextMessage)msg).getText());
            }
            catch (JMSException e)
            {
                System.err.println("Error retrieving message text.");
                e.printStackTrace(System.err);
            }
        }

        public void onException(Message msg, Exception ex)
        {
            try 
            {
                System.err.printf("Error sending message %s.\n",
                        ((TextMessage)msg).getText());
            }
            catch (JMSException e)
            {
                System.err.println("Error retrieving message text.");
                e.printStackTrace(System.err);
            }
            
            ex.printStackTrace(System.err);
        }
        
    }
    
    public HelloWorldProducer(String[] args)
    {
        parseArgs(args);

        try
        {
            tibjmsUtilities.initSSLParams(serverUrl,args);
        }
        catch (JMSSecurityException e)
        {
            System.err.println("JMSSecurityException: "+e.getMessage()+", provider="+e.getErrorCode());
            e.printStackTrace();
            System.exit(0);
        }

        /* print parameters */
        System.err.println("\n------------------------------------------------------------------------");
        System.err.println("tibjmsMsgProducer SAMPLE");
        System.err.println("------------------------------------------------------------------------");
        System.err.println("Server....................... "+((serverUrl != null)?serverUrl:"localhost:7222"));
        System.err.println("User......................... "+((userName != null)?userName:"(null)"));
        System.err.println("Destination.................. "+name);
        System.err.println("Send Asynchronously.......... "+useAsync);
        System.err.println("Message Text................. ");
        for (int i=0;i<data.size();i++)
        {
            System.err.println(data.elementAt(i));
        }
        System.err.println("------------------------------------------------------------------------\n");

        try 
        {
            TextMessage msg;
            int         i;

            if (data.size() == 0)
            {
                System.err.println("***Error: must specify at least one message text\n");
                usage();
            }

            System.err.println("Publishing to destination '"+name+"'\n");

            ConnectionFactory factory = new com.tibco.tibjms.TibjmsConnectionFactory(serverUrl);

            connection = factory.createConnection(userName,password);

            /* create the session */
            session = connection.createSession(javax.jms.Session.AUTO_ACKNOWLEDGE);

            /* create the destination */
            if (useTopic)
                destination = session.createTopic(name);
            else
                destination = session.createQueue(name);

            /* create the producer */
            msgProducer = session.createProducer(null);

            if (useAsync)
                completionListener = new TibjmsCompletionListener();
            
            /* publish messages */
            for (i = 0; i<data.size(); i++)
            {
                /* create text message */
                msg = session.createTextMessage();

                /* set message text */
                msg.setText((String)data.elementAt(i));

                /* publish message */
                if (useAsync == false)
                   msgProducer.send(destination, msg);
                else
                    msgProducer.send(destination, msg, completionListener);

                System.err.println("Published message: "+data.elementAt(i));
            }

            /* close the connection */
            connection.close();
        } 
        catch (JMSException e) 
        {
            e.printStackTrace();
            System.exit(-1);
        }
    }

    /*-----------------------------------------------------------------------
    * usage
    *----------------------------------------------------------------------*/
    private void usage()
    {
        System.err.println("\nUsage: java tibjmsMsgProducer [options] [ssl options]");
        System.err.println("                                <message-text-1>");
        System.err.println("                                [<message-text-2>] ...");
        System.err.println("\n");
        System.err.println("   where options are:");
        System.err.println("");
        System.err.println("   -server   <server URL>  - EMS server URL, default is local server");
        System.err.println("   -user     <user name>   - user name, default is null");
        System.err.println("   -password <password>    - password, default is null");
        System.err.println("   -topic    <topic-name>  - topic name, default is \"topic.sample\"");
        System.err.println("   -queue    <queue-name>  - queue name, no default");
        System.err.println("   -async                  - send asynchronously, default is false");
        System.err.println("   -help-ssl               - help on ssl parameters");
        System.exit(0);
    }

    /*-----------------------------------------------------------------------
     * parseArgs
     *----------------------------------------------------------------------*/
    void parseArgs(String[] args)
    {
        int i=0;

        while (i < args.length)
        {
            if (args[i].compareTo("-server")==0)
            {
                if ((i+1) >= args.length) usage();
                serverUrl = args[i+1];
                i += 2;
            }
            else
            if (args[i].compareTo("-topic")==0)
            {
                if ((i+1) >= args.length) usage();
                name = args[i+1];
                i += 2;
            }
            else
            if (args[i].compareTo("-queue")==0)
            {
                if ((i+1) >= args.length) usage();
                name = args[i+1];
                i += 2;
                useTopic = false;
            }
            else
            if (args[i].compareTo("-async")==0)
            {
                i += 1;
                useAsync = true;
            }
            else
            if (args[i].compareTo("-user")==0)
            {
                if ((i+1) >= args.length) usage();
                userName = args[i+1];
                i += 2;
            }
            else
            if (args[i].compareTo("-password")==0)
            {
                if ((i+1) >= args.length) usage();
                password = args[i+1];
                i += 2;
            }
            else
            if (args[i].compareTo("-help")==0)
            {
                usage();
            }
            else
            if (args[i].compareTo("-help-ssl")==0)
            {
                tibjmsUtilities.sslUsage();
            }
            else
            if (args[i].startsWith("-ssl"))
            {
                i += 2;
            }
            else
            {
            	for(int j=0; j<10;j++)
            		data.addElement(args[i]);
                //data.addElement("hello guys");
                i++;
            }
        }
    }

    /*-----------------------------------------------------------------------
     * main
     *----------------------------------------------------------------------*/
    public static void main(String[] args)
    {
    	String[] li = new String[1];
    	li[0] = "hello guys";
    	HelloWorldProducer t = new HelloWorldProducer(args);
    }
}


