package soundgood.startup;

import soundgood.controller.Controller;
import soundgood.integration.SoundgoodDBException;
import soundgood.view.BlockingInterpreter;

/**
 * Starts the bank client.
 */
public class Main {
    /**
     * @param args There are no command line arguments.
     */
    public static void main(String[] args) {
        try {
            new BlockingInterpreter(new Controller()).handleCmds();
            } catch (SoundgoodDBException sdbe) {
                System.out.println("Could not connect to soundgood db.");
                sdbe.printStackTrace();
            }
        }
}