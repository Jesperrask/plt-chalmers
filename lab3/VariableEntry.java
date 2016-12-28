import CPP.Absyn.Type;

/**
Contributors: Nachi
*/
public class VariableEntry {

	private Type t;
	private Integer address;
	
	public VariableEntry(Type t, Integer address) {
		this.setType(t);
		this.setAddress(address);
	}

	public Type getType() {
		return t;
	}

	public void setType(Type t) {
		this.t = t;
	}

	public Integer getAddress() {
		return address;
	}

	public void setAddress(Integer address) {
		this.address = address;
	}
	
	

}
