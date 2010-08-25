class SafeTile extends Tile {
	int type() {return 1;}
	char renderASCII() {
		return flagged() ? '\\' : '.';
	}
}
