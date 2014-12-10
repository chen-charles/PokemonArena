
import java.util.*;

public class Player
{
    public static int IDcount = 0;
    private int ID;
    public int ID(){ return ID; }
    public ArrayList<Pokemon> pokemons = new ArrayList<Pokemon>();
    private int currentPokemon = 0;
    public Pokemon getCurrentPokemon(){return pokemons.get(currentPokemon);}

    public Player(){ ID = IDcount++; }
    public Player(ArrayList<Pokemon> pokemons)
    {
        for (Pokemon i: pokemons) this.pokemons.add(i);
        ID = IDcount ++;
    }

    public void remove()
    {
        pokemons.remove(currentPokemon);
        currentPokemon = -1;
    }

    public boolean attack(Player enemy, int atkindx)
    {
        return this.pokemons.get(this.currentPokemon).attack(enemy.pokemons.get(enemy.currentPokemon), atkindx);
    }

    public void setCurrentPokemon(String name)
    {
        for (int i=0; i<pokemons.size(); i++)
        {
           if (pokemons.get(i).name().equals(name))
           {
                currentPokemon = i;
           }
        }
    }

    public void setCurrentPokemon(int indx)
    {
        currentPokemon = indx;
    }

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
