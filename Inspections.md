# Inspections list

Some example of inspections with explanation

* Unused expression
```
fun test() {
    var x = 5;
    x;
    5; 
}
```

* Ignore return value
```
fun test() {
   const5();
}

fun const5() {
    return 5;
}
```

* Double function declaration (with same amount of arguments)
```
fun test() {}
fun test() {}
fun test(a) {} // not a duplicate
```

* Access before declaration
```
fun test() {
    var y = x;
}
```

* Access before initialization
```
fun test() {
    var x;
    x;
} 
```

* Assignment return value of function that doesn't return anything
```
fun test() {
    var x = empty();
}

fun empty() {}
```

* Duplicate declaration of variable
```
fun test() {
    var x;
    var x;
}
```

* Call function without declaration
```
fun test() {
    test(1, 1); 
    testt();
}

fun test(a) {}
```

* Assignment variable to itself
```
fun test() {
    var x = 5;
    x = x;
}
```

* Assignment before declaration
```
fun test() {
    x = 5;
}
```

* Boolean const in condition 
```
fun test() {
    if (true) {
        ...
    } else {
        ...
    } 
}
```

* Duplicated names of parameters in function declaration
```
fun test(a, a, a) {}
```

* Function always returns const
```
fun random() {
    return 5;
}
```

* Int const in condition
```
fun test() {
    if (3) {
        ...
    }
}
```

* Function empty body
```
fun test() {}
```

* If empty body
```
fun test() {
    if(true) {} else {}
}
```

* Multiply return statements
```
fun test() {
    return 5;
    return 5;
}

fun test2() {
    if (true) {
        return 5;
        return 5;
    } else {
        return 4;
    }
}
```

* Unreachable code
```
fun test() {
    return 5;
    
    var x; 
    var y;
}

fun test2() {
    if (true) {
        return 5;
    } else {
        return 3;
    }

    var x;
    var y;
}
```