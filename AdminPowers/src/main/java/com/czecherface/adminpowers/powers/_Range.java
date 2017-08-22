package com.czecherface.adminpowers.powers;

public enum _Range {
    CLOSE                   (32),
    MEDIUM                  (128),
    LONG                    (256),
    VISION                  (512);
    
    public final int value;
    private _Range(int val)
    {
        value = val;
    }
}
