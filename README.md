## Maru
Generic Java Library

### Features
Maru features a number of helpful packages and classes to make working in Java much easier.

__org.csdgn.maru.__

- __checksum__
 - 8,16,32 bit generic checksums, CRC16, FNV, Jenkins
- __crypto__
 - Rabbit Stream Cypher
- __io__
 - NullOutputStream, DirectoryModel, ReaderLoader, StreamLoader
 - FilesystemToolkit with many convenient helper methods.
- __swing__
 - StackLayout, Centering all children for LayedPanes
 - TableLayout, A simple HTML table layout
- __thread__
 - DelayHelper, for setting FPS
- __util__
 - BitMap, BitSet, FlatFile, Initialization (ini files), ListHashSet, StringUtils (escpaing, splitting)
- __deprecated__
 - A number of older toolkits with varied uses, to be merged into the library proper at some point.

### F.A.Q.
#### Why a library?
Well actually Maru is my personal use library where I stick any functions I might find useful in the future. So all this is me deciding to share my current library implementation.

#### What's different about it?
Most of the classes have very few dependencies on each other. Meaning you don't need the entire library to use one of the classes. At most you may need one or two others. This is a personal preference.
