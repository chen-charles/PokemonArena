import java.util.*;

/**
 * Player.java
 *
 * This class is the layer between Pokemons and other classes.
 * This class groups pokemons into players and provides some convenient methods to arrange the pokemons.
 *
 * @author Charles-Jianye Chen
 */

public class Player
{
    private static int IDcount = 0;
    private int ID;

    /**
     * Returns the unique identifier for an instance of the Player.
     * @return  the unique ID of the Player instance.
     */
    public int ID(){ return ID; }

    /**
     * The public ArrayList of the pokemons the player has.
     * <p>The contents of this field should not be changed. </p>
     * <p>It is there only for the purpose of being used as READ-ONLY. </p>
     *
     * Since it will be referenced very oftenly, making a getter and then create a copy of this arraylist costs too much.
     */
    public ArrayList<Pokemon> pokemons = new ArrayList<Pokemon>();

    private int currentPokemon = 0;

    /**
     * Returns the current Pokemon that is in use of the Player
     * @return  the current Pokemon instance of the Player.
     */
    public Pokemon getCurrentPokemon(){return pokemons.get(currentPokemon);}

    /**
     * Construct the Player instance with no pokemons.
     */
    public Player(){ ID = IDcount++; }

    /**
     * Construct the Player instance with a list of pokemons provided.
     * @param pokemons      a list of pokemons of the Player
     */
    public Player(ArrayList<Pokemon> pokemons)
    {
        for (Pokemon i: pokemons) this.pokemons.add(i);
        ID = IDcount ++;
    }

    /**
     * Removes the current Pokemon from the Player's Pokemon list.
     * <p>Note, the currentPokemon indx will be set to invalid. </p>
     * <p>A call to setCurrentPokemon must be issued before any sensitive operations. </p>
     */
    public void remove()
    {
        pokemons.remove(currentPokemon);
        currentPokemon = -1;
    }

    /**
     * Uses the current Pokemon of the Player to attack the enemy Player's current Pokemon by the attack(atkindx)
     * @param enemy     the enemy's Player
     * @param atkindx   the attack(the index of the attack)
     * @return  true if the attack is being done successfully.
     */
    public boolean attack(Player enemy, int atkindx)
    {
        return this.pokemons.get(this.currentPokemon)
                .attack(enemy.pokemons.get(enemy.currentPokemon), atkindx);
    }

    /**
     * Sets the current Pokemon by its name.
     * <p>If multiple pokemons with same names are present, the first one (lower index) would be selected.  </p>
     * @param name      the name of the Pokemon
     */
    public void setCurrentPokemon(String name)
    {
        for (int i=0; i<pokemons.size(); i++)
        {
           if (pokemons.get(i).name().equals(name))
           {
               currentPokemon = i;
               break;
           }
        }
    }

    /**
     * Sets the current Pokemon by its indx in Player.pokemons
     * @param indx      the index of the Pokemon
     */
    public void setCurrentPokemon(int indx)
    {
        currentPokemon = indx;
    }

    /**
     * Checks if the Player is still considered "alive"
     * <p>That is, at least one Pokemon belongs to the Player is alive. </p>
     * @return  true if the Player is still considered "alive".
     */
    public boolean isAlive()
    {
        int count = 0;
        for (Pokemon i: this.pokemons)
        {
            if (i.isAlive()) count ++;
        }
        return count != 0;
    }
}
