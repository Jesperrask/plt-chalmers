import CPP.Absyn.*;
import java.util.*;

public class TypeChecker
{
  // Global signature of functions
  private Map<String,FunType> sig;

  // Stack of contexts
  private List<Map<String,Type>> cxt;

  // Return type of function we are checking
  private Type returnType;

  // Share type constants
  public final Type BOOL   = new Type_bool();
  public final Type INT    = new Type_int();
  public final Type DOUBLE = new Type_double();
  public final Type VOID   = new Type_void();

  // Entry point

  public void typecheck(Program p) {
    p.accept(new ProgramVisitor(), null);
  }

  ////////////////////////////// Program //////////////////////////////

  public class ProgramVisitor implements Program.Visitor<Void,Void>
  {
    public Void visit(CPP.Absyn.PDefs p, Void arg)
    {
      // Put primitive functions into signature
      sig = new TreeMap<String,FunType>();
      sig.put("printInt"   , new FunType(VOID, singleArg(INT)));
      sig.put("readInt"    , new FunType(INT, new ListArg()));
      // TODO: other primitive functions

      // Extend signature by all the definitions
      for (Def x: p.listdef_) {
        DFun d = (DFun)x;
        // make sure x is not already in the signature.
        if (sig.get(d.id_) != null)
          throw new TypeException("function " + d.id_ + " has already been declared");
        sig.put(d.id_, new FunType(d.type_, d.listarg_));
      }

      // Check definitions
      for (Def x: p.listdef_) {
        x.accept(new DefVisitor(), arg);
      }

      // TODO: Check for main
      FunType ft = sig.get("main");
      if (ft == null)
        throw new TypeException("main function missing");
      // TODO: Check type of main to be int().
      return null;
    }
  }

  public ListArg singleArg (Type t) {
    ListArg l = new ListArg();
    l.add(new ADecl(t, "x"));
    return l;
  }

  ////////////////////////////// Function //////////////////////////////

  public class DefVisitor implements Def.Visitor<Void,Void>
  {
    public Void visit(CPP.Absyn.DFun p, Void arg)
    {
      // set return type and initial context
      returnType = p.type_;
      cxt = new LinkedList();
      cxt.add(new TreeMap());

      // add all function parameters to context
      for (Arg a: p.listarg_) {
        a.accept(new ArgVisitor(), arg);
      }

      // check function statements
      for (Stm s: p.liststm_) {
        s.accept(new StmVisitor(), arg);
      }

      return null;
    }
  }

  ///////////////////////// Function argument /////////////////////////

  // Add a type declaration to the context
  public class ArgVisitor implements Arg.Visitor<Void,Void>
  {
    public Void visit(CPP.Absyn.ADecl p, Void arg)
    {
      newVar(p.id_, p.type_);
      return null;
    }
  }

  ////////////////////////////// Statement //////////////////////////////

  public class StmVisitor implements Stm.Visitor<Void,Void>
  {
    public Void visit(CPP.Absyn.SExp p, Void arg)
    {
      Type t = p.exp_.accept(new ExpVisitor(), arg);
      return null;
    }

    public Void visit(CPP.Absyn.SDecls p, Void arg)
    {
      return null;
    }

    // E.g. "int x = 1";
    public Void visit(CPP.Absyn.SInit p, Void arg)
    {
      check (p.type_, p.exp_.accept(new ExpVisitor(), arg));
      newVar (p.id_, p.type_);
      return null;
    }

    public Void visit(CPP.Absyn.SReturn p, Void arg)
    {
      return null;
    }

    public Void visit(CPP.Absyn.SWhile p, Void arg)
    {
      return null;
    }

    // E.g. int x; { int x = 1; x++; }
    public Void visit(CPP.Absyn.SBlock p, Void arg)
    {
      newBlock();
      for (Stm s: p.liststm_) s.accept(new StmVisitor(), arg);
      popBlock();
      return null;
    }
    public Void visit(CPP.Absyn.SIfElse p, Void arg)
    {
      return null;
    }
  }

  ////////////////////////////// Expression //////////////////////////////

  public class ExpVisitor implements Exp.Visitor<Type,Void>
  {

    // Literals
    public Type visit(CPP.Absyn.ETrue p, Void arg)
    {
      return BOOL;
    }
    public Type visit(CPP.Absyn.EFalse p, Void arg)
    {
      return BOOL;
    }
    public Type visit(CPP.Absyn.EInt p, Void arg)
    {
      return INT;
    }
    public Type visit(CPP.Absyn.EDouble p, Void arg)
    {
      return DOUBLE;
    }

