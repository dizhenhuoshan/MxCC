# Compiler 2019 MidTerm Report

### 1. 总体设计
目前为止，semantic test 总共分为三个部分

- Lexer and Paser
- AST builder
- Scope and Symbol Table Builder

其中 Laxer and Paser 部分是借助ANTLR 4工具根据g4语法文件自动生成的。

AST builder 借助ANTLR 4 提供的语法树的visitor模式编写。虽然listener模式更适合构建抽象语法树，因为具体语法树上的每个节点都会恰好被访问一次。但是visitor模式相比listener有更大的灵活性，可以自行控制具体语法树的遍历行为，故仍然选择使用visitor模式。

Scope and Symbol Table builder 是建立在AST visitor的基础上的。通过三遍AST扫描构建符号表和作用域。
其中第一遍扫描主要是扫描全局的class 和 function，解决全局的前向引用问题。同时，放入内建函、string 和 array 类，以及检查程序入口main()的合法性
第二遍扫描主要是扫描class内部的variables 和 functions，解决 class 内部的前向引用问题
第三遍扫描是比较细致的扫描，每个节点都会被访问，做详细地检查，以及完成符号表和作用域的构建

### 2. 遇到的问题
目前写semantic最大的问题就是心理没有底，从写完G4文件开始，就不断出现写了一半要去改前面的东西的现象，尤其是g4文件的修改对后面的影响很大。
