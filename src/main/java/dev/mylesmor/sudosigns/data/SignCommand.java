package dev.mylesmor.sudosigns.data;

/**
 * The SignCommand class for storing sign command data.
 * 
 * @author MylesMor
 * @author https://mylesmor.dev
 */
public class SignCommand {

    private String command;
    private PlayerInput type;
    private int number;
    private double delay;

    public SignCommand(int number, String cmd, double delay, PlayerInput type) {

        this.number = number;
        command = cmd;
        this.type = type;
        this.delay = delay;

    }

    public String getCommand() {

        return command;

    }

    public PlayerInput getType() {

        return type;

    }

    public int getNumber() {

        return number;

    }

    public double getDelay() {

        return delay;

    }

    public void setDelay(double delay) {

        this.delay = delay;

    }

}
