package account;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import models.Privilege;
import models.Session;
import models.User;


public class Account {
    
    private static final EntityManagerFactory emf = Persistence.createEntityManagerFactory("Casino_projectPU");
    private static final EntityManager em = emf.createEntityManager();
    
    private static Session session;
    
    public enum TransactionType {DEPOSIT, WITHDRAW};
    
    
    private Account() {}
    
    
    public static void closeAll() {
        em.close();
        emf.close();
    }
    
    
    public static EntityManager getEntityManager() {
        return em;
    }
    
    
    public static Session getSession() {
        return session;
    }
    
    
    static int loginUser(String username, String password) {
        if (username.contains("#")) return -1;  // Account is deleted
        
        username = username.toLowerCase();
        List<User> users = em.createNamedQuery("User.findAll", User.class).getResultList();
        User user = null;

        for (User u : users) {
            if (u.getUsername().equals(username)) {
                user = u;
                break;
            }
        }

        if (user == null) return -1;  // Username doesn't exist
        else if (!user.getPassword().equals(password)) return -2;  // Wrong password
        
        long playerID = user.getIDUser();
        BigDecimal balance = user.getBalance();
        Player.setCurrentPlayer(new Player(username, password, playerID, balance));
        
        session = new Session();
        session.setStartTime(new Date());
        session.setIDUser(user);
        
        try {
            em.getTransaction().begin();
            em.persist(session);
            em.getTransaction().commit();
        }
        finally {
            if (em.getTransaction().isActive()) em.getTransaction().rollback();
        }

        return 0;
    }

    
    static int registerUser(String fullName, String email, String username, String password) {
        // Check full name
        Pattern patternFullName = Pattern.compile("^[a-zA-Z]+([a-zA-Z]| [a-zA-Z])*$");
        Matcher matcherFullName = patternFullName.matcher(fullName);
        if (!matcherFullName.find()) return -1;
        
        // Check email
        email = email.toLowerCase();
        Pattern patternEmail = Pattern.compile("^\\w+@[a-z]+(\\.[a-z]+)*\\.[a-z]{2,3}$");
        Matcher matcherEmail = patternEmail.matcher(email);
        if (!matcherEmail.find()) return -2;
        
        // Check username
        username = username.toLowerCase();
        Pattern patternUsername = Pattern.compile("^[a-zA-Z]\\w*$");
        Matcher matcherUsername = patternUsername.matcher(username);
        if (!matcherUsername.find()) return -3;
        
        // Check password
        Pattern patternPassword = Pattern.compile("^.{6,}$");
        Matcher matcherPassword = patternPassword.matcher(password);
        if (!matcherPassword.find()) return -4;
        
        List<User> users = em.createNamedQuery("User.findAll", User.class).getResultList();

        for (User u : users) {
            if (u.getUsername().equals(username)) return -5;  // Username already exists
            else if (u.getEmail().equals(email)) return -6;  // Email already exists
        }

        User user = new User();
        user.setFullName(fullName);
        user.setEmail(email);
        user.setUsername(username);
        user.setPassword(password);
        user.setRegistrationDate(new Date());
        user.setBalance(BigDecimal.ZERO);
        
        Privilege userPrivilege = em.createNamedQuery("Privilege.findByName", Privilege.class).setParameter("name", "user").getSingleResult();
        user.setIDPri(userPrivilege);
        
        try {
            em.getTransaction().begin();
            em.persist(user);
            em.getTransaction().commit();
        }
        finally {
            if (em.getTransaction().isActive()) em.getTransaction().rollback();
        }
        
        return 0;
    }
    
    
    static int deleteAccountForUser(String username, String password) {
        username = username.toLowerCase();
        List<User> users = em.createNamedQuery("User.findAll", User.class).getResultList();
        User user = null;

        for (User u : users) {
            if (u.getUsername().equals(username)) {
                user = u;
                break;
            }
        }

        if (user == null) return -1;  // Username doesn't exist
        else if (!user.getPassword().equals(password)) return -2;  // Wrong password
        
        try {
            em.getTransaction().begin();
            user.setUsername(username + "#rm" + user.getIDUser());
            user.setEmail(user.getEmail() + "#rm" + user.getIDUser());
            em.getTransaction().commit();
        }
        finally {
            if (em.getTransaction().isActive()) em.getTransaction().rollback();
        }

        return 0;
    }

    
    static void logOutUser() {
        Player.setCurrentPlayer(null);
        
        try {
            em.getTransaction().begin();
            session.setEndTime(new Date());
            long duration = session.getEndTime().getTime() - session.getStartTime().getTime();
            session.setDuration(new Date(duration - 3600000));    // 1 hour = 3 600 000 milliseconds
            em.getTransaction().commit();
        }
        finally {
            if (em.getTransaction().isActive()) em.getTransaction().rollback();
        }
    }
    
    
    public static int changeBalanceForUser(TransactionType type, String username, String password, BigDecimal amount) {
        username = username.toLowerCase();
        List<User> users = em.createNamedQuery("User.findAll", User.class).getResultList();
        User user = null;

        for (User u : users) {
            if (u.getUsername().equals(username)) {
                user = u;
                break;
            }
        }

        if (user == null) return -1;  // Username doesn't exist
        else if (!user.getPassword().equals(password)) return -2;  // Wrong password
        
        try {
            em.getTransaction().begin();
            if (type == TransactionType.DEPOSIT) user.setBalance(user.getBalance().add(amount));
            else user.setBalance(user.getBalance().subtract(amount));
            em.getTransaction().commit();
        }
        finally {
            if (em.getTransaction().isActive()) em.getTransaction().rollback();
        }
        
        if (Player.getCurrentPlayer() != null && Player.getCurrentPlayer().getUsername().equals(username)) {
            Player.getCurrentPlayer().setBalance(user.getBalance());
        }

        return 0;
    }

}
