class BombTile extends Tile {
	int type() {return 2;}
	void press() {
		super.press();
		throw new BoomException();
	}
	char renderASCII() {
		return flagged() ? '/' : ',';
	}
}
