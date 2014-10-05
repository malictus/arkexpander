arkexpander
===========

Ark Expander is a simple program that will expand a Guitar Hero ARK file into a number of individual folders and files, and alternatively turn these files back into an ARK file. It works with the PlayStation2 version of Guitar Hero, Guitar Hero 2, and Guitar Hero Rocks The 80's. It is written in 100% pure Java, and should run on any Windows or Mac OS X capable of running Java. 

This program requires Java 1.5 or later to run. Once you have Java installed, you should be able to simply click on the '.jar' file to run the program. 

How to use
----------
This program will take an ARK file (along with its associated HDR file), and expand it into its component files. Also, it will take an expanded folder, and turn it back into an ARK file. To use, simply specify the folder that contains your ARK file (or will contain it), and specify the folder that contains (or will contain) your expanded files. When expanding an ARK file, the specified expansion folder must be empty to begin with.
VERY IMPORTANT - You may alter any of the expanded files, but you must not rename any of them, or add new files. If you do, your ARK file will not work. 

Developer's notes
-----------------
* There are directories in the original directory structure with names like '..'. These are unix references to a parent folder. Since you cannot name a folder '..', I have used the substitution 'dotdot' for these directory names.
* Since there are references in the HDR file to strings that are not files, I found it necessary to retain the original header file and edit it, rather than rebuilding it from scratch. The header file is placed into the expanded folder at the root level.

Thanks
------
Thanks to the customs community at scorehero, and special thanks to Riff, who decoded the ARK format and published his results at http://www.scorehero.com/forum/viewtopic.php?t=1179.
