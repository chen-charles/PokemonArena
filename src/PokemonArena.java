/*
PokemonArena.java
Charles-Jianye Chen

ICS4U project: PokemonArena
design a pokemon game with only battle-ing stuff

 */

import java.io.*;
import java.util.*;

public class PokemonArena
{
	public static Hashtable<String, Pokemon.TYPE> pkmonTypeMap = new Hashtable<String, Pokemon.TYPE>();
	public static Hashtable<String, Pokemon.SPECIAL> pkmonSpecialMap = new Hashtable<String, Pokemon.SPECIAL>();
	public static String abspath = PokemonArena.class.getProtectionDomain().getCodeSource().getLocation().getPath();

	public static void init()
	{
		abspath = abspath.replaceAll("%20", " ");

		pkmonSpecialMap.put(" ", Pokemon.SPECIAL.NONE);
		pkmonSpecialMap.put("wild storm", Pokemon.SPECIAL.WILDSTORM);
		pkmonSpecialMap.put("disable", Pokemon.SPECIAL.DISABLE);
		pkmonSpecialMap.put("recharge", Pokemon.SPECIAL.RECHARGE);
		pkmonSpecialMap.put("wild card", Pokemon.SPECIAL.WILDCARD);
		pkmonSpecialMap.put("stun", Pokemon.SPECIAL.STUN);

		pkmonTypeMap.put(" ", Pokemon.TYPE.NONE);
		pkmonTypeMap.put("electric", Pokemon.TYPE.ELECTRIC);
		pkmonTypeMap.put("earth", Pokemon.TYPE.EARTH);
		pkmonTypeMap.put("fire", Pokemon.TYPE.FIRE);
		pkmonTypeMap.put("grass", Pokemon.TYPE.GRASS);
		pkmonTypeMap.put("fighting", Pokemon.TYPE.FIGHTING);
		pkmonTypeMap.put("water", Pokemon.TYPE.WATER);
		pkmonTypeMap.put("leaf", Pokemon.TYPE.LEAF);
	}

	public static Pokemon parseRaw(String raw) //parse the RAW data, then call the constructor
	{
		String[] data = raw.split(",");
		int numAttacks = Integer.parseInt(data[5]);
		Pokemon.AttackInfo[] attacks = new Pokemon.AttackInfo[numAttacks];
		int j = 0;
		for (int i = 0; i < numAttacks; i++)
		{
			//public AttackInfo(String name, int cost, int damage, SPECIAL special);
			attacks[i] = new Pokemon.AttackInfo(data[6 + j], Integer.parseInt(data[6 + j + 1]),
					Integer.parseInt(data[6 + j + 2]), pkmonSpecialMap.get(data[6 + j + 3]));
			j += 4;
		}

		//	public Pokemon(String name, int hp, Pokemon.TYPE type, Pokemon.TYPE resistance, Pokemon.TYPE weakness, int numAttacks,
		//Pokemon.AttackInfo[] attacks);
		return new
				Pokemon(data[0], Integer.parseInt(data[1]), pkmonTypeMap.get(data[2]), pkmonTypeMap.get(data[3]),
				pkmonTypeMap.get(data[4]),
				numAttacks, attacks);
	}

	public static Pokemon[] loadPokemons(String fname) throws IOException
	{
		Scanner infile;
		infile = new Scanner(new File(fname));  //throws IOException

		int numPokemons;

		numPokemons = Integer.parseInt(infile.nextLine());
		Pokemon[] pokemons = new Pokemon[numPokemons];

		int counter = 0;
		String str;
		while (infile.hasNextLine())
		{
			str = infile.nextLine();
			if (str == null) break;
			pokemons[counter++] = parseRaw(str);
		}
		return pokemons;
	}

	public static <T> ArrayList<T> toArrayList(T[] objectArray)
	{
		ArrayList<T> arr = new ArrayList<T>();
		for (T i : objectArray) arr.add(i);
		return arr;
	}

	public static Scanner stdin = new Scanner(System.in);

