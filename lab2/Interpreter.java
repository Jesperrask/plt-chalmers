import java.util.*;
import CPP.Absyn.*;

public class Interpreter {

  // signature
  private Map<String,DFun> sig;

  // environment
  private List<Map<String,Value>> env;

  // entrypoint
  public void interpret(Program p) {
    throw new RuntimeException("Not yet an interpreter");
  }

  public class ProgramVisitor implements Program.Visitor<Void,Void>
  {
    public Void visit(CPP.Absyn.PDefs p, Void arg)
    { /* Code For PDefs Goes Here */
      for (Def x: p.listdef_)
      { /* ... */ }
      return null;
    }
  }
  public class DefVisitor implements Def.Visitor<Void,Void>
  {
    public Void visit(CPP.Absyn.DFun p, Void arg)
    { /* Code For DFun Goes Here */
      // p.type_.accept(new TypeVisitor<Void,Void>(), arg);
      //p.id_;
      for (Arg x: p.listarg_)
      { /* ... */ }
      for (Stm x: p.liststm_)
      { /* ... */ }
      return null;
    }
  }

  ////////////////////////////// Statements //////////////////////////////

  public class StmVisitor implements Stm.Visitor<Value,Void>
  {
    public Value visit(CPP.Absyn.SExp p, Void arg)
    { /* Code For SExp Goes Here */
      p.exp_.accept(new ExpVisitor(), arg);
      return null;
    }
    public Value visit(CPP.Absyn.SDecls p, Void arg)
    { /* Code For SDecls Goes Here */
      // p.type_.accept(new TypeVisitor(), arg);
      for (String x: p.listid_)
      { /* ... */ }
      return null;
    }
    public Value visit(CPP.Absyn.SInit p, Void arg)
    { /* Code For SInit Goes Here */
      // p.type_.accept(new TypeVisitor(), arg);
      //p.id_;
      p.exp_.accept(new ExpVisitor(), arg);
      return null;
    }
    public Value visit(CPP.Absyn.SReturn p, Void arg)
    { /* Code For SReturn Goes Here */
      return p.exp_.accept(new ExpVisitor(), arg);
    }
    public Value visit(CPP.Absyn.SWhile p, Void arg)
    { /* Code For SWhile Goes Here */
      p.exp_.accept(new ExpVisitor(), arg);
      p.stm_.accept(new StmVisitor(), arg);
      return null;
    }

    public Value visit(CPP.Absyn.SBlock p, Void arg)
    { /* Code For SBlock Goes Here */
      Value v = null;
      //TODO - newBlock();
      for (Stm s: p.liststm_) {
        v = s.accept(new StmVisitor(), arg);
        if (v != null) break;
      }
      //TODO - popBlock();
      return v;
    }

    public Value visit(CPP.Absyn.SIfElse p, Void arg)
    { /* Code For SIfElse Goes Here */
      p.exp_.accept(new ExpVisitor(), arg);
      p.stm_1.accept(new StmVisitor(), arg);
      p.stm_2.accept(new StmVisitor(), arg);
      return null;
    }
  }

  ////////////////////////////// Expressions //////////////////////////////

