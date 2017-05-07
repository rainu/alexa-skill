# cloud module
This module contains features for (next/own)cloud. At the moment only the feature of CalDAV.

# how to configure alexa

* login to [amazon developer](http://developer.amazon.com)
* go to _alexa_ tab
* go to _alexa skills kit_ item
* add a new skill
  * _Skill Type_: custom interaction model
  * _Language_: german
  * _Name_: any name
  * _Interaction Model_: paste the code from __resources/alexa_skill_definition.json__

# how to configure server

This module can connect to multiple calendars. It reads the environment variables for configuration. In the following
table you can find the base environment variables you have to define.

| Key  | Example value  |  Description  |
|---|---|---|
| CALDAV_NAME | parties  | A natural name/alias of this callendar. This is important because the user can ask for this name!  |
| CALDAV_URL  | https://cloud.nextcloud.example/remote.php/dav | The CalDav-URL of your calendar. In next/own-cloud instances it is normaly &lt;domain&gt;/remove.php/dav |
| CALDAV_USER  | rainu | The user name for this calendar. |
| CALDAV_PASSWORD  | secret | The users password. |
| CALDAV_CALENDAR_URL  | calendars/rainu/1ca2131  | The specific calendar url. |
| CALDAV_TIMEZONE | UTC | (optional) The default time zone for this calendar |

You can configure multiple calendars. Just put a number (begining by zero) between **CALDAV_** and the **_suffix**. If you have multiple calendars the environment without numbers will be ignored! Lets say you will configure two calendars. Your environment have to look like this:

| Key  | value |
|---|---|
| CALDAV_0_NAME | parties |
| CALDAV_0_URL | https://cloud.nextcloud.example/remote.php/dav |
| CALDAV_0_USER | rainu |
| CALDAV_0_PASSWORD | secret |
| CALDAV_0_CALENDAR_URL | https://cloud.nextcloud.example/remote.php/dav/calendars/rainu/1ca2131/ |
| CALDAV_0_TIMEZONE | UTC |
| CALDAV_1_NAME | birthdays |
| CALDAV_1_URL | https://cloud.nextcloud.example/remote.php/dav |
| CALDAV_1_USER | rainu |
| CALDAV_1_PASSWORD | secret |
| CALDAV_1_CALENDAR_URL | calendars/rainu/456213af |

# what can i say to alexa?

## get a list events (german):

* Alexa, frage _skill_ was __query__ ansteht.
* Alexa, frage _skill_ was __query__ zu tun ist.
* Alexa, frage _skill_ was __query__ abgeht.

* Alexa, starte _skill_.
  * Was steht __query__ an?
  * Was ist __query__ zu tun?
  * Was geht __query__ ab?
  
Where __query__ can be the following:
* (nothing)
* heute
* morgen
* übermorgen
* diese Woche
* diesen Montag
* diesen Dienstag
* diesen Mittwoch
* diesen Donnerstag
* diesen Freitag
* diesen Samstag
* diesen Sonntag
* nächste Woche
* nächsten Montag
* nächsten Dienstag
* nächsten Mittwoch
* nächsten Donnerstag
* nächsten Freitag
* nächsten Samstag
* nächsten Sonntag

## create a new event (german):

* Alexa, starte _skill_ und erstelle einen neuen Termin.
* Alexa, starte _skill_ und lege einen neuen Termin an.
* Alexa, starte _skill_ und erstelle einen Termin.
* Alexa, starte _skill_ und lege einen Termin an.

* Alexa, starte _skill_.
  * Erstelle einen Termin.
  * Erstelle einen neuen Termin.
  * Lege einen Termin an.
  * Lege einen neuen Termin an.
  
Alexa will start a dialog with you where she ask you about event information.