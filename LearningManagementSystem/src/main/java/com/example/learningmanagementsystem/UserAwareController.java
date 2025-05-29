package com.example.learningmanagementsystem;

public interface UserAwareController {

    /**
     * Initializes the controller with the authenticated user's data.
     * Implement this in all dashboards that receive the logged-in user.
     * @param user The logged-in user
     */
    void initUserData(User user);

    void initializeWithUser(User user);
}