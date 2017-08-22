package com.czecherface.adminpowers;

import com.czecherface.adminpowers.powers._Power;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Hashtable;

/**
 * My goal was to encapsulate this data as much as possible to keep myself
 * from accidentally modifying it and breaking its consistency.  Basically this code
 * used to reside in AdminPowers.java, but I felt that it was too exposed.
 */
public class PowerTracker {
    private Hashtable<String, _Power> playerPowers;
    
    public PowerTracker()
    {
        playerPowers = new Hashtable<String, _Power>();
    }
    
    public void applyPlayerPowers(String playerName, _Power power)
    {
        removePlayerPowers(playerName);
        playerPowers.put(playerName, power);
    }
    
    public boolean playerHasPowers(String playerName)
    {
        return playerPowers.containsKey(playerName);
    }
    
    public _Power getPlayerPowers(String playerName)
    {
        return playerPowers.get(playerName);
    }
    
    public void removePlayerPowers(String playerName)
    {
        _Power p = getPlayerPowers(playerName);
        if (p == null)
            return;
        if (p.mustCleanUp())
            p.cleanUp();
        playerPowers.remove(playerName);
    }
    
    /**
     * This is a special method which will return all current active powers
     * that match the subclass of _Power provided.
     */
    <X> X[] getPowers(Class<X> powerType) {
        if (!powerType.getSuperclass().equals(_Power.class))
            throw new RuntimeException("You must provide a subclass of _Power: " + powerType.toString());
        ArrayList<X> l = new ArrayList<X>();
        for (String key : playerPowers.keySet())
        {
            _Power power = getPlayerPowers(key);
            if (!(power.getClass().isAssignableFrom(powerType)))
                continue;
            l.add((X)power);
        }
        X[] toReturn = (X[])Array.newInstance(powerType, l.size());
        return l.toArray(toReturn);
    }
}
