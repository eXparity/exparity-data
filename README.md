# exparity-data
Data scraping and manipulation tools for Java
=======
eXparity Data  [![Build Status](https://travis-ci.org/eXparity/exparity-data.svg?branch=master)](https://travis-ci.org/eXparity/exparity-data) [![Coverage Status](https://coveralls.io/repos/eXparity/exparity-data/badge.png?branch=master)](https://coveralls.io/r/eXparity/exparity-data?branch=master)
=============

A date matching library for [Java Hamcrest][]

Licensed under [BSD License][].

What is eXparity Data?
-----------------
eXparity Data is a Java Library which provides data scraping, manipulation, and ingestion tools for both structured and unstructured data sourced from the www, local files, or any other source

Downloads
---------
You can obtain eXparity Data binaries from [maven central][]. To include in your project:

A maven project

    <dependency>
        <groupId>org.exparity</groupId>
        <artifactId>exparity-data</artifactId>
        <version>1.0.0</version>
    </dependency>

            
Binaries
--------
eXparity Data has a single binary, exparity-data.jar, which contains all the utilities. Sources and JavaDoc jars are available.

Usage
-------------

TBD

The Javadocs include examples on all methods so you can look there for examples for specific methods

Source
------
The source is structured along the lines of the maven standard folder structure for a jar project.

  * Core classes [src/main/java]
  * Unit tests [src/test/java]

The source includes a pom.xml for building with Maven 

Release Notes
-------------

Acknowledgements
----------------
Developers:
  * Stewart Bissett


[BSD License]: http://opensource.org/licenses/BSD-3-Clause
[Maven central]: http://search.maven.org/#search%7Cga%7C1%7Ca%3A%22exparity-data%22