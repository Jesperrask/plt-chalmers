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

absyn: Cpp/Absyn/Program.java Cpp/Absyn/PDefs.java Cpp/Absyn/ListDef.java Cpp/Absyn/Def.java Cpp/Absyn/DFun.java Cpp/Absyn/DType.java Cpp/Absyn/DDcl.java Cpp/Absyn/DUs.java Cpp/Absyn/ListArg.java Cpp/Absyn/Arg.java Cpp/Absyn/ArgVar.java Cpp/Absyn/ArgCons.java Cpp/Absyn/ArgDecl.java Cpp/Absyn/ADeclType.java Cpp/Absyn/ADeclVar.java Cpp/Absyn/ADeclInit.java Cpp/Absyn/FBod.java Cpp/Absyn/FBodBlck.java Cpp/Absyn/FBodNil.java Cpp/Absyn/ListStm.java Cpp/Absyn/Stm.java Cpp/Absyn/SExp.java Cpp/Absyn/SDecl.java Cpp/Absyn/SRet.java Cpp/Absyn/SWhile.java Cpp/Absyn/SDoWhile.java Cpp/Absyn/SFor.java Cpp/Absyn/SIf.java Cpp/Absyn/SIfEl.java Cpp/Absyn/SBlock.java Cpp/Absyn/STyp.java Cpp/Absyn/Decl.java Cpp/Absyn/DclVar.java Cpp/Absyn/DclCons.java Cpp/Absyn/DeclInit.java Cpp/Absyn/DclNoInit.java Cpp/Absyn/Dcls.java Cpp/Absyn/DclInit.java Cpp/Absyn/ListCIdent.java Cpp/Absyn/TypeDef.java Cpp/Absyn/TDef.java Cpp/Absyn/Type.java Cpp/Absyn/TBool.java Cpp/Absyn/TInt.java Cpp/Absyn/TChar.java Cpp/Absyn/TDouble.java Cpp/Absyn/TVoid.java Cpp/Absyn/TConst.java Cpp/Absyn/TRef.java Cpp/Absyn/Exp.java Cpp/Absyn/EThr.java Cpp/Absyn/ECond.java Cpp/Absyn/EAss.java Cpp/Absyn/EPEq.java Cpp/Absyn/EMEq.java Cpp/Absyn/EOr.java Cpp/Absyn/EAnd.java Cpp/Absyn/EEq.java Cpp/Absyn/ENEq.java Cpp/Absyn/ELt.java Cpp/Absyn/EGt.java Cpp/Absyn/ELtEq.java Cpp/Absyn/EGtWq.java Cpp/Absyn/ELShift.java Cpp/Absyn/ERShift.java Cpp/Absyn/EAdd.java Cpp/Absyn/ESub.java Cpp/Absyn/EMul.java Cpp/Absyn/EDiv.java Cpp/Absyn/EMod.java Cpp/Absyn/EIncr.java Cpp/Absyn/EDecr.java Cpp/Absyn/ENot.java Cpp/Absyn/EPtr.java Cpp/Absyn/EPIncr.java Cpp/Absyn/EPDecr.java Cpp/Absyn/EDot.java Cpp/Absyn/EArr.java Cpp/Absyn/EFCall.java Cpp/Absyn/EInd.java Cpp/Absyn/EQConst.java Cpp/Absyn/EInt.java Cpp/Absyn/EChar.java Cpp/Absyn/EDoub.java Cpp/Absyn/EStr.java Cpp/Absyn/ListString.java Cpp/Absyn/ListExp.java Cpp/Absyn/QConst.java Cpp/Absyn/QConstMain.java Cpp/Absyn/QConstEl.java Cpp/Absyn/QConstCIdent.java Cpp/Absyn/QConstTp.java Cpp/Absyn/ListQConstEl.java Cpp/Absyn/ListType.java
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
	rm -f Cpp/Absyn/*.class Cpp/*.class

distclean: vclean

vclean:
	 rm -f Cpp/Absyn/Program.java Cpp/Absyn/PDefs.java Cpp/Absyn/ListDef.java Cpp/Absyn/Def.java Cpp/Absyn/DFun.java Cpp/Absyn/DType.java Cpp/Absyn/DDcl.java Cpp/Absyn/DUs.java Cpp/Absyn/ListArg.java Cpp/Absyn/Arg.java Cpp/Absyn/ArgVar.java Cpp/Absyn/ArgCons.java Cpp/Absyn/ArgDecl.java Cpp/Absyn/ADeclType.java Cpp/Absyn/ADeclVar.java Cpp/Absyn/ADeclInit.java Cpp/Absyn/FBod.java Cpp/Absyn/FBodBlck.java Cpp/Absyn/FBodNil.java Cpp/Absyn/ListStm.java Cpp/Absyn/Stm.java Cpp/Absyn/SExp.java Cpp/Absyn/SDecl.java Cpp/Absyn/SRet.java Cpp/Absyn/SWhile.java Cpp/Absyn/SDoWhile.java Cpp/Absyn/SFor.java Cpp/Absyn/SIf.java Cpp/Absyn/SIfEl.java Cpp/Absyn/SBlock.java Cpp/Absyn/STyp.java Cpp/Absyn/Decl.java Cpp/Absyn/DclVar.java Cpp/Absyn/DclCons.java Cpp/Absyn/DeclInit.java Cpp/Absyn/DclNoInit.java Cpp/Absyn/Dcls.java Cpp/Absyn/DclInit.java Cpp/Absyn/ListCIdent.java Cpp/Absyn/TypeDef.java Cpp/Absyn/TDef.java Cpp/Absyn/Type.java Cpp/Absyn/TBool.java Cpp/Absyn/TInt.java Cpp/Absyn/TChar.java Cpp/Absyn/TDouble.java Cpp/Absyn/TVoid.java Cpp/Absyn/TConst.java Cpp/Absyn/TRef.java Cpp/Absyn/Exp.java Cpp/Absyn/EThr.java Cpp/Absyn/ECond.java Cpp/Absyn/EAss.java Cpp/Absyn/EPEq.java Cpp/Absyn/EMEq.java Cpp/Absyn/EOr.java Cpp/Absyn/EAnd.java Cpp/Absyn/EEq.java Cpp/Absyn/ENEq.java Cpp/Absyn/ELt.java Cpp/Absyn/EGt.java Cpp/Absyn/ELtEq.java Cpp/Absyn/EGtWq.java Cpp/Absyn/ELShift.java Cpp/Absyn/ERShift.java Cpp/Absyn/EAdd.java Cpp/Absyn/ESub.java Cpp/Absyn/EMul.java Cpp/Absyn/EDiv.java Cpp/Absyn/EMod.java Cpp/Absyn/EIncr.java Cpp/Absyn/EDecr.java Cpp/Absyn/ENot.java Cpp/Absyn/EPtr.java Cpp/Absyn/EPIncr.java Cpp/Absyn/EPDecr.java Cpp/Absyn/EDot.java Cpp/Absyn/EArr.java Cpp/Absyn/EFCall.java Cpp/Absyn/EInd.java Cpp/Absyn/EQConst.java Cpp/Absyn/EInt.java Cpp/Absyn/EChar.java Cpp/Absyn/EDoub.java Cpp/Absyn/EStr.java Cpp/Absyn/ListString.java Cpp/Absyn/ListExp.java Cpp/Absyn/QConst.java Cpp/Absyn/QConstMain.java Cpp/Absyn/QConstEl.java Cpp/Absyn/QConstCIdent.java Cpp/Absyn/QConstTp.java Cpp/Absyn/ListQConstEl.java Cpp/Absyn/ListType.java Cpp/Absyn/Program.class Cpp/Absyn/PDefs.class Cpp/Absyn/ListDef.class Cpp/Absyn/Def.class Cpp/Absyn/DFun.class Cpp/Absyn/DType.class Cpp/Absyn/DDcl.class Cpp/Absyn/DUs.class Cpp/Absyn/ListArg.class Cpp/Absyn/Arg.class Cpp/Absyn/ArgVar.class Cpp/Absyn/ArgCons.class Cpp/Absyn/ArgDecl.class Cpp/Absyn/ADeclType.class Cpp/Absyn/ADeclVar.class Cpp/Absyn/ADeclInit.class Cpp/Absyn/FBod.class Cpp/Absyn/FBodBlck.class Cpp/Absyn/FBodNil.class Cpp/Absyn/ListStm.class Cpp/Absyn/Stm.class Cpp/Absyn/SExp.class Cpp/Absyn/SDecl.class Cpp/Absyn/SRet.class Cpp/Absyn/SWhile.class Cpp/Absyn/SDoWhile.class Cpp/Absyn/SFor.class Cpp/Absyn/SIf.class Cpp/Absyn/SIfEl.class Cpp/Absyn/SBlock.class Cpp/Absyn/STyp.class Cpp/Absyn/Decl.class Cpp/Absyn/DclVar.class Cpp/Absyn/DclCons.class Cpp/Absyn/DeclInit.class Cpp/Absyn/DclNoInit.class Cpp/Absyn/Dcls.class Cpp/Absyn/DclInit.class Cpp/Absyn/ListCIdent.class Cpp/Absyn/TypeDef.class Cpp/Absyn/TDef.class Cpp/Absyn/Type.class Cpp/Absyn/TBool.class Cpp/Absyn/TInt.class Cpp/Absyn/TChar.class Cpp/Absyn/TDouble.class Cpp/Absyn/TVoid.class Cpp/Absyn/TConst.class Cpp/Absyn/TRef.class Cpp/Absyn/Exp.class Cpp/Absyn/EThr.class Cpp/Absyn/ECond.class Cpp/Absyn/EAss.class Cpp/Absyn/EPEq.class Cpp/Absyn/EMEq.class Cpp/Absyn/EOr.class Cpp/Absyn/EAnd.class Cpp/Absyn/EEq.class Cpp/Absyn/ENEq.class Cpp/Absyn/ELt.class Cpp/Absyn/EGt.class Cpp/Absyn/ELtEq.class Cpp/Absyn/EGtWq.class Cpp/Absyn/ELShift.class Cpp/Absyn/ERShift.class Cpp/Absyn/EAdd.class Cpp/Absyn/ESub.class Cpp/Absyn/EMul.class Cpp/Absyn/EDiv.class Cpp/Absyn/EMod.class Cpp/Absyn/EIncr.class Cpp/Absyn/EDecr.class Cpp/Absyn/ENot.class Cpp/Absyn/EPtr.class Cpp/Absyn/EPIncr.class Cpp/Absyn/EPDecr.class Cpp/Absyn/EDot.class Cpp/Absyn/EArr.class Cpp/Absyn/EFCall.class Cpp/Absyn/EInd.class Cpp/Absyn/EQConst.class Cpp/Absyn/EInt.class Cpp/Absyn/EChar.class Cpp/Absyn/EDoub.class Cpp/Absyn/EStr.class Cpp/Absyn/ListString.class Cpp/Absyn/ListExp.class Cpp/Absyn/QConst.class Cpp/Absyn/QConstMain.class Cpp/Absyn/QConstEl.class Cpp/Absyn/QConstCIdent.class Cpp/Absyn/QConstTp.class Cpp/Absyn/ListQConstEl.class Cpp/Absyn/ListType.class
	 rm -f Cpp/Absyn/*.class
	 rmdir Cpp/Absyn/
	 rm -f Cpp/Yylex Cpp/_cup.cup Cpp/Yylex.java Cpp/VisitSkel.java Cpp/ComposVisitor.java Cpp/AbstractVisitor.java Cpp/FoldVisitor.java Cpp/AllVisitor.java Cpp/PrettyPrinter.java Cpp/Skeleton.java Cpp/Test.java Cpp/parser.java Cpp/sym.java Cpp/*.class
	 rm -f Makefile
	 rmdir -p Cpp/
