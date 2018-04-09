ConfigJson
========================
A gradle plugin for getting java constant from JSON config files for Android.

This plugin generates java config from json files and then in the java code these configs can be used as just any other public static final fields, with all the benifits of having static analysis and compilation.

Usage
----

1. Add the following to your build.gradle

   ```groovy
   buildscript {
      ...
      dependencies {
         classpath 'com.aamirabro:configjsonplugin:0.9.3'
      }
   }

   // in app.gradle file
   apply plugin: 'com.android.application'
   apply plugin: 'com.aamirabro.configjson'
   ```
   alternatively, you can use the new plugin syntax for gradle `2.1+`
   ```groovy
   plugins {
      id "com.aamirabro.configjson" version "0.9.3"
   }
   ```

2. Create json config files and tell ConfigJson to use them


   In app folder create config.json
   ``` json
    {
      "SOME_KEY" : "default value"
    }
   ```
   Config json support getting Config from multiple files, config_debug.json
   ``` json
    {
      "SOME_KEY" : "default value"
    }
   ```
 
3. Upadte gradle file use those config files

    ```groovy
    ext.configJsonFiles = ['config.json']
    ext.configJsonPackage = 'com.your.package' // this is where the ConfigJson.Java file will be generated.
    android {
    ...
      buildTypes {
        ext.configJsonFiles = ['config_debug.json']
      }
    ```


 
3. Use config just as any other constant field

   ```java
    class MainActivity extends Activity {
      @Override
      protected void onCreate(Bundle savedInstanceState) {
          Log.i("config", ConfigJson.SOME_KEY);
      }
    }
    ```


Pulbish
-------
use this to publish a new version https://github.com/novoda/bintray-release


License
-------

Copyright 2017 Aamir Abro

Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
