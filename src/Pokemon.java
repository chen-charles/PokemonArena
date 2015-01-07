import java.util.*;

/**
 * Pokemon.java
 *
 * This class consists most of the Pokemon Logics in the PokemonArena game.
 *
 * @author Charles-Jianye Chen
 */

public class Pokemon
{
	private String name;

	/**
	 * Fetch the Pokemon's name
	 * @return  the name of the Pokemon(two Pokemons might have identical names though).
	 */
	public String name(){return this.name;}

	/**
	 * Rename the Pokemon
	 * @param name      the String repr of the Pokemon's new name
	 */
	public void rename(String name){this.name = name;}

	private int hp;
	private int maxhp;

	/**
	 * Fetch the Pokemon's current health points(the amount of damage the poor fella can take before being KO’ed)
	 * @return  the HP of the Pokemon.
	 */
	public int hp(){return this.hp;}

	/**
	 * Charge the Pokemon's current health points.  HP does not go over the health point limit.
	 * @param amount    the amount of health points to charge into the Pokemon
	 */
	public void charge_hp(int amount)
	{
		if (amount > 0) this.hp = Math.min(this.hp+amount, maxhp);
	}

	/**
	 * Checks if the Pokemon is still alive.
	 * @return  true if the Pokemon is alive.
	 */
	public boolean isAlive(){return this.hp > 0;}

	private int energy;

	/**
	 * Fetch the Pokemon's current energy points(used to pay for attacks)
	 * @return  the energy points of the Pokemon.
	 */
	public int energy(){return this.energy;}

	/**
	 * Charge the Pokemon's current energy points.  Energy Points does not go over the limit(that is, 50).
	 * @param amount    the amount of energy points to charge into the Pokemon
	 */
	public void charge_energy(int amount)
	{
		if (amount > 0) this.energy = Math.min(this.energy+amount, 50);
	}

	private TYPE type;

	/**
	 * Fetch the Pokemon's type(this val is not changed through the Pokemon's lifetime)
	 * <p>The type of the Pokemon determines the type of damage it does, and thus affects weakness and resistance. </p>
	 * @return  the type of the Pokemon.
	 */
	public TYPE type(){return this.type;}

	private TYPE resistance;

	/**
	 * Fetch the Pokemon's resistance type.
	 * <p>If a Pokemon is hit by a Pokemon that he is resistant to, then the damage from that attack is cut in half.</p>
	 * @return  the resistance type of the Pokemon.
	 */
	public TYPE resistance(){return this.resistance;}

	private TYPE weakness;

	/**
	 * Fetch the Pokemon's weakness type.
	 * <p>If a Pokemon is hit by a Pokemon that he is resistant to, then the damage from that attack is doubled. </p>
	 * @return  the weakness type of the Pokemon.
	 */
	public TYPE weakness(){return this.weakness;}

	private AttackInfo attacks[];

	/**
	 * Randomly select a doable attack from all the attacks avail. to the Pokemon
	 * @return  the atkindx(int index) of the attack / -1 if none is avail.
	 */
	public int randAtkindx()
	{
		ArrayList<Integer> arr = this.affordables();
		if (arr.size() == 0) return -1;
		return new Random().nextInt(arr.size());
	}

	/**
	 * Fetch the copy of the array for the attacks.  This is being set once only during object creation time.
	 * @return the copy of the existing attacks.
	 */
	public AttackInfo[] attacks()
	{
		AttackInfo[] arr = new AttackInfo[attacks.length];
		System.arraycopy(this.attacks, 0, arr, 0, attacks.length);
		return arr;
	}

	/**
	 * <p>specialstates has a length of 2 </p>
	 * <p>specialstates[0] repr the state of STUN </p>
	 * <p>specialstates[1] repr the state of DISABLE </p>
	 */
	private boolean[] specialstates;

	/**
	 * Checks if the Pokemon is Stunned.
	 * @return  true if the Pokemon is Stunned.
	 */
	public boolean isStunned(){return specialstates[0];}

