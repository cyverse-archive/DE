## Create a settings.jar file

Create a `settings.jar` file using the IntelliJ settings files under this directory.
For example, `cd` into this directory from the command line and execute the following:

    jar cf settings.jar *

## Import into IntelliJ IDEA

With the UI project open, select the IntelliJ IDEA `File` menu, then select `Import Settings...` and
choose the `settings.jar` file created above.

In the `Select Components to Import` dialog, ensure the `Code Style` option is checked (should be the only
option), then click `OK`.

IntelliJ will need to be restarted. After the restart, the `iPlant UI Layer Code Style` scheme should be
selected under the `Editor > Code Style > Java` preferences.
