# cloud module
This module contains features for (next/own)cloud. At the moment only the feature of CalDAV.

# how to configure

This module can connect to multiple calendars. It reads the environment variables for configuration. In the following
table you can find the base environment variables you have to define.

| Key  | Example value  |  Description  |
|---|---|---|
| CALDAV_NAME | parties  | A natural name/alias of this callendar. This is important because the user can ask for this name!  |
| CALDAV_URL  | https://cloud.nextcloud.example/remote.php/dav | The CalDav-URL of your calendar. In next/own-cloud instances it is normaly &lt;domain&gt;/remove.php/dav |
| CALDAV_USER  | rainu | The user name for this calendar. |
| CALDAV_PASSWORD  | secret | The users password. |
| CALDAV_CALENDAR_URL  | https://cloud.nextcloud.example/remote.php/dav/calendars/rainu/1ca2131/  | The specific calendar url. |

You can configure multiple calendars. Just put a number (begining by zero) between **CALDAV_** and the **_suffix**. If you have multiple calendars the environment without numbers will be ignored! Lets say you will configure two calendars. Your environment have to look like this:

| Key  | value |
|---|---|
| CALDAV_0_NAME | parties |
| CALDAV_0_URL | https://cloud.nextcloud.example/remote.php/dav |
| CALDAV_0_USER | rainu |
| CALDAV_0_PASSWORD | secret |
| CALDAV_0_CALENDAR_URL | https://cloud.nextcloud.example/remote.php/dav/calendars/rainu/1ca2131/ |
| CALDAV_1_NAME | birthdays |
| CALDAV_1_URL | https://cloud.nextcloud.example/remote.php/dav |
| CALDAV_1_USER | rainu |
| CALDAV_1_PASSWORD | secret |
| CALDAV_1_CALENDAR_URL | https://cloud.nextcloud.example/remote.php/dav/calendars/rainu/456213af/ |
