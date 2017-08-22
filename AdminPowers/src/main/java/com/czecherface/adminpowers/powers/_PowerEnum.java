package com.czecherface.adminpowers.powers;

public enum _PowerEnum {

    BOX                 ("b", "(m:outer) [m:inner] [b:override]", Box.class),
    BLOCKDATA           ("bd", "(byte:data)", BlockData.class),
    CONJURE             (true, "c", "(blockname)", Conjure.class),
    CLICKPORT           ("cp", null, ClickPort.class),
    FILL                ("f", "(m:fill) [m:replace] [b:override]", Fill.class),
    HEALTH              ("h", "[i:amount | playerName]", Health.class),
    IDENTIFIER          ("i", null, Identifier.class),
    INSTAMINE           ("im", "[b:fullPower]", Instamine.class),
    JUMP                ("j", "(d:up) [b:lunge]", Jump.class),
    KILL                ("kill", "[playerName]", Kill.class),
    LIGHTNING           ("l", "[b:superStrike]", Lightning.class),
    MOUNT               ("m", null, Mount.class),
    PLACE               ("p", "(m:block)", Place.class),
    REPLACE             ("r", "(m:replaceWith) [m:replaceOnly]", Replace.class),
    SPAWNER             ("s", "(i:creatureType_id)", Spawner.class),
    TELEPORT            ("tp", "(i:x) (i:z) <-> [i:y]", Teleport.class),
    WEATHER             ("w", "(n|s|t)", Weather.class);
    public static final int length = _PowerEnum.values().length;
    private String command, paramsForHelp;
    private boolean isDisabled;
    private Class<?> c;

    private _PowerEnum(String command, String paramsForHelp, Class<?> c) {
        this(false, command, paramsForHelp, c);
    }
    private _PowerEnum(boolean disabled, String command, String paramsForHelp, Class<?> c) {
        if (command == null || c == null) {
            throw new NullPointerException();
        }
        isDisabled = disabled;
        this.command = command;
        this.paramsForHelp = "";
        if (paramsForHelp != null && paramsForHelp.length() != 0) {
            this.paramsForHelp = " " + paramsForHelp;
        }
        this.c = c;
    }

    public boolean isDisabled()
    {
        return isDisabled;
    }
    
    public String getName() {
        return c.getSimpleName();
    }

    public boolean matchesCommand(String command) {
        return this.command.equalsIgnoreCase(command);
    }

    public _Power getNewInstance() {
        try {
            return (_Power) c.newInstance();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }

    public String getHelpString() {
        if (isDisabled())
            return "{gray}/ap " + command + paramsForHelp + " - " + c.getSimpleName();
        return "{green}/ap " + command + paramsForHelp + "{white} - " + c.getSimpleName();
    }
}