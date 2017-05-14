# Sap Hybris Datahub Checking Utility

Have your source and target definition files checked while you type them. So chances are, once you run the datahub
everything is just fine as far as consistency is concerned.

## covers

* defined dependencies
* mapping of raw items to canonical definitions
* raw definitions kind of duplicate the canonical reference, so the duplication is checked against the canonical counterpart too 
* item and attribute check from target definitions against the canonical references
* spel expressions using the (possibly nested) "resolve" function

## limitations

* It is checked that definitions in one file are consistent with the content of another file. Special semantics brought in
by underlying mechanisms during execution are not covered.

* The directory passed as a parameter will be searched recursively for all files matching the pattern for datahub definition files,
that is `(-raw|-canonical|-target)-datahub-extension.xml` There is no check for the consistency of the location of those files 
itself, e.g. if you have them in some test resource directory, they will be taken too.

* Spel expressions allow to use java object references. Those can not be distinguished from item references in datahub definition files. 
The checker can not decide if it is a wrong item reference or a correct object reference and 
will display an error in any case.