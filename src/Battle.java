import java.util.*;

/**
 * Battle.java
 *
 * This class handles all the battle related logics that is needed in PokemonArena game.
 * This class is designed in the logic of (start -- running -- end).
 *
 * @author Charles-Jianye Chen
 */

public class Battle
{
	private boolean isDone = false;
	private boolean isInitialized = false;
	private int countActions = 0;
	
	/**
	 * If nextRound holds this value, the attacker(first param during construction) would go for the nextRound.  
	 *
	 * @see	Battle#nextRound
	 */
	public static final boolean ATTACKER = true;
	public static final boolean DEFENSER = false;
	
	/**
	 * This field holds the value prepared for next round.  It determines who goes next.  
	 *
	 * @see	Battle#ATTACKER
	 * @see	Battle#DEFENSER
	 */
	public boolean nextRound =  randomBoolean();   //randomly select the starter
	
	/**
	 * Randomly select a boolean value.  The chance is 50:50.  
	 *
	 * @return	a random boolean
	 */
	public static boolean randomBoolean()
	{
		return new Random().nextInt(1) == 0;
	}

	private boolean winner = ATTACKER;
	
	/**
	 * Fetch the winner of this battle.  The value would be valid only if Battle.end is called.  
	 *
	 * @see	Battle#ATTACKER
	 * @see	Battle#DEFENSER
	 * @return	the boolean repr. of the winner
	 */
	public boolean winner()
	{
		return winner;
	}

	public Player attacker;
	public Player defenser;

	/**
	 * Constructs the Battle instance by 2 Players.  
	 * @see	Player
	 */
	public Battle(Player attacker, Player defenser)
	{
		this.attacker = attacker;
		this.defenser = defenser;
	}

	/**
	 * The enum for the battle actions in the game.  
	 */
	public enum ACTION
	{
		ATTACK, RETREAT, PASS
	}

	/**
	 * Init. the Player's state(do not need to be explicitly called).  
	 * <p> At the start of each battle all Pokemon start with 50 energy.  </p>
	 *
	 * @return	true if init. was successful.  
	 */
	private boolean init()
	{
		boolean result = true;

		for (Pokemon i: attacker.pokemons) if(i.isAlive()) i.charge_energy(50);
		for (Pokemon i: defenser.pokemons) if(i.isAlive()) i.charge_energy(50);

		isInitialized = result;
		return result;
	}


	/**
	 * The enum for the results of nextRound.  
	 *
	 * <p> TRUE		there is a next round (the battle is not done yet)	</p>
	 * <p> FALSE	one of the player's last Pokemon is dead, the battle is done  </p>
	 * <p> PADEAD	the attacker's pokemon of THIS round is dead, the attacker has other ones.  </p>
	 * <p> PBDEAD	the defenser's pokemon of THIS round is dead, the defenser has other ones.  </p>
	 *
	 * @see	Battle#nextRound
	 */
	public enum nextRoundr
	{
		TRUE, FALSE, PADEAD, PBDEAD, UNDETERMINED
	}

	/**
	 * The function handles the logics of the battle-ing process.  
	 * <p> It determines who's round it is, and performs the action specified by the ACTION.  </p>
	 *
	 * <p> ACTION.ATTACK, atkindx(attack) </p>
	 * <p> ACTION.RETREAT, pokemon's indx </p>
	 * <p> ACTION.PASS, reserved </p>
	 *
	 * <p> After each round each Pokemon recovers 10 energy, to a maximum of 50.  </p>
	 * @return		nextRoundr
	 * @see	Battle#nextRoundr
	 */
	public nextRoundr nextRound(ACTION action, String param)
	{
		if (!isInitialized) init();
		if (isDone) return nextRoundr.FALSE;
		
		countActions++;	//2 actions make a round
		
		nextRoundr result;

		Player playerA, playerB;
		if (this.nextRound == ATTACKER) {playerA = attacker; playerB = defenser;}
		else {playerA = defenser; playerB = attacker;}

		//perform action(s)
		if (action == ACTION.ATTACK)  playerA.attack(playerB, Integer.parseInt(param));
		else if (action == ACTION.RETREAT) playerA.setCurrentPokemon(Integer.parseInt(param));
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
		if (countActions%2 == 0)
		{
			for (Pokemon i: attacker.pokemons) i.charge_energy(10);
			for (Pokemon i: defenser.pokemons) i.charge_energy(10);
		}

		//set flags, return result
		boolean bothAlive = attacker.isAlive() && defenser.isAlive();
		this.nextRound = !this.nextRound;
		isDone = !bothAlive;
		if (isDone) {this.end(); result = nextRoundr.FALSE;}
		if (bothAlive && result == nextRoundr.UNDETERMINED) result = nextRoundr.TRUE;
		return result;
	}

	/**
	 * Finish the battle, Perform post-battle actions.  
	 */
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
