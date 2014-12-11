import java.util.*;

public class Pokemon
{
	private String name;
	public String name(){return this.name;}

	private int hp;
	private int maxhp;
	public int hp(){return this.hp;}
	public void charge_hp(int amount)
	{
		if (amount > 0) this.hp = Math.min(this.hp+amount, maxhp);
	}
	public boolean isAlive(){return this.hp > 0;}

	private int energy;
	public int energy(){return this.energy;}
	public void charge_energy(int amount)
	{
		if (amount > 0) this.energy = Math.min(this.energy+amount, 50);
	}

	private TYPE type;
	public TYPE type(){return this.type;}

	private TYPE resistance;
	public TYPE resistance(){return this.resistance;}

	private TYPE weakness;
	public TYPE weakness(){return this.weakness;}

	private AttackInfo attacks[];
	public int randAtkindx()
	{
		ArrayList<Integer> arr = this.affordables();
		if (arr.size() == 0) return -1;
		return new Random().nextInt(arr.size());
	}
	public AttackInfo[] attacks()
	{
		AttackInfo[] arr = new AttackInfo[attacks.length];
		System.arraycopy(this.attacks, 0, arr, 0, attacks.length);
		return arr;
	}

	//2 digits: STUN, DISABLE
	private boolean[] specialstates;
	public boolean isStunned(){return specialstates[0];}
	public boolean popStunned()
	{
		boolean tmp = isStunned();
		specialstates[0] = false;
		return tmp;
	}
	public boolean isDisabled(){return specialstates[1];}
	public boolean[] specialstates()
	{
		boolean[] arr = new boolean[specialstates.length];
		System.arraycopy(this.specialstates, 0, arr, 0, specialstates.length);
		return arr;
	}

	public enum TYPE
	{
		EARTH, FIRE, GRASS, WATER, FIGHTING, ELECTRIC
	}
	
	public enum SPECIAL
	{
		STUN, WILDCARD, WILDSTORM, DISABLE, RECHARGE, NONE
	}

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

	}

	public Pokemon(String name, int hp, Pokemon.TYPE type, Pokemon.TYPE resistance, Pokemon.TYPE weakness, int numAttacks,
	               Pokemon.AttackInfo[] attacks)
	{
		this.name = name;
		this.hp = hp;
		this.maxhp = hp;
		this.type = type;
		this.resistance = resistance;
		this.weakness = weakness;
		this.energy = 50;

		this.attacks = new AttackInfo[numAttacks];
		this.specialstates = new boolean[2];
		System.arraycopy(attacks, 0, this.attacks, 0, numAttacks);
	}

	public ArrayList<Integer> affordables()
	{
		ArrayList<Integer> arr = new ArrayList<Integer>();
		for(int i=0; i<attacks.length; i++)
			if (testAfford(i)) arr.add(i);
		return arr;
	}

	private boolean testAfford(int atkindx)
	{
		return energy() - attacks[atkindx].cost() > 0;
	}

	public boolean attack(Pokemon that, int atkindx)
	{
		if(!testAfford(atkindx)) return false;

		int damage = attacks[atkindx].damage();
		if (that.weakness() == this.type()) damage *= 2;
		if (that.resistance() == this.type()) damage /= 2;
		if (this.isDisabled()) damage = Math.min(damage-10, 0);

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
	
	public String toString()
	{
		String line = new String(new char[80]).replace("\0", "=");
		return String.format(line+"\n"+"Name: %s \tType: %s \tResis.: %s \tWeakness: %s\n", name, type, resistance, weakness)
			+ String.format("Hp: %d / %d \tEnergy: %d / %d\t", hp, maxhp, energy, 50)
			+ (isStunned() ? String.format("STUNNED \t") : "")
			+ (isDisabled() ? String.format("DISABLED \t") : "") + "\n"
			+ ""
			+ "\n" + line;
	}
}
