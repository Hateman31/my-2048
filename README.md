# my-2048

Clone of popular game 2048 made via ClojureScript.

For playing demo:
 1) Download zip-archive from [here](https://drive.google.com/file/d/1J36LkySbvqL9UZSVoHzoxBbFO3FR3cx3/view?usp=sharing)
 2) Unzip it
 3) Open ```index.html``` in your browser
 4) Enjoy!

## Overview

Good way to learn a new programming language it is a building app on this language!
I have choiced CLojure.

## Setup

To get an interactive development environment run:

    lein figwheel

and open your browser at [localhost:3449](http://localhost:3449/).
This will auto compile and send all changes to the browser without the
need to reload. After the compilation process is complete, you will
get a Browser Connected REPL. An easy way to try it is:

    (js/alert "Am I connected?")

and you should see an alert in the browser window.

To clean all compiled files:

    lein clean

To create a production build run:

    lein do clean, cljsbuild once min

And open your browser in `resources/public/index.html`. You will not
get live reloading, nor a REPL. 

## License

Copyright © 2014 Hateman31

Distributed under the GNU Public License either version 1.0 or (at your option) any later version.
