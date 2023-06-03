ArgumentMap
=================================================

![Points](../../blob/badges/points.svg)

For this homework, you will create a class to parse command-line arguments and store them in a map. For example, consider the following command-line arguments:

```
"-max", "false", "-min", "0", "-min", "-10", "hello", "-debug", "-f", "output.txt", "-verbose"
```

In this case, `-max` `-min` `-debug` `-f` and `-verbose` are all flags since they start with a `-` dash followed by at least 1 letter character. The values are `false` `0` `hello` and `output.txt` since they do not start with a `-` and letter character.

Note that `-10` is *not* a flag because the `-` dash is followed by a digit character. Instead it should be interpreted as a value representing a negative number.

Not all flags have values, not all values have associated flags, and values will be overwritten if there are repeated flags. For example, flag `-max` has value `false`. Flag `-min` has initial value `0`, but the value get replaced by the second occurrence of the `-min` flag with the value `-10` instead. The value `hello` has no associated flag and is ignored. The flags `-debug` and `-verbose` have no associated value, but are still stored. The resulting map should look similar to:

```json
{
  "-max": "false",
  "-min": "-10",
  "-debug": null,
  "-f": "output.txt",
  "-verbose": null
}
```

Use an efficient data structure to store the key/value pairs. The key/value pairs does *not* need to be stored in insertion order or sorted order.

Avoid looping more often than necessary. For example, `numFlags()` should not require a loop.

## Hints ##

Below are some hints that may help with this homework assignment:

- Many methods may be implemented with one line of code if you are familiar with the methods in [HashMap](https://www.cs.usfca.edu/~cs212/javadoc/api/java.base/java/util/HashMap.html) or [TreeMap](https://www.cs.usfca.edu/~cs212/javadoc/api/java.base/java/util/TreeMap.html).

- The `parse(...)` method is easier if you use a traditional `for` loop instead of an enhanced `for` loop.

- The [`String.codePointAt(int)`](https://www.cs.usfca.edu/~cs212/javadoc/api/java.base/java/lang/String.html#codePointAt(int)) and [`Character.isLetter(int)`](https://www.cs.usfca.edu/~cs212/javadoc/api/java.base/java/lang/Character.html#isLetter(int)) methods work with more languages than using [`String.charAt(int)`](https://www.cs.usfca.edu/~cs212/javadoc/api/java.base/java/lang/String.html#charAt(int)) and [`Character.isLetter(char)`](https://www.cs.usfca.edu/~cs212/javadoc/api/java.base/java/lang/Character.html#isLetter(char)) methods. However, it is not necessary to pass the tests.

These hints are *optional*. There may be multiple approaches to solving this homework.
