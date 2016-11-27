JAVAC=javac
JAVAC_FLAGS=-sourcepath .
JAVA=java
JAVA_FLAGS=
PARSER=${JAVA} ${JAVA_FLAGS} java_cup.Main
PARSER_FLAGS=-nopositions -expect 100
LEXER=${JAVA} ${JAVA_FLAGS} JLex.Main
LEXER_FLAGS=
all: test

test: absyn Cpp/Yylex.class Cpp/PrettyPrinter.class Cpp/Test.class Cpp/ComposVisitor.class Cpp/AbstractVisitor.class Cpp/FoldVisitor.class Cpp/AllVisitor.class Cpp/parser.class Cpp/sym.class Cpp/Test.class

.PHONY: absyn

%.class: %.java
	${JAVAC} ${JAVAC_FLAGS} $^

absyn: Cpp/Absyn/Program.java Cpp/Absyn/PDefs.java Cpp/Absyn/Def.java Cpp/Absyn/DFun.java Cpp/Absyn/ListDef.java Cpp/Absyn/Arg.java Cpp/Absyn/ADecl.java Cpp/Absyn/ListArg.java Cpp/Absyn/Stm.java Cpp/Absyn/SExp.java Cpp/Absyn/SDecls.java Cpp/Absyn/SInit.java Cpp/Absyn/SReturn.java Cpp/Absyn/SWhile.java Cpp/Absyn/SBlock.java Cpp/Absyn/SIfElse.java Cpp/Absyn/ListStm.java Cpp/Absyn/Exp.java Cpp/Absyn/ETrue.java Cpp/Absyn/EFalse.java Cpp/Absyn/EInt.java Cpp/Absyn/EDouble.java Cpp/Absyn/EId.java Cpp/Absyn/EApp.java Cpp/Absyn/EPostIncr.java Cpp/Absyn/EPostDecr.java Cpp/Absyn/EPreIncr.java Cpp/Absyn/EPreDecr.java Cpp/Absyn/ETimes.java Cpp/Absyn/EDiv.java Cpp/Absyn/EPlus.java Cpp/Absyn/EMinus.java Cpp/Absyn/ELt.java Cpp/Absyn/EGt.java Cpp/Absyn/ELtEq.java Cpp/Absyn/EGtEq.java Cpp/Absyn/EEq.java Cpp/Absyn/ENEq.java Cpp/Absyn/EAnd.java Cpp/Absyn/EOr.java Cpp/Absyn/EAss.java Cpp/Absyn/ListExp.java Cpp/Absyn/Type.java Cpp/Absyn/Type_bool.java Cpp/Absyn/Type_int.java Cpp/Absyn/Type_double.java Cpp/Absyn/Type_void.java Cpp/Absyn/ListId.java
	${JAVAC} ${JAVAC_FLAGS} $^

Cpp/Yylex.java: Cpp/Yylex
	${LEXER} ${LEXER_FLAGS} Cpp/Yylex

Cpp/parser.java Cpp/sym.java: Cpp/_cup.cup
	${PARSER} ${PARSER_FLAGS} Cpp/_cup.cup
	mv parser.java sym.java Cpp/

Cpp/Yylex.class: Cpp/Yylex.java Cpp/parser.java Cpp/sym.java

Cpp/sym.class: Cpp/sym.java

Cpp/parser.class: Cpp/parser.java Cpp/sym.java

Cpp/PrettyPrinter.class: Cpp/PrettyPrinter.java

