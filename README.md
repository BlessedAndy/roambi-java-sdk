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


## RoambiScript

You may compile from this project or download compiled versions here:
* (Latest Stable) https://s3.amazonaws.com/roambi-api-downloads/api/roambi-script/latest/roambi-api-cli.jar
* (Latest Beta) https://s3.amazonaws.com/roambi-api-downloads/api/roambi-script/beta/roambi-api-cli.jar

### RoambiScript Usage

The client supports the following functions:

* create - upload a new source file to the library
* update - update an existing source file in the library
* refresh - create a new document (RBI) with a template and source file

To start, in Command Prompt/Terminal, you can type:

```
java -jar roambi-api-cli.jar
```

That will give a list of commands available and brief descriptions.

### RoambiScript Configuration

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
    --continue-on-failure, -C
       Continue the rest of the script file on failure.
       Default: false
    --file, -f
       Script File
    --help, -h
       Shows help
       Default: false
    -props, --props
       Property file location. If not specified, default to
       roambi-api-cli.properties
    --verbose
       Verbose mode
       Default: false       
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

    ls      List folder content
      Usage: ls [options]
        Options:
        *     --folder
             parent folder

    mkdir      Create a folder in the Roambi Repository
      Usage: mkdir [options]
        Options:
              --folder
             parent folder
              --ignoreFailure
             Do not report error when failed.
             Default: false
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

    sync_dir      Set sync of folder(s) in Roambi Repository
      Usage: sync_dir [options]
        Options:
        *     --folders
             folders to be updated
          -s, --sync
             Enable sync for the folder
             Default: false

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

    version      Usage: version [options]

```

### Notes
* All RFS Paths should be prepended with “/”.  This is a hack to differentiate them from UIDs.