	/**
	 * Checks if the Pokemon is Stunned.  Then clears the state of STUN.
	 * @return  true if the Pokemon is Stunned.
	 */
	public boolean popStunned()
	{
		boolean tmp = isStunned();
		specialstates[0] = false;
		return tmp;
	}

	/**
	 * Checks if the Pokemon is Disabled.
	 * @return  true if the Pokemon is Disabled.
	 */
	public boolean isDisabled(){return specialstates[1];}

	/**
	 * Fetch the copy of the array for the specialstates.
	 * @return the copy of the current specialstates.
	 */
	public boolean[] specialstates()
	{
		boolean[] arr = new boolean[specialstates.length];
		System.arraycopy(this.specialstates, 0, arr, 0, specialstates.length);
		return arr;
	}

	/**
	 * There are 6 types of Pokemon.  TYPE.NONE is being used for Pokemons with no resis. / weakness only.
	 */
	public enum TYPE
	{
		EARTH, FIRE, GRASS, WATER, FIGHTING, ELECTRIC, LEAF, NONE
	}

	/**
	 * There are 5 spcial attacks.  SPECIAL.NONE is being used for attacks with no SPECIAL states.
	 *
	 * <p>
	 * STUN: on top of normal damage there is a 50% change that the opponent will be stunned for one turn. If a Pokemon is stunned it my not attack or retreat.
	 * </p>
	 * <p>
	 * WILD CARD: The attack only has a 50% chance of success. If it does not succeed no damage is done.
	 * </p>
	 * <p>
	 * STORM: Base attack has a 50% chance of success, again no damage on a miss, but if it succeeds then the Pokemon
	 * does a free wild storm attack (yes this can go on forever.)
	 * </p>
	 * <p>
	 * DISABLE: The target Pokemon becomes disabled, and its attacks will do 10 less damage for the rest of the battle
	 *  (to a minimum of zero).  A Pokemon can only be disabled once.
	 * </p>
	 * <p>
	 * RECHARGE: Adds 20 energy to the attacking Pokemon.
	 * </p>
	 */
	public enum SPECIAL
	{
		STUN, WILDCARD, WILDSTORM, DISABLE, RECHARGE, NONE
	}

	/**
	 * This class consists a toString() method for displaying the String repr of the attack.
	 * <p>This class behaves like a struct consisting a name, a (energy) cost, a (hp) damage and a special state. </p>
	 */
	public static class AttackInfo
	{
		private String name;
		private int cost;
		private int damage;
		private SPECIAL special;

        public AttackInfo(String name, int cost, int damage, SPECIAL special)
        {
			this.name = name;
	        this.cost = cost;
	        this.damage = damage;
	        this.special = special;
        }

		public String name(){return this.name;}
		public int cost(){return this.cost;}
		public int damage(){return this.damage;}
		public SPECIAL special(){return this.special;}
		
		public String toString()
		{
			return name + "\n\t\tEnergy Cost: " + cost + "\tDamage: " + damage + "\tSpecial: " + special;
		}
	}

	private static int idcount = 0;
	protected int ID = idcount++;

	/**
	 * Returns the unique identifier for an instance of the Pokemon.
	 * @return  the unique ID of the Pokemon instance.
	 */
	public int ID(){return ID;}

