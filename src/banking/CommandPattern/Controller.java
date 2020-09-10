package banking.CommandPattern;

public class Controller {

    private Command command;

    public void setCommand(Command command) {
        this.command = command;
    }

    public boolean executeCommand() {
        return command.execute();
    }
}
