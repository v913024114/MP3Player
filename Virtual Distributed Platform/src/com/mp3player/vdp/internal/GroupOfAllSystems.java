/**
 * 
 */
package com.mp3player.vdp.internal;

/**
 * Creates and maintains a list of all systems running a VDP instance.
 * A TCP connection is kept open with each of these systems.
 * Systems of this group may (but need not) be part of a dedicated 
 * VDP peer group, eg. one for a distrobuted MP3 player or one for a distributed 
 * video player.
 * 
 * The P2P system discovery implemented in this class uses UDP multicasts 
 * every few seconds to discover new or newly attached systems.
 * 
 * A TCP connection is set up with each new client. The TCP connection
 * handles keepalive so that clients leaving the group are detected via closing
 * of the TCP connection.
 * 
 * GroupOfAllSystems starts a thread that waits for multicast messages
 * from other systems and checks whether these are already in its systems list.
 * If so, it discards the message.
 * If not, it builds up a TCP connection to the system, adds it to its systems list 
 * and calls the callback function provided in the constructor to make
 * the new system known to the owner of the GroupOfAllSystems instance
 * 
 * GroupOfAllSystems also starts a thread that sends multicast messages of its own
 * every (number of known clients) seconds. This ensures that network traffic
 * goes up only linearly with the number of VDP systems.
 *  
 * In another thread, GroupOfAllSystems waits for incoming TCP connections 
 * requests, usually triggered by the multicast messages sent by it. 
 * It accepts the incoming connection request, adds it to its systems list 
 * and calls the callback function provided in the constructor to make
 * the new system known to the owner of the GroupOfAllSystems instance.
 * 
 * @author Matthias Holl
 *
 */
public class GroupOfAllSystems {

}
