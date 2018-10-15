package mapmaker;

public class ToolState {

	
	private final static ToolState s = new ToolState();
	private Tool t = Tool.SELECT;
	private int option = 0;
	private String name;

	public static ToolState getState() {
		return s;
	}
	
	public int getOption() {
		return option;
	}
	public Tool getTool() {
		return t;
	}
	
	public void setTool(Tool t) {
		this.t = t;
	}

	public void setOption(int option) {
		this.option = option;
	}

	public String getText() {
		return this.name;
	}
	
	public void setText(String name) {
		this.name = name;
	}
	
}
