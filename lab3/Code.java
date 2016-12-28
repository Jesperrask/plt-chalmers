// Code.java
// Created by github.com/andreasabel/java-adt

import CPP.Absyn.*;

class Fun {
	public String id;
	public FunType funType;
	public Fun (String id, FunType funType) {
		this.id = id;
		this.funType = funType;
	}
	public String toJVM() {
		return id + funType.toJVM();
	}
}

abstract class Code {
	public abstract <R> R accept (CodeVisitor<R> v);
}

class Store extends Code {
	public Type type;
	public Integer addr;
	public Store (Type type, Integer addr) {
		this.type = type;
		this.addr = addr;
	}
	public <R> R accept (CodeVisitor<R> v) {
		return v.visit (this);
	}
}

class Load extends Code {
	public Type type;
	public Integer addr;
	public Load (Type type, Integer addr) {
		this.type = type;
		this.addr = addr;
	}
	public <R> R accept (CodeVisitor<R> v) {
		return v.visit (this);
	}
}

class IConst extends Code {
	public Integer immed;
	public IConst (Integer immed) {
		this.immed = immed;
	}
	public <R> R accept (CodeVisitor<R> v) {
		return v.visit (this);
	}
}

class DConst extends Code {
	public Double immed;
	public DConst (Double immed) {
		this.immed = immed;
	}
	public <R> R accept (CodeVisitor<R> v) {
		return v.visit (this);
	}
}

class Dup extends Code {
	public Type type;
	public Dup (Type type) {
		this.type = type;
	}
	public <R> R accept (CodeVisitor<R> v) {
		return v.visit (this);
	}
}

class Pop extends Code {
	public Type type;
	public Pop (Type type) {
		this.type = type;
	}
	public <R> R accept (CodeVisitor<R> v) {
		return v.visit (this);
	}
}

class Return extends Code {
	public Type type;
	public Return (Type type) {
		this.type = type;
	}
	public <R> R accept (CodeVisitor<R> v) {
		return v.visit (this);
	}
}

class Call extends Code {
	public Fun fun;
	public Call (Fun fun) {
		this.fun = fun;
	}
	public <R> R accept (CodeVisitor<R> v) {
		return v.visit (this);
	}
}

class Label extends Code {
	public Label label;
	public Label (Label label) {
		this.label = label;
	}
	public <R> R accept (CodeVisitor<R> v) {
		return v.visit (this);
	}
}

class Goto extends Code {
	public Label label;
	public Goto (Label label) {
		this.label = label;
	}
	public <R> R accept (CodeVisitor<R> v) {
		return v.visit (this);
	}
}

class IfZ extends Code {
	public Label label;
	public IfZ (Label label) {
		this.label = label;
	}
	public <R> R accept (CodeVisitor<R> v) {
		return v.visit (this);
	}
}

class IfNZ extends Code {
	public Label label;
	public IfNZ (Label label) {
		this.label = label;
	}
	public <R> R accept (CodeVisitor<R> v) {
		return v.visit (this);
	}
}

class IfEq extends Code {
	public Type type;
	public Label label;
	public IfEq (Type type, Label label) {
		this.type = type;
		this.label = label;
	}
	public <R> R accept (CodeVisitor<R> v) {
		return v.visit (this);
	}
}

class IfNe extends Code {
	public Type type;
	public Label label;
	public IfNe (Type type, Label label) {
		this.type = type;
		this.label = label;
	}
	public <R> R accept (CodeVisitor<R> v) {
		return v.visit (this);
	}
}

class IfLt extends Code {
	public Type type;
	public Label label;
	public IfLt (Type type, Label label) {
		this.type = type;
		this.label = label;
	}
	public <R> R accept (CodeVisitor<R> v) {
		return v.visit (this);
	}
}

class IfGt extends Code {
	public Type type;
	public Label label;
	public IfGt (Type type, Label label) {
		this.type = type;
		this.label = label;
	}
	public <R> R accept (CodeVisitor<R> v) {
		return v.visit (this);
	}
}

class IfLe extends Code {
	public Type type;
	public Label label;
	public IfLe (Type type, Label label) {
		this.type = type;
		this.label = label;
	}
	public <R> R accept (CodeVisitor<R> v) {
		return v.visit (this);
	}
}

