# ExplosionView
[![Release](https://jitpack.io/v/Zuluft/ExplosionView.svg)](https://jitpack.io/#Zuluft/ExplosionView)
[![](https://jitpack.io/v/Zuluft/ExplosionView/month.svg)](https://jitpack.io/#Zuluft/ExplosionView)

<p align="center">
<img width="880" height="480" src="preview.gif">
</p>

# Quick start guide

## 1. Declare ```ExplosionView``` in xml

```xml
<androidx.constraintlayout.widget.ConstraintLayout 
    xmlns:android="http://schemas.android.com/apk/res/android"
    android:layout_width="match_parent"
    android:layout_height="match_parent">


    <com.zuluft.lib.ExplosionView
        android:id="@+id/explosionView"
        android:layout_width="match_parent"
        android:layout_height="match_parent" />


</androidx.constraintlayout.widget.ConstraintLayout>
```

## 2. Get ```ExplosionView``` object and call ```start()``` method

```kotlin
  override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        explosionView.start()
    }
```
 

# Documentation

* [Installation](#installation)
  - [Gradle](#gradle)
* [Properties](#properties)
  - [Icon](#icon)
  - [Size](#size)
  - [Move factor](#move-factor)
  - [Animation duration](#animation-duration)
  - [Animation delay](#animation-delay)
  - [Scale](#scale)
  - [Alpha](#alpha)
  - [Dragging](#dragging)
  - [Spread direction](#spread-direction)
  - [Spread mode](#spread-mode)
  - [Horizontal offset](#horizontal-offset)
  - [Repeat count](#repeat-count)
* [Instantiation](#instantiation)
  - [XML](#xml)
  - [Kotlin](#kotlin)
* [Public methods](#public-methods)
  - [start](#start)
  - [finish](#finish)
  - [finishSmoothly](#finishSmoothly)
  - [isAnimRunning](#isAnimRunning)
  - [getSettings](#getSettings)
  - [changeSettings](#getSettings)

## Installation

### Gradle:
Add it in your root build.gradle at the end of repositories:

```Groovy
	allprojects {
		repositories {
			...
			maven { url 'https://jitpack.io' }
		}
	}
```
Add dependency to app gradle:

```Groovy
 implementation 'com.github.Zuluft:ExplosionView:VERSION_TAG
```
 

## Properties

----

### Icon

#### ```drawable```
Item icon drawable.

Default value: :heart:

---

### Size

#### ```itemWidth```
Original width of the item.

Default value: ```40dp```.

#### ```itemHeight```
Original height of the item.

Default value: ```0dp```.

> If either ```itemWidth``` or ```itemHeight``` is 0dp, item size will be computed using drawable's aspect ratio.

---

### Move factor

Move factor controls single item moving behaviour.

As much as move factor value is near to 0, item behaviour becomes more chaosic.

As much as move factor is greater then 0, item behaviour becomes more ordered.

Move factor values for the items are distributed randomly between ```minMoveFactor``` and ```maxMoveFactor```.

#### ```minMoveFactor```

Minimal move factor value.

Default value: ```0.7```

#### ```maxMoveFactor```

Maximal move factor value.

Default value: ```0.8```

---

### Animation duration

Animation duration controls, how much time should take one item's moving scene.

Animation duration values for the items are distributed randomly between ```minAnimDuration``` and ```maxAnimDuration```.

#### ```minAnimDuration```

Minimal animation duration value.

Default value: ```1000```

#### ```maxAnimDuration```

Maximal animation duration value.

Default value: ```10000```

---

### Animation delay

Animation delay controls how much time should be delayed item's moving scene.

Animation delay values for the items are distributed randomly between ```minAnimDelay``` and ```maxAnimDelay```.

#### ```minAnimDelay```

Minimal animation delay value.

Default value: ```1000```

#### ```maxAnimDelay```

Maximal animation delay value.

Default value: ```10000```

---

### Scale

Scale controls how much should be resized item's original size.

Scale values for the items are distributed randomly between ```minScale``` and ```maxScale```.

#### ```minScale```

Minmal scale value.

Default value: ```0.7```

#### ```maxScale```

Maximal scale value.

Defaul value: ```1.1```

---

### Alpha

Alpha controls opacity of the item.

Alpha values for the items are distributed randomly between ```minAlpha``` and ```maxAlpha```.

#### ```minAlpha```

Minimal alpha value.

Default value: ```0.5```

#### ```maxAlpha```

Maximal alpha value.

Default value: ```1```

---

### Dragging

When dragging is enabled, you can catch moving item and drag it. When you release it, it'll continue it's moving scene.

#### ```isDraggable```

Enables or disables dragging mode.

Default value: ```false```

---

### Spread direction

Spred direction controls where to start and where to finish every single item's moving scene.

#### ```spreadDirection```

Possible values: ```TOP```, ```BOTTOM```

Default value: ```TOP```

---

### Spread mode

There are 2 spread modes available. 

In unidirectional mode, all items moving scenes are started from top or from bottom of the view.(It's depended on ```spreadDirection``` property)

In Bidirectional mode, half of items moving scenes are started from one site and another half's moving scenes-from another site.

#### ```spreadMode```

Possible values: ```UNIDIRECTIONAL```, ```BIDIRECTIONAL```

Default value: ```UNIDIRECTIONAL```

---

### Horizontal offset

Horizontal offset controls the range of randomly generated ```X``` coordinates of item's path, during animation scene.

For example, if horizontal offset is ```20``` pixels, randomly generated ```X``` coordinates will be in ```[20, view.width-20]``` range

#### ```horizontalOffset```

Horizontal offset value.

Default value: ```1dp```

---

### Repeat count

Repeat count controls, how many times should animation scene of every single item be repeated.

#### ```repeatCount```

Repeat count value.

Default value: ```-1```

> When repeat count is ```-1```, it means that animation should be repeated infinitely.

---

## Instantiation

### XML

```xml
  <com.zuluft.lib.ExplosionView
            android:id="@+id/explosionView"
            android:layout_width="match_parent"
            android:layout_height="match_parent"
            app:itemWidth="50dp"
            app:itemHeight="30dp"
            app:ItemsCount="100"
            app:minMoveFactor="0.2"
            app:maxMoveFactor="1.1"
            app:minAnimDuration="400"
            app:maxAnimDuration="15000"
            app:minAnimDelay="1000"
            app:maxAnimDelay="7000"
            app:minScale="0.5"
            app:maxScale="1.5"
            app:minAlpha="0.3"
            app:maxAlpha="1"
            app:isDraggable="true"
            app:horizontalOffset="20dp"
            app:spreadDirection="BOTTOM"
            app:spreadMode="UNIDIRECTIONAL"
            app:repeatCount="5"
    />
```


### Kotlin


```kotlin
ExplosionView(
            ExplosionViewSettings
                .Builder(context!!)
                .itemWidth(50)
                .itemHeight(30)
                .itemCount(100)
                .minMoveFactor(0.5f)
                .maxMoveFactor(1.1f)
                .minAnimDuration(400L)
                .maxAnimDuration(15000L)
                .minScale(0.5f)
                .maxScale(1.5f)
                .minAlpha(0.3f)
                .maxAlpha(1f)
                .isDraggable(true)
                .horizontalOffset(20)
                .spreadDirection(Bottom)
                .spreadMode(Unidirectional)
                .repeatCount(5)
                .build()
        )
```


## Public methods

### ```start()```

Starts all item's animation scenes.

### ```finish()```

Immediately stops all item's animation scenes.

### ```finishSmoothly()```

Waits until current animation scenes are finished and then stops all of them. 

### ```isAnimRunning()```

Return type: ```Boolean```

Returns ```true```, if animations are running. Othervise - ```false```.

### ```getSettings()```

Return type: ```ExplosionViewSettings```

Returns current settings of ```ExplosionView```

### ```changeSettings(explosionViewSettings: ExplosionViewSettings)```

Changes settings of ```ExplosionView```

Example:

```kotlin
explosionView.changeSettings(
            explosionView.getSettings()
                .newBuilder()
                .isDraggable(true)
                .spreadMode(Bidirectional)
                .minAlpha(0.5f)
                .maxMoveFactor(1.5f)
                .build()
        )
```
