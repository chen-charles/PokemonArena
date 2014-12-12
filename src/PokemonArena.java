
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

		pkmonTypeMap.put("electric", Pokemon.TYPE.ELECTRIC);
		pkmonTypeMap.put("earth", Pokemon.TYPE.EARTH);
		pkmonTypeMap.put("fire", Pokemon.TYPE.FIRE);
		pkmonTypeMap.put("grass", Pokemon.TYPE.GRASS);
		pkmonTypeMap.put("fighting", Pokemon.TYPE.FIGHTING);
		pkmonTypeMap.put("water", Pokemon.TYPE.WATER);
	}

	public static Pokemon parseRaw(String raw) //parse the RAW data, then call the constructor
	{
		String[] data = raw.split(",");
		int numAttacks = Integer.parseInt(data[5]);
		Pokemon.AttackInfo[] attacks = new Pokemon.AttackInfo[numAttacks];
		int j = 0;
		for (int i = 0; i < numAttacks; i++)
		{
			attacks[i] = new Pokemon.AttackInfo(data[6 + j], Integer.parseInt(data[6 + j + 1]),
					Integer.parseInt(data[6 + j + 2]), pkmonSpecialMap.get(data[6 + j + 3]));
			j += 4;
		}

		return new
				Pokemon(data[0], Integer.parseInt(data[1]), pkmonTypeMap.get(data[2]), pkmonTypeMap.get(data[3]),
				pkmonTypeMap.get(data[4]),
				numAttacks, attacks);
	}

	public static Pokemon[] loadPokemons(String fname) throws IOException
	{
		Scanner infile;
		try
		{
			infile = new Scanner(new File(fname));
		} catch (IOException err)
		{
			System.out.println(err);
			throw err;
		}
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
		init();

		ArrayList<Pokemon> pokemons;
		try
		{
			Pokemon[] tmp = loadPokemons(abspath + "pokemon.txt");
			pokemons = toArrayList(tmp);
		} catch (IOException err)
		{
			return;
		}

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
			Player enemy = new Player();
			int indx = new Random().nextInt(pokemons.size());

			enemy.pokemons.add(pokemons.get(indx));
			pokemons.remove(indx);

			//new battle
			Player atk = user;
			Player def = enemy;
			boolean isComputerTurn = false;
			Battle battle = new Battle(atk, def);

			battle.init(); //init battle states before making decisions


			while (true)
			{
				Battle.ACTION action;
				String param;
				Battle.nextRoundr result;

				if (isComputerTurn)
				{
					action = Battle.ACTION.ATTACK;
					int i = def.getCurrentPokemon().randAtkindx();
					if (i == -1) action = Battle.ACTION.PASS;
					param = i + "";

				} else
				{
					System.out.println("States ATK: " + atk.getCurrentPokemon().name() + " " + atk.getCurrentPokemon().hp());
					System.out.println("States DEF: " + def.getCurrentPokemon().name() + " " + def.getCurrentPokemon().hp());
					String[] strs = ask(atk, ASK.NEXTACTION).split(" ");
					if (strs[0].equals("ATTACK")) action = Battle.ACTION.ATTACK;
					else if (strs[0].equals("RETREAT")) action = Battle.ACTION.RETREAT;
					else action = Battle.ACTION.PASS;
					param = strs[1];
				}


				result = battle.nextRound(action, param);
				//since only one pokemon for enemy, this wont matter, only the user will be asked for next pokemon
				if (result == Battle.nextRoundr.FALSE) break;
				else if (result == Battle.nextRoundr.PADEAD || result == Battle.nextRoundr.PBDEAD)
				//only user will remove, computer only has one pokemon, if it is dead, nextRound would return FALSE
				{
					atk.remove();
					if (atk.pokemons.isEmpty()) break;  //should not be reached, nextRound should return false for this
					atk.setCurrentPokemon(Integer.parseInt(ask(atk, ASK.NEXTPOKEMON)));

				}


				isComputerTurn = !isComputerTurn;
			}
			System.out.println(battle.winner());
			//battle.end() will be called before program reaches here

			if (!atk.isAlive()) //user's dead
			{
				break;
			}


		}

	}

	public enum ASK
	{
		NEXTPOKEMON, NEXTACTION
	}

	public static String ask(Player player, ASK type)
	{
		switch (type)
		{
		case NEXTPOKEMON:
			int[] selection = {0};
			pickpokemons(player.pokemons, selection);
			return selection[0] + "";
		case NEXTACTION:

			return nextaction(player);
		default:
			return "";
		}
	}

	public static String nextaction(Player player)
	{
		while (true)
		{
			
			ArrayList<Integer> arr = player.getCurrentPokemon().affordables();
			int t;
			if (arr.isEmpty())
			{
				System.out.println("1. RETREAT \n2. PASS");
				t = stdin.nextInt(); stdin.nextLine(); 
				t ++ ;
			}
			else
			{
				System.out.println("1. ATTACK \n2. RETREAT \n3. PASS");
				t = stdin.nextInt(); stdin.nextLine(); 
			}
			
			switch (t)
			{
			case 1:
				for (int i: arr)
				{
					System.out.println(i+". "+player.getCurrentPokemon().attacks()[i].toString());
				}
				t = stdin.nextInt(); stdin.nextLine(); 
				return "ATTACK " + t;
			case 2:
				return "RETREAT " + ask(player, ASK.NEXTPOKEMON);
			case 3:
				return "PASS 0";
			default:
				return nextaction(player);
			}
		}

	}

	public static void pickpokemons(ArrayList<Pokemon> pokemons, int[] selections)
	{
		for (int i = 0; i < pokemons.size(); i++)
		{
			System.out.println(i + ". " + pokemons.get(i).name());
		}
		for (int i = 0; i < selections.length; i++)
		{
			while (true)
			{
				selections[i] = stdin.nextInt();
				stdin.nextLine();
				if (selections[i] < pokemons.size()) break;
			}
		}
	}
}
