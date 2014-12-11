/*
CICs built-in for DynamicConsole
 */

import java.util.*;

interface ConsoleInternalCommand
{
	public String name();
	public int run(String param);
}

class pass implements ConsoleInternalCommand
{
	public int run(String param)
	{
		System.out.println("pass executed");
		return 0;
	}
	public String name(){return "pass";}
}

public class DynamicConsole extends Console
{
	public static enum builtinCIC
	{
		HELP, BASH, CD, EXIT
	}

	protected HashSet<ConsoleInternalCommand> loadedCICs = new HashSet<ConsoleInternalCommand>();
	protected HashSet<builtinCIC> loadedBuiltinCICs = new HashSet<builtinCIC>();

	public boolean loadCIC(ConsoleInternalCommand cic)
	{
		for (ConsoleInternalCommand i: loadedCICs)
		{
			if (i.name().equals(cic.name())) return false;
		}
		loadedCICs.add(cic);
		return true;
	}

	public boolean unloadCIC(ConsoleInternalCommand cic)
	{
		if (loadedCICs.contains(cic))
		{
			loadedCICs.remove(cic);
			return true;
		}
		return false;
	}

	public boolean unloadCIC(String name)
	{
		for (ConsoleInternalCommand i: loadedCICs)
		{
			if (i.name().equals(name))
			{
				loadedCICs.remove(i);
				return true;
			}
		}
		return false;
	}

	public void loadBuiltinCIC(builtinCIC bcic)
	{
		loadedBuiltinCICs.add(bcic);
	}

	public void unloadBuiltinCIC(builtinCIC bcic)
	{
		loadedBuiltinCICs.remove(bcic);
	}

	public boolean isCICloaded(builtinCIC bcic)
	{
		return loadedBuiltinCICs.contains(bcic);
	}

	public boolean isCICloaded(ConsoleInternalCommand cic)
	{
		return loadedCICs.contains(cic);
	}

	public DynamicConsole(builtinCIC[] usingCICs)
	{
		super();
		for (builtinCIC cic: usingCICs) loadBuiltinCIC(cic);
	}

	public DynamicConsole()
	{
		this(new builtinCIC[]{});
	}

	protected int inputHandler(String cmd, String param)
	{
		int result = super.inputHandler(cmd, param);
		if (result != CMD_NOT_HANDLED) return result;

		result = CMD_RETURN_VOID;
		if (cmd.equals("cd") && isCICloaded(builtinCIC.CD))
		{
			result = cd(param);
		} else if (cmd.equals("exit") && isCICloaded(builtinCIC.EXIT))
		{
			exit(param);
		} else if (cmd.equals("help") && isCICloaded(builtinCIC.HELP))
		{

		} else if (cmd.equals("bash") && isCICloaded(builtinCIC.BASH))
		{

		} else
		{
			result = CMD_NOT_HANDLED;
			for (ConsoleInternalCommand cic: loadedCICs)
			{
				if (cmd.equals(cic.name())) {result = cic.run(param); break;}
			}
		}
		return result;
	}

	public static void main(String[] args)
	{
		DynamicConsole dc = new DynamicConsole(new DynamicConsole.builtinCIC[]{DynamicConsole.builtinCIC.CD, DynamicConsole.builtinCIC.EXIT});
		dc.loadCIC(new pass());
		while (dc.next());
	}
}
