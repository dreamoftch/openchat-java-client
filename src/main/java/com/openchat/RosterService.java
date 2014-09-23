package com.openchat;

/**
 * Service interface for chat rosters.
 *
 * @author wche
 * @since 9/23/14
 */
public interface RosterService {

    /**
     * Add an user to buddy list
     * @param username
     */
    public void addBuddy(String username);

}