    // Variable
    public Type visit(CPP.Absyn.EId p, Void arg)
    {
      return lookupVar (p.id_);
    }

    // Function call  plus(4,3)  where  int plus(int x, int y);
    public Type visit(CPP.Absyn.EApp p, Void arg)
    {
      FunType ft = sig.get(p.id_);
      if (ft == null)
        throw new TypeException("Undefined function " + p.id_);
      if (ft.args.size() != p.listexp_.size())
        throw new TypeException("Function " + p.id_ + " not applied to correct number of arguments");
      // Check function arguments
      int i = 0;
      for (Exp e: p.listexp_) {
        ADecl a = (ADecl)(ft.args.get(i));
        check(a.type_, e.accept(new ExpVisitor(), arg));
        i++;
      }
      return ft.returnType;
    }

    // Increment, decrement

    // x++
    public Type visit(CPP.Absyn.EPostIncr p, Void arg)
    {
      return numericType(lookupVar(isVar(p.exp_)));
    }
    public Type visit(CPP.Absyn.EPostDecr p, Void arg)
    {
      return numericType(lookupVar(isVar(p.exp_)));
    }
    public Type visit(CPP.Absyn.EPreIncr p, Void arg)
    {
      return null;
    }
    public Type visit(CPP.Absyn.EPreDecr p, Void arg)
    {
      return null;
    }

    // Arithmetical operators

    public Type visit(CPP.Absyn.ETimes p, Void arg)
    {
      return null;
    }
    public Type visit(CPP.Absyn.EDiv p, Void arg)
    {
      return null;
    }
    public Type visit(CPP.Absyn.EPlus p, Void arg)
    {
      return null;
    }
    public Type visit(CPP.Absyn.EMinus p, Void arg)
    {
      return null;
    }

    // Comparison operators

    public Type visit(CPP.Absyn.ELt p, Void arg)
    {
      return null;
    }
    public Type visit(CPP.Absyn.EGt p, Void arg)
    {
      return null;
    }
    public Type visit(CPP.Absyn.ELtEq p, Void arg)
    {
      return null;
    }
    public Type visit(CPP.Absyn.EGtEq p, Void arg)
    {
      return null;
    }

    // Equality operators

    public Type visit(CPP.Absyn.EEq p, Void arg)
    {
      return null;
    }
    public Type visit(CPP.Absyn.ENEq p, Void arg)
    {
      return null;
    }

    // Logic operators

    public Type visit(CPP.Absyn.EAnd p, Void arg)
    {
      return null;
    }
    public Type visit(CPP.Absyn.EOr p, Void arg)
    {
      return null;
    }

    // Assignment
    public Type visit(CPP.Absyn.EAss p, Void arg)
    {
      return null;
    }
  }

  ///////////////////////// Context handling /////////////////////////

  public void newVar (String x, Type t) {
      // Get the top context block
      Map<String,Type> m = cxt.get(0);
      // Check that the variable is not declared already
      if (m.containsKey(x))
        throw new TypeException("duplicate variable binding " + x);
      // Add the binding
      m.put(x, t);
  }

  public void newBlock() {
    cxt.add(0, new TreeMap());
  }
  public void popBlock() {
    cxt.remove(0);
  }

  public Type lookupVar(String x) {
    for (Map<String,Type> m : cxt) {
      Type t = m.get(x);
      if (t != null) return t;
    }
    throw new TypeException("unbound variable " + x);
  }

  ////////////////////////////// Exp / Type shape //////////////////////////////

  public String isVar (Exp e) {
    if (e instanceof EId) {
      return ((EId)e).id_;
    } else throw new TypeException("expected variable, found " + e);
  }

  // Expected type: t
  // Inferred type: u
  public void check (Type t, Type u) {
    if (!t.equals(u))
      throw new TypeException("Expected type " + t + ", but found type " + u);
  }

  public Type numericType (Type t) {
    if (!t.equals(INT) && !t.equals(DOUBLE))
      throw new TypeException("expected expression of numeric type");
    return t;
  }
  public void equalTypes (Type t1, Type t2) {
    if (!t1.equals(t2))
      throw new TypeException("expected types " + t1 + " and " + t2 + " to be equal");
  }

}
