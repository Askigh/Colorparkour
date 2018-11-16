package net.starype.colorparkour.entity.platform.event;

/**
 * Interface that indicates when a platform needs special bindings when loaded.
 * For instance, the friction of the ice platforms must be set to 0 as soon as the body is created.
 */
public interface LoadEvent {

    void load();
}
