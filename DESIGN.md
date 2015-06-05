# Aight
### Design document
June 5th 2015

## Classes & Methods
To get an idea of the functions of this app, here is an overview of the classes & methods that need to be implemented in order for this app to work properly.

**ParseLoginUI** The ParseLoginUI will be used to show a login

**MapsView**

**CreateEvent**

## APIs & Frameworks

- Google Maps API
- Parse Login Library API

This app will use the Google Maps API for the MapsView activity.

To enable login, parse will be used.

a list of APIs and frameworks that you will be using to provide functionality in your app
a list of data sources, and database tables and fields (and their types) that you’ve decided to implement


## Sketches

### 2 Main activities: MapsView and CreateEvent
The third activity is LogIn
![](docs/wireframe.png)

### MapsView
Shows current position on the map, zoomed in to a level that you see no more than a 1 km radius around you.
Shows all events that are currently active within the view of the map as clickable dots.
When clicked these dots will expand showing the message that the creator of the event wrote and how much time there is left.
An options menu will be available in the top right corner in which the user can set whether all events or only friends' events should be shown, and it will be possible to log out.

### CreateEvent
In this activity there will be a text box, in which the user can input up to 140 characters on what their intended activity is about. There will be a slider for the duration of the event, which will determine when the event expires and disappears from the map.

### LogIn
The LogIn activity will only show when no login is detected. So generally only the first time a user uses the app on a device. Parse will handle log in.


