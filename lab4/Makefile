JAVAC=javac
JAVAC_FLAGS=-sourcepath .
JAVA=java
JAVA_FLAGS=
PARSER=${JAVA} ${JAVA_FLAGS} java_cup.Main
PARSER_FLAGS=-nopositions -expect 100
LEXER=${JAVA} ${JAVA_FLAGS} JLex.Main
LEXER_FLAGS=
all: test

test: absyn Fun/Yylex.class Fun/PrettyPrinter.class Fun/Test.class Fun/ComposVisitor.class Fun/AbstractVisitor.class Fun/FoldVisitor.class Fun/AllVisitor.class Fun/parser.class Fun/sym.class Fun/Test.class

.PHONY: absyn

%.class: %.java
	${JAVAC} ${JAVAC_FLAGS} $^

absyn: Fun/Absyn/Program.java Fun/Absyn/Prog.java Fun/Absyn/Main.java Fun/Absyn/DMain.java Fun/Absyn/Def.java Fun/Absyn/DDef.java Fun/Absyn/ListDef.java Fun/Absyn/ListIdent.java Fun/Absyn/Exp.java Fun/Absyn/EVar.java Fun/Absyn/EInt.java Fun/Absyn/EApp.java Fun/Absyn/EAdd.java Fun/Absyn/ESub.java Fun/Absyn/ELt.java Fun/Absyn/EIf.java Fun/Absyn/EAbs.java Interpreter.java lab4.java
	${JAVAC} ${JAVAC_FLAGS} $^

Fun/Yylex.java: Fun/Yylex
	${LEXER} ${LEXER_FLAGS} Fun/Yylex

Fun/parser.java Fun/sym.java: Fun/_cup.cup
	${PARSER} ${PARSER_FLAGS} Fun/_cup.cup
	mv parser.java sym.java Fun/

Fun/Yylex.class: Fun/Yylex.java Fun/parser.java Fun/sym.java

Fun/sym.class: Fun/sym.java

Fun/parser.class: Fun/parser.java Fun/sym.java

Fun/PrettyPrinter.class: Fun/PrettyPrinter.java

clean:
	rm -f Fun/Absyn/*.class Fun/*.class *.class

distclean: vclean

vclean:
	 rm -f Fun/Absyn/Program.java Fun/Absyn/Prog.java Fun/Absyn/Main.java Fun/Absyn/DMain.java Fun/Absyn/Def.java Fun/Absyn/DDef.java Fun/Absyn/ListDef.java Fun/Absyn/ListIdent.java Fun/Absyn/Exp.java Fun/Absyn/EVar.java Fun/Absyn/EInt.java Fun/Absyn/EApp.java Fun/Absyn/EAdd.java Fun/Absyn/ESub.java Fun/Absyn/ELt.java Fun/Absyn/EIf.java Fun/Absyn/EAbs.java Fun/Absyn/Program.class Fun/Absyn/Prog.class Fun/Absyn/Main.class Fun/Absyn/DMain.class Fun/Absyn/Def.class Fun/Absyn/DDef.class Fun/Absyn/ListDef.class Fun/Absyn/ListIdent.class Fun/Absyn/Exp.class Fun/Absyn/EVar.class Fun/Absyn/EInt.class Fun/Absyn/EApp.class Fun/Absyn/EAdd.class Fun/Absyn/ESub.class Fun/Absyn/ELt.class Fun/Absyn/EIf.class Fun/Absyn/EAbs.class
	 rm -f Fun/Absyn/*.class
	 rmdir Fun/Absyn/
	 rm -f Fun/Yylex Fun/_cup.cup Fun/Yylex.java Fun/VisitSkel.java Fun/ComposVisitor.java Fun/AbstractVisitor.java Fun/FoldVisitor.java Fun/AllVisitor.java Fun/PrettyPrinter.java Fun/Skeleton.java Fun/Test.java Fun/parser.java Fun/sym.java Fun/*.class
	 rm -f Makefile
	 rmdir -p Fun/
