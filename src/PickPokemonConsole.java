import java.util.*;

/**
 * PickPokemonConsole.java
 *
 * The console for picking pokemons.
 *
 *
 * @author  Charles-Jianye Chen
 * @see Console
 */
class PickPokemonConsole extends Console
{
	public ArrayList<Pokemon> pokemons = null;

	/**
	 * Set the pokemon arraylist for selection.
	 * @param pokemons      the pokemon arraylist
	 */
	public void setPokemons(ArrayList<Pokemon> pokemons)
	{
		this.pokemons = pokemons;
	}

	/**
	 * @see Console#inputHandler
	 * @param cmd       the first token of the user input
	 * @param param     the rest of the user input
	 * @return      the result of the command.
	 */
	protected int inputHandler(String cmd, String param)
	{
		int result = super.inputHandler(cmd, param);
		if (result != CMD_NOT_HANDLED) return result;

		result = CMD_RETURN_VOID;
		if (cmd.equals("help"))
		{
			result = help(param);
		} else if (cmd.equals("info"))
		{
			result = info(param);
		} else if (cmd.equals("list"))
		{
			result = list(param);
		} else if (cmd.equals("pick"))
		{
			result = pick(param);
		} else if (cmd.equals("retn"))
		{
			result = exit(cmd);
		} else
		{
			Exception(EXCEPTION.InvalidCommandException);
			return CMD_NOT_HANDLED;  //not handled
		}
		return result;
	}

	/**
	 * Display help Strings.
	 * @param param     not used.
	 * @return      CMD_RETURN_VOID.
	 */
	public static int help(String param)
	{
		System.out.println("The Following Commands are Available in this Console: ");
		System.out.println("HELP\t\tshow available commands");
		System.out.println("INFO\t\tID/name show information about a specified pokemon");
		System.out.println("LIST\t\tshow a list of available pokemons");
		System.out.println("PICK\t\tID/name pick a specified pokemon");
		System.out.println("RETN\t\tcancel the operation is possible");
		return CMD_RETURN_VOID;
	}

	/**
	 * Outputs a list of selections with their indx attached.
	 * @param param     not used.
	 * @return  CMD_SUCCESS if a valid pokemon arraylist is set previously.
	 */
	public int list(String param)
	{
		try
		{
			int j=0;
			for (int i=0; i<pokemons.size(); i++)
			{
				if (++j%4 == 0) System.out.println(i+". "+pokemons.get(i).name());
				else
				{
					System.out.printf("%-20s", i+". "+pokemons.get(i).name());   //80 columns
				}
			}
			if (j%4 != 0) System.out.println();
		}
		catch (NullPointerException err)
		{
			Exception(EXCEPTION.Exception, "ArrayList<Pokemon> is not initialized");
		}
		return CMD_SUCCESS;
	}

	/**
	 * Select/Pick the pokemon by name or indx displayed.
	 * @param param     the name or the (int)indx of the selection
	 * @return      CMD_RETURN_VOID if not a valid selection, indx otherwise.
	 */
	public int pick(String param)
	{
		Integer t = null;
		try
		{
			this.pokemons.get(Integer.parseInt(param));
			t = Integer.parseInt(param);
		}
		catch (NullPointerException err)
		{
			Exception(EXCEPTION.Exception, "ArrayList<Pokemon> is not initialized");
		}
		catch (Exception err)
		{
			for(int i=0; i<pokemons.size(); i++)
			{
				if (pokemons.get(i).name().equals(param))
				{
					t = i;
					break;
				}
			}
		}
		if (t == null) Exception(EXCEPTION.Exception, "Not Present");
		else exit();
		return t == null ? CMD_RETURN_VOID : t;
	}

	/**
	 * Display the Pokemon's detailed informations.
	 * @param param     the name or the (int)indx of the selection
	 * @return      CMD_SUCCESS.
	 */
	public int info(String param)
	{
		Pokemon t = null;
		try
		{
			t = this.pokemons.get(Integer.parseInt(param));
		}
		catch (NullPointerException err)
		{
			Exception(EXCEPTION.Exception, "ArrayList<Pokemon> is not initialized");
		}
		catch (Exception err)
		{
			for(Pokemon i: this.pokemons)
			{
				if (i.name().equals(param))
				{
					t = i;
					break;
				}
			}
		}
		if (t == null) Exception(EXCEPTION.Exception, "Not Present");
		else System.out.println(t.toString());
		return CMD_SUCCESS;
	}
}