class IfGe extends Code {
	public Type type;
	public Label label;
	public IfGe (Type type, Label label) {
		this.type = type;
		this.label = label;
	}
	public <R> R accept (CodeVisitor<R> v) {
		return v.visit (this);
	}
}

class DGt extends Code {
	public DGt () {
	}
	public <R> R accept (CodeVisitor<R> v) {
		return v.visit (this);
	}
}

class DLt extends Code {
	public DLt () {
	}
	public <R> R accept (CodeVisitor<R> v) {
		return v.visit (this);
	}
}

class Inc extends Code {
	public Type type;
	public Integer addr;
	public Integer delta;
	public Inc (Type type, Integer addr, Integer delta) {
		this.type = type;
		this.addr = addr;
		this.delta = delta;
	}
	public <R> R accept (CodeVisitor<R> v) {
		return v.visit (this);
	}
}

class Add extends Code {
	public Type type;
	public Add (Type type) {
		this.type = type;
	}
	public <R> R accept (CodeVisitor<R> v) {
		return v.visit (this);
	}
}

class Sub extends Code {
	public Type type;
	public Sub (Type type) {
		this.type = type;
	}
	public <R> R accept (CodeVisitor<R> v) {
		return v.visit (this);
	}
}

class Mul extends Code {
	public Type type;
	public Mul (Type type) {
		this.type = type;
	}
	public <R> R accept (CodeVisitor<R> v) {
		return v.visit (this);
	}
}

class Div extends Code {
	public Type type;
	public Div (Type type) {
		this.type = type;
	}
	public <R> R accept (CodeVisitor<R> v) {
		return v.visit (this);
	}
}

interface CodeVisitor<R> {
	public R visit (Store c);
	public R visit (Load c);
	public R visit (IConst c);
	public R visit (DConst c);
	public R visit (Dup c);
	public R visit (Pop c);
	public R visit (Return c);
	public R visit (Call c);
	public R visit (Label c);
	public R visit (Goto c);
	public R visit (IfZ c);
	public R visit (IfNZ c);
	public R visit (IfEq c);
	public R visit (IfNe c);
	public R visit (IfLt c);
	public R visit (IfGt c);
	public R visit (IfLe c);
	public R visit (IfGe c);
	public R visit (DGt c);
	public R visit (DLt c);
	public R visit (Inc c);
	public R visit (Add c);
	public R visit (Sub c);
	public R visit (Mul c);
	public R visit (Div c);
}

class CodeToJVM implements CodeVisitor<String> {
	public String visit (Store c) {
		if(c.type instanceof Type_int)
			return "istore_"+c.addr;
		else if (c.type instanceof Type_double)
			return "dstore_"+c.addr;
		else 
			return "astore_"+c.addr;
	}

	public String visit (Load c) {
		return "";
	}

	public String visit (IConst c) {
		int i = c.immed.intValue();
		if (i == -1) return "iconst_m1";
		if (i >= 0 && i <= 5) return "iconst_" + i;
		if (i >= -128 && i < 128) return "bipush " + i;
		return "ldc " + c.immed.toString();
	}

	public String visit (DConst c) {
		return "";
	}

	public String visit (Dup c) {
		if(c.type instanceof Type_double)
			return "dup2";
		else
			return "dup";
	}

	public String visit (Pop c) {
		if (c.type instanceof Type_double) 
			return "pop2";
		else
			return "pop";
	}

	public String visit (Return c) {
		return "";
	}

	public String visit (Call c) {
		return "";
	}

	public String visit (Label c) {
		return "";
	}

	public String visit (Goto c) {
		return "";
	}

	public String visit (IfZ c) {
		return "";
	}

	public String visit (IfNZ c) {
		return "";
	}

	public String visit (IfEq c) {
		return "";
	}

	public String visit (IfNe c) {
		return "";
	}

	public String visit (IfLt c) {
		return "";
	}

	public String visit (IfGt c) {
		return "";
	}

	public String visit (IfLe c) {
		return "";
	}

	public String visit (IfGe c) {
		return "";
	}

	public String visit (DGt c) {
		return "";
	}

	public String visit (DLt c) {
		return "";
	}

	public String visit (Inc c) {
		return "";
	}

	public String visit (Add c) {
		return "";
	}

	public String visit (Sub c) {
		return "";
	}

	public String visit (Mul c) {
		return "";
	}

	public String visit (Div c) {
		return "";
	}

}