	public static void main(String[] args)
	{
		init();     //setup fields

		//parse the data file and transfer into a list of pokemon instances
		ArrayList<Pokemon> pokemons;
		try
		{
			Pokemon[] tmp = loadPokemons(abspath + "pokemon.txt");
			pokemons = toArrayList(tmp);
		} catch (IOException err)
		{
			System.out.println(err.toString());
			return;
		}

		System.out.println("Welcome to Pokemon Arena BEAT THEM ALL.  ");
		System.out.println("Defeat ALL your enemies to become the Trainer Supreme!  ");

		//picking
		Player user = new Player();

		int[] picked = {0, 0, 0, 0};
		pickpokemons(pokemons, picked);


		for (int i : picked)
		{
			user.pokemons.add(pokemons.get(i));
		}
		for (Pokemon i : user.pokemons)
		{
			pokemons.remove(i);
		}

		//enter battle
		while (!pokemons.isEmpty())
		{
			//pop a pokemon from the list of unused
			Player enemy = new Player();
			int indx = new Random().nextInt(pokemons.size());
			enemy.pokemons.add(pokemons.get(indx));
			pokemons.remove(indx);


			//new battle, setup
			Player atk = user;
			Player def = enemy;

			Battle battle = new Battle(atk, def);

			//Battle auto. init. a random starter
			//true->atk, false->def
			//since user is always atk, the val should be the same
			boolean isUserTurn = battle.nextRound;

			battle.init(); //init battle states before making decisions

			while (true)
			{
				Battle.ACTION action;
				String param;
				Battle.nextRoundr result;
				int prevHP;

				if (isUserTurn)
				{
					System.out.println("It is Your Turn NOW.  ");
					System.out.println("Your Pokemon's State:\n"+atk.getCurrentPokemon().toStatusString());
					System.out.println("Enemy's Pokemons's State:\n" + def.getCurrentPokemon().toStatusString());

					String[] strs = nextaction(atk).split(" "); //{ACTION, PARAM}
					if (strs[0].equals("ATTACK")) action = Battle.ACTION.ATTACK;
					else if (strs[0].equals("RETREAT")) action = Battle.ACTION.RETREAT;
					else action = Battle.ACTION.PASS;
					param = strs[1];
					prevHP = def.getCurrentPokemon().hp();
				}
				else
				{
					//decide computer's action
					action = Battle.ACTION.ATTACK;
					int i = def.getCurrentPokemon().randAtkindx();
					if (i == -1) action = Battle.ACTION.PASS;   //none of the attacks avail.
					param = i + "";
					prevHP = atk.getCurrentPokemon().hp();
				}


				result = battle.nextRound(action, param);
				if (isUserTurn)
				{
					System.out.printf("%s's Turn: \t\tDamage Dealt: %d\n",
							atk.getCurrentPokemon().name(), (prevHP - def.getCurrentPokemon().hp()));
				}
				else
				{
					System.out.printf("%s's Turn: \t\tDamage Dealt: %d\n",
							def.getCurrentPokemon().name(), (prevHP - atk.getCurrentPokemon().hp()));
				}

				//since only one pokemon for enemy, this wont matter, only the user will be asked for next pokemon
				if (result == Battle.nextRoundr.FALSE) break;
				else if (result == Battle.nextRoundr.PADEAD || result == Battle.nextRoundr.PBDEAD)
				//only user will remove, computer only has one pokemon, if it is dead, nextRound would return FALSE
				{
					System.out.printf("The current pokemon %s is dead(HP = 0).  Please pick the next one! \n",
							atk.getCurrentPokemon().name());
					atk.remove();   //remove the current pokemon

					atk.setCurrentPokemon(Integer.parseInt(nextpokemon(atk)));

					//re-gener. random starter
					battle.nextRound = battle.randomBoolean();
				}



				isUserTurn = battle.nextRound;
			}
			//battle.end() will be called by Battle

			if (!atk.isAlive()) //user's dead
			{
				System.out.println("You Losted.  GAME OVER.  ");
				break;
			}
			else
			{
				if (pokemons.size() != 0)
					System.out.printf("WON!  %d enemy(enemies) left! \n", pokemons.size());
				else
					System.out.println("Excellent Job!  Mr. Trainer Supreme!  ");
			}


		}

	}

