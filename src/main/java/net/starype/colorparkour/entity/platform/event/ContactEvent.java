package net.starype.colorparkour.entity.platform.event;

import net.starype.colorparkour.entity.player.PlayerPhysicSY;

/**
 * The {@link net.starype.colorparkour.entity.player.Player} calls the methods when something happens
 * between the platform that implements ContactEvent and the player.
 *
 * collided : When the player arrives onto the platform
 * collision : When the player is in collision with the platform
 * leaveByJump : When the player leaves the platform because he jumped
 * leave : When the player leaves the platform because he fell from it
 */
public interface ContactEvent {

    /*
        All the methods below are default
        The child class may not need to use all of the provided methods
     */
    default void collided(PlayerPhysicSY physicSY){}
    default void collision(PlayerPhysicSY physicSY){}
    default void leaveByJump(PlayerPhysicSY physicSY){}
    default void leave(PlayerPhysicSY physicSY){}
}
