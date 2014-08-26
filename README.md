# roambi-java-sdk

[![Build Status](https://api.travis-ci.org/Roambi/roambi-java-sdk.png)](https://api.travis-ci.org/Roambi/roambi-java-sdk)

The Roambi Java Software Development Kit (SDK) provides client libraries and documentation
to help you integrate with the Roambi platform's API.  The core functionality of the Roambi API is to publish your data as Roambi visualizations.

Currently, the Java SDK is composed of a single module (roambi-api-java-client) which provides a low-level client for communicating with the API.  This client can be used in other projects to expose a higher-level API interface to end users.  As an example, see the [RoambiScript](https://github.com/Roambi/roambi-script) project.


## Installation

1. clone the repository
	* `$ git clone https://github.com/Roambi/roambi-java-sdk`
* cd into the module directory
	* `$ cd roambi-java-sdk/roambi-api-java-client`
* install to your local repository using maven
	* `$ mvn install`