	/**
	 * Construct a Pokemon instance.
	 * @param name          the name of the Pokemon
	 * @param hp            the (max) health points of the Pokemon(max damage it can take)
	 * @param type          the type (of attack it offers) of the Pokemon
	 * @param resistance    the resistance type of the Pokemon
	 * @param weakness      the weakness of the Pokemon
	 * @param attacks       the AttackInfo struct of the available attacks
	 *
	 * @see Pokemon#name
	 * @see Pokemon#hp
	 * @see Pokemon#type
	 * @see Pokemon#resistance
	 * @see Pokemon#weakness
	 * @see Pokemon#attacks
	 * @see Pokemon.AttackInfo
	 */
	public Pokemon(String name, int hp, Pokemon.TYPE type, Pokemon.TYPE resistance, Pokemon.TYPE weakness, Pokemon.AttackInfo[] attacks)
	{
		this.name = name;
		this.hp = hp;
		this.maxhp = hp;
		this.type = type;
		this.resistance = resistance;
		this.weakness = weakness;
		this.energy = 50;

		this.attacks = new AttackInfo[attacks.length];
		this.specialstates = new boolean[2];
		System.arraycopy(attacks, 0, this.attacks, 0, attacks.length);
	}

	/**
	 * Test through the list of attacks the Pokemon have.  Determine the atkindx(es) that are possible to use.
	 * @see Pokemon#testAfford
	 * @return  the list of possible attacks the Pokemon could use at this moment.
	 */
	public ArrayList<Integer> affordables()
	{
		ArrayList<Integer> arr = new ArrayList<Integer>();
		for(int i=0; i<attacks.length; i++)
			if (testAfford(i)) arr.add(i);
		return arr;
	}

	/**
	 * Test if the specified attack(atkindx) is possible to use.
	 * <p>That is, if the current energy points is greater than the cost of the attack. </p>
	 * @param atkindx   the index of the attack in this#attacks()
	 * @return  true if it is possible to use this attack.
	 */
	private boolean testAfford(int atkindx)
	{
		return energy() - attacks[atkindx].cost() > 0;
	}

	/**
	 * Attack the "that" Pokemon by "this" Pokemon through the given attack(atkindx)
	 * @param that      the other pokemonn that is under attack
	 * @param atkindx   the attack to be used(the index of the attack in this#attacks())
	 * @return  true if the attack is being done successfully.
	 */
	public boolean attack(Pokemon that, int atkindx)
	{
		if(!testAfford(atkindx)) return false;

		int damage = attacks[atkindx].damage();
		if (that.weakness() == this.type()) damage *= 2;
		if (that.resistance() == this.type()) damage /= 2;
		if (this.isDisabled()) damage = Math.max(damage-10, 0);

		switch(attacks[atkindx].special())
		{
			case NONE:
				break;
			case STUN:
				if (Math.random()*100 < 50) that.specialstates[0] = true;
				break;
			case WILDCARD:
				if (Math.random()*100 < 50) damage = 0;
				break;
			case WILDSTORM:
				int multiple = 0;
				while (Math.random()*100 < 50) multiple ++;
				damage *= multiple;
				break;
			case DISABLE:
				that.specialstates[1] = true;
				break;
			case RECHARGE:
				this.charge_energy(20);
				break;
			default:
				return false;
		}

		this.energy -= attacks[atkindx].cost(); //checked by testAfford(), will not below 0
		that.hp = Math.max(that.hp-damage, 0);
		return true;
	}

	/**
	 * Returns the String repr of the Pokemon with AttackInfo(s) being attached.
	 * @return  the String repr of the Pokemon.
	 */
	public String toString()
	{
		StringBuilder sb = new StringBuilder();
		for (AttackInfo info: this.attacks)
		{
			sb.append("\t"+info.toString()+"\n");
		}

		return this.toStatusString()
			+ "Attack Info: \n"
			+ sb.toString();
	}

	/**
	 * Returns the String repr of the Pokemon without AttackInfo(s).
	 * @return  the String repr of the Pokemon.
	 */
	public String toStatusString()
	{
		return String.format("Name: %s \tType: %s \tResis.: %s \tWeakness: %s\n", name, type, resistance, weakness)
			+ String.format("Hp: %d / %d \tEnergy: %d / %d\t", hp, maxhp, energy, 50)
			+ (isStunned() ? String.format("STUNNED \t") : "")
			+ (isDisabled() ? String.format("DISABLED \t") : "") + "\n";
	}
}
