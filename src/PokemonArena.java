import java.io.*;
import java.util.*;

/**
 * PokemonArena.java
 *
 * The PokemonArena Game.  ICS4U.
 * A pokemon game with only battle-ing stuff.
 *
 * @see PickPokemonConsole
 * @author  Charles-Jianye Chen
 */
public class PokemonArena
{
	public static Hashtable<String, Pokemon.TYPE> pkmonTypeMap = new Hashtable<String, Pokemon.TYPE>();
	public static Hashtable<String, Pokemon.SPECIAL> pkmonSpecialMap = new Hashtable<String, Pokemon.SPECIAL>();
	public static String abspath = PokemonArena.class.getProtectionDomain().getCodeSource().getLocation().getPath();

	/**
	 * Init. the static fields of PokemonArena.
	 *
	 */
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

	/**
	 * Parses String repr of a Pokemon instance into a Pokemon instance.
	 * @param raw       String repr. of Pokemon.
	 * @return      the newly generated Pokemon.
	 * @see PokemonArena#loadPokemons
	 */
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

		//	public Pokemon(String name, int hp, Pokemon.TYPE type, Pokemon.TYPE resistance, Pokemon.TYPE weakness,
		//Pokemon.AttackInfo[] attacks);
		return new
				Pokemon(data[0], Integer.parseInt(data[1]), pkmonTypeMap.get(data[2]), pkmonTypeMap.get(data[3]),
				pkmonTypeMap.get(data[4]), attacks);
	}

	/**
	 * Load String repr of Pokemon instances from a file.
	 *
	 * <p>File Format: </p>
	 *
	 * <p>Line 1: num of entries</p>
	 * <p>The following lines:
	 * name, hp, type, resistance, weakness, numattacks, numattacks*[attackname, energycost, damage, special]
	 * </p>
	 *
	 * @param fname     the file's name which contains the String repr. of Pokemon instances.
	 * @return      an array of the Pokemon instances generated from the file.
	 * @throws IOException  if the it occurs while opening the file.
	 */
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

	/**
	 * Convert an object array into an ArrayList of the instances.
	 * @param objectArray   the array which is going to be transformed.
	 * @param <T>           type of the object array.
	 * @return      the ArrayList of the instances in the array.
	 */
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

	/**
	 * Calls pickpokemons(ArrayList, int[]) to fetch the Pokemon indx of the selection.
	 * @param player        the Player object which contains the pokemons to be selected.
	 * @return      the String repr. of an indx.
	 */
	public static String nextpokemon(Player player) //this method ensures the selection is being done(see pickpokemons)
	{
		int[] sel = {0};
		pickpokemons(player.pokemons, sel);
		return "" + sel[0];
	}

	/**
	 * Asks the user to select their next action.
	 * @param player        the Player who needs the next action.
	 * @return      the String repr of the next action: Battle.ACTION, param
	 */
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

	/**
	 * This method creates a console and asks user to input a total amount of (selection.length) non-rep pokemons.
	 * <p>This method does not allow "retn" to happen.  </p>
	 * @param pokemons      the arraylist containing pokemons to select.
	 * @param selections    the array for storing (returning) indx (length must be specified).
	 * @see PickPokemonConsole
	 */
	public static void pickpokemons(ArrayList<Pokemon> pokemons, int[] selections)
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

	/**
	 * This method creates a console and asks user to input one pokemon selection.
	 * <p>This method does allow "retn" to happen.  </p>
	 * @param pokemons      the arraylist containing pokemons to select.
	 *
	 * @see PickPokemonConsole
	 * @throws Exception    if a "retn" is received from the console.
	 *
	 * @return      the indx of the selection.
	 */
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