clean:
	rm -f Cpp/Absyn/*.class Cpp/*.class Cpp/*.bak Cpp/Absyn/*.back

distclean: vclean

vclean:
	 rm -f Cpp/Absyn/Program.java Cpp/Absyn/PDefs.java Cpp/Absyn/Def.java Cpp/Absyn/DFun.java Cpp/Absyn/ListDef.java Cpp/Absyn/Arg.java Cpp/Absyn/ADecl.java Cpp/Absyn/ListArg.java Cpp/Absyn/Stm.java Cpp/Absyn/SExp.java Cpp/Absyn/SDecls.java Cpp/Absyn/SInit.java Cpp/Absyn/SReturn.java Cpp/Absyn/SWhile.java Cpp/Absyn/SBlock.java Cpp/Absyn/SIfElse.java Cpp/Absyn/ListStm.java Cpp/Absyn/Exp.java Cpp/Absyn/ETrue.java Cpp/Absyn/EFalse.java Cpp/Absyn/EInt.java Cpp/Absyn/EDouble.java Cpp/Absyn/EId.java Cpp/Absyn/EApp.java Cpp/Absyn/EPostIncr.java Cpp/Absyn/EPostDecr.java Cpp/Absyn/EPreIncr.java Cpp/Absyn/EPreDecr.java Cpp/Absyn/ETimes.java Cpp/Absyn/EDiv.java Cpp/Absyn/EPlus.java Cpp/Absyn/EMinus.java Cpp/Absyn/ELt.java Cpp/Absyn/EGt.java Cpp/Absyn/ELtEq.java Cpp/Absyn/EGtEq.java Cpp/Absyn/EEq.java Cpp/Absyn/ENEq.java Cpp/Absyn/EAnd.java Cpp/Absyn/EOr.java Cpp/Absyn/EAss.java Cpp/Absyn/ListExp.java Cpp/Absyn/Type.java Cpp/Absyn/Type_bool.java Cpp/Absyn/Type_int.java Cpp/Absyn/Type_double.java Cpp/Absyn/Type_void.java Cpp/Absyn/ListId.java Cpp/Absyn/Program.class Cpp/Absyn/PDefs.class Cpp/Absyn/Def.class Cpp/Absyn/DFun.class Cpp/Absyn/ListDef.class Cpp/Absyn/Arg.class Cpp/Absyn/ADecl.class Cpp/Absyn/ListArg.class Cpp/Absyn/Stm.class Cpp/Absyn/SExp.class Cpp/Absyn/SDecls.class Cpp/Absyn/SInit.class Cpp/Absyn/SReturn.class Cpp/Absyn/SWhile.class Cpp/Absyn/SBlock.class Cpp/Absyn/SIfElse.class Cpp/Absyn/ListStm.class Cpp/Absyn/Exp.class Cpp/Absyn/ETrue.class Cpp/Absyn/EFalse.class Cpp/Absyn/EInt.class Cpp/Absyn/EDouble.class Cpp/Absyn/EId.class Cpp/Absyn/EApp.class Cpp/Absyn/EPostIncr.class Cpp/Absyn/EPostDecr.class Cpp/Absyn/EPreIncr.class Cpp/Absyn/EPreDecr.class Cpp/Absyn/ETimes.class Cpp/Absyn/EDiv.class Cpp/Absyn/EPlus.class Cpp/Absyn/EMinus.class Cpp/Absyn/ELt.class Cpp/Absyn/EGt.class Cpp/Absyn/ELtEq.class Cpp/Absyn/EGtEq.class Cpp/Absyn/EEq.class Cpp/Absyn/ENEq.class Cpp/Absyn/EAnd.class Cpp/Absyn/EOr.class Cpp/Absyn/EAss.class Cpp/Absyn/ListExp.class Cpp/Absyn/Type.class Cpp/Absyn/Type_bool.class Cpp/Absyn/Type_int.class Cpp/Absyn/Type_double.class Cpp/Absyn/Type_void.class Cpp/Absyn/ListId.class
	 rm -f Cpp/Absyn/*.class
	 rmdir Cpp/Absyn/
	 rm -f Cpp/Yylex Cpp/_cup.cup Cpp/Yylex.java Cpp/VisitSkel.java Cpp/ComposVisitor.java Cpp/AbstractVisitor.java Cpp/FoldVisitor.java Cpp/AllVisitor.java Cpp/PrettyPrinter.java Cpp/Skeleton.java Cpp/Test.java Cpp/parser.java Cpp/sym.java Cpp/*.class
	 rm -f Makefile
	 rmdir -p Cpp/
