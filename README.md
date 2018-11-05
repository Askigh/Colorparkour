# Colorparkour

**Welcome to the Colorparkour project !**

You can find a gif showing the current advancement [here](https://i.imgur.com/aC1fDGU.gifv)

To keep yourself updated about was has been done or what needs to be done, you can always check the TODO list in the wiki.

**Extra content - Using the [Referential](https://github.com/Askigh/Colorparkour/blob/master/src/main/java/net/starype/colorparkour/utils/Referential.java) class**

* Sample - Make a player follow a vehicle smoothly (pseudo code)

```
RigidBodyControl car = (...);

RigidBodyControl player = (...)

Referential ref = new Referential(car, player);
ref.setEnabled(false);

if(player arrives in the car) {
    ref.setEnabled(true);
}

if(player wants to move inside the car, the car still drives) {
    // You can either do :
    Referential.of(player).get().getExternalBody().applyCentralForce(the force you want to apply);
    // or
    ref.getExternalBody().applyCentralForce(the force you want to apply);
}

if(player leaves the car) {
    ref.setEnabled(false)
}
```