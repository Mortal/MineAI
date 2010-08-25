abstract class Tile {
	abstract int type();
	private boolean pressed;
	private boolean flagged;
	public int surroundingBombs = 0;
	void press() { this.pressed = true; }
	boolean pressed() { return this.pressed; }
	void flag() { this.flagged = true; }
	boolean flagged() { return this.flagged; }
	abstract char renderASCII();
	void renderASCII(StringBuilder out) { out.append(renderASCII()); }
}
