designed by tianxinghe 2024.05.13

## 环境配置修改

labx需使用z3约束求解器进行约束求解工作，如果同学有兴趣自行实现约束求解器也可以，但**十分不推荐**

比如：
```Sysy
int main(int a, int b, int c){
    int d = a * b + c;
    int e = d / c;
    if (e > d) {
    
    } // a, b, c各自什么范围能够使得e > d成立？
}
```

z3:

        include Makefile.git
        
        export CLASSPATH=/usr/local/lib/antlr-*-complete.jar
        
        DOMAINNAME = oj.compilers.cpl.icu
        ANTLR = java -jar /usr/local/lib/antlr-*-complete.jar -listener -visitor -long-messages
        Z3 = java -jar /usr/local/lib/com.microsoft.z3.jar 
        JAVAC = javac -g
        JAVA = java
        
        
        PFILE = $(shell find . -name "SysYParser.g4")
        LFILE = $(shell find . -name "SysYLexer.g4")
        JAVAFILE = $(shell find . -name "*.java")
        ANTLRPATH = $(shell find /usr/local/lib -name "antlr-*-complete.jar")
        Z3PATH = $(shell find /usr/local/lib -name "com.microsoft.z3.jar")
        
        compile: antlr
            $(call git_commit,"make")
            mkdir -p classes
            $(JAVAC) -classpath $(ANTLRPATH):$(Z3PATH) $(JAVAFILE) -d classes
        
        run: compile
            java -classpath ./classes:$(ANTLRPATH):$(Z3PATH) Main $(FILEPATH)

ubuntu操作系统下

将com.microsoft.z3.jar移动到/usr/local/lib                
    
    sudo cp com.microsoft.z3.jar /usr/local/lib

将libz3java.so移动到/usr/lib/x86_64-linux-gnu         
    
    sudo cp libz3java.so /usr/lib/x86_64-linux-gnu 

    /usr/lib/x86_64-linux-gnu/libz3java.so

make之前先执行

    java -Djava.library.path="/usr/lib/x86_64-linux-gnu/libz3java.so"

或自行编译z3，添加z3环境变量并执行以上操作

    https://github.com/Z3Prover/z3

