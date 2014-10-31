roambi-script
=============

[![Build Status](https://api.travis-ci.org/Roambi/roambi-script.png)](https://api.travis-ci.org/Roambi/roambi-script)

RoambiScript is a command line interface to the Roambi API Java Client.  It allows the user another way to invoke api calls to Roambi from the terminal.

Requirements:
* roambi-api-cli.jar
* Roambi account (credentials and account)
* Roambi API Client information
* Java

## Installation
1. Clone the java sdk project
	* `$ git clone https://github.com/Roambi/roambi-java-sdk`
* Compile the java client library (see the [full instructions](https://github.com/Roambi/roambi-java-sdk))
	* `$ cd roambi-java-sdk/roambi-api-java-client`
	* `$ mvn install`
* Compile the RoambiScript library
	* `$ cd -`
	* `$ git clone https://github.com/Roambi/roambi-script`
	* `$ cd roambi-script`
	* `$ mvn install`
* The RoambiScript jar file will now be available at `target/roambi-api-cli.jar`

Alternatively, you may download the latest released version here:
https://s3.amazonaws.com/roambi-api-downloads/roambi-script/latest/roambi-api-cli.jar

## Usage

The client supports the following functions:

* create - upload a new source file to the library
* update - update an existing source file in the library
* refresh - create a new document (RBI) with a template and source file

To start, in Command Prompt/Terminal, you can type:

```
java -jar roambi-api-cli.jar
```

That will give a list of commands available and brief descriptions.

### Configuration

First thing you want to do is to create a properties file with the account information

```
java -jar roambi-api-cli.jar configure
```

Answer the prompts, and it will generate a roambi-api-cli.properties file in the current directory. Here is an example of the contents:

```
server.url=https://api.roambi.com
consumer.key=3d7c8sdf1316a8cd5bac0d87
consumer.secret=73252asdfbc9e3291066df92756a62bbb760238d6
redirect.uri=roambi-api://client.roambi.com/authorize
username=someone@yourdomain.com
password=mypassword
```

If you want the properties with different name or different location, you can use the --props option:

```
java -jar roambi-api-cli.jar -props=path/to/my/file.properties [command]
```

### Performing multiple commands using script file.
To perform multiple command, you can invoke RoambiScript multiple times, like:

```
java -jar roambi-api-cli.jar upload --file A.xlsx --folder XXXX
java -jar roambi-api-cli.jar upload --file B.xlsx --folder XXXX
```

Alternatively, you can create a text file that contains all the commands in a text file, for example, `my_file.roambiscript` with the following content:

```
# this is a comment
upload --file "this is a file with spaces.xlsx" --folder XXXX
upload --file B.xlsx --folder XXXX
```
and run:

```
java -jar roambi-api-cli.jar --file my_file.roambiscript
```



### Command line help

By default, the RoambiScript library will display inline help:

```
Usage: <main class> [options] [command] [command options]
  Options:
        --file
       Script File
        --help
       Shows help
       Default: false
    -props, --props
       Property file location. If not specified, default to
       roambi-api-cli.properties
  Commands:
    addPermission      add permissions to a file
      Usage: addPermission [options]
        Options:
              --access
             'view' or 'publish'
             Default: view
              --groupIds
             group ids
              --target
             target file
              --userIds
             user ids

    configure      Bootstrap a the client .properties file
      Usage: configure [options]

    delete      Delete a file in the Roambi Repository
      Usage: delete [options]
        Options:
              --file
             file to be deleted

    mkdir      Create a folder in the Roambi Repository
      Usage: mkdir [options]
        Options:
              --folder
             parent folder
              --permission
             set permissions for folder
              --title
             title of the new folder

    publish      Refresh a Roambi document
      Usage: publish [options]
        Options:
              --folder
             remote folder destination
              --permission
             set permissions for new document
              --source
             remote source file
              --template
             template rbi
              --title
             title of the new document

    publish-with-file      Refresh a Roambi document based on data in a local file
      Usage: publish-with-file [options]
        Options:
              --file
             local source file
              --folder
             remote folder destination
              --permission
             set permissions for new document
              --template
             template rbi
              --title
             title of the new document

    removePermission      remove permissions to a file
      Usage: removePermission [options]
        Options:
              --groupIds
             group ids
              --target
             target file
              --userIds
             user ids

    rmdir      Delete a folder in the Roambi Repository
      Usage: rmdir [options]
        Options:
              --folder
             folder to be deleted

    update      Upload and update a file in the Roambi Repository
      Usage: update [options]
        Options:
              --file
             locale file you with to upload
              --target
             target file uid

    upload      Upload and create a file in the Roambi Repository
      Usage: upload [options]
        Options:
              --file
             locale file you with to upload
              --folder
             remote folder destination
              --permission
             set permissions for new file
              --title
             title of the new file

    version     Usage: version [options]

    sync_dir    Set sync of folder(s) in Roambi Repository
      Usage: sync_dir [options]
        Options:
        *     --folders
             folders to be updated
          -s, --sync
             Enable sync for the folder
             Default: false

```

### Notes
* All RFS Paths should be prepended with “/”.  This is a hack to differentiate them from UIDs.
