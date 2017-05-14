# maven plugin for Sap Hybris Datahub Checking Utility
 
## usage

Make sure you have the checking utility itself locally installed.
 
 

 
 ```
             <plugin>
                 <groupId>org.cg.mvn</groupId>
                 <artifactId>mvndatahubchecker</artifactId>
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
