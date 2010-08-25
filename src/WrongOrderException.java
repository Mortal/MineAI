class WrongOrderException extends IllegalArgumentException {
	WrongOrderException() {
		super("Wrong order in position or size array (array length != dimension count)");
	}
}
