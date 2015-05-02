JTimber
=======

JTimber is a framework that supports the creation of complex Java object trees by introducing concepts like object parent storage.
More information and downloads can be found on the [wiki page](http://quartercode.com/wiki/JTimber).

License
-------

Copyright (c) 2015 QuarterCode <http://quartercode.com/>

JTimber may be used under the terms of the GNU Lesser General Public License (LGPL) v3.0. See the LICENSE.md file or https://www.gnu.org/licenses/lgpl-3.0.txt for details.

Compilation
-----------

We use maven to handle our dependencies and build, so you need the Java JDK and Maven for compiling the sourcecode.

* Download & install [Java JDK](http://www.oracle.com/technetwork/java/javase/downloads/index.html)
* Download & install [Maven 3](http://maven.apache.org/download.cgi).
* Check out this repository (clone or download).
* Navigate to the project folder of this repository which contains a `pom.xml` and run:

        mvn clean install
