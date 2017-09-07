# exparity-data
Data scraping and manipulation tools for Java
=======
eXparity Data  [![Build Status](https://travis-ci.org/eXparity/exparity-data.svg?branch=master)](https://travis-ci.org/eXparity/exparity-data) [![Coverage Status](https://coveralls.io/repos/eXparity/exparity-data/badge.png?branch=master)](https://coveralls.io/r/eXparity/exparity-data?branch=master)
=============

A data scraping library for Java

Licensed under [BSD License][].

What is eXparity Data?
-----------------
eXparity Data is a Java Library which provides data scraping, manipulation, and ingestion tools for both structured and unstructured data sourced from the internet, local files, or any other source

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
-----

The exparity-data library current supports 4 file formats; HTML, XML, CSV, and Text, and it can load them from the internet, a classpath resource, the file system, and InputStream and Reader implementations.

The file format classes are all found in the org.exparity.data package and can be instantiated through static methods. For example:

	HTML html = HTML.openURL("http://www.google.com/");
	CSV csv = CSV.openFile("C:/Users/Bob/Desktop/MyCSV.csv");

Once a file has been instantiated then the library provides tools to interrogate and process the data. For example

	List<String> headers = CSV.openFile("...").getHeaders();
	List<Anchor> anchors = HTML.openURL("...").findAnchors();

The Javadocs include examples on all methods so you can look there for examples for specific methods

Source
------
The source is structured along the lines of the maven standard folder structure for a jar project.

  * Core classes [src/main/java]
  * Unit tests [src/test/java]

The source includes a pom.xml for building with Maven 

Release Notes
-------------
1.0.0 
  * Initial release cut of code

Acknowledgements
----------------
Developers:
  * Stewart Bissett


[BSD License]: http://opensource.org/licenses/BSD-3-Clause
[Maven central]: http://search.maven.org/#search%7Cga%7C1%7Ca%3A%22exparity-data%22