  public class ExpVisitor implements Exp.Visitor<Value,Void>
  {
    public Value visit(CPP.Absyn.ETrue p, Void arg)
    { /* Code For ETrue Goes Here */
      return null;
    }
    public Value visit(CPP.Absyn.EFalse p, Void arg)
    { /* Code For EFalse Goes Here */
      return null;
    }
    public Value visit(CPP.Absyn.EInt p, Void arg)
    { /* Code For EInt Goes Here */
      //p.integer_;
      return null;
    }
    public Value visit(CPP.Absyn.EDouble p, Void arg)
    { /* Code For EDouble Goes Here */
      //p.double_;
      return null;
    }
    public Value visit(CPP.Absyn.EId p, Void arg)
    { /* Code For EId Goes Here */
      //p.id_;
      return null;
    }
    public Value visit(CPP.Absyn.EApp p, Void arg)
    { /* Code For EApp Goes Here */
      //p.id_;
      for (Exp x: p.listexp_)
      { /* ... */ }
      return null;
    }
    public Value visit(CPP.Absyn.EPostIncr p, Void arg)
    { /* Code For EPostIncr Goes Here */
      p.exp_.accept(new ExpVisitor(), arg);
      return null;
    }
    public Value visit(CPP.Absyn.EPostDecr p, Void arg)
    { /* Code For EPostDecr Goes Here */
      p.exp_.accept(new ExpVisitor(), arg);
      return null;
    }
    public Value visit(CPP.Absyn.EPreIncr p, Void arg)
    { /* Code For EPreIncr Goes Here */
      p.exp_.accept(new ExpVisitor(), arg);
      return null;
    }
    public Value visit(CPP.Absyn.EPreDecr p, Void arg)
    { /* Code For EPreDecr Goes Here */
      p.exp_.accept(new ExpVisitor(), arg);
      return null;
    }
    public Value visit(CPP.Absyn.ETimes p, Void arg)
    { /* Code For ETimes Goes Here */
      p.exp_1.accept(new ExpVisitor(), arg);
      p.exp_2.accept(new ExpVisitor(), arg);
      return null;
    }
    public Value visit(CPP.Absyn.EDiv p, Void arg)
    { /* Code For EDiv Goes Here */
      p.exp_1.accept(new ExpVisitor(), arg);
      p.exp_2.accept(new ExpVisitor(), arg);
      return null;
    }
    public Value visit(CPP.Absyn.EPlus p, Void arg)
    { /* Code For EPlus Goes Here */
      p.exp_1.accept(new ExpVisitor(), arg);
      p.exp_2.accept(new ExpVisitor(), arg);
      return null;
    }
    public Value visit(CPP.Absyn.EMinus p, Void arg)
    { /* Code For EMinus Goes Here */
      p.exp_1.accept(new ExpVisitor(), arg);
      p.exp_2.accept(new ExpVisitor(), arg);
      return null;
    }
    public Value visit(CPP.Absyn.ELt p, Void arg)
    { /* Code For ELt Goes Here */
      p.exp_1.accept(new ExpVisitor(), arg);
      p.exp_2.accept(new ExpVisitor(), arg);
      return null;
    }
    public Value visit(CPP.Absyn.EGt p, Void arg)
    { /* Code For EGt Goes Here */
      p.exp_1.accept(new ExpVisitor(), arg);
      p.exp_2.accept(new ExpVisitor(), arg);
      return null;
    }
    public Value visit(CPP.Absyn.ELtEq p, Void arg)
    { /* Code For ELtEq Goes Here */
      p.exp_1.accept(new ExpVisitor(), arg);
      p.exp_2.accept(new ExpVisitor(), arg);
      return null;
    }
    public Value visit(CPP.Absyn.EGtEq p, Void arg)
    { /* Code For EGtEq Goes Here */
      p.exp_1.accept(new ExpVisitor(), arg);
      p.exp_2.accept(new ExpVisitor(), arg);
      return null;
    }
    public Value visit(CPP.Absyn.EEq p, Void arg)
    { /* Code For EEq Goes Here */
      p.exp_1.accept(new ExpVisitor(), arg);
      p.exp_2.accept(new ExpVisitor(), arg);
      return null;
    }
    public Value visit(CPP.Absyn.ENEq p, Void arg)
    { /* Code For ENEq Goes Here */
      p.exp_1.accept(new ExpVisitor(), arg);
      p.exp_2.accept(new ExpVisitor(), arg);
      return null;
    }
    public Value visit(CPP.Absyn.EAnd p, Void arg)
    { /* Code For EAnd Goes Here */
      p.exp_1.accept(new ExpVisitor(), arg);
      p.exp_2.accept(new ExpVisitor(), arg);
      return null;
    }
    public Value visit(CPP.Absyn.EOr p, Void arg)
    { /* Code For EOr Goes Here */
      p.exp_1.accept(new ExpVisitor(), arg);
      p.exp_2.accept(new ExpVisitor(), arg);
      return null;
    }
    public Value visit(CPP.Absyn.EAss p, Void arg)
    { /* Code For EAss Goes Here */
      p.exp_1.accept(new ExpVisitor(), arg);
      p.exp_2.accept(new ExpVisitor(), arg);
      return null;
    }
  }
}
