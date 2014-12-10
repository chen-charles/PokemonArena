

/**
 *
 */

public class Battle
{
	private boolean isDone = false;
	private boolean isInitialized = false;
	public static final boolean ATTACKER = true;
	public static final boolean DEFENSER = false;
	private boolean nextRound = ATTACKER;

	private boolean winner = ATTACKER;
	public boolean winner()
	{
		return winner;
	}

	public Player attacker;
	public Player defenser;

	public Battle(Player attacker, Player defenser)
	{
		this.attacker = attacker;
		this.defenser = defenser;
	}


	public enum ACTION
	{
		ATTACK, RETREAT, PASS
	}

	public boolean init()
	{
		boolean result = true;

		for (Pokemon i: attacker.pokemons) if(i.isAlive()) i.charge_energy(50);
		for (Pokemon i: defenser.pokemons) if(i.isAlive()) i.charge_energy(50);

		isInitialized = result;
		return result;
	}

	//ATTACK, NAME ABILITY
	//RETREAT, NAME POKEMON
	//PASS, RESERVED
	//ATTACKER ALWAYS PERFORMS ACTIONS FIRST
	public enum nextRoundr
	{
		TRUE, FALSE, PADEAD, PBDEAD, UNDETERMINED
	}
	//PADEAD=PBDEAD=TRUE!

	public nextRoundr nextRound(ACTION action, String param)
	{
		if (!isInitialized) init();
		if (isDone) return nextRoundr.FALSE;

		nextRoundr result;

		Player playerA, playerB;
		if (this.nextRound == ATTACKER) {playerA = attacker; playerB = defenser;}
		else {playerA = defenser; playerB = attacker;}

		//perform action(s)
		if (action == ACTION.ATTACK)  playerA.attack(playerB, Integer.parseInt(param));
		else if (action == ACTION.RETREAT) playerA.setCurrentPokemon(param);
		else{}

		Pokemon currentPokemonA = playerA.getCurrentPokemon();
		Pokemon currentPokemonB = playerB.getCurrentPokemon();

		//check if any death
		if (currentPokemonA.isAlive() && currentPokemonB.isAlive())
		{
			result = nextRoundr.UNDETERMINED;
		}
		else if (currentPokemonA.isAlive()) //B is dead
		{
			result = nextRoundr.PBDEAD;
		}
		else
		{
			result = nextRoundr.PADEAD;
		}

		//end of round procedure
		for (Pokemon i: attacker.pokemons) i.charge_energy(10);
		for (Pokemon i: defenser.pokemons) i.charge_energy(10);

		//set flags, return result
		boolean bothAlive = attacker.isAlive() && defenser.isAlive();
		this.nextRound = !this.nextRound;
		isDone = !bothAlive;
		if (isDone) {this.end(); result = nextRoundr.FALSE;}
		if (bothAlive && result == nextRoundr.UNDETERMINED) result = nextRoundr.TRUE;
		return result;
	}

	private void end()
	{
		//determine winner
		if (attacker.isAlive() && defenser.isAlive()) return;
		else if(attacker.isAlive())
		{
			winner = ATTACKER;
			for (Pokemon i: attacker.pokemons) if (i.isAlive()) i.charge_hp(20);
		}
		else
		{
			winner = DEFENSER;
			for (Pokemon i: defenser.pokemons) if (i.isAlive()) i.charge_hp(20);
		}



	}
}
