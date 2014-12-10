import java.util.*;

public class Console
{
    protected ArrayList<String> currentDirectory = new ArrayList<String>();
    protected String bufferedDirectory = "";
    protected String dirSep = "/";
    protected Scanner stdin = new Scanner(System.in);
    protected boolean isTerminated = false;

    public Console(String dirSep)
    {
    	this.dirSep = dirSep;
    }

    public Console()
    {
        this("/");
        makeDirectoryBuffer();
    }

    protected void makeDirectoryBuffer()
    {
        if (!currentDirectory.isEmpty()) bufferedDirectory = dirSep+String.join(dirSep, currentDirectory)+">";
        else bufferedDirectory = dirSep+">";
    }

    protected void popDirectoryElement()
    {
        if (!currentDirectory.isEmpty())
        {
            currentDirectory.remove(currentDirectory.size() - 1);
        }
    }

    public boolean cd(String param)
    {
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
                else
                {
                    currentDirectory.add(str);
                }
            }
        }
        makeDirectoryBuffer();
    	return true;
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
            System.out.println("Exception Called");
            break;
        case Error:
            System.out.println("Error Called");
            break;
        case InvalidParameterException:
            System.out.println("InvalidParameterException Called");
            break;
        case InvalidCommandException:
            System.out.println("InvalidCommandException Called");
            break;
        }
    }

    public void Exception(EXCEPTION exptype)
    {
        Exception(exptype, "");
    }

    public void Exception()
    {
        Exception(EXCEPTION.Exception);
    }

    public boolean exit(String param)   //is exiting?
    {
        return true;
    }

    protected boolean inputHandler(String cmd, String param)    //return true only if handled this cmd
{
    if (cmd.equals("cd"))
    {
        cd(param);
    }
    else if(cmd.equals("exit"))
    {
        if (exit(param))
        {
            isTerminated = true;
        }
    }
    else
    {
        return false;
    }
    return true;
}

    public boolean inputHandler(String userInput)
    {
        String[] arr = parseInput(userInput);
        return inputHandler(arr[0], arr[1]);
    }

    protected String[] parseInput(String userInput)
    {
        String[] arr = userInput.split(" ");
        String[] result = new String[2];
        result[0] = arr[0].toLowerCase();
        result[1] = userInput.substring(userInput.indexOf(" ")+1);
        return result;
    }

    public static void main(String[] args)
    {
        Console cl = new Console();
        while (cl.next());
    }

    public boolean next()
    {
        if (!isTerminated)
        {
            System.out.print(bufferedDirectory);
            inputHandler(stdin.nextLine());
            return true;
        }
        return false;
    }
}

