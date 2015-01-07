import java.util.*;

/**
 * Console.java
 *
 * This class emulates a console's behavior.
 * @see ConsoleTest
 *
 * @author Charles-Jianye Chen
 */
public class Console
{
    protected ArrayList<String> currentDirectory = new ArrayList<String>();
    protected String bufferedDirectory = "";
    protected String dirSep = "/";
    protected Scanner stdin = new Scanner(System.in);
    protected boolean isTerminated = false;

    /**
     * Constructs a console instance.
     * @param dirSep    path seperator
     */
    public Console(String dirSep)
    {
    	this.dirSep = dirSep;
    }

    /**
     * Constructs a console instance with dirSep="/"
     */
    public Console()
    {
        this("/");
        makeDirectoryBuffer();
    }

    /**
     * This method joins the Strings in a given ArrayList, with the seperator.
     * @param r     the ArrayList of String to be joined
     * @param d     the seperator between each String
     * @return      the String joined.
     */
    public static String join(ArrayList<String> r, String d)
    {
        if (r.size() == 0) return "";
        StringBuilder sb = new StringBuilder();
        int i = 0;
        for(i=0; i<r.size()-1; i++)
            sb.append(r.get(i)+d);
        return sb.toString()+r.get(i);
    }

    protected void makeDirectoryBuffer()
    {
        if (!currentDirectory.isEmpty()) bufferedDirectory = dirSep + join(currentDirectory, dirSep)+">";
        else bufferedDirectory = dirSep+">";
    }

    protected void popDirectoryElement()
    {
        if (!currentDirectory.isEmpty())
        {
            currentDirectory.remove(currentDirectory.size() - 1);
        }
    }

    /**
     * This method emulates the behavior of "cd" command under win/xinx.  It does not check if "path" exists.
     * @param param     the param passed by inputHandler(which is the user input excluding the first token).
     * @return      CMD_SUCCESS
     *
     * @see Console#CMD_SUCCESS
     */
    public int cd(String param)
    {
        if (param.isEmpty()) return 0;
        if (param.equals(".."))
        {
            popDirectoryElement();
        }
        else
        {
            if (param.charAt(0) == '"' && param.charAt(param.length()-1) == '"')    //path specifier
            {
                param = param.subSequence(1, param.length()-1).toString();
            }
            String[] path;
            if (param.indexOf(dirSep) == 0)
            {
                currentDirectory.clear();
                param = param.substring(dirSep.length());
            }
            if (param.contains(dirSep)) path = param.split(dirSep);
            else path = new String[]{param};

            for (String str: path)
            {
                if (str.equals("."))
                {

                }
                else if (str.equals(".."))
                {
                    popDirectoryElement();
                }
                else if (!str.isEmpty())
                {
                    currentDirectory.add(str);
                }
            }
        }
        makeDirectoryBuffer();
    	return 0;
    }

    public static enum EXCEPTION
    {
        Exception, Error, InvalidParameterException, InvalidCommandException
    }

    public void Exception(EXCEPTION expType, String expInfo)
    {
        switch(expType)
        {
        case Exception:
            System.out.print("Exception: ");
            break;
        case Error:
            System.out.print("Error: ");
            break;
        case InvalidParameterException:
            System.out.print("InvalidParameterException: ");
            break;
        case InvalidCommandException:
            System.out.print("InvalidCommandException: ");
            break;
        }
        System.out.println(expInfo);
    }

    public void Exception(EXCEPTION exptype)
    {
        Exception(exptype, "");
    }

    public void Exception()
    {
        Exception(EXCEPTION.Exception);
    }

    /**
     * Terminates the console.
     * @param param     the exit status.
     * @return      CMD_SUCCESS(0).
     */
    public int exit(String param)   //is exiting?
    {
        exitStatus = param;
        return exit();
    }

    /**
     * Terminates the console.
     * @return      CMD_SUCCESS(0).
     */
    public int exit()
    {
        if (exitStatus == null) exitStatus = "";
        isTerminated = true;
        return 0;
    }

    private String exitStatus = "";

    /**
     * Fetch the exit status String.
     * @see Console#exit
     * @return      the exit status
     */
    public String getExitStatus()
    {
        return exitStatus;
    }

    private int lastResult = 0;

    /**
     * Fetch the last result of the command processed.
     *
     * @return      the result returned by the command.
     */
    public int getLastResult()
    {
        return lastResult;
    }

    public static final int CMD_SUCCESS = 0;
    public static final int CMD_RETURN_VOID = -1;
    public static final int CMD_NOT_HANDLED = -2;

    /**
     * This method distributes the command by input.
     * <p>This Method Should Be Overwritten. </p>
     * <p>This Method Should NOT Be Called Directly.  (Call inputHandler(String) instead)</p>
     * @param cmd       the first token of the user input
     * @param param     the rest of the user input
     * @return      the result of handling this command
     *
     * <p>return values:
     *      CMD_SUCCESS(0): handled cmd, successfully.
     *      CMD_RETURN_VOID(-1): handled cmd, cmd return type is VOID.
     *      CMD_NOT_HANDLED(-2): not handled cmd, could be handled in the overwritten methods.
     *      anythingelse: cmd-specific results.
     * </p>
     *
     * @see ConsoleTest#inputHandler
     */
    protected int inputHandler(String cmd, String param)
    {
        return CMD_NOT_HANDLED;
    }

    private boolean _inputHandler(String cmd, String param)    //return true only if handled this cmd
    {
        lastResult = inputHandler(cmd, param);
        return true;
    }

    /**
     * This method handles the user input, parses it, then distributes it.
     * @param userInput     the line of user's input
     * @return      true.
     */
    public boolean inputHandler(String userInput)
    {
        String[] arr = parseInput(userInput);
        return _inputHandler(arr[0], arr[1]);
    }

    protected String[] parseInput(String userInput)
    {
        String[] arr = userInput.split(" ");
        String[] result = new String[2];
        result[0] = arr[0].toLowerCase();
        result[1] = userInput.substring(arr[0].length()).trim();
        return result;
    }

    public static void main(String[] args)  //sample main
    {
        ConsoleTest cl = new ConsoleTest();
        while (cl.next());
    }

    /**
     * Instead of letting main() to do all the rough work, calling to this method will handle the console behaviour automatically.
     * <p>while (console.next()); </p>
     * @return      true if the console is not yet terminated.
     * @see Console#main
     */
    public boolean next()
    {
        if (!isTerminated)
        {
            System.out.print(bufferedDirectory);
            inputHandler(stdin.nextLine());
            return !isTerminated;
        }
        return false;
    }
}

/**
 * The sample class which demonstrates how the Console is used.
 *
 * @see Console
 */
class ConsoleTest extends Console
{
    protected int inputHandler(String cmd, String param)
    {
        int result = super.inputHandler(cmd, param);
        if (result != CMD_NOT_HANDLED) return result;

        result = CMD_RETURN_VOID;
        if (cmd.equals("cd"))
        {
            result = cd(param);
        } else if (cmd.equals("exit"))
        {
            exit(param);
        } else
        {
            return CMD_NOT_HANDLED;  //not handled
        }
        return result;
    }
}
