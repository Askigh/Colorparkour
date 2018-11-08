package net.starype.colorparkour.entity.platform.event;

import net.starype.colorparkour.entity.player.PlayerPhysicSY;

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
