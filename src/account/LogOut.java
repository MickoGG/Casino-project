package account;

import application.Main;
import game.BaseGame;


public class LogOut { 
    
    private LogOut() {}
    
    
    public static void logOut() {
        onLogOut(false);
        Main.setStartMenuPane();
    }
    
    
    public static void onLogOut(boolean isExit) {
        boolean stillRunning = false;
        
        BaseGame.acquireMutex();
        
        for (BaseGame g : Main.getListOfGames()) {
            if (g.getIsBonusActive()) {
                if (isExit) g.skipBonusBeforeExitApp();
                else g.skipBonusBeforeLogOut();
                stillRunning = true;
            }
            else {
                Main.finalizeGameBeforeClose(g.toString());
                g.callInterrupt();
            }
        }
        
        BaseGame.releaseMutex();
        
        if (!stillRunning) {
            beforeLogOut(isExit);
        }
    }
    
    
    public static void beforeLogOut(boolean isExit) {
        if (Player.getCurrentPlayer() != null) Account.logOutUser();
        if (isExit) Account.closeAll();
        else Main.getListOfGames().clear();
    }
    
}
