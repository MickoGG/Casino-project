package game;

import account.Account;
import account.LogOut;
import account.Player;
import application.Main;
import java.math.BigDecimal;
import java.util.Random;
import java.util.concurrent.Semaphore;
import javafx.scene.layout.BorderPane;
import models.Game;


public abstract class BaseGame extends Thread {

    private static int runningGamesCnt = 0;
    private static final Semaphore mutex = new Semaphore(1);
    
    protected final Random rand = new Random();
    private final Player currentPlayer;
    protected BigDecimal balance;
    protected double chip = 1;
    protected BigDecimal bet;
    protected Semaphore sem = new Semaphore(0);
    
    private boolean firstSpin = true;
    private boolean spinBonus = false;
    protected boolean skippableBonus = false;
    protected boolean pause = true;
    protected boolean bonusActive = false;
    private boolean backPressed = false;
    protected boolean loggedOut = false;
    private boolean exitedApp = false;
    
    protected static final int DEFAULT_NEXT_SPIN_SLEEP_TIME = 1800;
    protected static final int DEFAULT_SKIP_SLEEP_TIME = 100;
    protected static final int DEFAULT_SPIN_BONUS_SLEEP_TIME = 3000;
    protected static final int DEFAULT_BONUS_START_SLEEP_TIME = 1500;
    protected static final int DEFAULT_ANIMATION_SLEEP_TIME = 50;
    
    protected int nextSpinSleep = DEFAULT_NEXT_SPIN_SLEEP_TIME;
    protected int animationSleep = DEFAULT_ANIMATION_SLEEP_TIME;
    
    
    public BaseGame() {
        super();
        currentPlayer = Player.getCurrentPlayer();
        start();
    }

    
    protected interface ResetVariables {
        void afterSpinReset();
        
        void afterBonusReset();
    }
    
    
    /*
    -------------------------------------------------------
        THIS IS WHAT ALL GAMES SHOULD IMPLEMENT - START
    -------------------------------------------------------
    */
    
    protected abstract ResetVariables getResetVariables();                  // <--- Get object that contains all fields that are required for bonus to function, 
                                                                            // where there are 2 implemented methods about how to reset that fields.
                                                                            // Subclass is required to define nested class that implements ResetVariables interface,
                                                                            // and then in this method to return object of that type.
    
    protected abstract String getGameName();                                // <--- The name of the game
    
    public abstract void setBalanceLabel(BigDecimal newBalance);            // <--- Define how to show balance label when given new balance
    
    public abstract BorderPane game();                                      // <--- GUI
    
    protected abstract void spin();                                         // <--- Define what happens on spin
    
    protected abstract void onBonusEnd() throws InterruptedException;       // <--- from run() method
    
    protected abstract void onFirstSpin() throws InterruptedException;      // <--- from run() method
    
    protected abstract void animation() throws InterruptedException;        // <--- from run() method
    
    protected abstract void connectionLogic() throws InterruptedException;  // <--- from run() method
    
    protected abstract void onNextSpin() throws InterruptedException;       // <--- from run() method
    
    /*
    -----------------------------------------------------
        THIS IS WHAT ALL GAMES SHOULD IMPLEMENT - END
    -----------------------------------------------------
    */
    
    
    /*
    ---------------------------------------
        GENERAL PURPOSE METHODS - START
    ---------------------------------------
    */
    
    public final void init() {
        updateBalance();
        setBalanceLabel(balance);
        backPressed = false;
    }
    
    
    @Override
    public final String toString() {
        return getGameName();
    }
    
    
    public final int getGameID() {
        return Account.getEntityManager().createNamedQuery("Game.findByName", Game.class).setParameter("name", this.toString()).getSingleResult().getIDGame();
    }
    
    
    public final static int getRunningGamesCnt() {
        return runningGamesCnt;
    }
    
    
    public final static void acquireMutex() {
        try {
            mutex.acquire();
        } catch (InterruptedException ex) {}
    }
    
    
    public final static void releaseMutex() {
        mutex.release();
    }
    
    
    public final void updateBalance() {
        balance = currentPlayer.getBalance();
    }
    
    
    private void addAndUpdateBalance(BigDecimal payment) {
        currentPlayer.addBalance(payment);
        balance = currentPlayer.getBalance();
    }
    
    
    public final boolean getIsBonusActive() {
        return bonusActive;
    }
    
    
    public final void callInterrupt() {
        interrupt();
    }
    
    /*
    -------------------------------------
        GENERAL PURPOSE METHODS - END
    -------------------------------------
    */
    
    
    /*
    -----------------------------------
        BACK BUTTON METHODS - START
    -----------------------------------
    */
    
    protected final void onBack() {
        acquireMutex();
        
        if (bonusActive) backPressed = true;
        else Main.finalizeGameBeforeClose(this.toString());
        
        Main.setGamePickPane();
        
        releaseMutex();
    }
    
    /*
    ---------------------------------
        BACK BUTTON METHODS - END
    ---------------------------------
    */
    
    
    /*
    --------------------------------------
        LOG OUT / EXIT METHODS - START
    --------------------------------------
    */
    
    public final void skipBonusBeforeLogOut() {
        loggedOut = true;
        nextSpinSleep = 0;
        animationSleep = 0;
    }
    
    
    public final void skipBonusBeforeExitApp() {
        skipBonusBeforeLogOut();
        exitedApp = true;
        sem.release(100);
    }
    
    /*
    ------------------------------------
        LOG OUT / EXIT METHODS - END
    ------------------------------------
    */
    
    
    /*
    -----------------------------------------------
        UPDATE CurrentlyPlaying METHODS - START
    -----------------------------------------------
    */