	public static String nextpokemon(Player player) //this method ensures the selection is being done(see pickpokemons)
	{
		int[] sel = {0};
		pickpokemons(player.pokemons, sel);
		return "" + sel[0];
	}

	public static String nextaction(Player player)
	{
		while (true)
		{
			
			ArrayList<Integer> aff = player.getCurrentPokemon().affordables();
			int t;
			if (aff.isEmpty())
			{
				System.out.println("1. RETREAT \n2. PASS");
				t = stdin.nextInt(); stdin.nextLine(); 
				t ++ ;      //please compare to the "else" clause
			}
			else
			{
				System.out.println("1. ATTACK \n2. RETREAT \n3. PASS");
				t = stdin.nextInt(); stdin.nextLine(); 
			}
			
			switch (t)
			{
			case 1:
				for (int i: aff)
				{
					System.out.println(i+". "+player.getCurrentPokemon().attacks()[i].toString());
				}
				t = stdin.nextInt(); stdin.nextLine(); 
				if (t >= aff.size()) continue;  //failed, ask again
				return "ATTACK " + t;
			case 2:
				System.out.println("Retreat!  Selected your next pokemon!  ");
				int sel;
				try
				{
					sel = pickpokemons(player.pokemons);
				}
				catch(Exception err)
				{
					if (err.getMessage().equals("retn")) continue; //ask user again
					else throw new IllegalStateException(err.getMessage());
				}

				System.out.println(player.pokemons.get(sel).name() + " Selected!");
				return "RETREAT " + sel;
			case 3:
				return "PASS 0";
			default:
				
			}
		}

	}

	public static void pickpokemons(ArrayList<Pokemon> pokemons, int[] selections)
	//***this method does not allow "retn" to happen
	{
		HashSet<Integer> set = new HashSet<Integer>();
		Pokemon[] sel = new Pokemon[selections.length];
		int t = 0;
		System.out.println("Pick Your Pokemons!  Type in 'HELP' for more information.  ");

		while (set.size() != selections.length)
		{
			System.out.println((set.size()-selections.length) + " spots left! ");
			while (true)    //ensures "retn" does not happen
			{
				try
				{
					sel[t] = pokemons.get(pickpokemons(pokemons));
					break;
				} catch (Exception err)
				{
					if (err.getMessage().equals("retn"))
					{
						System.out.println("RETN is NOT allowed in this session!  ");
					} else
					{
						throw new IllegalStateException(err.getMessage());  //unexpected
					}
				}
			}
			if (set.add(sel[t].ID())) t++;      //set.add: return true if not in it yet
			else System.out.println("Already Picked!");
		}

		//figure out the right indx for the selection
		t = 0;
		for (int i=0; i<pokemons.size(); i++)
		{
			if (set.contains(pokemons.get(i).ID()))
			{
				selections[t++] = i;
			}
		}
	}

	public static int pickpokemons(ArrayList<Pokemon> pokemons) throws Exception
	//Unless the action is successfully done, an Exception will occur
	//***RETN is not acceptable in this method
	{
		PickPokemonConsole console = new PickPokemonConsole();
		console.setPokemons(pokemons);
		while (console.next());
		if (console.getExitStatus() == null || !console.getExitStatus().equals(""))
			throw new Exception(console.getExitStatus());   //invalid exit status, SHOULD BE CAUGHT
		return console.getLastResult();
	}
}


class PickPokemonConsole extends Console
{
	public ArrayList<Pokemon> pokemons = null;
	public void setPokemons(ArrayList<Pokemon> pokemons)
	{
		this.pokemons = pokemons;
	}

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
					System.out.printf("%20s", i+". "+pokemons.get(i).name());   //80 columns
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

