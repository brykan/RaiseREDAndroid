package co.createlou.RaiseRED;

/**
 * Created by Bryan on 12/13/16.
 */

public class User {

    public String fullName;

    public User() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public User(String fullName) {
        this.fullName = fullName;
    }

}