    private void updateCurrentlyPlayingTotalWager(BigDecimal totalPayment) {
        try {
            Account.getEntityManager().getTransaction().begin();
            Main.getCurrPlaying(this.toString()).setTotalWager(Main.getCurrPlaying(this.toString()).getTotalWager().add(totalPayment));
            Account.getEntityManager().getTransaction().commit();
        }
        finally {
            if (Account.getEntityManager().getTransaction().isActive()) Account.getEntityManager().getTransaction().rollback();
        }
    }
    
    
    private void updateCurrentlyPlayingTotalReceived(BigDecimal totalPayment) {
        try {
            Account.getEntityManager().getTransaction().begin();
            Main.getCurrPlaying(this.toString()).setTotalReceived(Main.getCurrPlaying(this.toString()).getTotalReceived().add(totalPayment));
            Account.getEntityManager().getTransaction().commit();
        }
        finally {
            if (Account.getEntityManager().getTransaction().isActive()) Account.getEntityManager().getTransaction().rollback();
        }
    }
    
    
    protected final void processPayment(BigDecimal payment) {
        acquireMutex();
        
        // Update balance
        addAndUpdateBalance(payment);
        
        // Update for table CurrentlyPlaying
        updateCurrentlyPlayingTotalReceived(payment);
        
        releaseMutex();
    }
    
    
    private void processWager() {
        acquireMutex();
        
        // Update balance
        addAndUpdateBalance(bet.negate());
        
        // Update for table CurrentlyPlaying
        updateCurrentlyPlayingTotalWager(bet);
        
        releaseMutex();
    }
    
    /*
    ---------------------------------------------
        UPDATE CurrentlyPlaying METHODS - END
    ---------------------------------------------
    */
    
    
    /*
    ----------------------------
        SPIN METHODS - START
    ----------------------------
    */
    
    protected final int onSpin() {
        updateBalance();
        bet = BigDecimal.valueOf(chip);
        
        if (bet.compareTo(balance) != 1) {
            processWager();
            setBalanceLabel(balance);
            
            spin();
            
            return 0;
        }
        
        return -1;
    }
    
    
    protected final void onSpinBonus(int chipMultiplier) {
        bet = BigDecimal.valueOf(chip * chipMultiplier);
        updateBalance();      // For update if "Add balance" was done from menu
        setBalanceLabel(balance);

        spinBonus = true;
        startBonus();
    }
    
    /*
    --------------------------
        SPIN METHODS - END
    --------------------------
    */
    
    
    /*
    -----------------------------
        BONUS METHODS - START
    -----------------------------
    */
    
    private void startBonus() {
        bonusActive = true;
        acquireMutex();
        runningGamesCnt++;
        releaseMutex();
        
        pause = false;
        synchronized (this) {
            notify();
        }
    }
    
    
    protected final int onBonusBought(int chipMultiplier) {
        updateBalance();      // For update if "Add balance" was done from menu
        bet = BigDecimal.valueOf(chip * chipMultiplier);
        
        if (bet.compareTo(balance) != 1) {
            processWager();
            setBalanceLabel(balance);
            
            startBonus();
            
            return 0;
        }
        
        return -1;
    }
    
    
    protected final void skipBonus() {
        nextSpinSleep = DEFAULT_SKIP_SLEEP_TIME;
        animationSleep = 0;
    }
    
    
    private void onBonusEndWrapper() throws InterruptedException {
        onBonusEnd();
        
        // Reset of variables
        getResetVariables().afterBonusReset();
    }
    
    
    private synchronized void finalizeBonus() throws InterruptedException {
        mutex.acquire();
        bonusActive = false;
        runningGamesCnt--;

        if (loggedOut) {
            Main.finalizeGameBeforeClose(this.toString());
            callInterrupt();
            if (runningGamesCnt == 0) {
                LogOut.beforeLogOut(exitedApp);
            }
        }
        else if (backPressed) {
            backPressed = false;
            Main.finalizeGameBeforeClose(this.toString());
        }
        mutex.release();

        // Reset of variables
        firstSpin = true;
        skippableBonus = false;
        nextSpinSleep = DEFAULT_NEXT_SPIN_SLEEP_TIME;
        animationSleep = DEFAULT_ANIMATION_SLEEP_TIME;
        
        while (pause) {
            wait();
        }
    }
    
    
    private void onFirstSpinWrapper() throws InterruptedException {
        if (!firstSpin) return;
        else firstSpin = false;

        if (spinBonus) {
            sleep(DEFAULT_SPIN_BONUS_SLEEP_TIME);
            spinBonus = false;
        }
        
        skippableBonus = true;
        
        onFirstSpin();
        
        // Waiting for bonus to start
        sleep(DEFAULT_BONUS_START_SLEEP_TIME);
    }
    
    
    private void animationWrapper() throws InterruptedException {
        animation();
    }
    
    
    private void connectionLogicWrapper() throws InterruptedException {
        connectionLogic();
    }
    
    
    private void onNextSpinWrapper() throws InterruptedException {
        // Waiting for next spin
        sleep(nextSpinSleep);
        
        onNextSpin();
        
        // Reset of variables
        getResetVariables().afterSpinReset();
    }
    
    /*
    ---------------------------
        BONUS METHODS - END
    ---------------------------
    */
    
    
    @Override
    public final void run() {
        try {
            synchronized (this) {
                while (pause) {
                    wait();
                }
            }

            while (!isInterrupted()) {
                synchronized (this) {
                    if (pause) {                // If pause == true then bonus is over
                        onBonusEndWrapper();
                        
                        finalizeBonus();        // Already implemented
                    }
                }
                
                onFirstSpinWrapper();

                animationWrapper();
                
                connectionLogicWrapper();

                onNextSpinWrapper();
            }
        } catch (InterruptedException e) {}
    }
    
}
