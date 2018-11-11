# Colorparkour

**Welcome to the Colorparkour project !**

You can find a gif showing the current advancement [here](https://i.imgur.com/aC1fDGU.gifv)
(last gif update : around october 30th 2018)

To keep yourself updated about was has been done or what needs to be done, you can always check the TODO list in the wiki.
***
**Game description**

A jump game, that's basically what Colorparkour is. You have to go through modules, jumping onto the colored platforms.
Once You've completed all the levels, your time is saved and you can always try to improve your speed !
Each platform has its own color, and its own effect. There are four different colors: 
* yellow
* red
* green
* blue. 

The importance of a color will be explained below. 
There are 8 different effects :
* Standard : No particular effect. You arrive onto the platform, you leave it by falling or jumping.
* Ice : Same as standard, but be careful: it may be a bit slippery in here.
* Double jump : Pretty cool power : After falling or jumping off the platform, you get another jump!
* Moving : Platforms that move at a constant speed, and no randomness in their movement. Be careful, they are not sticky!
* Sticky : Same as Moving, but if you don't move, no panic! You always follow the platform. You are free to move though
* Bounce : No platform. Just trampoline.
* Low / High gravity : Looks like you are either in the moon or in Jupiter here. Gravity has changed!
* Gravity direction change : The intensity of the gravity doesn't change. However, you may not fall downward anymore.

As said above, the color of the platforms is significant. In fact, you need to choose which color you want, and only the
platforms being this color and the white ones will be displayed. You can constantly change the color you want, and the
current chosen coloris always displayed on the screen. For instance, if you are on a white platform and the two next ones
are blue and red, select "blue", jump onto the blue platform, jump again and as soon as you are in the air, switch the
color to red and the red platform will appear.
***
**Extra content - Using the [Referential](https://github.com/Askigh/Colorparkour/blob/master/src/main/java/net/starype/colorparkour/utils/Referential.java) class**

* Sample - Make a player follow a vehicle smoothly (pseudo code)

```java
RigidBodyControl car = (...);

RigidBodyControl player = (...);

Referential ref = new Referential(car, player);
ref.setEnabled(false);

if(player arrives in the car) {
    ref.setEnabled(true);
}

if(player wants to move inside the car, the car still drives) {
    // In this case you have the referential as a variable so you can execute
    ref.getExternalBody().applyCentralForce(the force you want to apply);
    // if you don't, there is still this possibility :
    Referential.of(player).get().getExternalBody().applyCentralForce(the force you want to apply);
}

if(player leaves the car) {
    ref.setEnabled(false)
}
```