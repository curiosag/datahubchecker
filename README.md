# Sap Hybris Datahub Checking Utility

Have your source and target definition files checked while you type them. So chances are, once you run the datahub
everything is just fine as far as consistency is concerned.

## checks cover

* defined dependencies
* mapping of raw items to canonical definitions
* raw definitions kind of duplicate the canonical reference, so the duplication is checked against the canonical counterpart too 
* check of item and attributes from target definitions against the canonical references
* spel expression syntax and item/attribute references coming from the (possibly nested) "resolve" function

## limitations

* Only the consistency of file against each other gets checked. Special semantics brought in
by underlying mechanisms during execution are not covered.

* The directory passed as a parameter will be searched recursively for all files matching the pattern for datahub definition files,
that is `^*.(-raw|-canonical|-target)-datahub-extension.xml` There is no check for the consistency of the location of those files 
itself, e.g. if some lurk around in test resource directories, they will be taken too.

* Spel expressions allow to use java object references. The checker can not distinguish between a wrong item reference 
and a correct object reference, so it will display an error in any case. You can define a list of regex filters for error messages, though.
Find details about that below.

## usage

Import subproject `datahub-utility` directly into your own project and proceed as you please.

### maven plugin

Alternatively there's a maven plugin included offering two goals

* check ... check the project once. Fails if any error gets detected which the error filter doesen't exclude
* watch ... keeps checking on each file change

run it directly by invoking

`mvn com.github.curiosag:maven-datahubchecker:1.0-SNAPSHOT:check` (or :watch)

or include it in your pom

 ```
                        <plugin>
                             <groupId>com.github.curiosag</groupId>
                             <artifactId>maven-datahubchecker</artifactId>
                             <version>1.0-SNAPSHOT</version>
                             <inherited>false</inherited>
                             <executions>
                                 <execution>
                                     <phase>compile</phase>
                                     <goals>
                                         <goal>check</goal>
                                     </goals>
                                 </execution>
                             </executions>
                         </plugin>
```

It will check the directory it was executed in.

### error message filtering

Put a file named `datahubchecker.errorfilters` into the directory you want to check, put a list of regular expressions 
(one per line) into it in order to filter out those error messages, that should be ignored. 