![image1](https://github.com/tianxinghe/static-checker/assets/26410605/04cd66ca-41cb-4e0d-a45d-8dcd8257b3dd)


（在make指令后添加 --java）


## 静态检查器实验步骤

### 遍历语法树以构造控制流图
在设计静态检查器时，常年存在精度与成本的博弈。如果检查器是流不敏感，路径不敏感或上下文不敏感的，那么虽然其速度很快，但是精度较低，会产生大量误报，如下所示：

- 流不敏感

使用流不敏感的分析时，检查器仅对程序代码遍历一次，分析的结果是全局的，如变量a的值可能是1或0，那么对于int b = 10 / a; 这条语句，就会产生一个除零错误的误报
```Sysy
int a = 1；
int b = 10 / a;
int c = 2;
a = 0;
```

- 路径不敏感但流敏感

使用路径不敏感的分析时，不会考虑到路径条件及不同路径上程序状态的不同，则可能会出现误报（或因算法设计的问题产生漏报）：

不可达路径上的错误
```Sysy
int a = 1；
if (a < 0) {
    bug; // 实际不可能导致程序崩溃
}
```

因算法设计导致的漏报
```Sysy
if (a < 0) {
    malloc();
}

if (a > 0) {
    free(); // 实际上开辟的空间未被释放
}
```

- 上下文不敏感

在进行上下文不敏感分析时，不考虑函数具体的调用位置，认为x可能为{1, 2, 3}中的任意可能值，则会使得a，b，c的可能值也为{1,2,3}，这显然存在精度的损失。
```Sysy
int func(int x) {
    return x + 1;
}

int a = func(1);
int b = func(2);
int c = func(3);
```

部分静态检查器（如clang static analyzer等工具）是流敏感，路径敏感且上下文敏感的。

相较于数据流分析而言，这种分析方式更加精确，它着眼于每一条可能执行的路径，依次进行模拟执行，并报告路径上的缺陷。这类检查器使用符号执行技术进行可达性的分析，并且使用内联的方式处理循环，函数调用等场景。

每一条可能执行的路径：
```Sysy
int main(int x) {
    code A;
    if (x > 1) {
        code B;
    } else {
        code C;
    }
    code D;
    while (x < 10) {
        code E;
    }
}
```

如以上这段代码，检查器的执行路径可能有以下几条：

path 1： A - B - D - E

path 2： A - C - D - E

path 3： A - B - D - E - E ... 

内联： 

静态检查器通过将函数体或循环体中的代码内联进行分析，如对于以下的这段代码：

```Sysy
int x = 0;
while(x < 3) {
    x = x + 1;
}
```

将其转化为：

```Sysy
int x = 0;
x = x + 1;
x = x + 1;
x = x + 1;  
```

路径敏感的分析可能会导致路径爆炸、分析无法停止等问题。

路径爆炸
```Sysy
int a = 1；
while (a > 0） {
    a ++;
}
```

对于以上的这段代码，while body中的代码可能被执行无限次。为了避免花费巨量开销仅用于分析单个循环的情况，通常路径敏感的静态检查器仅会内联一段代码三次。

路径敏感的静态检查器，对程序内部的每一条路径进行分析，通常是基于已经建立好的程序控制流图，建立exploded graph，并在exploded graph上进行分析。为什么不直接把每一条路径提出来单独进行分析呢？如下所示：

```Sysy
int main(int x) {
    code A;
    if (x > 1) {
        code B;
    } else {
        code C;
    }
    code D;
    while (x < 10) {
        code E;
    }
}
```

对于上面这段代码，如果对于

path1: A - B - D - E

path2: A - C - D - E

这两条路径分别进行分析，那么对于分支条件x > 1就需要重复分析两次。实际上，x > 1的约束求解结果是可以被多次使用的。

path1: A - B - D - E - ...

path2:     C - D - E - ...

在进行路径敏感的检查时，对分支条件的约束求解是时间开销的大头，如果直接对每条可执行路径进行分析，可能导致对数百万行的代码的检查耗费数天时间才能完成。

有关数据流分析相关理论，可阅读学习《编译原理》及许畅、冯洋老师编写的《编译方法、技术与实践》（如果已经印出来了的话）。

在labx中，我已经帮你造好了数据流图，学有余力的同学可以考虑一下当存在if和while嵌套、函数返回值做其他函数调用实参等情况时，应当如何进行分析。

数据流图相关的代码分为三个类： Function、Basic Block和Instruction，对应方法、基本块及基本块中的每一条指令。同学们可以调用print方法打印出生成的基本块。

    func.print();
    
### 基于数据流图构造exploded graph，并模拟执行程序

构造检查器时，你需要操心以下几个事情：

1. 当读到一条路径时，需对程序状态进行什么样的改变？

你需要实现以下的几个方法：

![image2](https://github.com/tianxinghe/static-checker/assets/26410605/87aaa554-7e7d-4351-adf0-37ba89d560e1)


Value ： 新的符号表中记录不同变量当前的值的抽象类型

你需要分别处理不同类型的语句在执行后改变int、array变量值的情况。

2. 当读到一个分支跳转语句时，你需要实现program state中的两个方法，分别是

   - 添加约束求解的addConstraint
![image3](https://github.com/tianxinghe/static-checker/assets/26410605/b73c1fb5-7d9e-499b-b90f-0ba56ae47bc0)


   - 对程序状态的拷贝copy (因一个program state对应一条路径，如果混用会导致符号表里存的值混乱)
![image4](https://github.com/tianxinghe/static-checker/assets/26410605/b78dfdbc-0f8d-4a7f-83d7-d467584286aa)


   在读到分支跳转语句时，你需要添加路径约束，调用explodedNode类中的check方法确定当前路径是否可达，并在某种情况下添加新的exploded node，复制program state，以备之后roll back继续分析。

   如对于以下代码：

   ```Sysy
    int main(int x) {
        code A;
        if (cond) { // 复制的exploded node及program state停留在此处
            code B;
        } else {
            code C;
        }
        code D;
        while (x < 10) {
            code E;
        }
    }
    ```

   在读到if(cond)时，若约束求解器判定true分支和false分支均可能执行到，则需要

   - 复制exploded node
   
   - 将program state复制到新的exploded node中
     
   - 为两个exploded node(中的program state)分别添加路径条件cond和!cond
     
   - 先执行true分支，并进行缺陷检查
  
   为了帮助同学们理解约束求解器，减少使用成本，我在program state类里写了一个例子checkExample。
    
    
3. 对一条路径的检查何时停止，并如何回滚并检查其他路径信息。

   如，对于以下的这段程序代码：

   ```Sysy
    int main() {
        int x = 1; // code A
        if (cond) {
            x = 0; // code B
        } else {
            x = 2; // code C
        }
        int y = 2 / x; // code D;
        other errors
    }
    ```

 检查器首先检查A - B - D，并且检查到一个除零错误。在报告错误之后，你需要设计一个roll back方法，回滚程序状态，并做到：

 - 程序的状态需要回滚到什么地方？（上一个有两条分支路径的地方，即if(cond)这条语句）

 - 抛弃掉这之后的程序状态（如将x赋值为0）
   
 - 约束求解器pop掉一些路径约束（如cond），并添加新的路径约束（如！cond）

 - 继续执行新路径上的检查 (新的路径上的code C是否已经内联三次了？)
   

### 语义错误报告
同lab3

