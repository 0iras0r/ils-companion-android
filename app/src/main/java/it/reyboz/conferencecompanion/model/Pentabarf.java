package it.reyboz.conferencecompanion.model;

import java.util.ArrayList;

/**
 * This is a pointless wrapper to juggle some stuff in and out of parsers.
 */
public class Pentabarf {
    public Conference conference;
    public ArrayList<Event> events; // TODO: what happens when there are no events?
}
